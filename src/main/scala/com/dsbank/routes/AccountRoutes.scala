package com.dsbank.routes

import java.util

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import com.dsbank.JsonSupport
import com.dsbank.Remote.MessageWithId
import com.dsbank.actors.BankAccountActor._
import akka.pattern.ask
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.Future

trait AccountRoutes extends JsonSupport {

  implicit def system: ActorSystem

  def bankAccountActorsCluster: ActorRef

  implicit lazy val timeout = Timeout(10.seconds)
  val clockMap = new ConcurrentHashMap[String,AtomicInteger]()

  def getNextClockValue(bankAccount:String) : Int = {
    val currentClock = clockMap.computeIfAbsent(
      bankAccount, (k: String) => new AtomicInteger(0)).getAndIncrement()
    println("1) Created Key: "+ bankAccount + ":" + currentClock.toString + "\n")
    return currentClock
  }

  lazy val accountRoutes: Route =
    pathPrefix("accounts") {
      post {
        entity(as[CreateAPI]) { create =>
          val accountCreated: Future[OperationOutcome] =
                      (bankAccountActorsCluster ? MessageWithId(
                        create.accountNumber,
                        Create(getNextClockValue(create.accountNumber), create.accountNumber))).mapTo[OperationOutcome]

          onSuccess(accountCreated) {
            _ => complete(StatusCodes.Created)
          }
        }
      } ~
        pathPrefix(Segment) {
          accountNumber =>
          path("balance") {
            get {
              val balanceRetrieved: Future[OperationOutcome] =
                (bankAccountActorsCluster ? MessageWithId(
                  accountNumber,
                  GetBalance(getNextClockValue(accountNumber)))
                  ).mapTo[OperationOutcome]

              onSuccess(balanceRetrieved) {
                case OperationSuccess(result) =>
                  complete(result)
                case OperationFailure(_) =>
                  complete(StatusCodes.NotFound)
              }
            }
          }~
          path("withdraw") {
            post {
              entity(as[WithdrawAPI]) { withdraw =>
                bankAccountActorsCluster ! MessageWithId(
                  accountNumber,
                  Withdraw(getNextClockValue(accountNumber),withdraw.amount)
                )
                complete(StatusCodes.NoContent)
              }
            }
          }~
          path("interest") {
            post {
              entity(as[InterestAPI]) { interest =>
                bankAccountActorsCluster ! MessageWithId(
                  accountNumber,
                  Interest(getNextClockValue(accountNumber), interest.constant))
                complete(StatusCodes.NoContent)
              }
            }
          }~
          path("deposit") {
            post {
              println("Will deposit")
              entity(as[DepositAPI]) { deposit =>
                println("done - as[DepositAPI]")
                bankAccountActorsCluster ! MessageWithId(
                  accountNumber,
                  Deposit(getNextClockValue(accountNumber), deposit.amount)
//                  Deposit(deposit.amount)
                )
                complete(StatusCodes.NoContent)
              }
            }
          }~
          path("transfer") {
            post {
              entity(as[TransferAPI]) { transfer =>
                bankAccountActorsCluster ! MessageWithId(
                  accountNumber,
                  Transfer(
                    getNextClockValue(accountNumber),
                    getNextClockValue(transfer.accountNumberDestination),
                    bankAccountActorsCluster, transfer.accountNumberDestination,
                    transfer.amount)
                )
                complete(StatusCodes.NoContent)
              }
            }
          }
        }
    }
}
