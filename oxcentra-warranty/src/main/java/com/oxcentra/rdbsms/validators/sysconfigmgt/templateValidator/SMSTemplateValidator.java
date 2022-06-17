package com.oxcentra.rdbsms.validators.sysconfigmgt.templateValidator;

import com.oxcentra.rdbsms.bean.sysconfigmgt.templatemgt.SMSTemplateInputBean;
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
public class SMSTemplateValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return SMSTemplateInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(SMSTemplateInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (SMSTemplateInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("code")) {
                        //validate the null and empty in taskCode
                        String userName = ((SMSTemplateInputBean) o).getCode();
                        if (validation.isEmptyFieldValue(userName)) {
                            errors.rejectValue(fieldName, MessageVarList.TEMPLATE_MGT_EMPTY_CODE, MessageVarList.TEMPLATE_MGT_EMPTY_CODE);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in taskCode
                        String userName = ((SMSTemplateInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(userName)) {
                            errors.rejectValue(fieldName, MessageVarList.TEMPLATE_MGT_EMPTY_DESCRIPTION, MessageVarList.TEMPLATE_MGT_EMPTY_DESCRIPTION);
                        }

                    } else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((SMSTemplateInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.TEMPLATE_MGT_EMPTY_STATUS, MessageVarList.TEMPLATE_MGT_EMPTY_STATUS);
                        }
                    } else if (fieldName.equals("messageformat")) {
                        String field1 = ((SMSTemplateInputBean) o).getField1();
                        String field2 = ((SMSTemplateInputBean) o).getField2();
                        String field3 = ((SMSTemplateInputBean) o).getField3();
                        String field4 = ((SMSTemplateInputBean) o).getField4();
                        String field5 = ((SMSTemplateInputBean) o).getField5();
                        String field6 = ((SMSTemplateInputBean) o).getField6();
                        String field7 = ((SMSTemplateInputBean) o).getField7();

                        if (validation.isEmptyFieldValue(field1) && validation.isEmptyFieldValue(field2) && validation.isEmptyFieldValue(field3) && validation.isEmptyFieldValue(field4) && validation.isEmptyFieldValue(field5) && validation.isEmptyFieldValue(field6) && validation.isEmptyFieldValue(field7)) {
                            errors.rejectValue(fieldName, MessageVarList.TEMPLATE_MGT_EMPTY_MESSAGE_FORMAT, MessageVarList.TEMPLATE_MGT_EMPTY_MESSAGE_FORMAT);
                        } else if (makeSmsFormat(((SMSTemplateInputBean) o)).length() > 250) {
                            errors.rejectValue(fieldName, MessageVarList.SMS_TEMPLATE_INVALID_LENGTH_SMS_FORMAT, MessageVarList.SMS_TEMPLATE_INVALID_LENGTH_SMS_FORMAT);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, SMSTemplateInputBean o) {
        return new Field[]{
                allFields.get("code"),
                allFields.get("description"),
                allFields.get("status"),
                allFields.get("messageformat"),
        };
    }

    private String makeSmsFormat(SMSTemplateInputBean inputBean) throws Exception {
        String smsFormat = "";
        try {
            smsFormat = inputBean.getDes1() + "|" + inputBean.getField1() + "|" + inputBean.getDes2() + "|" + inputBean.getField2()
                    + "|" + inputBean.getDes3() + "|" + inputBean.getField3() + "|" + inputBean.getDes4() + "|" + inputBean.getField4()
                    + "|" + inputBean.getDes5() + "|" + inputBean.getField5() + "|" + inputBean.getDes6() + "|" + inputBean.getField6()
                    + "|" + inputBean.getDes7() + "|" + inputBean.getField7();
        } catch (Exception e) {
            throw e;
        }

        return smsFormat;
    }
}
