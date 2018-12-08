lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.18"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.12.4"
    )),
    name := "dsbank",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                  % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"       % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"              % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"                % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding"      % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence"           % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.91",

      "com.typesafe.akka" %% "akka-http-testkit"          % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"               % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"        % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                  % "3.0.1"         % Test,
      "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % "0.91" % Test

//      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion

    )
  )
