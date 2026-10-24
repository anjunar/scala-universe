package com.anjunar.scala.universe.introspector

import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

object BeanIntrospector {

  private val cache = new ConcurrentHashMap[ResolvedClass, BeanModel]()

  /** One model per class for the lifetime of the program. */
  def create(aClass: ResolvedClass): BeanModel = cache.computeIfAbsent(aClass, value => new BeanModel(value))

  def createWithType(aType: Type): BeanModel = create(TypeResolver.resolve(aType))

}
