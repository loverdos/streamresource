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

import java.net.URL
import java.io.{BufferedReader, Reader, InputStream, File => JFile}
import com.ckkloverdos.maybe._

/**
 * A `StreamResource` is something for which we can potentially obtain an `InputStream`.
 *
 * Each stream resource is identifiable by a URL.
 *
 * The character- and string-oriented methods assume UTF-8 as the encoding.
 * 
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */

trait StreamResource {
  /**
   * Returns the [[com.ckkloverdos.resource.StreamResourceContext]] that resolved this resource.
   */
  def resolver: StreamResourceContext

  def exists: Boolean
  def url: URL

  def name: String
  def path: String
  def canonicalPath: String
  
  def metadata: Map[String, String]

  def mapStream[A](f: InputStream => A): Maybe[A]
  def flatMapStream[A](f: InputStream => Maybe[A]): Maybe[A]

  def mapReader[A](f: Reader => A): Maybe[A]
  def mapBufferedReader[A](f: BufferedReader => A): Maybe[A]
  def mapBytes[A](f: Array[Byte] => A): Maybe[A]
  def mapString[A](f: String => A): Maybe[A]

  def stringContent: Maybe[String]
  def byteContent: Maybe[Array[Byte]]
}

object StreamResource {
  // TODO move elsewhere
  def readBytes(is: InputStream, close: Boolean = false, bufferSize: Int = 4096): Maybe[Array[Byte]] = Maybe {
    var result = new Array[Byte](0)
    var buffer = new Array[Byte](bufferSize)
    var count = is.read(buffer)
    while(count > -1) {
      var newresult = new Array[Byte](result.length + count)
      System.arraycopy(result, 0, newresult, 0, result.length)
      System.arraycopy(buffer, 0, newresult, result.length, count)
      result = newresult

      count = is.read(buffer)
    }
    if(close) {
      is.close()
    }
    result
  }

  // TODO move elsewhere
  def readStringFromStream(
      is: InputStream,
      encoding: String = "UTF-8",
      close: Boolean = false,
      bufferSize: Int = 4096
  ): Maybe[String] =
    readBytes(is, close, bufferSize).map(x ⇒ new String(x, encoding))

  
  def apply(file: JFile): Maybe[StreamResource] =
    FileSystemRootResourceContext.getResource(file.getAbsolutePath)
}
