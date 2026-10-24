package com.anjunar.scala.universe

import com.anjunar.scala.universe.annotations.{Annotated, ResolvedAnnotation}
import com.google.common.reflect.ClassPath
import jakarta.enterprise.event.Observes
import jakarta.enterprise.inject.spi.{AfterDeploymentValidation, Extension, ProcessAnnotatedType}

import java.lang.annotation.Annotation
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

/** As a CDI extension, indexes the annotations of every bean class once the deployment is validated. */
class ClassPathResolver extends Extension {

  val collectedClasses: ListBuffer[ResolvedClass] = ListBuffer.empty

  def collectAllClasses(@Observes pat: ProcessAnnotatedType[?]): Unit = {
    val clazz: Class[?] = pat.getAnnotatedType.getJavaClass
    collectedClasses.addOne(TypeResolver.resolve(clazz))
  }

  def afterDeployment(@Observes adv: AfterDeploymentValidation): Unit = {
    ClassPathResolver.annotationIndex(collectedClasses.toSet)
  }

}

object ClassPathResolver {

  private val annotations = new ConcurrentHashMap[Class[? <: Annotation], mutable.Set[ResolvedAnnotation[?]]]()

  /** Every indexed occurrence of an annotation. An annotation that was never indexed is a NoSuchElementException. */
  def findAnnotation[A <: Annotation](clazz: Class[A]): mutable.Set[ResolvedAnnotation[A]] = {
    val found = annotations.get(clazz)
    if (found == null) throw new NoSuchElementException(s"Annotation ${clazz.getName} was not indexed")
    found.asInstanceOf[mutable.Set[ResolvedAnnotation[A]]]
  }

  def process(packagePrefix: String, classLoader: ClassLoader): Set[ResolvedClass] = {
    val classPath = ClassPath.from(classLoader)

    val classes = classPath.getAllClasses.asScala
      .filter(info => info.getPackageName.startsWith(packagePrefix))
      .map(info => TypeResolver.resolve(info.load()))
      .toSet

    annotationIndex(classes)

    classes
  }

  private def annotationIndex(classes: Set[ResolvedClass]): Unit =
    classes.foreach { clazz =>
      clazz.declaredAnnotations.foreach(index(_, clazz))

      clazz.declaredConstructors.foreach { constructor =>
        constructor.declaredAnnotations.foreach(index(_, constructor))
        constructor.parameters.foreach(parameter => parameter.declaredAnnotations.foreach(index(_, parameter)))
      }

      clazz.declaredFields.foreach(field => field.declaredAnnotations.foreach(index(_, field)))

      clazz.declaredMethods.foreach { method =>
        method.declaredAnnotations.foreach(index(_, method))
        method.parameters.foreach(parameter => parameter.declaredAnnotations.foreach(index(_, parameter)))
      }
    }

  private def index(annotation: Annotation, target: Annotated): Unit =
    annotations
      .computeIfAbsent(annotation.annotationType(), _ => ConcurrentHashMap.newKeySet[ResolvedAnnotation[?]]().asScala)
      .addOne(ResolvedAnnotation(annotation, target))

}
