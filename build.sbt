name := "image-comparison"
organization := "com.crimzie"
version := "0.2.1"

scalaVersion := "2.11.11"
scalacOptions in ThisBuild ++= Seq("-feature", "-language:postfixOps", "-language:implicitConversions")

lazy val root = project in file(".")

exportJars := true
mainClass := Some("com.crimzie.imagecomparator.CLI")