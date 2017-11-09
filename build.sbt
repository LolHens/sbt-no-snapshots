sbtPlugin := true

name := (name in ThisBuild).value

inThisBuild(Seq(
  name := "sbt-no-snapshots",
  organization := "de.lolhens.sbt",
  version := "0.1.0",

  bintrayReleaseOnPublish := false
))

addCrossSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.1")

def addCrossSbtPlugin(dependency: ModuleID): Setting[Seq[ModuleID]] =
  libraryDependencies += {
    val sbtV = (sbtBinaryVersion in pluginCrossBuild).value
    val scalaV = (scalaBinaryVersion in update).value
    Defaults.sbtPluginExtra(dependency, sbtV, scalaV)
  }

crossSbtVersions := Seq("0.13.16", "1.0.0")
