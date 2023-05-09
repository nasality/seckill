package com.itheida.validation;

import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidation implements ConstraintValidator<IsMobile,String> {
    private boolean required = false;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required) {
            return ValidationUtil.isMobile(value);
        }else {
            if (StringUtils.isEmpty(value)) {
                return true;
            }
            else {
                return ValidationUtil.isMobile(value);
            }
        }
    }
}
