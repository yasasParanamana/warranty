package com.oxcentra.rdbsms.validators.usermgt.systemuser;

import com.oxcentra.rdbsms.bean.usermgt.systemuser.SystemUserInputBean;
import com.oxcentra.rdbsms.util.validation.Validation;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.TaskVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SystemUserValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    private static boolean isValidEmail(String theInputString) {

        boolean isValid = true;

        //Set the email pattern string
        Pattern p = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

        //Match the given string with the pattern
        Matcher m = p.matcher(theInputString);

        //Check whether match is found
        boolean matchFound = m.matches();

        if (!matchFound) {
            isValid = false;
        }

        return isValid;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SystemUserInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            SortedMap<String, Field> allFields = new TreeMap<>();
            //get fields from dto
            for (Field field : o.getClass().getDeclaredFields()) {
                allFields.put(field.getName(), field);
            }

            if (o.getClass().equals(SystemUserInputBean.class)) {
                Field[] requiredFields = this.getRequiredFields(allFields, (SystemUserInputBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    //validate the system user add and edit
                    String userTask = ((SystemUserInputBean) o).getUserTask();
                    if (userTask.equals(TaskVarList.ADD_TASK)) {
                        if (fieldName.equals("userName")) {
                            //validate the user name
                            String userName = ((SystemUserInputBean) o).getUserName();
                            if (validation.isEmptyFieldValue(userName)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_USERNAME, MessageVarList.SYSTEMUSER_MGT_EMPTY_USERNAME);
                            }

                        } else if (fieldName.equals("fullName")) {
                            //validate the full name
                            String fullName = ((SystemUserInputBean) o).getFullName();
                            if (validation.isEmptyFieldValue(fullName)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_FULLNAME, MessageVarList.SYSTEMUSER_MGT_EMPTY_FULLNAME);
                            }

                        } else if (fieldName.equals("email")) {
                            //validate email
                            String email = ((SystemUserInputBean) o).getEmail();
                            if (validation.isEmptyFieldValue(email)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_EMAIL, MessageVarList.SYSTEMUSER_MGT_EMPTY_EMAIL);
                            } else if (!isValidEmail(email)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_INVALID_EMAIL, MessageVarList.SYSTEMUSER_MGT_INVALID_EMAIL);
                            }
                        } else if (fieldName.equals("userRoleCode")) {
                            //validate user role code
                            String userRoleCode = ((SystemUserInputBean) o).getUserRoleCode();
                            if (validation.isEmptyFieldValue(userRoleCode)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_USERROLECODE, MessageVarList.SYSTEMUSER_MGT_EMPTY_USERROLECODE);
                            }

                        } else if (fieldName.equals("status")) {
                            //validate status
                            String status = ((SystemUserInputBean) o).getStatus();
                            if (validation.isEmptyFieldValue(status)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_STATUS, MessageVarList.SYSTEMUSER_MGT_EMPTY_STATUS);
                            }

                        } else if (fieldName.equals("mobileNumber")) {
                            //validate mobile number
                            String mobileNumber = ((SystemUserInputBean) o).getMobileNumber();
                            if (validation.isEmptyFieldValue(mobileNumber)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_MOBILENUMBER, MessageVarList.SYSTEMUSER_MGT_EMPTY_MOBILENUMBER);
                            }

                        } else if (fieldName.equals("password")) {
                            //validate password
                            String password = ((SystemUserInputBean) o).getPassword();
                            if (validation.isEmptyFieldValue(password)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_PASSWORD, MessageVarList.SYSTEMUSER_MGT_EMPTY_PASSWORD);
                            }

                        } else if (fieldName.equals("confirmPassword")) {
                            //validate confirm password
                            String password = ((SystemUserInputBean) o).getPassword();
                            String confirmPassword = ((SystemUserInputBean) o).getConfirmPassword();
                            if (validation.isEmptyFieldValue(confirmPassword)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_CONFIRMPASSWORD, MessageVarList.SYSTEMUSER_MGT_EMPTY_CONFIRMPASSWORD);
                            }

                            if (!password.equals(confirmPassword)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_PASSWORDS_MISMATCH, MessageVarList.SYSTEMUSER_MGT_PASSWORDS_MISMATCH);
                            }
                        } else if (fieldName.equals("nic")) {
                            //validate nic
                            String nic = ((SystemUserInputBean) o).getNic();
                            if (validation.isEmptyFieldValue(nic)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_NIC, MessageVarList.SYSTEMUSER_MGT_EMPTY_NIC);
                            } else if (!validation.isEmptyFieldValue(nic) && !Validation.isAlphaNumeric(nic)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_INVALID_NIC, MessageVarList.SYSTEMUSER_MGT_INVALID_NIC);
                            } else if (!validation.isEmptyFieldValue(nic) && !Validation.isValidateNIC(nic)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_INVALID_NIC, MessageVarList.SYSTEMUSER_MGT_INVALID_NIC);
                            }
                        } else if (fieldName.equals("serviceId")) {
                            String serviceId = ((SystemUserInputBean) o).getServiceId();
                            if (validation.isEmptyFieldValue(serviceId)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_SERVICE_ID, MessageVarList.SYSTEMUSER_MGT_EMPTY_SERVICE_ID);
                            }
                        }
                    } else if (userTask.equals(TaskVarList.UPDATE_TASK)) {
                        if (fieldName.equals("userName")) {
                            //validate the user name
                            String userName = ((SystemUserInputBean) o).getUserName();
                            if (validation.isEmptyFieldValue(userName)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_USERNAME, MessageVarList.SYSTEMUSER_MGT_EMPTY_USERNAME);
                            }

                        } else if (fieldName.equals("fullName")) {
                            //validate the full name
                            String fullName = ((SystemUserInputBean) o).getFullName();
                            if (validation.isEmptyFieldValue(fullName)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_FULLNAME, MessageVarList.SYSTEMUSER_MGT_EMPTY_FULLNAME);
                            }

                        } else if (fieldName.equals("email")) {
                            //validate email
                            String email = ((SystemUserInputBean) o).getEmail();
                            if (validation.isEmptyFieldValue(email)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_EMAIL, MessageVarList.SYSTEMUSER_MGT_EMPTY_EMAIL);
                            } else if (!isValidEmail(email)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_INVALID_EMAIL, MessageVarList.SYSTEMUSER_MGT_INVALID_EMAIL);
                            }

                        } else if (fieldName.equals("userRoleCode")) {
                            //validate user role code
                            String userRoleCode = ((SystemUserInputBean) o).getUserRoleCode();
                            if (validation.isEmptyFieldValue(userRoleCode)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_USERROLECODE, MessageVarList.SYSTEMUSER_MGT_EMPTY_USERROLECODE);
                            }

                        } else if (fieldName.equals("status")) {
                            //validate status
                            String status = ((SystemUserInputBean) o).getStatus();
                            if (validation.isEmptyFieldValue(status)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_STATUS, MessageVarList.SYSTEMUSER_MGT_EMPTY_STATUS);
                            }

                        } else if (fieldName.equals("mobileNumber")) {
                            //validate mobile number
                            String mobileNumber = ((SystemUserInputBean) o).getMobileNumber();
                            if (validation.isEmptyFieldValue(mobileNumber)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_MOBILENUMBER, MessageVarList.SYSTEMUSER_MGT_EMPTY_MOBILENUMBER);
                            }
                        } else if (fieldName.equals("nic")) {
                            //validate nic
                            String nic = ((SystemUserInputBean) o).getNic();
                            if (validation.isEmptyFieldValue(nic)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_NIC, MessageVarList.SYSTEMUSER_MGT_EMPTY_NIC);
                            } else if (!validation.isEmptyFieldValue(nic) && !Validation.isAlphaNumeric(nic)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_INVALID_NIC, MessageVarList.SYSTEMUSER_MGT_INVALID_NIC);
                            } else if (!validation.isEmptyFieldValue(nic) && !Validation.isValidateNIC(nic)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_INVALID_NIC, MessageVarList.SYSTEMUSER_MGT_INVALID_NIC);
                            }
                        } else if (fieldName.equals("serviceId")) {
                            String serviceId = ((SystemUserInputBean) o).getServiceId();
                            if (validation.isEmptyFieldValue(serviceId)) {
                                errors.rejectValue(fieldName, MessageVarList.SYSTEMUSER_MGT_EMPTY_SERVICE_ID, MessageVarList.SYSTEMUSER_MGT_EMPTY_SERVICE_ID);
                            }
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

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, SystemUserInputBean o) {
        return new Field[]{
                allFields.get("userTask"),
                allFields.get("userName"),
                allFields.get("fullName"),
                allFields.get("email"),
                allFields.get("userRoleCode"),
                allFields.get("status"),
                allFields.get("mobileNumber"),
                allFields.get("password"),
                allFields.get("nic"),
                allFields.get("serviceId"),
                allFields.get("confirmPassword")
        };
    }
}
