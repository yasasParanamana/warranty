package com.epic.rdbsms.validators.customermgt.customerregistration;

import com.epic.rdbsms.bean.customermgt.customersearch.CustomerSearchInputBean;
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
public class CustomerSearchValidator implements Validator {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return CustomerSearchInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(CustomerSearchInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (CustomerSearchInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                   if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((CustomerSearchInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.CUSTOMER_MGT_EMPTY_STATUS, MessageVarList.CUSTOMER_MGT_EMPTY_STATUS);
                        }

                    } else if (fieldName.equals("waiveoffstatus")) {
                        //validate the null and empty in status
                        String sortKey = String.valueOf(((CustomerSearchInputBean) o).getWaiveoffstatus());
                        if (validation.isEmptyFieldValue(sortKey)) {
                            errors.rejectValue(fieldName, MessageVarList.CUSTOMER_MGT_EMPTY_WAIVE_OFF_STATUS, MessageVarList.CUSTOMER_MGT_EMPTY_WAIVE_OFF_STATUS);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, CustomerSearchInputBean o) {
        return new Field[]{
                allFields.get("status"),
                allFields.get("waiveoffstatus")};
    }
}
