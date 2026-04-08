package com.anjunar.scala.universe.fixtures;

public class ShadowChild extends ShadowParent {

    public Integer count = 2;

    @Override
    public Integer getCount() {
        return count;
    }
}
