package org.fusesource.scalate.sbt

import _root_.sbt._
import java.io.File
import java.{util => ju}
import scala.collection.jcl
import scala.collection.jcl.Conversions._

trait SiteGenWebProject extends ScalateWebProject with MavenStyleWebScalaPaths {
  
  def sitegenOutputPath: Path = outputPath / "sitegen"
  def sitegenTemplateProperties: Map[String, String] = Map.empty

  lazy val generateSite = generateSiteAction

  def generateSiteAction = generateSiteTask() describedAs("Generates a static site.")

  def generateSiteTask() = task {
    withScalateClassLoader { classLoader =>
      
      // Structural Typing FTW (avoids us doing manual reflection)
      type SiteGenerator = {
        var sources: Array[File]
        var workingDirectory: File
        var targetDirectory: File
        var templateProperties: ju.Map[String,String]
        var bootClassName:String
        var info: {def apply(v1:String):Unit}
        def execute():Unit
      }

      val className = "org.fusesource.scalate.support.SiteGenerator"
      val generator = classLoader.loadClass(className).newInstance.asInstanceOf[SiteGenerator]

      generator.info = (value:String)=>log.info(value)
      generator.sources = scalateSources.get.toArray map { p: Path => p.asFile }
      generator.workingDirectory = temporaryWarPath.asFile
      generator.targetDirectory = sitegenOutputPath.asFile
      generator.templateProperties = {
        val jclMap = new jcl.HashMap[String, String]
        jclMap ++= sitegenTemplateProperties
        jclMap
      }
      generator.bootClassName = scalateBootClassName.getOrElse(null)
      generator.execute()
      None
    }
  } named ("generate-site")

  //
  // We don't use a standard directory layout
  //
  override def mainScalaSourcePath = "ext"
  override def mainResourcesPath = "resources"
  override def webappPath = "src"

  override def packageAction = super.packageAction dependsOn generateSiteAction
}