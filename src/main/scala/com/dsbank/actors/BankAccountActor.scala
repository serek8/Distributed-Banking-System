package com.dsbank.actors

import akka.actor.{Actor, ActorLogging}

object BankAccountActor {

  final case class Create(accountNumber: String)

  final case class Withdraw(amount: Long)

  final case class Deposit(amount: Long)

  final case object GetBalance

  trait Status

  final case class Success(result: String) extends Status

  final case class Failure(reason: String) extends Status

}

class BankAccountActor extends Actor with ActorLogging {

  import BankAccountActor._

  var balance: Long = 0
  var active = false

  def receive: Receive = {
    case Create(accountNumber) =>
      active = true
      sender() ! Success("Bank account created")
    case GetBalance =>
      if (!active) {
        sender() ! Failure("Account doesn't exist") // TODO: Interceptor
      } else {
        sender() ! Success(balance.toString)
      }
    case Withdraw(amount) =>
      if (!active) {
        sender() ! Failure("Account doesn't exist") // TODO: Interceptor
      } else {
        if (balance >= amount) {
          balance -= amount
          sender() ! Success(s"$amount withdrawn successfully.")
        } else {
          sender() ! Failure("Insufficient balance.")
        }
      }
    case Deposit(amount) =>
      if (!active) {
        sender() ! Failure("Account doesn't exist") // TODO: Interceptor
      } else {
        balance += amount
        sender() ! Success(s"$amount deposited successfully.")
      }
  }
}