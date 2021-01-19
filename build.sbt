name := """userReviews"""
organization := "assignment"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.apache.commons" % "commons-csv" % "1.6" withJavadoc() withSources(),
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "joda-time" % "joda-time" % "2.7",
  "com.typesafe.play" %% "play-json-joda" % "2.9.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",
  //  "org.apache.lucene" % "lucene-analyzers-common" % "8.5.2"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "assignment.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "assignment.binders._"
