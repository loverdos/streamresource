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
import com.ckkloverdos.maybe.{NoVal, Maybe}
import com.ckkloverdos.resource.{StreamResourceContext, StreamResource, ThreadResourceContext}
import java.io.InputStream
import java.util.Properties

/**
 * Properties with conversion methods.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
class AnyProps(val map: Map[String, Any])(implicit conv: Converters = Converters.DefaultConverters) extends PropsBase[String, Any, AnyProps]{
  def newProps(map: AnyProps#MapType) = new AnyProps(map)

  val converters = conv

  def getBoolean(key: String, falseStrings: Set[String] = AnyProps.DefaultFalseStrings): Maybe[Boolean] = map.get(key) match {
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

  def getProps(key: String): Maybe[AnyProps] = map.get(key) match {
    case Some(value) ⇒ conv.convert[AnyProps](value)
    case None ⇒ NoVal
  }
  
  def getList(key: String, separatorRegex: String = "\\s*,\\s*") = map.get(key) match {
    case Some(value) ⇒ value match {
      case value: String ⇒
        value.split(separatorRegex).toList
      case _ ⇒
        Nil
    }
    case None ⇒ Nil
  }

  def getTrimmedList(key: String, separatorRegex: String = "\\s*,\\s*"): List[String] =
    getList(key, separatorRegex).map(_.trim).filter(_.length > 0)

  override def equals(any: Any) = any match {
    case props: AnyProps ⇒ props.getClass == this.getClass && props.map == this.map
    case _ ⇒ false
  }

  def equalsAnyProps(other: AnyProps): Boolean = other match {
    case null ⇒ false
    case _    ⇒ equalsMap(other.map)
  }

  def equalsMap(other: Map[String, Any]): Boolean = other match {
    case null ⇒ false
    case _ ⇒ other == this.map
  }

  override def hashCode() = map.##

  override def toString = "AnyProps(%s)".format(map.mkString(", "))
}

object AnyProps {
  lazy val DefaultFalseStrings = Set("false", "off", "0")

  lazy val empty = new Props(Map())
  
  def apply(rc: StreamResource)(implicit conv: Converters): Maybe[AnyProps] = {
    rc.mapStream { in ⇒
      val props = new java.util.Properties
      props.load(in)
      import collection.JavaConversions._
      props.toMap
    } map (new AnyProps(_))
  }

  def apply(in: InputStream)(implicit conv: Converters): Maybe[AnyProps] = {
    Maybe {
      val props = new java.util.Properties
      props.load(in)
      import collection.JavaConversions._
      new AnyProps(props.toMap)
    }
  }

  def apply(props: Properties)(implicit conv: Converters): Maybe[AnyProps] = {
    import collection.JavaConversions._
    Maybe(new AnyProps(props.toMap))
  }
  
  def apply(path: String, rc: StreamResourceContext = ThreadResourceContext)(implicit conv: Converters): Maybe[AnyProps] = {
    rc.getResource(path).flatMap(this(_))
  }

  def apply(keyvals: (String, Any)*)(implicit conv: Converters): AnyProps = new AnyProps(Map(keyvals: _*))
}