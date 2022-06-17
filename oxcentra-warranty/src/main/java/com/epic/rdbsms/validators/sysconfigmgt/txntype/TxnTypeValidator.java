package com.epic.rdbsms.validators.sysconfigmgt.txntype;

import com.epic.rdbsms.bean.sysconfigmgt.txntype.TxnTypeInputBean;
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

/**
 * @author Namila Withanage on 11/15/2021
 */
@Component
public class TxnTypeValidator implements Validator {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return TxnTypeInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(TxnTypeInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (TxnTypeInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("txntype")) {
                        //validate the null and empty in taskCode
                        String userName = ((TxnTypeInputBean) o).getTxntype();
                        if (validation.isEmptyFieldValue(userName)) {
                            errors.rejectValue(fieldName, MessageVarList.TXN_TYPE_MGT_EMPTY_CODE, MessageVarList.TXN_TYPE_MGT_EMPTY_CODE);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in description
                        String description = ((TxnTypeInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(description)) {
                            errors.rejectValue(fieldName, MessageVarList.TXN_TYPE_MGT_EMPTY_DESCRIPTION, MessageVarList.TXN_TYPE_MGT_EMPTY_DESCRIPTION);
                        }

                    } else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((TxnTypeInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.TXN_TYPE_MGT_EMPTY_STATUS, MessageVarList.TXN_TYPE_MGT_EMPTY_STATUS);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, TxnTypeInputBean o) {
        return new Field[]{allFields.get("txntype"), allFields.get("description"), allFields.get("status")};
    }

}
