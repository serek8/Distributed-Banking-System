package com.dsbank

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.dsbank.actors.BankAccountActor._

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val CreateAccountJsonFormat = jsonFormat1(CreateAPI)
  implicit val DepositJsonFormat = jsonFormat1(DepositAPI)
  implicit val WithdrawJsonFormat = jsonFormat1(WithdrawAPI)
  implicit val TransferJsonFormat = jsonFormat2(TransferAPI)
  implicit val InterestJsonFormat = jsonFormat1(InterestAPI)

}
