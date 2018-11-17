package com.dsbank

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.dsbank.actors.BankAccountActor.{ Create, Deposit }

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val CreateAccountJsonFormat = jsonFormat1(Create)
  implicit val DepositJsonFormat = jsonFormat1(Deposit)
}
