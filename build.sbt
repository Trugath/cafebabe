name := "Cafebabe"

version := "1.2"

scalaVersion := "2.11.5"

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

scalacOptions += "-Xexperimental"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

fork in Test := true

