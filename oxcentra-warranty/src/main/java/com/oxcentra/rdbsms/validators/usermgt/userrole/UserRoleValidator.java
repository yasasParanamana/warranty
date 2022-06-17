package com.oxcentra.rdbsms.validators.usermgt.userrole;

import com.oxcentra.rdbsms.bean.usermgt.userrole.UserRoleInputBean;
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
public class UserRoleValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return UserRoleInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(UserRoleInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (UserRoleInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("userroleCode")) {
                        //validate the null and empty in taskcode
                        String userroleCode = ((UserRoleInputBean) o).getUserroleCode();
                        if (validation.isEmptyFieldValue(userroleCode)) {
                            errors.rejectValue(fieldName, MessageVarList.USERROLE_MGT_EMPTY_USERROLECODE, MessageVarList.USERROLE_MGT_EMPTY_USERROLECODE);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in description
                        String description = ((UserRoleInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(description)) {
                            errors.rejectValue(fieldName, MessageVarList.USERROLE_MGT_EMPTY_DESCRIPTION, MessageVarList.USERROLE_MGT_EMPTY_DESCRIPTION);
                        }

                    } else if (fieldName.equals("userroleType")) {
                        //validate the null and empty in userrole type
                        String userroleType = ((UserRoleInputBean) o).getUserroleType();
                        if (validation.isEmptyFieldValue(userroleType)) {
                            errors.rejectValue(fieldName, MessageVarList.USERROLE_MGT_EMPTY_USERROLETYPE, MessageVarList.USERROLE_MGT_EMPTY_USERROLETYPE);
                        }

                    } else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((UserRoleInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.USERROLE_MGT_EMPTY_STATUS, MessageVarList.USERROLE_MGT_EMPTY_STATUS);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, UserRoleInputBean o) {
        return new Field[]{
                allFields.get("userroleCode"),
                allFields.get("description"),
                allFields.get("userroleType"),
                allFields.get("status")};
    }
}
