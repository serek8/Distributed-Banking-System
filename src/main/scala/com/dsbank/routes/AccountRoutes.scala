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
import com.dsbank.actors.ClockManagerActor
import com.dsbank.actors.ClockManagerActor.GetNextClockValue

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait AccountRoutes extends JsonSupport {

  implicit def system: ActorSystem

  def bankAccountActorsCluster: ActorRef
  val clockManagerActor: ActorRef

  implicit lazy val timeout = Timeout(10.seconds)
  val clockMap = new ConcurrentHashMap[String,AtomicInteger]()

  def getNextClockValue(bankAccount:String) : Int = {
    val currentClock = clockMap.computeIfAbsent(
      bankAccount, (k: String) => new AtomicInteger(0)).getAndIncrement()
    return currentClock
  }

  lazy val accountRoutes: Route =
    pathPrefix("accounts") {
      post {
        entity(as[CreateAPI]) { create =>
          val currentClockRetrieved: Future[Int] = (clockManagerActor ? GetNextClockValue(create.accountNumber)).mapTo[Int]
          onSuccess(currentClockRetrieved) { currentClock =>
            val accountCreated: Future[OperationOutcome] =
              (bankAccountActorsCluster ? MessageWithId(
                create.accountNumber,
                Create(currentClock, create.accountNumber)
              )).mapTo[OperationOutcome]

            onSuccess(accountCreated) {
              _ => complete(StatusCodes.Created)
            }
          }
        }
      } ~
        pathPrefix(Segment) {
          accountNumber =>
          path("balance") {
            get {
              val currentClockRetrieved: Future[Int] = (clockManagerActor ? GetNextClockValue(accountNumber)).mapTo[Int]
              onSuccess(currentClockRetrieved) { currentClock =>
                val balanceRetrieved: Future[OperationOutcome] =
                  (bankAccountActorsCluster ? MessageWithId(
                    accountNumber,
                    GetBalance(currentClock))
                    ).mapTo[OperationOutcome]

                onSuccess(balanceRetrieved) {
                  case OperationSuccess(result) =>
                    complete(result)
                  case OperationFailure(_) =>
                    complete(StatusCodes.NotFound)
                }
              }
            }
          }~
          path("withdraw") {
            post {
              entity(as[WithdrawAPI]) { withdraw =>
                val currentClockRetrieved: Future[Int] = (clockManagerActor ? GetNextClockValue(accountNumber)).mapTo[Int]
                onSuccess(currentClockRetrieved) { currentClock =>
                  bankAccountActorsCluster ! MessageWithId(
                    accountNumber,
                    Withdraw(currentClock, withdraw.amount)
                  )
                  complete(StatusCodes.NoContent)
                }
              }
            }
          }~
          path("interest") {
            post {
              entity(as[InterestAPI]) { interest =>
                val currentClockRetrieved: Future[Int] = (clockManagerActor ? GetNextClockValue(accountNumber)).mapTo[Int]
                onSuccess(currentClockRetrieved) { currentClock =>
                  bankAccountActorsCluster ! MessageWithId(
                    accountNumber,
                    Interest(currentClock, interest.constant))
                  complete(StatusCodes.NoContent)
                }
              }
            }
          }~
          path("deposit") {
            post {
              entity(as[DepositAPI]) { deposit =>
                val currentClockRetrieved: Future[Int] = (clockManagerActor ? GetNextClockValue(accountNumber)).mapTo[Int]
                onSuccess(currentClockRetrieved) { currentClock =>
                  bankAccountActorsCluster ! MessageWithId(
                    accountNumber,
                    Deposit(currentClock, deposit.amount)
                  )
                  complete(StatusCodes.NoContent)
                }
              }
            }
          }~
          path("transfer") {
            post {
              entity(as[TransferAPI]) { transfer =>
                val currentClockOriginRetrieved: Future[Int] = (clockManagerActor ? GetNextClockValue(accountNumber)).mapTo[Int]
                val currentClockDestRetrieved: Future[Int] = (clockManagerActor ? GetNextClockValue(transfer.accountNumberDestination)).mapTo[Int]
                val clocksRetrieved = for {
                  origin <- currentClockOriginRetrieved
                  dest <- currentClockDestRetrieved
                } yield Seq(origin, dest)
                onSuccess(clocksRetrieved) { clocks =>
                  bankAccountActorsCluster ! MessageWithId(
                    accountNumber,
                    Transfer(
                      clocks(0),
                      clocks(1),
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

  def testClock(): Unit = {
    bankAccountActorsCluster ! MessageWithId("1", Deposit(1, 200))
    bankAccountActorsCluster ! MessageWithId("1", Deposit(0, 400))
    complete(StatusCodes.NoContent)
  }
}
