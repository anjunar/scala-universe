package com.anjunar.scala.universe

import com.anjunar.scala.universe.annotations.Annotated
import com.anjunar.scala.universe.members.{ResolvedConstructor, ResolvedField, ResolvedMethod}

import scala.reflect.runtime.universe.Type

class ResolvedClass(val aType : Type) extends Annotated(aType.typeSymbol){

  lazy val hierarchy : Array[ResolvedClass] = aType
    .baseClasses
    .map(aClass => new ResolvedClass(aClass.info))
    .toArray

  lazy val declaredFields: Array[ResolvedField] = aType
    .decls
    .filter(decl => decl.isTerm && ! decl.isMethod && ! decl.isConstructor).map(decl => ResolvedField(decl, this))
    .toArray

  lazy val declaredConstructors : Array[ResolvedConstructor] = aType
    .decls
    .filter(decl => decl.isConstructor).map(decl => ResolvedConstructor(decl, this))
    .toArray

  lazy val declaredMethods: Array[ResolvedMethod] = aType
    .decls
    .filter(decl => decl.isMethod && ! decl.isConstructor && ! decl.isSynthetic).map(decl => ResolvedMethod(decl, this))
    .toArray

  lazy val fields : Array[ResolvedField] = {
    val allFields = hierarchy.flatMap(aClass => aClass.declaredFields)
    val hidden = allFields.flatMap(field => field.hidden)
    allFields.filter(field => ! hidden.contains(field))
  }

  lazy val constructors : Array[ResolvedConstructor] = declaredConstructors

  lazy val methods : Array[ResolvedMethod] = {
    val allMethods = hierarchy.flatMap(aClass => aClass.declaredMethods)
    val overrides = allMethods.flatMap(method => method.overrides)
    allMethods.filter(field => ! overrides.contains(field))
  }

  override def toString = s"${annotations.mkString(" ")} ${aType.typeSymbol.name}{\n${declaredFields.mkString("\n")}\n${declaredConstructors.mkString("\n")}\n${declaredMethods.mkString("\n")}\n}"
}
