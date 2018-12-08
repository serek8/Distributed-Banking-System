package com.dsbank.actors

import akka.actor.{ActorLogging, ActorRef}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.onSuccess
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.persistence.PersistentActor
import com.dsbank.Remote.MessageWithId
import akka.pattern.ask

import scala.concurrent.duration._
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object BankAccountActor {

  final case class Create(clock: Int, accountNumber: String)
  final case class CreateAPI(accountNumber: String)

  final case class Withdraw(clock: Int, amount: Float)
  final case class WithdrawAPI(amount: Float)

  final case class Deposit(clock: Int, amount: Float)
  final case class DepositAPI(amount: Float)

  final case class Interest(clock: Int, constant: Float)
  final case class InterestAPI(constant: Float)

  final case class Transfer(clockWithdraw: Int, clockDeposit: Int, bankAccountCluster: ActorRef, accountNumberDestination: String, amount: Float)
  final case class TransferAPI(accountNumberDestination: String, amount: Float)

  final case class GetBalance(clock: Int)
  final case class GetBalanceAPI()

  trait OperationOutcome

  final case class OperationSuccess(result: String) extends OperationOutcome

  final case class OperationFailure(reason: String) extends OperationOutcome

}

trait BankAccountEvent
case class BalanceIncreased(amount: Float) extends BankAccountEvent
case class BalanceDecreased(amount: Float) extends BankAccountEvent
case class AccountCreated() extends BankAccountEvent

class BankAccountActor extends PersistentActor with ActorLogging {

  implicit val timeout: Timeout = Timeout(5.seconds)
  import BankAccountActor._

  override def persistenceId: String = self.path.name

  var balance: Float = 0
  var active = false

  def updateState(event: BankAccountEvent): Unit = event match {
    case AccountCreated() => active = true
    case BalanceIncreased(amount) => balance += amount
    case BalanceDecreased(amount) => balance -= amount
  }

  val receiveRecover: Receive = {
    case evt: BankAccountEvent => updateState(evt)
  }

  val receiveCommand: Receive = {
    case Create(clock, accountNumber) =>
      persist(AccountCreated())(e => {
        updateState(e)
        sender() ! OperationSuccess("Bank account created")
      })
    case GetBalance(clock) =>
      if (!active) {
        sender() ! OperationFailure("Account doesn't exist")
      } else {
        sender() ! OperationSuccess(balance.toString)
      }
    case Withdraw(clock, amount) =>
      if (active) {
        if (balance >= amount) {
          persist(BalanceDecreased(amount))(e => {
            updateState(e)
          })
        }
      }
    case Deposit(clock, amount) =>
      if (active) {
        println("Received Key: "+ self.path.name + ":" + clock.toString + "\n")
        persist(BalanceIncreased(amount))(e => {
          updateState(e)
        })
      }
    case Interest(clock, constant) =>
      if(active){
        sender() ! OperationFailure("Account doesn't exist")
        val bankEvent = BalanceIncreased(balance * constant) // if constant < 0, then the value will be decreased anyway
        persist(bankEvent)(e => {
          updateState(e)
        })
      }
    case Transfer(clockWithdraw, clockDeposit, bankAccountCluster, accountNumberDestination, amount) =>
      if(active){
        if (amount <= balance) {
          persist(BalanceDecreased(amount))(e => {
            updateState(e)
            bankAccountCluster ! MessageWithId(accountNumberDestination, Deposit(clockDeposit, amount))
            // What is 'accountNumberDestination' isn't created yet?
          })
        }
        else {
          sender() ! OperationFailure("Not enough funds")
        }
      }
  }
}