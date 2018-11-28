package com.dsbank.actors

import akka.actor.{ActorLogging}
import akka.persistence.PersistentActor

object BankAccountActor {

  final case class Create(accountNumber: String)

  final case class Withdraw(amount: Long)

  final case class Deposit(amount: Long)

  final case object GetBalance

  trait OperationOutcome

  final case class OperationSuccess(result: String) extends OperationOutcome

  final case class OperationFailure(reason: String) extends OperationOutcome

}

trait BankAccountEvent
case class BalanceIncreased(amount: Long) extends BankAccountEvent
case class BalanceDecreased(amount: Long) extends BankAccountEvent
case class AccountCreated() extends BankAccountEvent

class BankAccountActor extends PersistentActor with ActorLogging {

  import BankAccountActor._

  override def persistenceId: String = self.path.name

  var balance: Long = 0
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
            sender() ! OperationSuccess(s"$amount withdrawn successfully.")
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
          sender() ! OperationSuccess(s"$amount deposited successfully.")
        })
      }
  }
}