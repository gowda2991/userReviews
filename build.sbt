name := """userReviews"""
organization := "assignment"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-csv" % "1.6" withJavadoc() withSources(),
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "joda-time" % "joda-time" % "2.7",
  "com.typesafe.play" %% "play-json-joda" % "2.9.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0"
)

libraryDependencies += "com.h2database" % "h2" % "1.4.192"

