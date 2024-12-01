package com.anjunar.scala.universe

import java.lang.reflect
import java.lang.reflect.{GenericArrayType, ParameterizedType, TypeVariable, WildcardType}
import scala.reflect.runtime.universe as ru
import scala.reflect.runtime.universe.{Type, appliedType}

object TypeResolver {

  val mirror = ru.runtimeMirror(Thread.currentThread().getContextClassLoader)

  private val array = mirror.staticClass("scala.Array")

  def resolve(aType: reflect.Type) : ResolvedClass = new ResolvedClass(resolveType(aType))

  private def resolveType(aType : reflect.Type): Type = aType match
    case typeVariable: TypeVariable[_] => resolveType(typeVariable.getBounds.apply(0))
    case genericArrayType : GenericArrayType => appliedType(array, resolveType(genericArrayType.getGenericComponentType))
    case aCLass : Class[?] => mirror.staticClass(aCLass.getName).toType
    case parameterizedType: ParameterizedType => appliedType(
      resolveType(parameterizedType.getRawType),
      parameterizedType.getActualTypeArguments.map(aType => resolveType(aType)).toList
    )
    case wildCardType: WildcardType =>
      if wildCardType.getLowerBounds.isEmpty then resolveType(wildCardType.getUpperBounds.apply(0))
      else resolveType(wildCardType.getLowerBounds.apply(0))
}
