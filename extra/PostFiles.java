package com.wang.okhttpparamsget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * deprecated use {@link PostFile}.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
@Deprecated
public @interface PostFiles {

}