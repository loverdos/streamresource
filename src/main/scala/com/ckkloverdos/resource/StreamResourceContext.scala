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

import java.io.File
import com.ckkloverdos.maybe.{Failed, NoVal, Maybe}
import com.ckkloverdos.sys.SysProp

/**
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */

trait StreamResourceContext {
  def /(child: String): StreamResourceContext
  
  def parent: Option[StreamResourceContext]

  def getResource(path: String): Maybe[StreamResource]
  def getResourceOpt(path: String): Option[StreamResource]
  def getResourceEx(path: String): StreamResource

  def getLocalResource(path: String): Maybe[StreamResource]
  def getLocalResourceOpt(path: String): Option[StreamResource]
  def getLocalResourceEx(path: String): StreamResource
}

object StreamResourceContext {
  def apply(file: File): Maybe[FileStreamResourceContext] = file match {
    case null ⇒
      Failed(new NullPointerException("null file"))

    case file if file.exists() ⇒
      Maybe(new FileStreamResourceContext(file))

    case _ ⇒
      NoVal
  }

  def !!(file: File): FileStreamResourceContext = new FileStreamResourceContext(file)

  def apply(sysp: SysProp): Maybe[FileStreamResourceContext] = sysp match {
    case null ⇒
      NoVal

    case sysp ⇒
      sysp.value.map(v ⇒ Maybe(new FileStreamResourceContext(new File(v)))) getOr NoVal
  }

  def apply(clz: Class[_]): Maybe[ClassLoaderStreamResourceContext] = clz match {
    case null ⇒
      NoVal

    case clz ⇒
      Maybe(new ClassLoaderStreamResourceContext(clz.getClassLoader))
  }

  def !!(clz: Class[_]): ClassLoaderStreamResourceContext =
    new ClassLoaderStreamResourceContext(clz.getClassLoader)

  def !!(classLoader: ClassLoader): ClassLoaderStreamResourceContext =
    new ClassLoaderStreamResourceContext(classLoader)
}
