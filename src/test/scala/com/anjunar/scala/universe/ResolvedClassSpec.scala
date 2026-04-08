package com.anjunar.scala.universe

import com.anjunar.scala.universe.fixtures.{ShadowChild, ShadowParent, StringBox}
import munit.FunSuite

class ResolvedClassSpec extends FunSuite {

  test("resolved generic members use concrete subclass types") {
    val resolvedClass = TypeResolver.resolve(classOf[StringBox])
    val field = resolvedClass.findField("value")
    val method = resolvedClass.findMethod("getValue")

    assertEquals(field.fieldType.raw, classOf[String])
    assertEquals(method.returnType.raw, classOf[String])
  }

  test("hidden fields are filtered from flattened field list") {
    val resolvedClass = TypeResolver.resolve(classOf[ShadowChild])
    val countFields = resolvedClass.fields.filter(_.name == "count")

    assertEquals(countFields.length, 1)
    assertEquals(countFields.head.owner.raw, classOf[ShadowChild])
    assertEquals(resolvedClass.findDeclaredField("count").fieldType.raw, classOf[Integer])
    assertEquals(resolvedClass.hierarchy.map(_.raw).toSeq, Seq(classOf[ShadowChild], classOf[ShadowParent]))
  }

  test("overridden methods are filtered from flattened method list") {
    val resolvedClass = TypeResolver.resolve(classOf[ShadowChild])
    val countMethods = resolvedClass.methods.filter(_.name == "getCount")

    assertEquals(countMethods.length, 1)
    assertEquals(countMethods.head.owner.raw, classOf[ShadowChild])
    assertEquals(countMethods.head.returnType.raw, classOf[Integer])
  }
}
