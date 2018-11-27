package com.dsbank

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.dsbank.actors.BankAccountActor._

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  implicit val CreateAccountJsonFormat = jsonFormat1(Create)
  implicit val DepositJsonFormat = jsonFormat1(Deposit)
  implicit val WithdrawJsonFormat = jsonFormat1(Withdraw)
  implicit val InterestJsonFormat = jsonFormat1(Interest)
  implicit val TransferJsonFormat = jsonFormat2(TransferAPI)

}
