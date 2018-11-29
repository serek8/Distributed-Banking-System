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

  final case class Create(accountNumber: String)

  final case class Withdraw(amount: Float)

  final case class Deposit(amount: Float)

  final case class Interest(constant: Float)

  final case class Transfer(bankAccountCluster: ActorRef, accountNumberDestination: String, amount: Float)

  final case class TransferAPI(accountNumberDestination: String, amount: Float)

  final case object GetBalance

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
    case Create(_) =>
      persist(AccountCreated())(e => {
        updateState(e)
        sender() ! OperationSuccess("Bank account created")
      })
    case GetBalance =>
      if (!active) {
        sender() ! OperationFailure("Account doesn't exist")
      } else {
        sender() ! OperationSuccess(balance.toString)
      }
    case Withdraw(amount) =>
      if (!active) {
        sender() ! OperationFailure("Account doesn't exist")
      } else {
        if (balance >= amount) {
          persist(BalanceDecreased(amount))(e => {
            updateState(e)
            sender() ! OperationSuccess("Withdrawn successfully.")
          })
        } else {
          sender() ! OperationFailure("Insufficient balance.")
        }
      }
    case Deposit(amount) =>
      if (!active) {
        sender() ! OperationFailure("Account doesn't exist")
      } else {
        persist(BalanceIncreased(amount))(e => {
          updateState(e)
          sender() ! OperationSuccess("Deposited successfully.")
        })
      }
    case Interest(constant) =>
      if(!active){
        sender() ! OperationFailure("Account doesn't exist")
      } else {
        val bankEvent = if(constant > 0) {
          BalanceIncreased(balance * constant)
        } else{
          BalanceDecreased(balance * constant)
        }
        persist(bankEvent)(e => {
          updateState(e)
          sender() ! OperationSuccess("The interest has been applied successfully.")
        })
      }
    case Transfer(bankAccountCluster, accountNumberDestination, amount) =>

      if(!active){
        sender() ! OperationFailure("Account doesn't exist")
      } else {
        if (amount <= balance) {
          persist(BalanceDecreased(amount))(e => {
            updateState(e)
//            val moneyDeposit : Future[OperationOutcome] = (bankAccountCluster ? MessageWithId(accountNumberDestination, Deposit(amount))).mapToFuture[OperationOutcome]
              val moneyDeposited: Future[OperationOutcome] =
                (bankAccountCluster ? MessageWithId(accountNumberDestination, Deposit(amount))).mapTo[OperationOutcome]
              moneyDeposited.onComplete {
              case Success(value) =>
                value match {
                  case OperationSuccess(_) => updateState(e)
                  case OperationFailure(_) => self ! Deposit(amount)
                }
              case Failure(ex) =>
                self ! Deposit(amount)
                ex.printStackTrace
            }
            bankAccountCluster ! MessageWithId(accountNumberDestination, Deposit(amount))
            sender() ! OperationSuccess(s"$amount withdrawn successfully.")
          })
        }
        else {
          sender() ! OperationFailure("Not enough funds")
        }
      }
  }
}