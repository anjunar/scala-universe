package com.anjunar.scala.universe.fixtures;

import java.util.List;

/** A legal raw override retains its concrete generic binding only in the parent declaration. */
public class ErasedGenericFixture extends GenericCollectionBase<String> {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List transform(List values) { return values; }
}

class GenericCollectionBase<T> {
    public List<T> transform(List<T> values) { return values; }
}