package com.anjunar.scala.universe

import com.anjunar.scala.universe.fixtures.HasCompanion
import munit.FunSuite
import java.lang.{String as LangString}

class TypeResolverSpec extends FunSuite {

  test("resolve caches resolved classes per type") {
    val first = TypeResolver.resolve(classOf[String])
    val second = TypeResolver.resolve(classOf[String])

    assert(first eq second)
  }

  test("companion lookup returns Scala companion class and instance") {
    val companionClass = TypeResolver.companionClass(classOf[HasCompanion])
    val companion = TypeResolver.companionInstance[HasCompanion.type](classOf[HasCompanion])

    assertEquals(companionClass, classOf[HasCompanion.type])
    assertEquals(companion, HasCompanion)
  }

  test("companion lookup returns null when no companion exists") {
    assertEquals(TypeResolver.companionClass(classOf[LangString]), null)
    assertEquals(TypeResolver.companionInstance[AnyRef](classOf[LangString]), null)
  }
}
