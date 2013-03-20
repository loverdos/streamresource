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

import com.ckkloverdos.maybe.{NoVal, Maybe}
import java.net.URLClassLoader

/**
 * 
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
final class ClassLoaderStreamResourceContext private(
    cl: ClassLoader,
    parent: Option[StreamResourceContext],
    extraPath: String)
  extends StreamResourceContextSkeleton(parent) {

  def this(cl: ClassLoader, parent: Option[StreamResourceContext]) = this(cl, parent, "")
  def this(cl: ClassLoader, parent: StreamResourceContext) = this(cl, Some(parent))
  def this(cl: ClassLoader) = this(cl, None)
  def this(clz: Class[_], parent: Option[StreamResourceContext]) = this(clz.getClassLoader, parent)
  def this(clz: Class[_], parent: StreamResourceContext) = this(clz.getClassLoader, parent)

  def /(child: String) = new ClassLoaderStreamResourceContext(cl, parent, concatResourcePaths(this.extraPath, child))

  def getLocalResource(path: String) = {
    val actualPath = concatResourcePaths(extraPath, path)
    logger.debug("Searching for local resource %s (actual: %s) in %s".format(path, actualPath, this))

    cl.getResource(actualPath) match {
      case null ⇒
        NoVal

      case localURL ⇒
        Maybe(new URLStreamResource(actualPath, localURL, this))
    }
  }

  def getLocalResourceEx(path: String) = {
    val actualPath = concatResourcePaths(extraPath, path)
    logger.debug("Searching for local resource %s (actual: %s) in %s".format(path, actualPath, this))

    cl.getResource(actualPath) match {
      case null ⇒
        throw new Exception("Resource %s [actual: %s] not found in %s".format(path, actualPath, this))

      case localURL ⇒
        new URLStreamResource(actualPath, localURL, this)
    }
  }

  override def toString = {
    val clStr = cl match {
      case urlcs: URLClassLoader ⇒
        val urls = urlcs.getURLs
        "%s@%s(URLs = [%s])".format(urlcs.getClass.getName, System.identityHashCode(urlcs), urls.toList.mkString(", "))

      case cl ⇒
        cl.toString
    }
    "ClassLoaderStreamResourceContext(%s, %s, %s)".format(clStr, parent, extraPath)
  }
}