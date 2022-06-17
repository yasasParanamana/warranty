package com.oxcentra.rdbsms.validators.alerttypetelcomgt.categorytelco;

import com.oxcentra.rdbsms.bean.alerttypetelcomgt.categorytelco.CategoryTelcoInputBean;
import com.oxcentra.rdbsms.util.validation.Validation;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class CategoryTelcoValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return CategoryTelcoInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {

            ValidationUtils.rejectIfEmpty(errors, "category", MessageVarList.CATEGORY_TELCO_EMPTY_CATEGORY, "Category can not be empty.");
            ValidationUtils.rejectIfEmpty(errors, "telco", MessageVarList.CATEGORY_TELCO_EMPTY_TELCO, "Telco can not be empty.");
            ValidationUtils.rejectIfEmpty(errors, "mtPort", MessageVarList.CATEGORY_TELCO_EMPTY_MT_PORT, "Mt Port can not be empty.");
            ValidationUtils.rejectIfEmpty(errors, "status", MessageVarList.CATEGORY_TELCO_EMPTY_STATUS, "Status can not be empty.");

            if (o.getClass().equals(CategoryTelcoInputBean.class)) {

            } else {
                errors.reject(commonVarList.COMMON_VALIDATION_INVALID_BEANTYPE);
            }
        } catch (Exception ex) {
            logger.error("Exception : ", ex);
            errors.reject(commonVarList.COMMON_VALIDATION_FAIL_CODE);
        }
    }
}
