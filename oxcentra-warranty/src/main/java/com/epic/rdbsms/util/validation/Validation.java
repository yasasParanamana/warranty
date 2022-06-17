package com.epic.rdbsms.util.validation;

import com.epic.rdbsms.util.varlist.CommonVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Validation {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    /**
     * @Author shalika_w
     * @CreatedTime 2021-01-12 09:31:16 AM
     * @Version V1.00
     * @MethodName isEmptyFieldValue
     * @MethodParams [value]
     * @MethodDescription - Validate the null and empty string
     */
    public boolean isEmptyFieldValue(String value) {
        boolean isErrorField = true;
        try {
            if (value != null && !value.isEmpty()) {
                isErrorField = false;
            }
        } catch (Exception e) {
            logger.error("Exception : ", e);
        }
        return isErrorField;
    }

    public static boolean isAlphaNumeric(String value) throws Exception {
        boolean valied = true;

        try {
            if (!value.matches("[a-zA-Z0-9]*")) {
                valied = false;
            }

        } catch (Exception ex) {
            valied = false;
        }

        return valied;
    }

    public static boolean isValidateNIC(String nic) {

        boolean status = true;

        try {
            if ((nic.length() == 10)) {
                String nicFirst_9_Digit = nic.substring(0, 9);
                String nicLastCharacter = nic.substring(9, 10);
                //    String nicLastCharacter = nic.substring(9);
                if (!isNumeric(nicFirst_9_Digit)) {
                    status = false;
                }

                if (!nicLastCharacter.equalsIgnoreCase("v") && !nicLastCharacter.equalsIgnoreCase("x")) {
                    status = false;

                }
            } else if ((nic.length() == 12)) {

                if (!isNumeric(nic)) {
                    status = false;

                }
            } else {
                status = false;
            }

        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    public static boolean isNumeric(String theInputString) {

        boolean isValid = false;

        for (int i = 0; i < theInputString.length(); i++) {
            char c = theInputString.charAt(i);

            if ((c >= '0') && (c <= '9')) {
                isValid = true;
            } else {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    /**
     * @Author shalika_w
     * @CreatedTime 2021-01-12 09:35:07 AM
     * @Version V1.00
     * @MethodName checkSpecialCharacters
     * @MethodParams [input]
     * @MethodDescription - Validate the special characters in a string.
     */
    public boolean checkSpecialCharacters(String input) {
        boolean status = false;
        if (!input.matches("[A-Za-z0-9]+")) {
            status = true;
        }
        return status;
    }
}
