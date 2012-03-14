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

package com.ckkloverdos.resource

import org.slf4j.LoggerFactory
import com.ckkloverdos.maybe.{Failed, NoVal, Just, Maybe}

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
abstract class StreamResourceContextSkeleton(_parent: Maybe[StreamResourceContext]) extends StreamResourceContext {
  protected val logger = LoggerFactory.getLogger(getClass)

  def parent = _parent

  def getResource(path: String) = {
    getResourceX(path).map(_.resource)
  }

  def getLocalResource(path: String) = {
    getLocalResourceX(path).map(_.resource)
  }

  def getResourceX(path: String) = {
    getLocalResourceX(path) match {
      case j@Just(rrc) ⇒
        logger.debug("  Found %s".format(rrc))
        j
      case NoVal ⇒
        logger.debug("  ==> Not found")
        if(parent.isJust) {
          logger.debug("  Trying parent %s".format(parent))
          parent.flatMap(_.getResourceX(path))
        } else {
          NoVal
        }
      case f@Failed(_) ⇒
        logger.warn("Error %s".format(f))
        f
    }
  }
}
