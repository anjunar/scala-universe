package com.anjunar.scala.universe

import java.lang.reflect.{ParameterizedType, Type, TypeVariable, WildcardType}
import java.util.concurrent.ConcurrentHashMap
import scala.annotation.tailrec

object TypeResolver {

  // A ResolvedClass defers all its work, so concurrent first requests for a type may race without harm.
  private val cache = new ConcurrentHashMap[Type, ResolvedClass]()

  private val companionCache = new ConcurrentHashMap[Class[?], Option[Class[?]]]()

  /** The Scala companion class `Name$` of a class, loaded next to it, or null when there is none. */
  def companionClass(clazz: Class[?]): Class[?] =
    companionCache.computeIfAbsent(
      clazz,
      value =>
        try {
          Some(Class.forName(value.getName + "$", true, value.getClassLoader))
        } catch {
          case _: ClassNotFoundException => None
        }
    ).orNull

  inline def companionInstance[E](clazz: Class[?]): E = {
    try {
      val value = companionClass(clazz)
      if (value == null) {
        null.asInstanceOf[E]
      } else {
        val companion = value.getField("MODULE$").get(null)
        companion match {
          case e: E =>
            e
          case _ =>
            null.asInstanceOf[E]
        }
      }
    } catch {
      case e: NoSuchFieldException => null.asInstanceOf[E]
    }
  }

  /** One ResolvedClass per type for the lifetime of the program. */
  def resolve(aType: Type): ResolvedClass = cache.computeIfAbsent(aType, value => new ResolvedClass(value))

  @tailrec
  def rawType(aType: Type): Class[?] = aType match {
    case aClass: Class[?]                     => aClass
    case parameterizedType: ParameterizedType => rawType(parameterizedType.getRawType)
    case typeVariable: TypeVariable[?]        => rawType(generateParameterizedType(typeVariable))
    case wildCardType: WildcardType           => rawType(wildCardType.getUpperBounds.head)
    case _                                    => throw new IllegalStateException("Unexpected value: " + aType)
  }

  private def generateParameterizedType(typeVariable: TypeVariable[?]): ParameterizedType = new ParameterizedType() {
    override def getActualTypeArguments: Array[Type] = typeVariable.getBounds

    override def getRawType: Type = typeVariable.getGenericDeclaration.asInstanceOf[Type]

    override def getOwnerType: Type = null
  }

}
