package com.anjunar.scala.universe

import munit.FunSuite

class ForwardedGenericMethodSpec extends FunSuite {
  test("Scala trait forwarders retain their substituted parameter and result types") {
    val owner = TypeResolver.resolve(classOf[StringForwardedMethods])
    val echo = owner.methods.find(_.name == "echo").get
    assertEquals(echo.parameters.head.parameterType.raw, classOf[String])
    assertEquals(echo.returnType.raw, classOf[String])
    val many = owner.methods.find(_.name == "many").get
    assertEquals(many.parameters.head.parameterType.typeArguments.head.raw, classOf[String])
    assertEquals(many.returnType.typeArguments.head.raw, classOf[String])
    assertEquals(echo.invoke(new StringForwardedMethods, "value"), "value")
  }

  test("raw Java overrides recover the bound collection type from their generic declaration") {
    val owner = TypeResolver.resolve(classOf[fixtures.ErasedGenericFixture])
    val method = owner.methods.find(_.name == "transform").get
    assertEquals(method.parameters.head.parameterType.typeArguments.head.raw, classOf[String])
    assertEquals(method.returnType.typeArguments.head.raw, classOf[String])
  }
  test("a concrete covariant result is never replaced by a broader inherited type") {
    val method = TypeResolver.resolve(classOf[ConcreteCovariantMethod]).findMethod("value")
    assertEquals(method.returnType.raw, classOf[String])
  }
}

trait ForwardedMethods[A] {
  def echo(value: A): A = value
  def many(values: List[A]): List[A] = values
}
class StringForwardedMethods extends ForwardedMethods[String]
trait GenericResult[A] { def value: A }
class ConcreteCovariantMethod extends GenericResult[CharSequence] { override def value: String = "concrete" }
