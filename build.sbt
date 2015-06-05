name := "Cafebabe"

version := "1.2"

scalaVersion := "2.11.6"

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

scalacOptions += "-Xexperimental"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

fork in Test := true

