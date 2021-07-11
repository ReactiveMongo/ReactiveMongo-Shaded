import sbt._
import sbt.Keys._

import scala.xml.{ Elem => XmlElem, Node => XmlNode, NodeSeq, XML }
import scala.xml.transform.{ RewriteRule, RuleTransformer }

import sbtassembly.{
  AssemblyKeys, MergeStrategy, PathList, ShadeRule
}, AssemblyKeys._

object Shaded {
  import XmlUtil.transformPomDependencies

  val nettyVer = "4.1.65.Final"

  lazy val commonModule = Project("ReactiveMongo-Shaded", file("shaded")).
    settings(
      Publish.settings ++ Seq(
        crossPaths := false,
        autoScalaLibrary := false,
        resolvers += Resolver.mavenLocal,
        libraryDependencies += "io.netty" % "netty-handler" % nettyVer,
        assemblyShadeRules in assembly := Seq(
          ShadeRule.rename("io.netty.**" -> "reactivemongo.io.netty.@1").inAll
        ),
        assemblyMergeStrategy in assembly := {
          case "META-INF/io.netty.versions.properties" => MergeStrategy.last
          case x => (assemblyMergeStrategy in assembly).value(x)
        },
        pomPostProcess := transformPomDependencies(_ => None),
        makePom := makePom.dependsOn(assembly).value,
        packageBin in Compile := target.value / (
          assemblyJarName in assembly).value
      )
    )

  def nativeModule(classifier: String, nettyVariant: String): Project =
    Project(s"ReactiveMongo-Shaded-Native-${classifier}",
      file(s"shaded-native-${classifier}")).settings(
      Publish.settings ++ Seq(
        name := "ReactiveMongo-Shaded-Native",
        crossPaths := false,
        autoScalaLibrary := false,
        version := {
          val ver = (version in ThisBuild).value
          val verClassifier = classifier.replaceAll("_", "-")

          if (ver endsWith "-SNAPSHOT") {
            s"${ver dropRight 9}-${verClassifier}-SNAPSHOT"
          } else {
            s"${ver}-${verClassifier}"
          }
        },
        resolvers += Resolver.mavenLocal,
        libraryDependencies ++= Seq(
          (("io.netty" % s"netty-transport-native-${nettyVariant}" % nettyVer).classifier(classifier)).
            exclude("io.netty", "netty-common").
            exclude("io.netty", "netty-transport")
            exclude("io.netty", "netty-buffer")
        ),
        assemblyShadeRules in assembly := Seq(
          ShadeRule.rename("io.netty.**" -> "reactivemongo.io.netty.@1").inAll
        ),
        assemblyMergeStrategy in assembly := {
          case "META-INF/io.netty.versions.properties" => MergeStrategy.last
          case x => (assemblyMergeStrategy in assembly).value(x)
        },
        pomPostProcess := transformPomDependencies(_ => None),
        makePom := makePom.dependsOn(assembly).value,
        packageBin in Compile := Def.task[File] {
          val dir = baseDirectory.value / "target" / (
            s"asm-${System.currentTimeMillis()}")

          IO.unzip(assembly.value, dir)

          // META-INF
          val metaInf = dir / "META-INF"

          IO.listFiles(metaInf, AllPassFilter).foreach { f =>
            val nme = f.getName

            if (nme startsWith "io.netty") {
              f.renameTo(metaInf / s"reactivemongo.${nme}")
            }
          }

          // Rename native libs
          val nativeDir = metaInf / "native"

          IO.listFiles(nativeDir, AllPassFilter).foreach { f =>
            val nme = f.getName

            if (nme startsWith "libnetty") {
              f.renameTo(nativeDir / s"libreactivemongo_${nme drop 3}")
            }
          }

          // New JAR
          IO.zip(Path.contentOf(dir), assembly.value)

          assembly.value
        }.dependsOn(assembly).value
      )
    ).dependsOn(commonModule % Provided)
}

object XmlUtil {
  def transformPomDependencies(tx: XmlElem => Option[XmlNode]): XmlNode => XmlNode = { node: XmlNode =>
    val tr = new RuleTransformer(new RewriteRule {
      override def transform(node: XmlNode): NodeSeq = node match {
        case e: XmlElem if e.label == "dependency" => tx(e) match {
          case Some(n) => n
          case _ => NodeSeq.Empty
        }

        case _ => node
      }
    })

    tr.transform(node).headOption match {
      case Some(transformed) => transformed
      case _ => sys.error("Fails to transform the POM")
    }
  }
}
