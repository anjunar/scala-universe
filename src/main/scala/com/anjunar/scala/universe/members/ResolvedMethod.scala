package com.anjunar.scala.universe.members

import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import scala.reflect.runtime.universe.*

class ResolvedMethod(underlying : Symbol, owner : ResolvedClass) extends ResolvedExecutable(underlying, owner) {

  lazy val name : String = underlying.name.decodedName.toString

  lazy val overrides : Array[ResolvedMethod] = underlying
    .overrides.map(method => new ResolvedMethod(method, owner.hierarchy.find(aClass => aClass.aType == method.owner.info).get))
    .toArray

  override def toString: String = s"${annotations.mkString(" ")} ${underlying.toString}${super.toString}"

}
