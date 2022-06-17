package com.oxcentra.warranty.validators.profile;

import com.oxcentra.warranty.bean.profile.PasswordChangeBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.mapping.passwordhistory.PasswordHistory;
import com.oxcentra.warranty.mapping.passwordpolicy.PasswordPolicy;
import com.oxcentra.warranty.mapping.usermgt.User;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.repository.sysconfigmgt.passwordpolicy.PasswordPolicyRepository;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.security.SHA256Algorithm;
import com.oxcentra.warranty.util.validation.Validation;
import com.oxcentra.warranty.util.varlist.CommonVarList;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
public class ChangePasswordValidator implements Validator {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Autowired
    PasswordPolicyRepository passwordPolicyRepository;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    SHA256Algorithm sha256Algorithm;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    Common common;

    @Autowired
    MessageSource messageSource;

    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordChangeBean.class.isAssignableFrom(aClass);
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
            if (o.getClass().equals(PasswordChangeBean.class)) {
                boolean isNewPasswordOk = true;
                boolean isVisitedNewPassword = false;

                boolean isNewConfirmPasswordOk = true;
                boolean isVisitedNewConfirmPassword = false;

                Field[] requiredFields = this.getRequiredFields(allFields, (PasswordChangeBean) o);
                //validate fields
                for (Field field : requiredFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (fieldName.equals("oldPassword")) {
                        //validate the empty and null in old password
                        String oldPassword = ((PasswordChangeBean) o).getOldPassword();
                        if (validation.isEmptyFieldValue(oldPassword)) {
                            errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_CURRENTPASSWORD_EMPTY, null, locale), messageSource.getMessage(MessageVarList.USER_CURRENTPASSWORD_EMPTY, null, locale));
                        }
                        //check the current password is match with user password
                        User user = sessionBean.getUser();
                        String password = user.getPassword();
                        String hashPassword = sha256Algorithm.makeHash(oldPassword);
                        if (!password.equals(hashPassword)) {
                            errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_CURRENTPASSWORD_INVALID, null, locale), messageSource.getMessage(MessageVarList.USER_CURRENTPASSWORD_INVALID, null, locale));
                        }

                    } else if (fieldName.equals("newPassword")) {
                        isVisitedNewPassword = true;
                        //validate the empty and null in new password
                        String newPassword = ((PasswordChangeBean) o).getNewPassword();
                        if (validation.isEmptyFieldValue(newPassword)) {
                            isNewPasswordOk = false;
                            errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_EMPTY, null, locale), messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_EMPTY, null, locale));
                        }

                    } else if (fieldName.equals("confirmNewPassword")) {
                        isVisitedNewConfirmPassword = true;
                        //validate the empty and null in confirm new password
                        String newConfirmPassword = ((PasswordChangeBean) o).getConfirmNewPassword();
                        String newpassword = ((PasswordChangeBean) o).getNewPassword();
                        if (validation.isEmptyFieldValue(newConfirmPassword)) {
                            isNewConfirmPasswordOk = false;
                            errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_NEWCONFIRMPASSWORD_EMPTY, null, locale), messageSource.getMessage(MessageVarList.USER_NEWCONFIRMPASSWORD_EMPTY, null, locale));
                        }
                        if (!newpassword.equals(newConfirmPassword)) {
                            errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_PASSWORDS_MISMATCH, null, locale), messageSource.getMessage(MessageVarList.USER_PASSWORDS_MISMATCH, null, locale));
                        }
                    }

                    //check the custom validations
                    if (isVisitedNewPassword && isNewPasswordOk && isVisitedNewConfirmPassword && isNewConfirmPasswordOk) {
                        boolean isPasswordPolicyInvalid = false;

                        int minUpperCharacters = 0;
                        int minLowerCharacters = 0;
                        int minNumericCharacters = 0;
                        int minSpecialCharacters = 0;
                        int repeatCharacters = 0;

                        String newPassword = ((PasswordChangeBean) o).getNewPassword();
                        repeatCharacters = common.countRepeatedCharacters(newPassword);
                        for (int i = 0; i < newPassword.length(); i++) {
                            char letter = newPassword.charAt(i);

                            if (Character.isUpperCase(letter)) {
                                minUpperCharacters++;
                            } else if (Character.isLowerCase(letter)) {
                                minLowerCharacters++;
                            } else if (Character.isDigit(letter)) {
                                minNumericCharacters++;
                            } else if (!Character.isLetterOrDigit(letter)) {
                                minSpecialCharacters++;
                            }
                        }

                        char[] inp = newPassword.toCharArray();
                        for (int i = 0; i < newPassword.length(); i++) {
                            for (int j = i + 1; j < newPassword.length(); j++) {
                                if (inp[i] == inp[j]) {
                                    repeatCharacters++;
                                    break;
                                }
                            }
                        }

                        //check the password policy
                        PasswordPolicy passwordPolicy = passwordPolicyRepository.getPasswordPolicy("1");
                        if (passwordPolicy != null) {
                            if (passwordPolicy.getMinimumLength() > newPassword.length()) {
                                isPasswordPolicyInvalid = true;
                                errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_TOO_SHORT, null, locale), messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_TOO_SHORT, null, locale));

                            } else if (passwordPolicy.getMaximumLength() < newPassword.length()) {
                                isPasswordPolicyInvalid = true;
                                errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_TOO_LONG, null, locale), messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_TOO_LONG, null, locale));

                            } else if (passwordPolicy.getMinimumSpecialCharacters() > minSpecialCharacters) {
                                isPasswordPolicyInvalid = true;
                                errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_LESS_SPECIALCHARACTERS, null, locale), messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_LESS_SPECIALCHARACTERS, new Object[]{(int) passwordPolicy.getMinimumSpecialCharacters()}, locale));

                            } else if (passwordPolicy.getMinimumUpperCaseCharacters() > minUpperCharacters) {
                                isPasswordPolicyInvalid = true;
                                errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_LESS_UPPERCHARACTERS, null, locale), messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_LESS_UPPERCHARACTERS, new Object[]{(int) passwordPolicy.getMinimumUpperCaseCharacters()}, locale));

                            } else if (passwordPolicy.getMinimumLowerCaseCharacters() > minLowerCharacters) {
                                isPasswordPolicyInvalid = true;
                                errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_LESS_LOWERCHARACTERS, null, locale), messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_LESS_LOWERCHARACTERS, new Object[]{(int) passwordPolicy.getMinimumLowerCaseCharacters()}, locale));

                            } else if (passwordPolicy.getMinimumNumericalCharacters() > minNumericCharacters) {
                                isPasswordPolicyInvalid = true;
                                errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_LESS_NUMERICCHARACTERS, null, locale), messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_LESS_NUMERICCHARACTERS, new Object[]{(int) passwordPolicy.getMinimumNumericalCharacters()}, locale));

                            } else if (passwordPolicy.getRepeatCharactersAllow() < repeatCharacters) {
                                isPasswordPolicyInvalid = true;
                                errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_MORE_REPEATCHARACTERS, null, locale), messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_MORE_REPEATCHARACTERS, null, locale));

                            }
                        }

                        if (!isPasswordPolicyInvalid) {
                            //check the password history
                            int noOfHistoryPassword = (int) passwordPolicy.getNoOfHistoryPassword(); /*commonRepository.getPasswordParam(commonVarList.PARAMCODE_NOOF_HISTORYPASSWORD, commonVarList.USERROLE_TYPE_WEB);*/
                            String userName = sessionBean.getUsername();
                            if (noOfHistoryPassword != 0) {
                                List<PasswordHistory> passwordHistoryList = passwordPolicyRepository.getPasswordHistoryList(userName, noOfHistoryPassword);
                                if (passwordHistoryList != null && !passwordHistoryList.isEmpty() && passwordHistoryList.size() > 0) {
                                    for (PasswordHistory password : passwordHistoryList) {
                                        //check the new password with old password one by one
                                        String hashPassword = sha256Algorithm.makeHash(newPassword);
                                        if (password.getPassword().trim().equals(hashPassword)) {
                                            errors.rejectValue(fieldName, messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_EXIST_PASSWORDHISTORY, null, locale), messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_EXIST_PASSWORDHISTORY, null, locale));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Exception : ", ex);
            errors.reject(commonVarList.COMMON_VALIDATION_FAIL_CODE);
        }
    }

    private Field[] getRequiredFields(SortedMap<String, Field> allFields, PasswordChangeBean o) {
        return new Field[]{allFields.get("oldPassword"), allFields.get("newPassword"), allFields.get("confirmNewPassword")};
    }
}
