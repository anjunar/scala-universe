ThisBuild / version := "1.0.0"
ThisBuild / organization := "com.anjunar"
ThisBuild / organizationName := "Anjunar"
ThisBuild / organizationHomepage := Some(url("https://github.com/anjunar"))
ThisBuild / scalaVersion := "3.8.3"
ThisBuild / homepage := Some(url("https://github.com/anjunar/scala-universe"))
ThisBuild / description := "A JVM-based type introspection library for classes, generics, annotations, and bean-style property models."
ThisBuild / licenses := List("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/anjunar/scala-universe"),
    "scm:git:https://github.com/anjunar/scala-universe.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "anjunar",
    name = "Patrick Bittner",
    email = "anjunar@gmx.de",
    url = url("https://github.com/anjunar")
  )
)
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}

lazy val root = (project in file("."))
  .settings(
    name := "scala-universe",
    moduleName := "scala-universe",
    libraryDependencies ++= Seq(
      "com.anjunar" % "scala-reflect_3" % "1.0.0",
      "com.google.guava" % "guava" % "33.5.0-jre",
      "jakarta.enterprise" % "jakarta.enterprise.cdi-api" % "4.1.0",
      "ch.qos.logback" % "logback-classic" % "1.5.32",
      "org.slf4j" % "slf4j-api" % "2.0.17",
      "org.slf4j" % "jul-to-slf4j" % "2.0.17",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.6",
      "org.scalameta" %% "munit" % "1.1.1" % Test
    )
  )
