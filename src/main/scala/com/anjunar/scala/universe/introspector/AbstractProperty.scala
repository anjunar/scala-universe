package com.anjunar.scala.universe.introspector

import com.anjunar.scala.universe.annotations.Annotated
import com.anjunar.scala.universe.members.{ResolvedField, ResolvedMethod}
import com.anjunar.scala.universe.{ResolvedClass, TypeResolver}
import com.google.common.reflect.TypeToken

import java.lang.annotation.Annotation

/** A property joined from its field, getter and setter; each of the three may be absent. */
class AbstractProperty(
  val name: String,
  val field: ResolvedField,
  val getter: ResolvedMethod,
  val setter: ResolvedMethod
) extends Annotated {

  def get(instance: AnyRef): Any =
    if (getter == null) field.get(instance) else getter.invoke(instance)

  def set(instance: AnyRef, value: Any): Unit = {
    if (setter == null) {
      throw new IllegalStateException("no Setter Method " + toString)
    }
    setter.invoke(instance, value)
  }

  /** The declared type as seen from the owner, boxed: the getter's return type, otherwise the field's type. */
  lazy val propertyType: ResolvedClass = {
    val declared = if (getter == null) field.fieldType else getter.returnType
    TypeResolver.resolve(TypeToken.of(declared.underlying).wrap().getType)
  }

  val isWriteable: Boolean = setter != null

  private def members: Array[Annotated] = Array[Annotated](field, getter, setter).filter(_ != null)

  override lazy val declaredAnnotations: Array[Annotation] = members.flatMap(_.declaredAnnotations)

  override lazy val annotations: Array[Annotation] = members.flatMap(_.annotations)

  override def toString = (if isWriteable then s"write $name" else s"read $name") + s": $propertyType"

}
