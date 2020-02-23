import Dependencies._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.lambdarat"

lazy val root = (project in file("."))
  .settings(
    name := "storyteller",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      scalameta,
      zio
    ) ++ atto
  )
