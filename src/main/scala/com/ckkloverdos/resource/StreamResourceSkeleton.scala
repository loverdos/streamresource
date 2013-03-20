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

import java.io.{BufferedReader, InputStreamReader, Reader, InputStream}
import com.ckkloverdos.maybe.{Maybe, Just, Failed}

/**
 * 
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
abstract class StreamResourceSkeleton(
    val resolver: StreamResourceContext,
    _metadata: Map[String, String] = Map()
) extends StreamResource {
  protected def _inputStream: InputStream

  def metadata = _metadata

  def mapStream[A](f: InputStream ⇒ A): Maybe[A] = {
    try {
      val in = _inputStream
      try {
        Just(f(in))
      } catch {
        case e: Exception ⇒ Failed(e)
      } finally {
        in.close()
      }
    } catch {
      case e: Exception ⇒ Failed(e)
    }
  }

  def flatMapStream[A](f: InputStream ⇒ Maybe[A]): Maybe[A] = {
    try {
      val in = _inputStream
      try {
        f(in)
      } catch {
        case e: Exception ⇒ Failed(e)
      } finally {
        in.close()
      }
    } catch {
      case e: Exception ⇒ Failed(e)
    }
  }

  def mapReader[A](f: (Reader) => A): Maybe[A] = {
    mapStream { in =>
      f(new InputStreamReader(in, "UTF-8"))
    }
  }

  def mapBufferedReader[A](f: (BufferedReader) => A): Maybe[A] = {
    mapStream { in =>
      f(new BufferedReader(new InputStreamReader(in, "UTF-8")))
    }
  }

  def mapBytes[A](f: (Array[Byte]) => A) = {
    flatMapStream { in =>
      StreamResource.readBytes(in).map(f)
    }
  }

  def mapString[A](f: (String) => A) = {
    flatMapStream { in =>
      StreamResource.readStringFromStream(in).map(f)
    }
  }

  def stringContent = this.mapString(scala.Predef.identity)

  def byteContent = this.mapBytes(scala.Predef.identity)
}
