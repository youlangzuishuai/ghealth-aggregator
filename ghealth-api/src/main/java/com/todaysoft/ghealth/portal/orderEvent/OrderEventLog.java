package com.todaysoft.ghealth.portal.orderEvent;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface OrderEventLog
{
    String eventType() default "";

    String handler();
}
