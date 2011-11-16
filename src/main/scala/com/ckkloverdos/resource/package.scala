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

import java.io.{Closeable, File}
import maybe.{Failed, Maybe}


package object resource {

  val FileSystemRootResourceContext = new FileStreamResourceContext(new File("/"))

  val UserDirResourceContext = new FileStreamResourceContext(com.ckkloverdos.sys.SysProp.UserDir.rawValue)

  val DefaultResourceContext = new ClassLoaderStreamResourceContext(Thread.currentThread().getContextClassLoader)

  def closeAfter[C <: Closeable, A](c: C)(f: C => A): Maybe[A] = {
    try {
      Maybe(f(c))
    } catch {
      case e: Throwable =>
        Failed(e)
    } finally {
      try c.close() catch { case _ => }
    }
  }

  def concatResourcePaths(a: String,  b: String): String = {
    // Slashes are important, so we have to be careful
    if(a.length() == 0) {
      b
    } else if(a endsWith "/") {
      if(b startsWith "/") {
        a + b.substring(1)
      } else {
        a + b
      }
    } else if(b startsWith "/") {
      a + b
    } else {
      a + "/" + b
    }
  }
}
