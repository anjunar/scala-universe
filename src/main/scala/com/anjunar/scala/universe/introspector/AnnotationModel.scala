package com.anjunar.scala.universe.introspector

import com.anjunar.scala.universe.ResolvedClass
import com.anjunar.scala.universe.annotations.Annotated
import com.anjunar.scala.universe.members.{ResolvedField, ResolvedMethod}

import java.lang.annotation.Annotation

class AnnotationModel(val underlying: ResolvedClass, val annotationClass: Class[? <: Annotation]) extends Annotated {

  private def decapitalize(name: String): String =
    if (name.isEmpty) name else name.substring(0, 1).toLowerCase + name.substring(1)

  private def getPropertyName(name: String): String =
    if (name.endsWith("_$eq")) {
      name.substring(0, name.length - 4)
    } else if (name.startsWith("get") && name.length > 3) {
      decapitalize(name.substring(3))
    } else if (name.startsWith("is") && name.length > 2) {
      decapitalize(name.substring(2))
    } else if (name.startsWith("set") && name.length > 3) {
      decapitalize(name.substring(3))
    } else {
      name
    }

  private def isGetter(method: ResolvedMethod, propertyName: String): Boolean =
    (method.name == propertyName && method.parameters.length == 0) ||
      (method.name == s"get${propertyName.substring(0, 1).toUpperCase}${propertyName.substring(1)}" && method.parameters.length == 0) ||
      (method.name == s"is${propertyName.substring(0, 1).toUpperCase}${propertyName.substring(1)}" && method.parameters.length == 0)

  private def isSetter(method: ResolvedMethod, propertyName: String): Boolean =
    (method.name == (propertyName + "_$eq") && method.parameters.length == 1) ||
      (method.name == s"set${propertyName.substring(0, 1).toUpperCase}${propertyName.substring(1)}" && method.parameters.length == 1)

  private def isAnnotated(annotated: Annotated): Boolean = {
    annotated.annotations.exists(_.annotationType() == annotationClass)
  }

  private def collectProperties(fields: Array[ResolvedField], methods: Array[ResolvedMethod]): Array[AnnotationProperty] = {
    val propertyNames = (
      fields.filter(isAnnotated).map(_.name) ++
      methods.filter(isAnnotated).map(m => getPropertyName(m.name))
    ).distinct

    propertyNames.map { name =>
      val field = fields.find(_.name == name).orNull
      val getter = methods.find(m => isGetter(m, name)).orNull
      val setter = methods.find(m => isSetter(m, name)).orNull
      new AnnotationProperty(this, name, field, getter, setter)
    }
  }

  lazy val declaredProperties: Array[AnnotationProperty] = collectProperties(underlying.declaredFields, underlying.declaredMethods)

  lazy val properties: Array[AnnotationProperty] = collectProperties(underlying.fields, underlying.methods)

  def findDeclaredProperty(name: String): AnnotationProperty = declaredProperties.find(_.name == name).orNull

  def findProperty(name: String): AnnotationProperty = properties.find(_.name == name).orNull

  override lazy val declaredAnnotations: Array[Annotation] = underlying.declaredAnnotations
  override lazy val annotations: Array[Annotation] = underlying.annotations
}
