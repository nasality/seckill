package com.itheida.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {IsMobileValidation.class}
)
public @interface IsMobile {
    //设定值必须填写
    boolean required() default true;

    String message() default "{javax.validation.constraints.IsMobile.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
