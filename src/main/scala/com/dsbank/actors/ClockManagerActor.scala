package com.dsbank.actors

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import akka.actor.Props

import akka.persistence.PersistentActor

case class ClockIncremented(bankAccount: String)

object ClockManagerActor {
  def props: Props = Props[ClockManagerActor]

  var clockMap = new ConcurrentHashMap[String, AtomicInteger]()

  def updateState(event: ClockIncremented): Int =
    clockMap.computeIfAbsent(
      event.bankAccount, _ => new AtomicInteger(0)
    ).getAndIncrement()

  case class GetNextClockValue(bankAccount: String)
}

class ClockManagerActor extends PersistentActor {
  import ClockManagerActor._

  override def persistenceId = "clock-manager"

  val receiveRecover: Receive = {
    case evt: ClockIncremented => updateState(evt)
  }

  val receiveCommand: Receive = {
    case GetNextClockValue(bankAccount: String) =>
      persist(ClockIncremented(bankAccount))(e => {
        sender() ! updateState(e)
      })
  }
}
