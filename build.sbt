name := "Cafebabe"

version := "1.2"

scalaVersion := "2.12.4"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.12.1"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

fork in Test := true

