package com.oxcentra.warranty.validators.sysconfigmgt.passwordparam;

import com.oxcentra.warranty.mapping.sectionmgt.PasswordParam;
import com.oxcentra.warranty.util.validation.Validation;
import com.oxcentra.warranty.util.varlist.CommonVarList;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
public class PasswordParamValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordParam.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(PasswordParam.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (PasswordParam) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("passwordparam")) {
                        //validate the null and empty in passwordparam
                        String passwordparam = ((PasswordParam) o).getPasswordparam();
                        if (validation.isEmptyFieldValue(passwordparam)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_PARAM_MGT_EMPTY_PASSWORDPARAM, MessageVarList.PASSWORD_PARAM_MGT_EMPTY_PASSWORDPARAM);
                        }

                    } else if (fieldName.equals("userroletype")) {
                        //validate the null and empty in userroletype
                        String userroletype = ((PasswordParam) o).getUserroletype();
                        if (validation.isEmptyFieldValue(userroletype)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_PARAM_MGT_EMPTY_USERROLETYPE, MessageVarList.PASSWORD_PARAM_MGT_EMPTY_USERROLETYPE);
                        }

                    } else if (fieldName.equals("value")) {
                        //validate the null and empty in value
                        String value = ((PasswordParam) o).getValue();
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_PARAM_MGT_EMPTY_VALUE, MessageVarList.PASSWORD_PARAM_MGT_EMPTY_VALUE);
                        }
                    }
                }
            } else {
                errors.reject(commonVarList.COMMON_VALIDATION_INVALID_BEANTYPE);
            }
        } catch (Exception ex) {
            logger.error("Exception : ", ex);
            errors.reject(commonVarList.COMMON_VALIDATION_FAIL_CODE);
        }
    }

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, PasswordParam o) {
        return new Field[]{allFields.get("passwordparam"), allFields.get("userroletype"), allFields.get("value")};
    }
}
