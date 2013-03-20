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

import org.junit.Assert
import org.junit.Test
import com.ckkloverdos.props.Props
import com.ckkloverdos.maybe.Just
import com.ckkloverdos.convert.Converters

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
class ResourceTest {
  val rcA_path = "a.txt";
  val rcA_name = "a.txt";
  val rcA_content = "Hello"

  val rcB_path = "b/b.txt";
  val rcB_pathSeenFromB = "b.txt";
  val rcB_name = "b.txt";
  val rcB_content = " world!"

  val rcC_path = "b/c/c.txt";
  val rcC_pathSeenFromB = "c/c.txt";
  val rcC_pathSeenFromC = "c.txt";
  val rcC_name = "c.txt";
  val rcC_content = "dash"

  val rcConfProps = "conf.properties"
  val rcKeyListComma = "listComma"
  val rcKeyListColon = "listColon"

  implicit val converters = Converters.DefaultConverters

  private[this] def _exists(context: StreamResourceContext, rc_path: String, rc_name: String) {
    val maybeRC = context.getResource(rc_path)
    Assert.assertTrue(maybeRC.isJust)
    for(rc <- maybeRC) {
      Assert.assertTrue(rc.exists)
      Assert.assertEquals(rc_name, rc.name)
    }
  }

  private[this] def _content(context: StreamResourceContext, rc_path: String, rc_content: String) {
    val maybeRC = context.getResource(rc_path)
    for(rc <- maybeRC) {
      val maybeContent = rc.stringContent
      Assert.assertTrue(maybeContent.isJust)
      for(content <- maybeContent) {
        Assert.assertEquals(rc_content, content)
      }
    }
    Assert.assertTrue(maybeRC.isJust)
  }

  @Test
  def testExistsA {
    _exists(ThreadResourceContext, rcA_path, rcA_name)
  }

  @Test
  def testExistsA2 {
    _exists(ThreadResourceContext / "", rcA_path, rcA_name)
  }

  @Test
  def testExistsB {
    _exists(ThreadResourceContext, rcB_path, rcB_name)
  }

  @Test
  def testExistsB2 {
    _exists(ThreadResourceContext / "b", rcB_pathSeenFromB, rcB_name)
  }

  @Test
  def testExistsC {
    _exists(ThreadResourceContext, rcC_path, rcC_name)
  }

  @Test
  def testExistsC2 {
    _exists(ThreadResourceContext / "b", rcC_pathSeenFromB, rcC_name)
  }

  @Test
  def testExistsC3 {
    _exists(ThreadResourceContext / "b" / "c", rcC_pathSeenFromC, rcC_name)
  }

  @Test
  def testContentOfA {
    _content(ThreadResourceContext, rcA_path, rcA_content)
  }

  @Test
  def testContentOfB {
    _content(ThreadResourceContext, rcB_path, rcB_content)
  }

  @Test
  def testContentOfC {
    _content(ThreadResourceContext, rcC_path, rcC_content)
  }

  @Test
  def testPropsGetList {
    val maybeProps = Props(rcConfProps)
    val props = maybeProps.getOr(throw new Exception)
    val listComma = props.getTrimmedList(rcKeyListComma)
    Assert.assertEquals(List("one", "two", "three"), listComma)
    val listColon = props.getTrimmedList(rcKeyListColon, "\\s*:\\s*")
    Assert.assertEquals(List("one", "two", "three"), listColon)
  }
  
  @Test
  def testPropsBoolean {
    val key1val = ("key1", "true", true)
    val key2val = ("key2",  "false", false)
    val key3val = ("key3", "0", false)
    val all = Seq(key1val, key2val, key3val)
    val keyvals = all map { case (k, v, _) => (k, v) }
    val props = Props(keyvals: _*)

    all foreach  { case (k, _, b) =>
      Assert.assertEquals("Testing boolean property: %s".format(k), Just(b), props.getBoolean(k))
    }
  }
  
  @Test
  def testPropsEqual1 {
    val props1 = Props("1" -> "one", "2" -> "two")
    val props2 = Props("2" -> "two", "1" -> "one")
    Assert.assertEquals(props1, props2)
    Assert.assertEquals(props2, props1)
  }

  @Test
  def testPropsEqual2 {
    class SuperProps(map: Map[String, String]) extends Props(map)
    val props1 = new SuperProps(Map("1" -> "one", "2" -> "two"))
    val props2 = Props("2" -> "two", "1" -> "one")
    Assert.assertTrue(props1.equalsProps(props2))
    Assert.assertTrue(props2.equalsProps(props1))
  }

  @Test
  def testPropsNotEqual1 {
    val props1 = Props("1" -> "one", "2" -> "two")
    val props2 = Props("2" -> "two", "1" -> "one1")
    Assert.assertFalse(props1 == props2)
  }

  @Test
  def testPropsNotEqual2 {
    val props1 = Props("1" -> "one", "2" -> "two")
    Assert.assertFalse(props1 == null)
  }
  
  @Test
  def testPropsNotEqual3 {
    class SuperProps(map: Map[String, String]) extends Props(map)
    val props1 = new SuperProps(Map("1" -> "one", "2" -> "two"))
    val props2 = Props("2" -> "two", "1" -> "one")
    Assert.assertFalse(props1 == props2)
  }
}