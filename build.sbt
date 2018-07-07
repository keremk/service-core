import sbt.Keys._

lazy val buildSettings = Seq(
  organization := "com.xing",
  scalaVersion := "2.12.6"
)

lazy val finagleVersion = "18.6.0"
lazy val twitterServerVersion = "18.6.0"
lazy val finchVersion = "0.21.0"
lazy val circeVersion = "0.9.3"
lazy val scalaTestVersion = "3.0.5"
lazy val logbackVersion = "1.2.3"


lazy val finagleHttp = "com.twitter" %% "finagle-http" % finagleVersion
lazy val twitterServer = "com.twitter" %% "twitter-server" % twitterServerVersion
lazy val finchCore = "com.github.finagle" %% "finch-core" % finchVersion
lazy val finchCirce = "com.github.finagle" %% "finch-circe" % finchVersion

lazy val circeCore = "io.circe" %% "circe-core" % circeVersion

lazy val logbackCore = "ch.qos.logback" % "logback-core" % logbackVersion
lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackVersion

lazy val scalactic = "org.scalactic" %% "scalactic" % scalaTestVersion
lazy val scalatest = "org.scalatest" %% "scalatest" % scalaTestVersion

// https://tpolecat.github.io/2014/04/11/scalac-flags.html
lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Xlint",
  //"-Yno-predef",
  //"-Ywarn-unused-import", // gives false positives
  "-Xfatal-warnings",
  "-Ywarn-value-discard",
  "-Ypartial-unification"
)

lazy val serviceDependencies = Seq(
  twitterServer,
  finagleHttp,
  finchCore,
  finchCirce,
  circeCore,
  scalactic,
  logbackCore,
  logbackClassic
)

lazy val testDependencies = Seq(
  scalatest
)

lazy val baseSettings = Seq(
  libraryDependencies ++= testDependencies.map(_ % "test"),
  resolvers ++= Seq(
    Resolver.jcenterRepo,
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "Twitter" at "http://maven.twttr.com",
    Resolver.url("bintray-sbt-plugin-releases", url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
  ),
  scalacOptions ++= compilerOptions,
  scalacOptions in Test ++= Seq("-Yrangepos"),
  scalacOptions in(Compile, console) += "-Yrepl-class-based",
  testOptions in Test += Tests.Setup(() => {
//    System.setProperty("ENV", "test")
//    System.setProperty("SYSTEM-ID", "servicetest")
//    System.setProperty("SYSTEM-NAME", "servicetest")
  })
)

lazy val settings = buildSettings ++ baseSettings ++ Seq(
  name := "servicecore",
  moduleName := "servicecore",
  libraryDependencies ++= serviceDependencies,
  aggregate in run := false,
)

val serviceTemplate = project.in(file("."))
  .settings(settings)

shellPrompt in ThisBuild := { state =>
  s"${scala.Console.MAGENTA}${Project.extract(state).currentRef.project}> ${scala.Console.RESET}"
}