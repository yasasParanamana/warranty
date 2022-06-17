package com.epic.rdbsms.validators.sysconfigmgt.department;

import com.epic.rdbsms.bean.sysconfigmgt.department.DepartmentInputBean;
import com.epic.rdbsms.util.validation.Validation;
import com.epic.rdbsms.util.varlist.CommonVarList;
import com.epic.rdbsms.util.varlist.MessageVarList;
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
public class DepartmentValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return DepartmentInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(DepartmentInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (DepartmentInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("code")) {
                        //validate the null and empty in taskCode
                        String userName = ((DepartmentInputBean) o).getCode();
                        if (validation.isEmptyFieldValue(userName)) {
                            errors.rejectValue(fieldName, MessageVarList.DEPARTMENT_MGT_EMPTY_CODE, MessageVarList.DEPARTMENT_MGT_EMPTY_CODE);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in description
                        String description = ((DepartmentInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(description)) {
                            errors.rejectValue(fieldName, MessageVarList.DEPARTMENT_MGT_EMPTY_DESCRIPTION, MessageVarList.DEPARTMENT_MGT_EMPTY_DESCRIPTION);
                        }

                    } else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((DepartmentInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.DEPARTMENT_MGT_EMPTY_STATUS, MessageVarList.DEPARTMENT_MGT_EMPTY_STATUS);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, DepartmentInputBean o) {
        return new Field[]{allFields.get("code"), allFields.get("description"), allFields.get("status")};
    }
}
