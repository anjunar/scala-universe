package com.anjunar.scala.universe.introspector

import com.anjunar.scala.universe.TypeResolver
import com.anjunar.scala.universe.fixtures.{Marker, StringBox}
import munit.FunSuite

class IntrospectorSpec extends FunSuite {

  test("BeanIntrospector caches models and exposes inherited bean properties") {
    val first = BeanIntrospector.createWithType(classOf[StringBox])
    val second = BeanIntrospector.create(TypeResolver.resolve(classOf[StringBox]))
    val property = first.findProperty("value")
    val instance = new StringBox()

    assert(first eq second)
    assert(property != null)
    assertEquals(property.propertyType.raw, classOf[String])
    assert(property.isWriteable)

    property.set(instance, "hello")
    assertEquals(property.get(instance), "hello")
  }

  test("AnnotationIntrospector collects annotated properties and caches models") {
    val first = AnnotationIntrospector.createWithType(classOf[StringBox], classOf[Marker])
    val second = AnnotationIntrospector.create(TypeResolver.resolve(classOf[StringBox]), classOf[Marker])
    val property = first.findProperty("value")
    val declared = first.findDeclaredProperty("value")

    assert(first eq second)
    assert(property != null)
    assertEquals(property.name, "value")
    assert(property.findAnnotation(classOf[Marker]) != null)
    assert(property.getter.findDeclaredAnnotation(classOf[Marker]) != null)
    assert(property.setter.findDeclaredAnnotation(classOf[Marker]) != null)
    assertEquals(declared, null)
  }
}
