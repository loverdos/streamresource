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
import com.ckkloverdos.maybe.{NoVal, Maybe}
import com.ckkloverdos.sys.SysProp

/**
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */

trait StreamResourceContext {
  def  /(child: String): StreamResourceContext
  
  def parent: Maybe[StreamResourceContext]

  def getResource(path: String): Maybe[StreamResource]

  def getLocalResource(path: String): Maybe[StreamResource]
  
  def getResourceX(path: String): Maybe[ResolvedStreamResource]

  def getLocalResourceX(path: String): Maybe[ResolvedStreamResource]
}

object StreamResourceContext {
  def apply(file: File): Maybe[FileStreamResourceContext] = file match {
    case null => NoVal
    case file => file.exists match {
      case true => Maybe(new FileStreamResourceContext(file))
      case false => NoVal
    }
  }

  def apply(sysp: SysProp): Maybe[FileStreamResourceContext] = sysp match {
    case null => NoVal
    case sysp => sysp.value.map(v => Maybe(new FileStreamResourceContext(new File(v)))) getOr NoVal
  }

  def apply(clz: Class[_]): Maybe[ClassLoaderStreamResourceContext] = clz match {
    case null => NoVal
    case clz => Maybe(new ClassLoaderStreamResourceContext(clz.getClassLoader))
  }
}
