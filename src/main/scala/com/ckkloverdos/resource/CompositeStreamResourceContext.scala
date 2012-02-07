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


/**
 * 
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
final class CompositeStreamResourceContext(
    parent: Maybe[StreamResourceContext],
    others: StreamResourceContext*)
  extends StreamResourceContextSkeleton(parent) {

  def /(child: String) = new CompositeStreamResourceContext(parent, others.map(_./(child)): _*)

  def getLocalResource(path: String): Maybe[StreamResource] =
    others find { rc => rc.getResource(path).isJust } map { _.getResource(path) } getOrElse NoVal

  override def toString = "CompositeStreamResourceContext(%s, %s)".format(parent, others)
}
