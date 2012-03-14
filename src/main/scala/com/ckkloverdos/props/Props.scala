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

import com.ckkloverdos.resource.{StreamResource, WrappingStreamResource, StreamResourceContext, DefaultResourceContext}
import java.io.InputStream
import java.net.URL
import java.util.Properties
import com.ckkloverdos.convert.Converters
import com.ckkloverdos.maybe.{NoVal, Maybe}
import com.ckkloverdos.key.StringKey
import com.ckkloverdos.env.Env

/**
 * Properties with conversion methods.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
class Props(val map: Map[String, String])(implicit conv: Converters = Converters.DefaultConverters) extends PropsBase[String, String, Props]{
  def newProps(map: Props#MapType) = new Props(map)

  def toAnyProps: AnyProps = new AnyProps(map)

  val converters = conv

  def getBoolean(key: String): Maybe[Boolean] = map.get(key) match {
    case Some(value) ⇒ conv.convertToBoolean(value)
    case None ⇒ NoVal
  }

  def getByte(key: String): Maybe[Byte] = map.get(key) match {
    case Some(value) ⇒ conv.convertToByte(value)
    case None ⇒ NoVal
  }

  def getShort(key: String): Maybe[Short] = map.get(key) match {
    case Some(value) ⇒ conv.convertToShort(value)
    case None ⇒ NoVal
  }

  def getInt(key: String): Maybe[Int] = map.get(key) match {
    case Some(value) ⇒ conv.convertToInt(value)
    case None ⇒ NoVal
  }

  def getLong(key: String): Maybe[Long] = map.get(key) match {
    case Some(value) ⇒ conv.convertToLong(value)
    case None ⇒ NoVal
  }
  
  def getDouble(key: String): Maybe[Double] = map.get(key) match {
    case Some(value) ⇒ conv.convertToDouble(value)
    case None ⇒ NoVal
  }

  def getFloat(key: String): Maybe[Float] = map.get(key) match {
    case Some(value) ⇒ conv.convertToFloat(value)
    case None ⇒ NoVal
  }

  def getProps(key: String): Maybe[Props] = map.get(key) match {
    case Some(value) ⇒ conv.convert[Props](value)
    case None ⇒ NoVal
  }
  
  def getList(key: String, separatorRegex: String = "\\s*,\\s*") = map.get(key) match {
    case Some(value) ⇒ value.split(separatorRegex).toList
    case None ⇒ Nil
  }

  def getTrimmedList(key: String, separatorRegex: String = "\\s*,\\s*"): List[String] =
    getList(key, separatorRegex).map(_.trim).filter(_.length > 0)

  override def equals(any: Any) = any match {
    case props: Props if(props.getClass == this.getClass) ⇒ props.map == this.map
    case _ ⇒ false
  }

  def equalsProps(other: Props): Boolean = other match {
    case null ⇒ false
    case _    ⇒ equalsMap(other.map)
  }

  def equalsMap(other: Map[String, String]): Boolean = other match {
    case null ⇒ false
    case _ ⇒ other == this.map
  }
  
  def toEnv: Env = new Env(map map { case (k, v) ⇒ (StringKey(k), v)})

  def group(keyPrefix: String): Props = {
    val dottedPrefix = keyPrefix + "."
    val newPairs = for {
      key <- map.keysIterator if(key.startsWith(dottedPrefix))
    } yield {
      key.substring(dottedPrefix.length) -> map(key)
    }

    new Props(Map(newPairs.toSeq: _*))
  }

  override def hashCode() = map.##

  override def toString = "Props(%s)".format(map.mkString(", "))
}

object Props {
  lazy val DummyWrappedURL = new URL("streamresource://wrapped")
  lazy val DummyWrappedPath = "wrapped"

  lazy val empty = new Props(Map())
  
  def apply(rc: StreamResource)(implicit conv: Converters): Maybe[Props] = {
    rc.mapInputStream { in ⇒
      val props = new java.util.Properties
      props.load(in)
      import collection.JavaConversions._
      props.toMap
    } map (new Props(_))
  }

  def apply(in: InputStream)(implicit conv: Converters): Maybe[Props] =
    this(new WrappingStreamResource(in, DummyWrappedPath, DummyWrappedURL))

  def apply(props: Properties)(implicit conv: Converters): Maybe[Props] = {
    import collection.JavaConversions._
    Maybe(new Props(props.toMap))
  }
  
  def apply(path: String, rc: StreamResourceContext = DefaultResourceContext)(implicit conv: Converters): Maybe[Props] = {
    rc.getResource(path).flatMap(this(_))
  }

  def apply(keyvals: (String, String)*)(implicit conv: Converters): Props = new Props(Map(keyvals: _*))
}