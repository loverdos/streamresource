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

import com.ckkloverdos.maybe.{NoVal, Maybe}
import java.io.File

/**
 * 
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
final class FileStreamResourceContext(
    root: File,
    parent: Maybe[StreamResourceContext] = NoVal)
  extends StreamResourceContextSkeleton(parent) {
  
  def this(root: File, parent: StreamResourceContext) = this(root, Maybe(parent))
  def this(root: String, parent: StreamResourceContext) = this(new File(root), parent)
  def this(root: String) = this(new File(root), NoVal)

  def /(child: String) = new FileStreamResourceContext(new File(this.root, child), parent.map(_./(child)))

  def getLocalResource(path: String, normalized: Boolean = true) = {
    new File(root, path) match {
      case file if file.exists => Maybe(new FileStreamResource(file))
      case _ => NoVal
    }
  }

  override def toString = "FileStreamResourceContext(%s, %s)".format(root, parent)
}