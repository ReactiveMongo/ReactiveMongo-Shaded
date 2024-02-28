ThisBuild / organization := "org.reactivemongo"

lazy val common = Shaded.commonModule

lazy val nativeOsxX86 = Shaded.nativeModule("osx-x86_64", "kqueue")

lazy val nativeOsxAarch = Shaded.nativeModule("osx-aarch_64", "kqueue")

lazy val linuxX86 = Shaded.nativeModule("linux-x86_64", "epoll")

lazy val linuxAarch = Shaded.nativeModule("linux-aarch_64", "epoll")

lazy val alias = project.in(file("alias")).
  settings(Publish.settings ++ Seq(
    name := "ReactiveMongo-Alias",
    description := "Library mappings (e.g. netty)",
    scalaVersion := "2.12.19",
    crossScalaVersions := {
      val scalaCompatVer = "2.11.12"

      Seq(
        scalaCompatVer,
        scalaVersion.value,
        "2.13.10",
        "3.2.2"
      )
    },
    libraryDependencies ++= Seq(
      "io.netty" % "netty-handler" % Shaded.nettyVer % Provided))
  )

lazy val shaded = project.in(file(".")).
  settings(
    publishArtifact := false,
    publishTo := None,
    publishLocal := {},
    publish := {},
  ).aggregate(common, nativeOsxX86, nativeOsxAarch, linuxX86, linuxAarch, alias)
