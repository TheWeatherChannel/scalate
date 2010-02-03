import sbt._

/**
 * @version $Revision : 1.1 $
 */
class ScalateProject(info: ProjectInfo) extends ParentProject(info) {

  // use local maven repo
  val mavenLocal = "Local Maven Repository" at "file://" + Path.userHome + "/.m2/repository"

  // Projects
  lazy val core = project("scalate-core", "Scalate Core", new Core(_))
  lazy val sample = project("scalate-sample", "Scalate Sample Web App", new Sample(_), core)


  class Core(info: ProjectInfo) extends DefaultProject(info) {
  }

  class Sample(info: ProjectInfo) extends DefaultWebProject(info) {
  }

}