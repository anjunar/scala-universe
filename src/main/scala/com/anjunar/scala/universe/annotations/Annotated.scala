package com.anjunar.scala.universe.annotations

import java.lang.annotation
import scala.reflect.runtime.universe.*
import java.lang.reflect.{InvocationHandler, Method, Proxy}

trait Annotated(underlying : Symbol) {

  lazy val annotations: Array[annotation.Annotation] =
    underlying.annotations.map(anno => {
        val name = anno.tree.tpe.toString
        val tuples = anno.tree.children
          .filter(a => a.isInstanceOf[NamedArgApi])
          .map(_.asInstanceOf[NamedArgApi])
          .map(ap => (ap.lhs, ap.rhs))

        val annotationClass: Class[annotation.Annotation] = Class.forName(name).asInstanceOf[Class[annotation.Annotation]]

        Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader,
            Array(annotationClass),
            new InvocationHandler {
              override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): Any = method.getName match {
                case "toString" => "@" + annotationClass.getSimpleName
                case "annotationType" => annotationClass
                case _ => tuples.find(tuple => tuple._1.asInstanceOf[Ident].name.toString == method.getName).get._2.asInstanceOf[Literal].value.value
              }
            })
          .asInstanceOf[annotation.Annotation]
      })
      .toArray

  def findAnnotation[A <: annotation.Annotation](aClass : Class[A]) : A = annotations.find(anno => anno.annotationType() == aClass).getOrElse(null).asInstanceOf[A]

}
