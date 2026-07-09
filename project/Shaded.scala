import sbt._
import sbt.Keys._

import scala.xml.{ Elem => XmlElem, Node => XmlNode, NodeSeq, XML }
import scala.xml.transform.{ RewriteRule, RuleTransformer }

import sbtassembly.{
  AssemblyKeys, MergeStrategy, PathList
}, AssemblyKeys._

import com.eed3si9n.jarjarabrams.ShadeRule

import xsbti.FileConverter

object Shaded {
  import XmlUtil.transformPomDependencies

  val nettyVer = "4.2.16.Final"

  type FileRef = HashedVirtualFileRef

  def toFileRef(x: File)(using conv: FileConverter): FileRef =
    conv.toVirtualFile(x.toPath())

  def toFile(ref: FileRef)(using conv: FileConverter): File =
    conv.toPath(ref).toFile()

  lazy val commonModule = Project("ReactiveMongo-Shaded", file("shaded")).
    settings(
      Publish.settings ++ Seq(
        crossPaths := false,
        autoScalaLibrary := false,
        resolvers += Resolver.mavenLocal,
        libraryDependencies ++= Seq(
          "io.netty" % "netty-handler" % nettyVer,
          "io.netty" % "netty-codec-compression" % nettyVer
        ),
        exportJars := false,
        assembly / assemblyShadeRules := Seq(
          ShadeRule.rename("io.netty.**" -> "reactivemongo.io.netty.@1").inAll
        ),
        assembly / assemblyMergeStrategy := {
          case "META-INF/io.netty.versions.properties" => MergeStrategy.last
          case "META-INF/versions/11/module-info.class" => MergeStrategy.last
          case x =>
            (assembly / assemblyMergeStrategy).value(x)
        },
        pomPostProcess := transformPomDependencies(_ => None),
        makePom := Def.uncached(makePom.dependsOn(assembly).value),
        Compile / packageBin := Def.uncached {
          assembly.value
        }
      )
    )

  def nativeModule(classifier: String, nettyVariant: String): Project =
    Project(s"ReactiveMongo-Shaded-Native-${classifier}",
      file(s"shaded-native-${classifier}")).settings(
      Publish.settings ++ Seq(
        name := {
          val c = classifier.replaceAll("_", "-")

          s"reactivemongo-shaded-native-${c}"
        },
        crossPaths := false,
        exportJars := false,
        autoScalaLibrary := false,
        resolvers += Resolver.mavenLocal,
        libraryDependencies ++= Seq(
          (("io.netty" % s"netty-transport-native-${nettyVariant}" % nettyVer).classifier(classifier)).
            exclude("io.netty", "netty-common").
            exclude("io.netty", "netty-transport").
            exclude("io.netty", "netty-buffer")
        ),
        assembly / assemblyShadeRules := Seq(
          ShadeRule.rename("io.netty.**" -> "reactivemongo.io.netty.@1").inAll
        ),
        assembly / assemblyMergeStrategy := {
          case "META-INF/io.netty.versions.properties" |
              "META-INF/versions/11/module-info.class" =>
            MergeStrategy.last

          case x => (assembly / assemblyMergeStrategy).value(x)
        },
        pomPostProcess := transformPomDependencies(_ => None),
        makePom := Def.uncached(makePom.dependsOn(assembly).value),
        Compile / packageBin := Def.uncached {
          assembly.value
        },
        Test / test := Def.task[sbt.protocol.testing.TestResult] {
          sbt.protocol.testing.TestResult.Passed
        }.value
      )
    ).dependsOn(sbt.projectToLocalProject(commonModule) % "provided")
}

object XmlUtil {
  def transformPomDependencies(tx: XmlElem => Option[XmlNode]): XmlNode => XmlNode = { (node: XmlNode) =>
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
