package com.anjunar.scala.universe.members

import com.anjunar.scala.universe.ResolvedClass

import scala.reflect.runtime.universe.*

class ResolvedConstructor(underlying: Symbol, owner : ResolvedClass) extends ResolvedExecutable(underlying, owner) {

  override def toString: String = s"${annotations.mkString(" ")} ${underlying.toString}${super.toString}"

}
