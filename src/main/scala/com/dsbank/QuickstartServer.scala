package com.dsbank

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success }
import akka.actor._
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings, ShardRegion }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.dsbank.Remote.MessageWithId
import com.dsbank.actors.{ BankAccountActor, ClockManagerActor }
import com.dsbank.routes.AccountRoutes

object QuickstartServer extends App with AccountRoutes {

  private val port = sys.env("PORT")
  println(s"Try to listen at port $port")
  System.setProperty("akka.remote.netty.tcp.port", port)

  implicit val system: ActorSystem = ActorSystem("BankCluster")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case MessageWithId(id, msg) => (id, msg)
  }

  val numberOfShards = 100

  val extractShardId: ShardRegion.ExtractShardId = {
    case MessageWithId(id, msg) => (id.hashCode % numberOfShards).toString
  }

  val bankAccountActorsCluster: ActorRef = ClusterSharding(system).start(
    typeName = "BankAccount",
    entityProps = Props[BankAccountActor],
    settings = ClusterShardingSettings(system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId
  )

  val clockManagerActor: ActorRef = system.actorOf(ClockManagerActor.props, "clockManager")

  lazy val routes: Route = accountRoutes

  private val httpRunEnv = sys.env.get("HTTP")

  if (httpRunEnv.isDefined) {
    val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "145.100.111.54", 8080)
    serverBinding.onComplete {
      case Success(bound) =>
        println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
      case Failure(e) =>
        Console.err.println(s"Server could not start!")
        e.printStackTrace()
        system.terminate()
    }
  }

  Await.result(system.whenTerminated, Duration.Inf)
}
