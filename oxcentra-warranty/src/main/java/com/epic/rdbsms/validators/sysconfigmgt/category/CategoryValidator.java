package com.epic.rdbsms.validators.sysconfigmgt.category;

import com.epic.rdbsms.bean.sysconfigmgt.category.CategoryInputBean;
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
public class CategoryValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return CategoryInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(CategoryInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (CategoryInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("categoryCode")) {
                        //validate the null and empty in taskCode
                        String userName = ((CategoryInputBean) o).getCategoryCode();
                        if (validation.isEmptyFieldValue(userName)) {
                            errors.rejectValue(fieldName, MessageVarList.CATEGORY_MGT_EMPTY_CODE, MessageVarList.CATEGORY_MGT_EMPTY_CODE);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in description
                        String description = ((CategoryInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(description)) {
                            errors.rejectValue(fieldName, MessageVarList.CATEGORY_MGT_EMPTY_DESCRIPTION, MessageVarList.CATEGORY_MGT_EMPTY_DESCRIPTION);
                        }

                    } else if (fieldName.equals("bulkEnable")) {
                        //validate the null and empty in description
                        String bulkEnable = ((CategoryInputBean) o).getBulkEnable();
                        if (validation.isEmptyFieldValue(bulkEnable)) {
                            errors.rejectValue(fieldName, MessageVarList.CATEGORY_MGT_EMPTY_BULKENABLE, MessageVarList.CATEGORY_MGT_EMPTY_BULKENABLE);
                        }

                    } else if (fieldName.equals("unsubscribe")) {
                        //validate the null and empty in description
                        String unsubscribe = ((CategoryInputBean) o).getUnsubscribe();
                        if (validation.isEmptyFieldValue(unsubscribe)) {
                            errors.rejectValue(fieldName, MessageVarList.CATEGORY_MGT_EMPTY_UNSUBSCRIBE, MessageVarList.CATEGORY_MGT_EMPTY_UNSUBSCRIBE);
                        }

                    } else if (fieldName.equals("ackwait")) {
                        //validate the null and empty in description
                        String ackwait = ((CategoryInputBean) o).getAckwait();
                        if (validation.isEmptyFieldValue(ackwait)) {
                            errors.rejectValue(fieldName, MessageVarList.CATEGORY_MGT_EMPTY_ACKWAIT, MessageVarList.CATEGORY_MGT_EMPTY_ACKWAIT);
                        }

                    } else if (fieldName.equals("ttlqueue")) {
                        //validate the null and empty in description
                        String ttlqueue = String.valueOf(((CategoryInputBean) o).getTtlqueue());
                        if (ttlqueue.equals("null") || ttlqueue.equals("0")) {
                            errors.rejectValue(fieldName, MessageVarList.CATEGORY_MGT_EMPTY_TTLQUEUE, MessageVarList.CATEGORY_MGT_EMPTY_TTLQUEUE);
                        }

                    } else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((CategoryInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.CATEGORY_MGT_EMPTY_STATUS, MessageVarList.CATEGORY_MGT_EMPTY_STATUS);
                        }
                    } else if (fieldName.equals("priority")) {
                        //validate the null and empty in status
                        String priority = ((CategoryInputBean) o).getPriority();
                        if (validation.isEmptyFieldValue(priority)) {
                            errors.rejectValue(fieldName, MessageVarList.CATEGORY_MGT_EMPTY_PRIORITY, MessageVarList.CATEGORY_MGT_EMPTY_PRIORITY);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, CategoryInputBean o) {
        return new Field[]{
                allFields.get("categoryCode"),
                allFields.get("description"),
                allFields.get("bulkEnable"),
                allFields.get("status"),
                allFields.get("priority"),
                allFields.get("unsubscribe"),
                allFields.get("ackwait"),
                allFields.get("ttlqueue"),
        };
    }
}
