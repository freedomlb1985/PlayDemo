name := "PlayDemo"

version := "1.0"

lazy val `playdemo` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

//libraryDependencies ++= Seq(
//  "com.typesafe.slick" %% "slick" % "3.2.0",
//  "org.slf4j" % "slf4j-nop" % "1.6.4",
//  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0"
//)

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test )

libraryDependencies += "com.iheart" %% "ficus" % "1.4.0"
libraryDependencies += "com.mohiva" %% "play-silhouette" % "4.0.0"
libraryDependencies += "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0"
libraryDependencies += "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0"
libraryDependencies += "com.mohiva" %% "play-silhouette-persistence" % "4.0.0"

libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.0"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.34"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.1.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "2.1.0"

libraryDependencies += "org.scala-debugger" %% "scala-debugger-api" % "1.1.0-M3"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.0"

libraryDependencies += specs2 % Test
libraryDependencies += filters

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  