/*
 * Copyright 2012 Christos KK Loverdos
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

import org.junit.Assert._
import org.junit.Test
import com.ckkloverdos.maybe.Just

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */

class PropsTest {
  val strKey1 = "str.key.1"
  val strVal1 = "str.val.1"
  val strKey2 = "str.key.2"
  val strVal2 = "str.val.2"

  lazy val map   = Map(strKey1 -> strVal1, strKey2 -> strVal2)
  lazy val props = new Props(map)

  @Test
  def testKey2: Unit = {
    val value2M = props.get(strKey2)
    assertEquals(Just(strVal2), value2M)
  }

  @Test
  def testGroup: Unit = {
    val strProps = props.group("str")
    
    assertEquals(2, strProps.size)

    assertEquals(Set("key.1", "key.2"), strProps.keySet)
    assertEquals(strProps.getEx("key.1"), props.getEx(strKey1))
    assertEquals(strProps.getEx("key.2"), props.getEx(strKey2))
  }
}