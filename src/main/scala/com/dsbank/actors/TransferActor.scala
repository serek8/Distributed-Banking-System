package com.dsbank.actors

import akka.actor.{Actor, ActorRef}


//object TransferActor {
//
//  final case class TransferAPI(accountNumberDestination: String, amount: Long)
//  final case class Transfer(bankAccountCluster: ActorRef, accountNumberDestination: String, amount: Long)
//
//  trait OperationOutcome
//  final case class OperationSuccess(result: String) extends OperationOutcome
//  final case class OperationFailure(reason: String) extends OperationOutcome
//
//}
//
//class TransferActor extends Actor {
//
//
//  def receive: Receive = {
//    case Transfer(bankAccountCluster, accountNumberDestination, amount) =>
//      print("We are in StartTransfer\n");
////      bankAccountCluster ! BankAccountActor.Withdraw(amount)
//
//  }
//
//}
