package com.anjunar.scala.universe

import scala.reflect.runtime.universe._

object Main {

  def main(args : Array[String]): Unit = {

    val mirror = runtimeMirror(Thread.currentThread().getContextClassLoader)

    val aClass = classOf[Person]

    val address = aClass.getMethod("address")

    val test = aClass.getMethod("test", classOf[String])

    println(test.getParameterAnnotations.apply(0).mkString(" "))

    val value = TypeResolver.resolve(aClass)

    println(value)

    value.declaredMethods.foreach(x => {
      val option = x.findAnnotation(classOf[Test])

      if (option != null) {
        println(option.value())
      }
    })
  }

}
