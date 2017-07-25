name := "ScalaPlayWork"
 
version := "1.0" 
      
lazy val `scalaplaywork` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.11.11"

libraryDependencies ++= Seq( jdbc , cache , ws , specs2 % Test ,
  "org.webjars"               % "bootstrap"           % "3.0.0" exclude("org.webjars", "jquery")

)


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

      