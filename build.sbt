lazy val supportedScalaVersions = List("2.13.2", "2.12.11")

scalaVersion := supportedScalaVersions.head
version          := "0.1.0-SNAPSHOT"
organization     := "com.thecookiezen"
organizationName := "thecookiezen.com"
scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings")

lazy val scalatags = "com.lihaoyi" %% "scalatags" % "0.8.2"
lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1"

lazy val root = (project in file("."))
  .aggregate(core, cli)
  .settings(
    name := "flames-stack",
    crossScalaVersions := Nil
  )

lazy val core = (project in file("core"))
  .settings(
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      scalatags % Compile,
      scalaTest % Test
    )
  )

lazy val cli = (project in file("cli"))
  .settings(
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "atto-core" % "0.7.0" % Compile
    )
  )
  .dependsOn(core)

