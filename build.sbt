ThisBuild / organization := "org.reactivemongo"

lazy val common = Shaded.commonModule

lazy val nativeOsx = Shaded.nativeModule("osx-x86_64", "kqueue")

lazy val linux = Shaded.nativeModule("linux-x86_64", "epoll")

lazy val alias = project.in(file("alias")).
  settings(Publish.settings ++ Seq(
    name := "ReactiveMongo-Alias",
    description := "Library mappings (e.g. netty)",
    scalaVersion := "2.12.15",
    crossScalaVersions := {
      val scalaCompatVer = "2.11.12"

      Seq(
        scalaCompatVer,
        scalaVersion.value,
        "2.13.7",
        "3.1.2-RC1"
      )
    },
    libraryDependencies ++= Seq(
      "io.netty" % "netty-handler" % "4.1.74.Final" % Provided))
  )

lazy val shaded = project.in(file(".")).
  settings(
    publishArtifact := false,
    publishTo := None,
    publishLocal := {},
    publish := {},
  ).aggregate(common, nativeOsx, linux, alias)
