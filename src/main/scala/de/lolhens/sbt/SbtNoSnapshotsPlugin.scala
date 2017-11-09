package de.lolhens.sbt

import bintray.{BintrayCredentials, BintrayPlugin}
import sbt.Keys._
import sbt.{AutoPlugin, Def, SettingKey, _}

object SbtNoSnapshotsPlugin extends AutoPlugin {

  object autoImport {
    val noSnapshots: SettingKey[Boolean] = settingKey[Boolean]("Specifies whether snapshots should be released")

    val bintrayCredentialsOption: TaskKey[Option[BintrayCredentials]] = taskKey[Option[BintrayCredentials]]("Get Bintray credentials if supplied")
  }

  override def requires: Plugins = BintrayPlugin
  override def trigger = allRequirements

  import BintrayPlugin.autoImport._
  import autoImport._

  override def buildSettings: Seq[Def.Setting[_]] = Seq(
    noSnapshots := false
  )

  override lazy val projectSettings: Seq[Def.Setting[_]] = Seq(
    bintrayCredentialsOption := bintrayEnsureCredentials.result.value.toEither.right.toOption,

    bintrayReleaseOnPublish := false,

    publish := Def.taskDyn[Unit] {
      val publishTask = publish.taskValue
      if ((noSnapshots.value && isSnapshot.value) || bintrayCredentialsOption.value.isEmpty)
        (Keys.`package` in Compile).map(_ => ())
      else Def.task(publishTask.value)
    }.value,

    bintrayRelease := Def.taskDyn[Unit] {
      val bintrayReleaseTask = bintrayRelease.taskValue
      if (noSnapshots.value && isSnapshot.value) Def.task(())
      else Def.task(bintrayReleaseTask.value)
    }.value
  )
}
