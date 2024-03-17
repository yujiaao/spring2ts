package com.sdadas.spring2ts.annotations;

import java.lang.annotation.*;

/**
 * @author Sławomir Dadas
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface SharedModel {
    /**
     * value as group name, with same name will be in same file,
     * default to model.ts
     */
    String value() default "";
}
