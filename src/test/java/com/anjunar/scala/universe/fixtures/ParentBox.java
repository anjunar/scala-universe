package com.anjunar.scala.universe.fixtures;

public class ParentBox<T> {

    @Marker
    private T value;

    @Marker
    public T getValue() {
        return value;
    }

    @Marker
    public void setValue(@Marker T value) {
        this.value = value;
    }
}
