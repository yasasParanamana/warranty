package com.oxcentra.warranty.validators.warranty.supplier;


import com.oxcentra.warranty.bean.warranty.supplier.SupplierInputBean;
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
public class SupplierValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return SupplierInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(SupplierInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (SupplierInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("supplierName")) {
                        //validate the null and empty in sectionCode
                        String sectionCode = ((SupplierInputBean) o).getSupplierName();
                        if (validation.isEmptyFieldValue(sectionCode)) {
                            errors.rejectValue(fieldName, MessageVarList.SECTION_MGT_EMPTY_SECTIONCODE, MessageVarList.SECTION_MGT_EMPTY_SECTIONCODE);
                        }

                    }  else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((SupplierInputBean) o).getStatus();
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, SupplierInputBean o) {
        return new Field[]{
                allFields.get("supplierName"),
                allFields.get("status")
        };
    }
}
