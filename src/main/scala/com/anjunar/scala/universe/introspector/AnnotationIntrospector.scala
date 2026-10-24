package com.anjunar.scala.universe.introspector

import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

object AnnotationIntrospector {

  private val cache = new ConcurrentHashMap[(ResolvedClass, Class[? <: Annotation]), AnnotationModel]()

  /** One model per class and annotation for the lifetime of the program. */
  def create(aClass: ResolvedClass, annotationClass: Class[? <: Annotation]): AnnotationModel =
    cache.computeIfAbsent((aClass, annotationClass), key => new AnnotationModel(key._1, key._2))

  def createWithType(aType: Type, annotationClass: Class[? <: Annotation]): AnnotationModel =
    create(TypeResolver.resolve(aType), annotationClass)

}
