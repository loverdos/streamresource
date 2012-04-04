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

package com.ckkloverdos.props

import com.ckkloverdos.convert.Converters
import com.ckkloverdos.maybe.{MaybeOption, NoVal, Maybe}


/**
 * Base trait for `Props`, `AnyProps`, parameterized by the type of values.
 * 
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
trait PropsBase[K, V, P <: PropsBase[K, V,  P]] {
  type MapType = Map[K,  V]

  val map: MapType
  val converters: Converters

  def newProps(map: MapType): P

  def contains(key: K) = map contains key
  
  def filterKeys(f: K => Boolean) = newProps(map filterKeys f)
  
  def filterValues(f: V => Boolean) = newProps(map filter {case (k, v) => f(v)})

  /**
   * Get a value or throw an exception if it doesnot exist.
   */
  @throws(classOf[NoSuchElementException])
  def getEx(key: K): V = map apply key

  def get(key: K): Maybe[V] = map.get(key): Maybe[V]

  def getOr(key: K, default: V): V = map.getOrElse(key, default)

  def size: Int = map.size

  def keySet: Set[K] = map.keySet

  def keysIterator: Iterator[K] = map.keysIterator
}