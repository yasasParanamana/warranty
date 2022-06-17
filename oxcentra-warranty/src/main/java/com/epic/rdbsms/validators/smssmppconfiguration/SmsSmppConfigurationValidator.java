package com.epic.rdbsms.validators.smssmppconfiguration;

import com.epic.rdbsms.bean.sysconfigmgt.smssmppconfiguration.SmsSmppConfigurationInputBean;
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
public class SmsSmppConfigurationValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;


    @Override
    public boolean supports(Class<?> aClass) {
        return SmsSmppConfigurationInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(SmsSmppConfigurationInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (SmsSmppConfigurationInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("smppCode")) {
                        //validate the null and empty in sectionCode
                        String smppCode = ((SmsSmppConfigurationInputBean) o).getSmppCode();
                        if (validation.isEmptyFieldValue(smppCode)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_SMPPCODE, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_SMPPCODE);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in description
                        String description = ((SmsSmppConfigurationInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(description)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_DESCRIPTION, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_DESCRIPTION);
                        }

                    } else if (fieldName.equals("status")) {
                        //validate the null and empty in status
                        String status = ((SmsSmppConfigurationInputBean) o).getStatus();
                        if (validation.isEmptyFieldValue(status)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_STATUS, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_STATUS);
                        }

                    } else if (fieldName.equals("maxTps")) {
                        //validate the null and empty in status
                        String maxTps = String.valueOf(((SmsSmppConfigurationInputBean) o).getMaxTps());
                        if (validation.isEmptyFieldValue(maxTps)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_MAXTPS, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_MAXTPS);
                        }

                    } else if (fieldName.equals("primaryIp")) {
                        //validate the null and empty in status
                        String primaryIp = ((SmsSmppConfigurationInputBean) o).getPrimaryIp();
                        if (validation.isEmptyFieldValue(primaryIp)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_PRIMARYIP, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_PRIMARYIP);
                        }

                    } else if (fieldName.equals("secondaryIp")) {
                        //validate the null and empty in status
                        String secondaryIp = ((SmsSmppConfigurationInputBean) o).getSecondaryIp();
                        if (validation.isEmptyFieldValue(secondaryIp)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_SECONDARYIP, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_SECONDARYIP);
                        }

                    } else if (fieldName.equals("systemId")) {
                        //validate the null and empty in status
                        String systemId = ((SmsSmppConfigurationInputBean) o).getSystemId();
                        if (validation.isEmptyFieldValue(systemId)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_SYSTEMID, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_SYSTEMID);
                        }

                    } else if (fieldName.equals("password")) {
                        //validate the null and empty in status
                        String password = ((SmsSmppConfigurationInputBean) o).getPassword();
                        if (validation.isEmptyFieldValue(password)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_PASSWORD, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_PASSWORD);
                        }

                    } else if (fieldName.equals("bindPort")) {
                        //validate the null and empty in status
                        String bindPort = String.valueOf(((SmsSmppConfigurationInputBean) o).getBindPort());
                        if (validation.isEmptyFieldValue(bindPort)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_BINDPORT, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_BINDPORT);
                        }

                    } else if (fieldName.equals("bindMode")) {
                        //validate the null and empty in status
                        String bindMode = ((SmsSmppConfigurationInputBean) o).getBindMode();
                        if (validation.isEmptyFieldValue(bindMode)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_BINDMODE, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_BINDMODE);
                        }

                    } else if (fieldName.equals("mtPort")) {
                        //validate the null and empty in status
                        String mtPort = ((SmsSmppConfigurationInputBean) o).getMtPort();
                        if (validation.isEmptyFieldValue(mtPort)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_MTPORT, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_MTPORT);
                        }

                    } else if (fieldName.equals("moPort")) {
                        //validate the null and empty in status
                        String moPort = ((SmsSmppConfigurationInputBean) o).getMoPort();
                        if (validation.isEmptyFieldValue(moPort)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_MOPORT, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_MOPORT);
                        }

                    } else if (fieldName.equals("maxBulkTps")) {
                        //validate the null and empty in status
                        String maxBulkTps = String.valueOf(((SmsSmppConfigurationInputBean) o).getMaxBulkTps());
                        if (validation.isEmptyFieldValue(maxBulkTps)) {
                            errors.rejectValue(fieldName, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_MAXBULKTPS, MessageVarList.SMPP_CONFIGURATION_MGT_EMPTY_MAXBULKTPS);
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, SmsSmppConfigurationInputBean o) {
        return new Field[]{
                allFields.get("smppCode"),
                allFields.get("description"),
                allFields.get("status"),
                allFields.get("maxTps"),
                allFields.get("primaryIp"),
                allFields.get("secondaryIp"),
                allFields.get("systemId"),
                allFields.get("password"),
                allFields.get("bindPort"),
                allFields.get("bindMode"),
                allFields.get("mtPort"),
                allFields.get("moPort"),
                allFields.get("maxBulkTps")
        };
    }
}
