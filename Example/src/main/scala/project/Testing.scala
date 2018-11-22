package project

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global

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

object TransactionActor {

  final case class StartTransference(actorA: ActorRef, actorB: ActorRef, amount: Long)

  final case class PaymentReceived(msg: String)

  trait Status

  final case class Done(result: String) extends Status

  final case class Failed(reason: String) extends Status

}

class TransactionActor extends Actor {
  import TransactionActor._

  //Withdraw amount from actorA
  def receive: Receive = {
    case StartTransference(actorA, actorB, amount) =>
      actorA ! BankAccountActor.Withdraw(amount)
      context.become(responseWithdraw(actorA, actorB, amount))
  }
  //If everything works fine, the deposit of money in actorB's account is performed. Otherwise, it is not done.
  def responseWithdraw(actorA: ActorRef, actorB: ActorRef, amount: Long): Receive = {
    //If this response is successful, then proceed with the deposit
    case BankAccountActor.Success =>
      actorB ! BankAccountActor.Deposit(amount)
      context.become(finalResponse(actorA: ActorRef, actorB: ActorRef))
      //The deposit is not performed
    case BankAccountActor.Failure =>
      actorA ! Failed("The transference was not completed.")
      context.stop(self)
      //How can we make something to retry transference?
  }
  //Transference status
  def finalResponse(actorA: ActorRef, actorB: ActorRef): Receive = {
    case BankAccountActor.Success =>
      actorA ! Done("The transference was successful")
      sender() ! PaymentReceived("Done")
    case BankAccountActor.Failure =>
      actorA ! Failed("Transference was not completed.")
      context.stop(self)
  }
}

object Testing extends App {

  import BankAccountActor._
  import TransactionActor._

  implicit val timeout: akka.util.Timeout = Timeout.apply(1, java.util.concurrent.TimeUnit.SECONDS)
  val system: ActorSystem = ActorSystem("bankAccount")

  val bank1: ActorRef = system.actorOf(Props[BankAccountActor], name = "bank1")
  val bank2: ActorRef = system.actorOf(Props[BankAccountActor], name = "bank2")
  val transactor: ActorRef = system.actorOf(Props[TransactionActor], name = "transactor")

  //Some scenarios
  bank1 ? Create("342534") map { x => println(x) }
  bank1 ? Deposit(10000) map { x => println(x) }
  bank1 ? Withdraw(300) map { x => println(x) }
  bank2 ? Create("453452") map { x => println(x) }
  bank2 ? Deposit(100) map { x => println(x) }
  bank1 ? GetBalance map { x => println(x) }
  transactor ? StartTransference(bank1, bank2, 100) map { x => println(x) }

}