ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "scala-3-advanced-rockthejvm",

    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.2.0",
      "com.github.sbt.junit" % "jupiter-interface" % "0.13.3" % Test
    )
  )
