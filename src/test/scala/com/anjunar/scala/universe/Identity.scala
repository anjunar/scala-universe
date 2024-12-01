package com.anjunar.scala.universe

import scala.compiletime.uninitialized

abstract class Identity[T] {

  def test(test : T) : T

}
