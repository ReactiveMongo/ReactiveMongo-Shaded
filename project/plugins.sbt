/*
resolvers ++= Seq(
  Resolver.url("sbt-repo", url(
    "https://dl.bintray.com/sbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("typesafe-repo", url(
   "https://dl.bintray.com/typesafe/sbt-plugins/"))(Resolver.ivyStylePatterns),
  Resolver.url("eed3si9n-repo", url(
    "https://dl.bintray.com/eed3si9n/sbt-plugins/"))(Resolver.ivyStylePatterns),
  Resolver.url("typesafe-repo", url(
   "https://dl.bintray.com/typesafe/sbt-plugins/"))(Resolver.ivyStylePatterns),
  Resolver.url("jsuereth-repo", url(
    "https://dl.bintray.com/jsuereth/sbt-plugins/"))(Resolver.ivyStylePatterns))
*/

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
