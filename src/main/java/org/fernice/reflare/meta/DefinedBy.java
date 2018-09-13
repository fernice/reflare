package org.fernice.reflare.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface DefinedBy {

    Api value();

    enum Api {

        CSS,

        CASCADE,

        LOOK_AND_FEEL,

        INTERNAL,
    }
}
