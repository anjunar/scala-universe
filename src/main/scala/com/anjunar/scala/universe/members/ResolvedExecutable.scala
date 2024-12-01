package com.anjunar.scala.universe.members

import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import scala.reflect.runtime.universe.*

class ResolvedExecutable(underlying : Symbol, owner : ResolvedClass) extends ResolvedMember(underlying, owner) {
  
  lazy val parameters : Array[ResolvedParameter] = underlying
    .asMethod
    .paramLists
    .head
    .map(param => new ResolvedParameter(param, this))
    .toArray

  def invoke(instance: Any, args: Any*): Any = TypeResolver.mirror.reflect(instance).reflectMethod(underlying.asMethod).apply(args *)
  
  override def toString = s"(${parameters.mkString(", ")})"
}
