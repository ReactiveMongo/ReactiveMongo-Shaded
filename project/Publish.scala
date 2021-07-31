import sbt._
import sbt.Keys._

object Publish {
  @inline def env(n: String): String = sys.env.get(n).getOrElse(n)

  private val repoName = env("PUBLISH_REPO_NAME")
  private val repoUrl = env("PUBLISH_REPO_URL")

  val siteUrl = "http://reactivemongo.org"

  lazy val settings = Seq(
    publishMavenStyle := true,
    Test / publishArtifact := false,
    publishTo := Some(repoUrl).map(repoName at _),
    credentials += Credentials(repoName, env("PUBLISH_REPO_ID"),
      env("PUBLISH_USER"), env("PUBLISH_PASS")),
    pomIncludeRepository := { _ => false },
    autoAPIMappings := true,
    apiURL := Some(url(s"$siteUrl/release/1.x/api/")),
    licenses := {
      Seq("Apache 2.0" ->
        url("http://www.apache.org/licenses/LICENSE-2.0"))
    },
    homepage := Some(url(siteUrl)),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/ReactiveMongo/ReactiveMongo"),
        "scm:git://github.com/ReactiveMongo/ReactiveMongo.git")),
    developers := List(
      Developer(
        id = "sgodbillon",
        name = "Stephane Godbillon",
        email = "",
        url = url("http://stephane.godbillon.com")),
      Developer(
        id = "cchantep",
        name = "Cédric Chantepie",
        email = "",
        url = url("http://github.com/cchantep/"))))
}
