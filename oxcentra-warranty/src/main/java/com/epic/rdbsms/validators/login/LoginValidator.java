package com.epic.rdbsms.validators.login;

import com.epic.rdbsms.bean.login.LoginBean;
import com.epic.rdbsms.util.validation.Validation;
import com.epic.rdbsms.util.varlist.CommonVarList;
import com.epic.rdbsms.util.varlist.MessageVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
public class LoginValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Autowired
    MessageSource messageSource;

    @Override
    public boolean supports(Class<?> aClass) {
        return LoginBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(LoginBean.class)) {
                Locale locale = Locale.getDefault();
                Field[] requiredFields = this.getRequiredFields(allFields, (LoginBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("username")) {
                        //validate the null and empty in username
                        String userName = ((LoginBean) o).getUsername();
                        if (validation.isEmptyFieldValue(userName)) {
                            errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.LOGIN_USERNAME_EMPTY, null, locale), messageSource.getMessage(MessageVarList.LOGIN_USERNAME_EMPTY, null, locale));
                        }
                        //validate the special characters in username
                        if (validation.checkSpecialCharacters(userName)) {
                            errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.LOGIN_USERNAME_INVALID, null, locale), messageSource.getMessage(MessageVarList.LOGIN_USERNAME_INVALID, null, locale));
                        }
                    } else if (fieldName.equals("username")) {
                        //validate the null and empty in password
                        String password = ((LoginBean) o).getPassword();
                        if (validation.isEmptyFieldValue(password)) {
                            errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.LOGIN_PASSWORD_EMPTY, null, locale), messageSource.getMessage(MessageVarList.LOGIN_PASSWORD_EMPTY, null, locale));
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, LoginBean o) {
        return new Field[]{allFields.get("username"), allFields.get("password")};
    }
}
