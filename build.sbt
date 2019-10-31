ThisBuild / organization := "org.reactivemongo"

ThisBuild / crossPaths := false

ThisBuild / autoScalaLibrary := false

lazy val common = Shaded.commonModule

lazy val nativeOsx = Shaded.nativeModule("osx-x86_64", "kqueue")

lazy val linux = Shaded.nativeModule("linux-x86_64", "epoll")

lazy val shaded = project.in(file(".")).
  settings(
    publishArtifact := false,
    publishTo := None,
    publishLocal := {},
    publish := {},
  ).aggregate(common, nativeOsx, linux)
