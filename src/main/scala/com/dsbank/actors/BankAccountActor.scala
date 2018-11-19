package com.dsbank.actors

import akka.actor.{Actor, ActorLogging}

object BankAccountActor {

  final case class Create(accountNumber: String)

  final case class Withdraw(amount: Long)

  final case class Deposit(amount: Long)

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
  }
}