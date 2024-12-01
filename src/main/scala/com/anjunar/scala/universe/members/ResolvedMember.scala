package com.anjunar.scala.universe.members

import com.anjunar.scala.universe.ResolvedClass
import com.anjunar.scala.universe.annotations.Annotated

import scala.reflect.runtime.universe.Symbol

class ResolvedMember(val underlying: Symbol, val owner: ResolvedClass) extends Annotated(underlying)
