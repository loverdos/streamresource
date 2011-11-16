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

package com.ckkloverdos
package runmode

import resource.StreamResourceContext
import sys.SysProp
import maybe._
import org.slf4j.LoggerFactory

sealed trait RunMode {
  def name: String

  def isDev: Boolean
  def isTest: Boolean
  def isStage: Boolean
  def isProd: Boolean
  def isAnyOther: Boolean
  def isOther(name: String): Boolean

  def providerForContext(resourceContext: StreamResourceContext): RunModeContext
  def providerForBaseContext(resourceContext: StreamResourceContext): RunModeContext
  def providerForDefaultBaseResourceContext: RunModeContext
}

object RunMode {
  private val logger = LoggerFactory.getLogger(getClass)
  
  val RunModeSystemPropertyName = "run.mode"
  val RunModeSysProp = SysProp(RunModeSystemPropertyName)

  RunModeSysProp.update("")
  
  lazy val JustDevMode  = Just(DevMode)
  lazy val JustTestMode = Just(TestMode)
  lazy val JustStageMode = Just(StageMode)
  lazy val JustProdMode = Just(ProdMode)

  def fromString(value: String): MaybeOption[RunMode] = {
    value match {
      case null => NoVal
      case value => value.toLowerCase match {
        case DevMode.name   => JustDevMode
        case TestMode.name  => JustTestMode
        case StageMode.name => JustStageMode
        case ProdMode.name  => JustProdMode
        case other if value.trim().length() > 0 => Just(OtherMode(other))
        case _ => NoVal
      }
    }
  }
  
  def fromSysProp: MaybeOption[RunMode] = {
    val maybeRunMode = RunModeSysProp.value match {
      case Just(runMode) =>
        fromString(runMode)
      case _ =>
        NoVal
    }

    maybeRunMode match {
      case j@Just(runMode) =>
        logger.debug("Discovered property run.mode=%s".format(runMode))
        j
      case NoVal =>
        NoVal
    }
  }

  def fromSysPropOrTestRun: (Boolean, RunMode) = {
    var _isTestFramework = false
    RunMode.fromSysProp match {
      case Just(runMode) =>
        (false, runMode)
      case NoVal =>
        val e = new Exception
        val trace = e.getStackTrace
        for(tpart <- trace if(!_isTestFramework)) {
          val cname = tpart.getClassName
          _isTestFramework = (cname contains "sbt.TestRunner") || (cname contains "org.junit.runners.Suite")
        }

        if(_isTestFramework) (true, TestMode) else (false, DevMode)
    }
  }
}

sealed abstract class RunModeSkeleton extends RunMode {
  def isDev = isOther(DevMode.name)

  def isTest = isOther(TestMode.name)

  def isStage = isOther(StageMode.name)

  def isProd = isOther(ProdMode.name)

  def isOther(name: String) = this.name.toLowerCase == name.toLowerCase

  def isAnyOther = false

  def providerForContext(resourceContext: StreamResourceContext) = FixedRunModeContext(this, resourceContext)

  def providerForBaseContext(resourceContext: StreamResourceContext) = FixedRunModeContext(this, resourceContext / this.name.toLowerCase)

  def providerForDefaultBaseResourceContext = providerForBaseContext(resource.DefaultResourceContext)

  override def hashCode() = this.name.hashCode

  override def equals(any: Any) = any match {
    case rm: RunMode => rm.getClass == this.getClass && rm.name == this.name
    case _ => false
  }
}

case object DevMode extends RunModeSkeleton {
  override def isDev = true
  val name = "dev"
}

case object TestMode extends RunModeSkeleton {
  override def isTest = true
  val name = "test"
}

case object StageMode extends RunModeSkeleton {
  override def isStage = true
  val name = "stage"
}

case object ProdMode extends RunModeSkeleton {
  override def isProd = true
  val name = "prod"
}

case class OtherMode(val name: String) extends RunModeSkeleton {
  override def isAnyOther = name.toLowerCase match {
    case "dev"|"test"|"stage"|"prod" => false
    case _ => true
  }
}
