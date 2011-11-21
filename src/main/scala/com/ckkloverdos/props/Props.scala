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
import com.ckkloverdos.maybe.{NoVal, Maybe}
import com.ckkloverdos.convert.Converters

/**
 * Properties with conversion methods.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
class Props(val map: Map[String, String])(implicit conv: Converters = Converters.DefaultConverters) {

  private def _toBoolean(value: String, falseStrings: Set[String]): Boolean = {
    if(null eq value) {
      throw new NullPointerException("Cannot get a Boolean from null")
    } else {
      !(falseStrings contains value.toLowerCase)
    }
  }

  def converters = conv

  def contains(key: String) = map contains key
  
  def filterKeys(f: String => Boolean) = new Props(map filterKeys f)
  
  def filterValues(f: String => Boolean) = new Props(map filter {case (k, v) => f(v)})

  /**
   * Get a value or throw an exception if it doesnot exist.
   */
  def getEx(key: String): String = map apply key

  def get(key: String): Maybe[String] = map.get(key): Maybe[String]

  def getOr(key: String, default: String = null): String = map.getOrElse(key, default)

  def getBoolean(key: String, falseStrings: Set[String] = Props.DefaultFalseStrings): Maybe[Boolean] = map.get(key) match {
    case Some(value) => Maybe(_toBoolean(value, falseStrings))
    case None => NoVal
  }

  def getByte(key: String): Maybe[Byte] = map.get(key) match {
      case Some(value) => conv.convertValueToByte(value)
      case None => NoVal
    }

  def getShort(key: String): Maybe[Short] = map.get(key) match {
    case Some(value) => conv.convertValueToShort(value)
    case None => NoVal
  }

  def getInt(key: String): Maybe[Int] = map.get(key) match {
    case Some(value) => conv.convertValueToInt(conv)
    case None => NoVal
  }

  def getLong(key: String): Maybe[Long] = map.get(key) match {
    case Some(value) => conv.convertValueToLong(value)
    case None => NoVal
  }
  
  def getDouble(key: String): Maybe[Double] = map.get(key) match {
    case Some(value) => conv.convertValueToDouble(value)
    case None => NoVal
  }

  def getFloat(key: String): Maybe[Float] = map.get(key) match {
    case Some(value) => conv.convertValueToFloat(value)
    case None => NoVal
  }

  def getProps(key: String): Maybe[Props] = map.get(key) match {
    case Some(value) => conv.convertValue(value, manifest[Props])
    case None => NoVal
  }
  
  def getList(key: String, separatorRegex: String = "\\s*,\\s*") = map.get(key) match {
    case Some(value) => value.split(separatorRegex).toList
    case None => Nil
  }

  def getTrimmedList(key: String, separatorRegex: String = "\\s*,\\s*"): List[String] =
    getList(key, separatorRegex).map(_.trim).filter(_.length > 0)

  override def equals(any: Any) = any match {
    case props: Props if(props.getClass == this.getClass) => props.map == this.map
    case _ => false
  }

  def equalsProps(other: Props): Boolean = other match {
    case null => false
    case _    => equalsMap(other.map)
  }

  def equalsMap(other: Map[String, String]): Boolean = other match {
    case null => false
    case _ => other == this.map
  }

  override def hashCode() = map.##

  override def toString = "Props(%s)".format(map.mkString(", "))
}

object Props {
  lazy val DefaultFalseStrings = Set("false", "off", "0")

  lazy val DummyWrappedURL = new URL("streamresource://wrapped")
  lazy val DummyWrappedPath = "wrapped"

  lazy val empty = new Props(Map())
  
  def apply(rc: StreamResource)(implicit conv: Converters): Maybe[Props] = {
    rc.mapInputStream { in =>
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