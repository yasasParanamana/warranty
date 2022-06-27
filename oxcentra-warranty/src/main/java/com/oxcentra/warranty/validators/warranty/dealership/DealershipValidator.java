package com.oxcentra.warranty.validators.warranty.dealership;


import com.oxcentra.warranty.bean.warranty.dealership.DealershipInputBean;
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
public class DealershipValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return DealershipInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(DealershipInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (DealershipInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("dealershipName")) {
                        //validate the null and empty in sectionCode
                        String sectionCode = ((DealershipInputBean) o).getDealershipName();
                        if (validation.isEmptyFieldValue(sectionCode)) {
                            errors.rejectValue(fieldName, MessageVarList.SECTION_MGT_EMPTY_SECTIONCODE, MessageVarList.SECTION_MGT_EMPTY_SECTIONCODE);
                        }

                    }  else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((DealershipInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.SECTION_MGT_EMPTY_STATUS, MessageVarList.SECTION_MGT_EMPTY_STATUS);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, DealershipInputBean o) {
        return new Field[]{
                allFields.get("dealershipName"),
                allFields.get("status")
        };
    }
}
