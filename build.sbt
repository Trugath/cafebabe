name := "Cafebabe"

version := "1.2"

scalaVersion := "2.11.8"

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

scalacOptions += "-Xexperimental"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.8"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0-M15" % "test"

fork in Test := true

