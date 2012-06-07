/*
 * Copyright 2012 zhongl
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.zhongl.housemd


import instrument.Instrumentation
import scala.annotation.tailrec
import com.github.zhongl.yascli.{Command, PrintOut}
import Reflections._


class Loaded(val inst: Instrumentation, out: PrintOut)
  extends Command("loaded", "display loaded classes information.", out)
          with ClassSimpleNameCompleter {

  private val tab = "\t"

  private val hierarchyable = flag("-h" :: "--classloader-hierarchies" :: Nil, "display classloader hierarchies of loaded class.")
  private val keyword       = parameter[String]("keyword", "keyword which class name contains.")

  override def run() {
    val k = keyword()
    val matched = inst.getAllLoadedClasses filter {_.getName.contains(k)}
    if (matched.isEmpty) println("No matched class")
    else matched foreach { c => println(c.getName + originOf(c)); if (hierarchyable()) layout(c.getClassLoader) }
  }

  @tailrec
  private def layout(cl: ClassLoader, lastIndents: String = "- ") {
    cl match {
      case null => Unit
      case _    =>
        val indents = tab + lastIndents
        println(indents + nativeToStringOf(cl))
        layout(cl.getParent, indents)
    }
  }

  private def originOf(c: Class[_]): String = " -> " + Utils.sourceOf(c)

}




