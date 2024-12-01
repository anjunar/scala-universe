package com.anjunar.scala.universe.members

import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}

import scala.reflect.runtime.universe.*

class ResolvedField(underlying : Symbol, owner : ResolvedClass) extends ResolvedMember(underlying, owner) {

  lazy val name : String = underlying.name.decodedName.toString

  lazy val hidden : Array[ResolvedField] = owner.hierarchy.drop(1).flatMap(aClass => aClass.fields.filter(field => field.name == name))

  def get(instance : Any) : Any = TypeResolver.mirror.reflect(instance).reflectField(underlying.asTerm).get

  override def toString: String = s"${annotations.mkString(" ")} ${underlying.toString} : ${underlying.info.resultType}"

}
