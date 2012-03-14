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

import org.junit.Assert
import org.junit.Test
import com.ckkloverdos.maybe.Just

/**
 * 
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */

class AnyPropsTest {
  val strKey1 = "str.key.1"
  val strVal1 = "str.val.1"
  val strKey2 = "str.key.2"
  val strVal2 = 12

  lazy val map   = Map(strKey1 -> strVal1, strKey2 -> strVal2)
  lazy val props = new AnyProps(map)

  @Test
  def testInt1: Unit = {
    val value2M = props.get(strKey2)
    Assert.assertEquals(Just(strVal2), value2M)
  }

  @Test
  def testInt2: Unit = {
    val value2M = props.getInt(strKey2)
    Assert.assertEquals(Just(strVal2), value2M)
  }
}