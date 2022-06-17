package com.oxcentra.rdbsms.validators.sysconfigmgt.userparam;

import com.oxcentra.rdbsms.bean.sysconfigmgt.userparam.UserParamInputBean;
import com.oxcentra.rdbsms.util.validation.Validation;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
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
public class UserParamValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return UserParamInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(UserParamInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (UserParamInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("paramCode")) {
                        //validate the null and empty in taskCode
                        String code = ((UserParamInputBean) o).getParamCode();
                        if (validation.isEmptyFieldValue(code)) {
                            errors.rejectValue(fieldName, MessageVarList.USERPARAM_MGT_EMPTY_PARAMCODE, MessageVarList.USERPARAM_MGT_EMPTY_PARAMCODE);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in description
                        String description = ((UserParamInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(description)) {
                            errors.rejectValue(fieldName, MessageVarList.USERPARAM_MGT_EMPTY_DESCRIPTION, MessageVarList.USERPARAM_MGT_EMPTY_DESCRIPTION);
                        }

                    } else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((UserParamInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.USERPARAM_MGT_EMPTY_STATUS, MessageVarList.USERPARAM_MGT_EMPTY_STATUS);
                        }
                    } else if (fieldName.equals("category")) {
                        //validate the null and empty in category
                        String category = ((UserParamInputBean) o).getCategory();
                        if (validation.isEmptyFieldValue(category)) {
                            errors.rejectValue(fieldName, MessageVarList.USERPARAM_MGT_EMPTY_CATEGORY, MessageVarList.USERPARAM_MGT_EMPTY_CATEGORY);
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


    private Field[] getRequiredFields(SortedMap<String, Field> allFields, UserParamInputBean o) {
        return new Field[]{allFields.get("paramCode"), allFields.get("description"), allFields.get("status"), allFields.get("category")};
    }
}
