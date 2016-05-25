import sbt.Project.projectToRef

lazy val clients = Seq(client)
lazy val scalaV = "2.11.8"

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    cache,
    ws,
    "com.typesafe.play" %% "play-slick" % "2.0.0",
    "com.typesafe.slick" %% "slick" % "3.1.0",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.vmunier" %% "play-scalajs-scripts" % "0.5.0",
    "org.webjars" % "jquery" % "1.11.1",
     "mysql" % "mysql-connector-java" % "5.1.34",
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
    "org.scala-js" %%% "scalajs-dom" % "0.8.0",
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
