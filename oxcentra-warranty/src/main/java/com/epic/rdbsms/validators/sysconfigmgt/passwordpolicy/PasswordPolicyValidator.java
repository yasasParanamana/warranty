package com.epic.rdbsms.validators.sysconfigmgt.passwordpolicy;

import com.epic.rdbsms.bean.sysconfigmgt.passwordpolicy.PasswordPolicyInputBean;
import com.epic.rdbsms.util.validation.Validation;
import com.epic.rdbsms.util.varlist.CommonVarList;
import com.epic.rdbsms.util.varlist.MessageVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
public class PasswordPolicyValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Autowired
    MessageSource messageSource;

    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordPolicyInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            Locale locale = Locale.getDefault();
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(PasswordPolicyInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (PasswordPolicyInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("passwordPolicyId")) {
                        //validate the null and empty in password param
                        String passwordparam = String.valueOf(((PasswordPolicyInputBean) o).getPasswordPolicyId());
                        if (validation.isEmptyFieldValue(passwordparam)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_PASSWORDPOLICYID, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_PASSWORDPOLICYID);
                        }

                    } else if (fieldName.equals("minimumLength")) {
                        //validate the null and empty in user role type
                        String userroletype = String.valueOf(((PasswordPolicyInputBean) o).getMinimumLength());
                        if (validation.isEmptyFieldValue(userroletype)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MINIMUM_LENGTH, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MINIMUM_LENGTH);
                        }

                    } else if (fieldName.equals("maximumLength")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getMaximumLength());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MAXIMUM_LENGTH, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MAXIMUM_LENGTH);
                        }

                    } else if (fieldName.equals("minimumSpecialCharacters")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getMinimumSpecialCharacters());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MINIMUM_SPECIAL_CHARACTERS, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MINIMUM_SPECIAL_CHARACTERS);
                        }

                    } else if (fieldName.equals("minimumUpperCaseCharacters")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getMinimumUpperCaseCharacters());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MINIMUM_UPPERCASE_CHARACTERS, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MINIMUM_UPPERCASE_CHARACTERS);
                        }

                    } else if (fieldName.equals("minimumNumericalCharacters")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getMinimumNumericalCharacters());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MINIMUM_NUMERICAL_CHARACTERS, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MINIMUM_NUMERICAL_CHARACTERS);
                        }

                    } else if (fieldName.equals("minimumLowerCaseCharacters")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getMinimumLowerCaseCharacters());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MINIMUM_LOWERCASE_CHARACTERS, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MINIMUM_LOWERCASE_CHARACTERS);
                        }

                    } else if (fieldName.equals("noOfInvalidLoginAttempt")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getNoOfInvalidLoginAttempt());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_INVALID_LOGIN_ATTEMPTS, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_INVALID_LOGIN_ATTEMPTS);
                        }

                    } else if (fieldName.equals("repeatCharactersAllow")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getRepeatCharactersAllow());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_REPEAT_CHARACTER_ALLOW, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_REPEAT_CHARACTER_ALLOW);
                        }

                    } else if (fieldName.equals("initialPasswordExpiryStatus")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getInitialPasswordExpiryStatus());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_PASSWORD_EXPIRY_STATUS, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_PASSWORD_EXPIRY_STATUS);
                        }

                    } else if (fieldName.equals("passwordExpiryPeriod")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getPasswordExpiryPeriod());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_PASSWORD_EXPIRY_PERIOD, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_PASSWORD_EXPIRY_PERIOD);
                        }

                    } else if (fieldName.equals("noOfHistoryPassword")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getNoOfHistoryPassword());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_NO_OF_HISTORY_PASSWORD, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_NO_OF_HISTORY_PASSWORD);
                        }

                    } else if (fieldName.equals("minimumPasswordChangePeriod")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getMinimumPasswordChangePeriod());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MIN_PASSWORD_CHANGE_PERIOD, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_MIN_PASSWORD_CHANGE_PERIOD);
                        }

                    } else if (fieldName.equals("idleAccountExpiryPeriod")) {
                        //validate the null and empty in value
                        String value = String.valueOf(((PasswordPolicyInputBean) o).getIdleAccountExpiryPeriod());
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_IDLE_ACCOUNT_EXPIRY_PERIOD, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_IDLE_ACCOUNT_EXPIRY_PERIOD);
                        }

                    } else if (fieldName.equals("description")) {
                        //validate the null and empty in value
                        String value = ((PasswordPolicyInputBean) o).getDescription();
                        if (validation.isEmptyFieldValue(value)) {
                            errors.rejectValue(fieldName, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_DESCRIPTION, MessageVarList.PASSWORD_POLICY_MGT_EMPTY_DESCRIPTION);
                        }
                    }
                }
            } else {
                errors.reject(commonVarList.COMMON_VALIDATION_INVALID_BEANTYPE);
            }

            if (!errors.hasErrors()) {

                if (((PasswordPolicyInputBean) o).getMinimumLowerCaseCharacters() != null && !((PasswordPolicyInputBean) o).getMinimumLowerCaseCharacters().isEmpty()
                        && ((PasswordPolicyInputBean) o).getMinimumNumericalCharacters() != null && !((PasswordPolicyInputBean) o).getMinimumNumericalCharacters().isEmpty()
                        && ((PasswordPolicyInputBean) o).getMinimumSpecialCharacters() != null && !((PasswordPolicyInputBean) o).getMinimumSpecialCharacters().isEmpty()
                        && ((PasswordPolicyInputBean) o).getMinimumUpperCaseCharacters() != null && !((PasswordPolicyInputBean) o).getMinimumUpperCaseCharacters().isEmpty()) {

                    Integer minLower = Integer.parseInt(((PasswordPolicyInputBean) o).getMinimumLowerCaseCharacters());
                    Integer minNumerical = Integer.parseInt(((PasswordPolicyInputBean) o).getMinimumNumericalCharacters());
                    Integer minSpecial = Integer.parseInt(((PasswordPolicyInputBean) o).getMinimumSpecialCharacters());
                    Integer minUpper = Integer.parseInt(((PasswordPolicyInputBean) o).getMinimumUpperCaseCharacters());

                    Integer minLength = minLower + minNumerical + minSpecial + minUpper;

                    Integer maxLength = minLength;

                    if (!errors.hasErrors()) {
                        if (Integer.parseInt(((PasswordPolicyInputBean) o).getMinimumLength()) >= Integer.parseInt(((PasswordPolicyInputBean) o).getMaximumLength())) {
                            errors.reject(MessageVarList.PASSPOLICY_MIN_MAX_LENGHT_DIFF);
                        }
                    }

                    if (!errors.hasErrors()) {
                        if (Integer.parseInt(((PasswordPolicyInputBean) o).getMinimumLength()) < minLength) {
                            errors.reject(MessageVarList.PASSPOLICY_MINLEN_INVALID, Integer.toString(minLength));
                        }
                    }

                    if (!errors.hasErrors()) {
                        if (Integer.parseInt(((PasswordPolicyInputBean) o).getMaximumLength()) <= maxLength) {
                            errors.reject(MessageVarList.PASSPOLICY_MAXLEN_INVALID, Integer.toString(maxLength));
                        }
                    }

                    if (!errors.hasErrors()) {
                        if (Integer.parseInt(((PasswordPolicyInputBean) o).getNoOfHistoryPassword()) <= 0) {
                            errors.reject(MessageVarList.PASSPOLICY_NO_OF_HISTORY_PASSWORD_INVALID, MessageVarList.PASSPOLICY_NO_OF_HISTORY_PASSWORD_INVALID);
                        }
                    }

                    //check Password Expiry Period is 10 or above
                    if (!errors.hasErrors()) {
                        if (Integer.parseInt(((PasswordPolicyInputBean) o).getPasswordExpiryPeriod()) < 1) {
                            errors.reject(MessageVarList.PASSPOLICY_PASSWORD_EXPIRY_PERIOD_INVALID, MessageVarList.PASSPOLICY_PASSWORD_EXPIRY_PERIOD_INVALID);
                        }
                    }

                    //check Idle Account Expiry Period is 10 or above
                    if (!errors.hasErrors()) {
                        if (Integer.parseInt(((PasswordPolicyInputBean) o).getIdleAccountExpiryPeriod()) < 1) {
                            errors.reject(MessageVarList.PASSPOLICY_IDLE_ACCOUNT_EXPIRY_PERIOD_INVALID, MessageVarList.PASSPOLICY_IDLE_ACCOUNT_EXPIRY_PERIOD_INVALID);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Exception : ", ex);
            errors.reject(commonVarList.COMMON_VALIDATION_FAIL_CODE);
        }
    }

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, PasswordPolicyInputBean o) {
        return new Field[]{
                allFields.get("passwordPolicyId"),
                allFields.get("minimumLength"),
                allFields.get("maximumLength"),
                allFields.get("minimumSpecialCharacters"),
                allFields.get("minimumUpperCaseCharacters"),
                allFields.get("minimumNumericalCharacters"),
                allFields.get("minimumLowerCaseCharacters"),
                allFields.get("noOfInvalidLoginAttempt"),
                allFields.get("repeatCharactersAllow"),
                allFields.get("initialPasswordExpiryStatus"),
                allFields.get("passwordExpiryPeriod"),
                allFields.get("noOfHistoryPassword"),
                allFields.get("minimumPasswordChangePeriod"),
                allFields.get("idleAccountExpiryPeriod"),
                allFields.get("description")
        };
    }
}
