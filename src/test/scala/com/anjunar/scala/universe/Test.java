package com.anjunar.scala.universe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.ANNOTATION_TYPE, 
        ElementType.CONSTRUCTOR, 
        ElementType.FIELD, 
        ElementType.TYPE, 
        ElementType.METHOD, 
        ElementType.LOCAL_VARIABLE, 
        ElementType.PARAMETER
})
public @interface Test {
    
    String value() default  "test";
    
}
