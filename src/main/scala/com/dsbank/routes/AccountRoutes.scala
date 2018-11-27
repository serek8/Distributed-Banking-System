package com.dsbank.routes

import akka.actor.{ActorRef, ActorSystem}
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
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.Future

trait AccountRoutes extends JsonSupport {

  implicit def system: ActorSystem

  def bankAccountActorsCluster: ActorRef

  implicit lazy val timeout = Timeout(5.seconds)

  lazy val accountRoutes: Route =
    pathPrefix("accounts") {
      post {
        entity(as[Create]) { create =>
          val accountCreated: Future[Status] =
            (bankAccountActorsCluster ? MessageWithId(create.accountNumber, Create(create.accountNumber))).mapTo[Status]

          onSuccess(accountCreated) {
            _ => complete(StatusCodes.Created)
          }
        }
      } ~
        pathPrefix(Segment) { accountNumber =>
          path("balance") {
            get {
              val balanceRetrieved: Future[Status] =
                (bankAccountActorsCluster ? MessageWithId(accountNumber, GetBalance)).mapTo[Status]

              onSuccess(balanceRetrieved) {
                case performed: Success =>
                  complete(performed.result)
                case _: Failure =>
                  complete(StatusCodes.NotFound)
              }
            }
          } ~
            path("withdraw") {
              post {
                // TODO: Add withdraw handler
                complete(s"Withdraw for account no $accountNumber")
              }
            } ~
            path("deposit") {
              post {
                entity(as[Deposit]) { deposit =>
                  val moneyDeposited: Future[Status] =
                    (bankAccountActorsCluster ? MessageWithId(accountNumber, Deposit(deposit.amount))).mapTo[Status]

                  onSuccess(moneyDeposited) {
                    case performed: Success =>
                      complete(StatusCodes.NoContent)
                    case _: Failure =>
                      complete(StatusCodes.NotFound)
                  }
                }
              }
            }
        }
    }
}
