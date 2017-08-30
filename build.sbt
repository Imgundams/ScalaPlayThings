name := "ScalaPlayWork"

version := "1.0"



lazy val `scalaplaywork` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(jdbc, cache, ws, specs2 % Test)

libraryDependencies += "org.webjars" % "bootstrap" % "3.0.0" exclude("org.webjars", "jquery")
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.12.5-play25"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.mongodb" %% "casbah" % "3.1.1"



unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

routesGenerator := InjectedRoutesGenerator
      