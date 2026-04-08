# scala-universe

`scala-universe` is a JVM-based type introspection library providing a structured and resolved view of classes, generics, and annotations.

It wraps low-level reflection APIs in a higher-level model built around:

- fully resolved types
- unified access to fields, methods, constructors, and parameters
- proper generic type resolution
- bean-style property introspection
- annotation-driven property introspection
- classpath scanning with an annotation index

## Why this library?

Java reflection is often:

- low-level
- inconsistent with generics
- difficult to compose
- hard to reason about

`scala-universe` provides a cleaner abstraction layer for runtime introspection on the JVM.

## Features

- Resolve a `java.lang.reflect.Type` into a reusable `ResolvedClass`
- Inspect class hierarchies with generic type resolution
- Find fields, methods, constructors, and parameters in a consistent way
- Access bean properties through getter/setter conventions
- Build property models from annotations
- Scan a package and index discovered annotations

## Installation

Add the dependency to your `build.sbt`:

```scala
libraryDependencies += "com.anjunar" %% "scala-universe" % "1.0.0"
```

## Example

```scala
import com.anjunar.scala.universe.TypeResolver

class User(val id: Long, val name: String)

val clazz = TypeResolver.resolve(classOf[User])
```

## Quick Start

### Resolve a class

```scala
import com.anjunar.scala.universe.TypeResolver

val resolved = TypeResolver.resolve(classOf[String])

println(resolved.name)      // String
println(resolved.fullName)  // java.lang.String
println(resolved.raw)       // class java.lang.String
```

### Inspect fields and methods

```scala
import com.anjunar.scala.universe.TypeResolver

class User(private var id: Long, val name: String) {
  def greeting(prefix: String): String = s"$prefix $name"
}

val userClass = TypeResolver.resolve(classOf[User])

val field = userClass.findField("name")
println(field.name)               // name
println(field.fieldType.fullName) // java.lang.String

val method = userClass.findMethod("greeting", classOf[String])
println(method.name)                  // greeting
println(method.returnType.fullName)   // java.lang.String
println(method.parameters.head.name)  // prefix
```

### Work with constructors and invocation

```scala
import com.anjunar.scala.universe.TypeResolver

class User(val id: Long, val name: String)

val resolved = TypeResolver.resolve(classOf[User])
val constructor = resolved.findDeclaredConstructor(classOf[Long], classOf[String])

val user = constructor.newInstance(Long.box(1L), "Ada").asInstanceOf[User]
println(user.name) // Ada
```

## Generic Type Resolution

One of the main benefits of the library is that inherited generic members are resolved against the concrete subtype.

```scala
import com.anjunar.scala.universe.TypeResolver

abstract class Box[T] {
  def getValue: T
}

class StringBox extends Box[String] {
  override def getValue: String = "hello"
}

val resolved = TypeResolver.resolve(classOf[StringBox])
val method = resolved.findMethod("getValue")

println(method.returnType.raw) // class java.lang.String
```

## Core Idea

Instead of working directly with:

- raw `Class`
- `Type`
- `ParameterizedType`

You work with a clean, unified abstraction layer centered around `ResolvedClass` and the related member models.

## Bean Introspection

`BeanIntrospector` creates a `BeanModel` based on JavaBean-style getters and setters.

```scala
import com.anjunar.scala.universe.introspector.BeanIntrospector

class Person {
  private var firstName: String = "Ada"

  def getFirstName: String = firstName
  def setFirstName(value: String): Unit = firstName = value
}

val beanModel = BeanIntrospector.createWithType(classOf[Person])
val property = beanModel.findProperty("firstName")

val person = new Person
println(property.propertyType.fullName) // java.lang.String
println(property.get(person))           // Ada

property.set(person, "Grace")
println(property.get(person))           // Grace
```

## Annotation Introspection

`AnnotationIntrospector` builds a property model from annotated fields and methods.

Define a runtime annotation:

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Exposed {
}
```

Then use it in your model:

```scala
import com.anjunar.scala.universe.introspector.AnnotationIntrospector

class Account {
  @Exposed
  private var owner: String = "Ada"

  @Exposed
  def getOwner: String = owner
}

val model = AnnotationIntrospector.createWithType(classOf[Account], classOf[Exposed])
val property = model.findProperty("owner")

println(property.name)                  // owner
println(property.propertyType.fullName) // java.lang.String
```

## Companion Lookup

You can resolve the generated companion class and instance for Scala types.

```scala
import com.anjunar.scala.universe.TypeResolver

class Service
object Service

val companionClass = TypeResolver.companionClass(classOf[Service])
val companion = TypeResolver.companionInstance[Service.type](classOf[Service])

println(companionClass.getName) // Service$
println(companion eq Service)   // true
```

## Classpath Scanning

`ClassPathResolver` can scan a package prefix and build an annotation index.

```scala
import com.anjunar.scala.universe.ClassPathResolver

val classes =
  ClassPathResolver.process(
    packagePrefix = "com.example",
    classLoader = Thread.currentThread().getContextClassLoader
  )

println(classes.size)

val services = ClassPathResolver.findAnnotation(classOf[jakarta.inject.Singleton])
println(services.size)
```

## Main Types

- `TypeResolver`: entry point for resolving `Type` values and Scala companions
- `ResolvedClass`: resolved view over a class or generic type
- `ResolvedField`, `ResolvedMethod`, `ResolvedConstructor`, `ResolvedParameter`: member abstractions
- `BeanIntrospector` / `BeanModel`: bean-style property model
- `AnnotationIntrospector` / `AnnotationModel`: annotation-driven property model
- `ClassPathResolver`: package scanning and annotation indexing

## When should you use it?

Use `scala-universe` if:

- you build frameworks or infrastructure
- you need reliable generic type handling
- you want a structured reflection model

## Positioning

- vs Java Reflection: higher-level and easier to reason about
- vs `scala-reflect`: runtime-focused instead of compile-time-focused
- vs frameworks such as Spring: explicit model without hidden magic

## Development

Run the test suite with:

```bash
sbt test
```
