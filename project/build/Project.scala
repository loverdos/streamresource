/*
 * Copyright 2011 Christos KK Loverdos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Calendar
import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  override def compileOptions = super.compileOptions ++
    Seq("-deprecation",
      "-Xmigration",
      "-Xcheckinit",
      "-optimise",
      "-explaintypes",
      "-unchecked",
      "-encoding", "utf8")
      .map(CompileOption(_))


  def extraResources = "LICENSE.txt"
  override def mainResources = super.mainResources +++ extraResources

  override def packageDocsJar = defaultJarPath("-javadoc.jar")
  override def packageSrcJar = defaultJarPath("-sources.jar")

  val sourceArtifact = Artifact.sources(artifactID)
  val docsArtifact = Artifact.javadoc(artifactID)

  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageDocs, packageSrc)

  override def packageAction = super.packageAction dependsOn test

  override def managedStyle = ManagedStyle.Maven

  override def pomExtra =
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
      <developers>
        <developer>
          <id>loverdos</id>
          <name>Christos KK Loverdos</name>
          <email>loverdos@gmail.com</email>
        </developer>
      </developers>;

  val lib_maybe           = "com.ckkloverdos" %% "maybe"          % "0.3.0"        % "compile"  withSources()
  val lib_sysprop         = "com.ckkloverdos" %% "sysprop"        % "0.1.0"        % "compile"  withSources()
  val lib_converter       = "com.ckkloverdos" %% "converter"      % "0.3.0"        % "compile"  withSources()
  val lib_slf4j           = "org.slf4j"       % "slf4j-api"       % "1.6.1"        % "compile"  withSources()
  val lib_logback_simple  = "ch.qos.logback"  % "logback-classic" % "0.9.28"       % "test"     withSources()
  val lib_junit_interface = "com.novocode"    % "junit-interface" % "0.7"          % "test"

  override def testOptions =
    super.testOptions ++
    Seq(TestArgument(TestFrameworks.JUnit, "-q", "-v"))

  // Set up publish repository (the tuple avoids SBT's ReflectiveRepositories detection)
  private lazy val ScalaToolsReleases_t  = ("Scala Tools Releases"  -> "http://nexus.scala-tools.org/content/repositories/releases/")
  private lazy val ScalaToolsSnapshots_t = ("Scala Tools Snapshots" -> "http://nexus.scala-tools.org/content/repositories/snapshots/")

  override def repositories =
    if (version.toString.endsWith("-SNAPSHOT")) super.repositories + ScalaToolsSnapshots
    else super.repositories

    lazy val publishTo =
    if (version.toString.endsWith("-SNAPSHOT")) {
      println("====> publishing SNAPSHOT: " + version)
      ScalaToolsSnapshots_t._1 at ScalaToolsSnapshots_t._2
    }
    else {
      println("====> publishing RELEASE: " + version)
      ScalaToolsReleases_t._1 at ScalaToolsReleases_t._2
    }

  Credentials(Path.userHome / ".ivy2" / ".credentials", log)

  lazy val publishRemote = propertyOptional[Boolean](false, true)

  private lazy val localDestRepo = Resolver.file("maven-local", Path.userHome / ".m2" / "repository" asFile)
  override def defaultPublishRepository =
    if (!publishRemote.value) Some(localDestRepo)
    else super.defaultPublishRepository

  lazy val projectInceptionYear       = "2010"

  private lazy val docBottom =
    "Copyright (c) Christos KK Loverdos. " +
      projectInceptionYear + "-" + Calendar.getInstance().get(Calendar.YEAR) +
      ". All Rights Reserved."
}
