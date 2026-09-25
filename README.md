# Scala Universe

A structured view of classes at runtime. Types are resolved once, generics are bound to the concrete subclass, and
fields, methods and annotations share one model.

| Version | Platform | Scala | License |
| --- | --- | --- | --- |
| 1.0.4 | JVM | 3.3 | MIT |

Documentation: [English](https://docs.anjunar.com/en/scala-universe) · [Deutsch](https://docs.anjunar.com/de/scala-universe)
Website: [English](https://anjunar.com/en/scala-universe) · [Deutsch](https://anjunar.com/de/scala-universe)

## Installation

One artifact for the JVM. It brings Guava's `TypeToken` for generic resolution and the CDI API for the optional
class path extension.

```scala
libraryDependencies += "com.anjunar" %% "scala-universe" % "1.0.4"
```

## First example

The field is declared as `List[E]` in the base class. Resolved through the subclass, it is a `List[User]` – the
generic type plain Java reflection forgets.

```scala
import com.anjunar.scala.universe.TypeResolver

class User
abstract class Repository[E] {
  var items: java.util.List[E] = new java.util.ArrayList[E]()
}
class UserRepository extends Repository[User]

val repository = TypeResolver.resolve(classOf[UserRepository])
val items = repository.findField("items").fieldType

println(items.name)                  // List
println(items.typeArguments(0).name) // User
```

## The principle

**01 / Resolve – One model per type.** `TypeResolver` turns any `java.lang.reflect.Type` into a cached
`ResolvedClass`: raw class, type arguments and hierarchy.

**02 / Bind – Generics seen from the subclass.** Field types, return types and parameter types of inherited members
are resolved against the type you started from.

**03 / Describe – Properties instead of members.** Bean and annotation introspectors join field, getter and setter
into one property with the annotations of all three.

[JSON Mapper](https://github.com/anjunar/json-mapper) uses Scala Universe as its type layer: it resolves every class
through `TypeResolver`, reads `@JsonbProperty` members through `AnnotationIntrospector` and finds schemas through
`companionInstance`.

## Contents

**Types**
- [Resolving types](https://docs.anjunar.com/en/scala-universe/resolving) – `TypeResolver` and `ResolvedClass`: one model for every kind of `Type`
- [Hierarchy and subtypes](https://docs.anjunar.com/en/scala-universe/hierarchy) – superclasses and interfaces, resolved, and subtype checks with generics
- [Generics](https://docs.anjunar.com/en/scala-universe/generics) – inherited members typed by the subclass, not by the declaration
- [Caching and identity](https://docs.anjunar.com/en/scala-universe/caching) – one instance per type, equality by type, and what that means for threads

**Members**
- [Fields](https://docs.anjunar.com/en/scala-universe/fields) – declared and inherited fields, hiding, types and access
- [Methods](https://docs.anjunar.com/en/scala-universe/methods) – overrides, bridges, return types and invocation
- [Constructors and parameters](https://docs.anjunar.com/en/scala-universe/constructors) – creating instances, and parameters with names, types and annotations
- [Annotations](https://docs.anjunar.com/en/scala-universe/annotations) – where annotations are looked up, and which ones are inherited

**Introspection**
- [Bean properties](https://docs.anjunar.com/en/scala-universe/beans) – properties from getters and setters, the JavaBean way
- [Annotated properties](https://docs.anjunar.com/en/scala-universe/annotated-properties) – properties chosen by an annotation, Scala accessors included
- [Companions and class path](https://docs.anjunar.com/en/scala-universe/discovery) – Scala companion objects, and an annotation index over a package

**In practice**
- [A small mapper](https://docs.anjunar.com/en/scala-universe/mapper) – objects to maps and back, in twenty lines on top of the introspector
- [Commands by annotation](https://docs.anjunar.com/en/scala-universe/commands) – find annotated methods, describe their parameters and call them by name

**Reference**
- [API](https://docs.anjunar.com/en/scala-universe/api) – every type and member of Scala Universe at a glance

## Limits

- JVM only. For compile-time metadata that also works on Scala.js, use
  [Scala Reflect](https://github.com/anjunar/scala-reflect).
- Generics are only as concrete as the type you start from. Starting from `Repository[?]` gives a type variable,
  whose raw class says nothing about its values; start from a concrete class such as `UserRepository`.
- `constructors` includes the superclasses' constructors, which cannot create the subclass. Use
  `declaredConstructors` or `findConstructor` to create instances.
- In 1.0.4 the type, companion and introspector caches are plain hash maps without synchronization. In a server,
  resolve the types you map during startup, before requests arrive. Reading cached types is safe as long as nothing
  writes at the same time.
- `ClassPathResolver.findAnnotation` throws `NoSuchElementException` for an annotation that was never indexed. Run
  `process`, or let the CDI extension run, before looking anything up.

## Development

Requires a JDK and sbt 2. Tests use MUnit.

```bash
sbt --server "Test/testOnly *"
```

### Releasing

Set `ThisBuild / version` in `build.sbt`, then sign, bundle and upload to the Sonatype Central Portal in one step.
Without a version argument the scripts read the one in `build.sbt`, and they wait until Maven Central has published
the release.

```powershell
.\scripts\publish-central.ps1
```

```bash
scripts/publish-central.sh
```

Credentials come from `SONATYPE_CENTRAL_USERNAME` and `SONATYPE_CENTRAL_PASSWORD`, or from the lines `user=` and
`password=` in `~/.sbt/sonatype_central_credentials`. `-PublishingType USER_MANAGED` (`PUBLISHING_TYPE=USER_MANAGED`)
stops after validation so the release is published by hand in the portal; `-SkipPublishSigned`
(`SKIP_PUBLISH_SIGNED=1`) uploads an existing staging directory again.

## License

Scala Universe is available under the [MIT License](LICENSE).
