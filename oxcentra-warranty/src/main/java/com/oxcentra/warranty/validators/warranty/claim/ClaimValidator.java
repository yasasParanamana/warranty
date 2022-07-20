package com.oxcentra.warranty.validators.warranty.claim;

import com.oxcentra.warranty.bean.usermgt.task.TaskInputBean;
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
public class ClaimValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return TaskInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(TaskInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (TaskInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("taskCode")) {
                        //validate the null and empty in taskCode
                        String userName = ((TaskInputBean) o).getTaskCode();
                        if (validation.isEmptyFieldValue(userName)) {
                            errors.rejectValue(fieldName, MessageVarList.TASK_MGT_EMPTY_TASKCODE, MessageVarList.TASK_MGT_EMPTY_TASKCODE);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in description
                        String description = ((TaskInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(description)) {
                            errors.rejectValue(fieldName, MessageVarList.TASK_MGT_EMPTY_DESCRIPTION, MessageVarList.TASK_MGT_EMPTY_DESCRIPTION);
                        }

                    } else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((TaskInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.TASK_MGT_EMPTY_STATUS, MessageVarList.TASK_MGT_EMPTY_STATUS);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, TaskInputBean o) {
        return new Field[]{allFields.get("taskCode"), allFields.get("description"), allFields.get("status")};
    }
}
