package com.anjunar.scala.universe.members

import com.anjunar.scala.universe.annotations.Annotated
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}
import com.google.common.reflect.TypeToken

import java.lang.annotation.Annotation
import java.lang.reflect.Parameter

class ResolvedParameter(val underlying: Parameter, owner: ResolvedExecutable) extends Annotated {

  lazy val name: String = underlying.getName

  lazy val parameterType: ResolvedClass = {
    // Scala trait forwarders can erase a concrete inherited binding to Object (or a raw collection).
    // Use the overridden generic declaration in its already resolved owner context.
    val inherited = owner match {
      case method: ResolvedMethod if underlying.getParameterizedType.isInstanceOf[Class[?]] =>
        val index = method.parameters.indexOf(this)
        method.overrides.iterator
          .map(_.parameters(index))
          .find(parameter => !parameter.underlying.getParameterizedType.isInstanceOf[Class[?]])
          .map(_.parameterType)
      case _ => None
    }
    inherited.getOrElse(
      TypeResolver.resolve(TypeToken.of(owner.owner.underlying).resolveType(underlying.getParameterizedType).getType)
    )
  }

  override lazy val declaredAnnotations: Array[Annotation] = underlying.getDeclaredAnnotations

  override lazy val annotations: Array[Annotation] = owner match
    case method: ResolvedMethod => declaredAnnotations ++
        method.overrides.flatMap(method => method.parameters(owner.parameters.indexOf(this)).declaredAnnotations)
    case _ => Array()

  override def toString = s"ResolvedParameter($name, $parameterType)"
}
