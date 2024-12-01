package com.anjunar.scala.universe

import scala.beans.BeanProperty

@Test
class Person(@Test val firstName : String,@Test val lastName : String, val address : Address[Array[String]]) extends Identity[String]() {

  @BeanProperty
  @Test
  var te : String = "test"

  @Test
  override def test(@Test test : String) : String = "test"

}
