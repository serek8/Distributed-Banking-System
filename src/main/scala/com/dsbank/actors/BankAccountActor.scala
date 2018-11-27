package com.dsbank.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.dsbank.Remote.MessageWithId

object BankAccountActor {

  final case class Create(accountNumber: String)

  final case class Withdraw(amount: Long)

  final case class Deposit(amount: Long)

  final case class Interest(constant: Long)

  final case class Transfer(bankAccountCluster: ActorRef, accountNumberDestination: String, amount: Long)

  final case class TransferAPI(accountNumberDestination: String, amount: Long)

  final case object GetBalance

  trait OperationOutcome

  final case class OperationSuccess(result: String) extends OperationOutcome

  final case class OperationFailure(reason: String) extends OperationOutcome

}

class BankAccountActor extends Actor with ActorLogging {

  import BankAccountActor._

  var balance: Long = 0
  var active = false

  def receive: Receive = {
    case Create(accountNumber) =>
      active = true
      sender() ! OperationSuccess("Bank account created")
    case GetBalance =>
      if (!active) {
        sender() ! OperationFailure("Account doesn't exist") // TODO: Interceptor
      } else {
        sender() ! OperationSuccess(balance.toString)
      }
    case Withdraw(amount) =>
      if (!active) {
        sender() ! OperationFailure("Account doesn't exist") // TODO: Interceptor
      } else {
        if (balance >= amount) {
          balance -= amount
          sender() ! OperationSuccess(s"$amount withdrawn successfully.")
        } else {
          sender() ! OperationFailure("Insufficient balance.")
        }
      }
    case Deposit(amount) =>
      if (!active) {
        sender() ! OperationFailure("Account doesn't exist") // TODO: Interceptor
      } else {
        balance += amount
        sender() ! OperationSuccess(s"$amount deposited successfully.")
      }
    case Interest(constant) =>
      if (balance > 0) {
        balance = balance + (balance*constant*0.01).toLong
        sender() ! OperationSuccess("The interest has been applied successfully")
      } else {
        sender() ! OperationFailure("Insufficient funds")
      }
    case Transfer(bankAccountCluster, accountNumberDestination, amount) =>
      if (amount <= balance) {
        balance = balance - amount
        bankAccountCluster ! MessageWithId(accountNumberDestination, Deposit(amount))
        sender() ! OperationSuccess(s"transfer deposited successfully.")
      }
      else {
        sender() ! OperationFailure(s"Not enough funds.")
      }
  }
}