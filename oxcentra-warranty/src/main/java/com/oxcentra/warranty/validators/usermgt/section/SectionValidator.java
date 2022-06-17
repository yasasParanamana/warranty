package com.oxcentra.warranty.validators.usermgt.section;


import com.oxcentra.warranty.bean.usermgt.section.SectionInputBean;
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
public class SectionValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return SectionInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(SectionInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (SectionInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("sectionCode")) {
                        //validate the null and empty in sectionCode
                        String sectionCode = ((SectionInputBean) o).getSectionCode();
                        if (validation.isEmptyFieldValue(sectionCode)) {
                            errors.rejectValue(fieldName, MessageVarList.SECTION_MGT_EMPTY_SECTIONCODE, MessageVarList.SECTION_MGT_EMPTY_SECTIONCODE);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in description
                        String description = ((SectionInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(description)) {
                            errors.rejectValue(fieldName, MessageVarList.SECTION_MGT_EMPTY_DESCRIPTION, MessageVarList.SECTION_MGT_EMPTY_DESCRIPTION);
                        }

                    } else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((SectionInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.SECTION_MGT_EMPTY_STATUS, MessageVarList.SECTION_MGT_EMPTY_STATUS);
                        }

                    } else if (fieldName.equals("sortKey")) {
                        //validate the null and empty in status
                        String sortKey = String.valueOf(((SectionInputBean) o).getSortKey());
                        if (validation.isEmptyFieldValue(sortKey)) {
                            errors.rejectValue(fieldName, MessageVarList.SECTION_MGT_EMPTY_SORTKEY, MessageVarList.SECTION_MGT_EMPTY_SORTKEY);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, SectionInputBean o) {
        return new Field[]{
                allFields.get("sectionCode"),
                allFields.get("description"),
                allFields.get("status"),
                allFields.get("sortKey")};
    }
}
