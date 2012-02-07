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

import java.io.{FileInputStream, File => JFile}

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
class FileStreamResource(file: JFile) extends StreamResourceSkeleton {
  def exists = file.exists

  def url = file.toURI.toURL

  def canonicalPath = file.getCanonicalPath

  def name = file.getName

  def path = file.getPath

  protected def _inputStream = new FileInputStream(file)

  override def toString = "FileStreamResource(%s)".format(file.getCanonicalPath)
}
