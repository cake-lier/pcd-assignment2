import sbt._

name := "assignment2"
version := "0.1"
scalaVersion := "2.13.5"

libraryDependencies ++= List {
    "io.vertx" % "vertx-lang-scala_2.12" % "3.9.1"
}
