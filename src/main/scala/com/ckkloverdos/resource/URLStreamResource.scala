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
import java.io.File

final class URLStreamResource(val path: String, val url: URL) extends StreamResourceSkeleton {
  def exists = this.mapInputStream(_ => true) getOr false

  def canonicalPath = url.toExternalForm
  def name = new File(path).getName

  protected def _inputStream = url.openStream

  override def toString = "URLStreamResource(%s, %s)".format(path, url)
}
