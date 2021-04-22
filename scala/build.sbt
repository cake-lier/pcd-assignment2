name := "assignment2"
version := "0.1"
scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
    "io.vertx" % "vertx-core" % "4.0.3",
    "io.vertx" % "vertx-web-client" % "4.0.3",
    "org.scalafx" %% "scalafx" % "16.0.0-R22"
)

lazy val javaFXModules = {
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
}

libraryDependencies ++= javaFXModules
