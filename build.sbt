name := """HolyJobs"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.slf4j" % "slf4j-nop" % "latest.release",
  "mysql" % "mysql-connector-java" % "latest.release",
  "org.webjars" % "jquery" % "latest.release",
  "com.typesafe.slick" %% "slick" % "latest.release",
  "com.typesafe.play" %% "play-mailer" % "latest.release"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

includeFilter in (Assets, LessKeys.less) := "style.less"
