import sbt.Project.projectToRef

lazy val clients = Seq(client)
lazy val scalaV = "2.11.8"

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  routesGenerator := InjectedRoutesGenerator,
  pipelineStages := Seq(scalaJSProd, gzip),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,

    "org.slf4j" % "slf4j-nop" % "latest.release",
    "com.vmunier" %% "play-scalajs-scripts" % "latest.release",
    "org.webjars" % "jquery" % "latest.release",
    "com.h2database" % "h2" % "latest.release",
    "com.typesafe.slick" %% "slick" % "latest.release",
    specs2 % Test
  ),
  includeFilter in (Assets, LessKeys.less) := "*.less"
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "latest.release",
    "com.lihaoyi" %% "scalatags" % "latest.release",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.0"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
