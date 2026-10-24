package com.anjunar.scala.universe.members

import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}
import com.google.common.reflect.TypeToken

import java.lang.annotation.Annotation
import java.lang.reflect.Method

class ResolvedMethod(override val underlying: Method, owner: ResolvedClass)
    extends ResolvedExecutable(underlying, owner) {

  lazy val name: String = underlying.getName

  /** The methods with this signature in the supertypes, which this one overrides. */
  lazy val overrides: Array[ResolvedMethod] =
    owner.hierarchy.drop(1).flatMap(aClass => Option(aClass.findMethod(name, underlying.getParameterTypes*)))

  lazy val returnType: ResolvedClass = {
    if (underlying.getGenericReturnType.isInstanceOf[Class[?]]) {
      val genericInfo = overrides.find(method =>
        method.underlying.getReturnType == underlying.getReturnType &&
          !method.underlying.getGenericReturnType.isInstanceOf[Class[?]]
      ).orNull
      if (genericInfo != null) {
        genericInfo.returnType
      } else {
        TypeResolver.resolve(TypeToken.of(owner.underlying).resolveType(underlying.getGenericReturnType).getType)
      }
    } else {
      TypeResolver.resolve(TypeToken.of(owner.underlying).resolveType(underlying.getGenericReturnType).getType)
    }
  }

  def invoke(instance: AnyRef, args: Any*): Any = {
    underlying.setAccessible(true)
    underlying.invoke(instance, args*)
  }

  override lazy val declaredAnnotations: Array[Annotation] = underlying.getDeclaredAnnotations

  override lazy val annotations: Array[Annotation] = declaredAnnotations ++
    overrides.flatMap(method => method.declaredAnnotations)

  override def toString = s"ResolvedMethod($name, $returnType, ${parameters.mkString(", ")})"
}
