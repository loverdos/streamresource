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

/**
 * Properties with conversion methods.
 *
 * TODO: integrate with `converter` project
 * 
 * @author Christos KK Loverdos <loverdos@gmail.com>.
 */
class Props(val map: Map[String, String]) {

  def get(key: String): Maybe[String] = map.get(key): Maybe[String]

  def getInt(key: String): Maybe[Int] = map.get(key) match {
    case Some(value) => Maybe(value.toInt)
    case None => NoVal
  }

  def getLong(key: String): Maybe[Long] = map.get(key) match {
    case Some(value) => Maybe(value.toLong)
    case None => NoVal
  }
  
  def getDouble(key: String): Maybe[Double] = map.get(key) match {
    case Some(value) => Maybe(value.toDouble)
    case None => NoVal
  }
  
  def getProps(key: String): Maybe[Props] = map.get(key) match {
    case Some(value) => Props(value)
    case None => NoVal
  }
  
  def getList(key: String, separatorRegex: String = "\\s*,\\s*") = map.get(key) match {
    case Some(value) => value.split(separatorRegex).toList
    case None => Nil
  }

  def getTrimmedList(key: String, separatorRegex: String = "\\s*,\\s*"): List[String] =
    getList(key, separatorRegex).map(_.trim).filter(_.length > 0)
}

object Props {
  lazy val DummyWrappedURL = new URL("streamresource://wrapped")
  lazy val DummyWrappedPath = "wrapped"

  lazy val empty = new Props(Map())
  
  def apply(rc: StreamResource): Maybe[Props] = {
    rc.mapInputStream { in =>
      val props = new java.util.Properties
      props.load(in)
      import collection.JavaConversions._
      props.toMap
    } map (new Props(_))
  }

  def apply(in: InputStream): Maybe[Props] =
    this(new WrappingStreamResource(in, DummyWrappedPath, DummyWrappedURL))

  def apply(props: Properties): Maybe[Props] = {
    import collection.JavaConversions._
    Maybe(new Props(props.toMap))
  }
  
  def apply(path: String, rc: StreamResourceContext = DefaultResourceContext): Maybe[Props] = {
    rc.getResource(path).flatMap(this(_))
  }
}