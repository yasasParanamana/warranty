package com.oxcentra.warranty.validators.usermgt.page;

import com.oxcentra.warranty.bean.usermgt.page.PageInputBean;
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
public class PageValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return PageInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(PageInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (PageInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("pageCode")) {
                        //validate the null and empty in taskcode
                        String pageCode = ((PageInputBean) o).getPageCode();
                        if (validation.isEmptyFieldValue(pageCode)) {
                            errors.rejectValue(fieldName, MessageVarList.PAGE_MGT_EMPTY_PAGECODE, MessageVarList.PAGE_MGT_EMPTY_PAGECODE);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in description
                        String description = ((PageInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(description)) {
                            errors.rejectValue(fieldName, MessageVarList.PAGE_MGT_EMPTY_DESCRIPTION, MessageVarList.PAGE_MGT_EMPTY_DESCRIPTION);
                        }

                    } else if (fieldName.equals("sortKey")) {
                        //validate the null and empty in status
                        String sortkey = String.valueOf(((PageInputBean) o).getSortKey());
                        if (validation.isEmptyFieldValue(sortkey)) {
                            errors.rejectValue(fieldName, MessageVarList.PAGE_MGT_EMPTY_SORTKEY, MessageVarList.PAGE_MGT_EMPTY_SORTKEY);
                        }

                    } else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((PageInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.PAGE_MGT_EMPTY_STATUS, MessageVarList.PAGE_MGT_EMPTY_STATUS);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, PageInputBean o) {
        return new Field[]{allFields.get("pageCode"), allFields.get("description"), allFields.get("sortKey"), allFields.get("status")};
    }
}
