package com.anjunar.scala.universe.members

import com.anjunar.scala.universe.annotations.Annotated

import scala.reflect.runtime.universe.Symbol

class ResolvedParameter(val underlying : Symbol, owner : ResolvedExecutable) extends Annotated(underlying) {

  override def toString: String = s"${annotations.mkString(" ")} ${underlying.name} : ${underlying.info.resultType}"

}
