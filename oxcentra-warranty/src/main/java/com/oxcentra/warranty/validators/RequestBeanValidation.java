package com.oxcentra.warranty.validators;

import org.springframework.validation.BindingResult;

@FunctionalInterface
public interface RequestBeanValidation<T> {

    /**
     * @Author shalika_w
     * @CreatedTime 2021-01-11 05:34:50 PM
     * @Version V1.00
     * @MethodName validateRequestBean
     * @MethodParams [t]
     * @MethodDescription - validate request bean
     */
    BindingResult validateRequestBean(T t);
}
