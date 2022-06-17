package com.oxcentra.rdbsms.service.sysconfigmgt.passwordpolicy;

import com.oxcentra.rdbsms.bean.common.TempAuthRecBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.passwordpolicy.PasswordPolicyInputBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.passwordpolicy.PasswordPolicy;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.passwordpolicy.PasswordPolicyRepository;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.varlist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Scope("prototype")
public class PasswordPolicyService {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    Common common;

    @Autowired
    Audittrace audittrace;

    @Autowired
    MessageSource messageSource;

    @Autowired
    PasswordPolicyRepository passwordPolicyRepository;

    private final String fields = "PasswordPolicyInputBean PasswordPolicy|Created Time|Last Updated Time|Last Updated User";

    public long getDataCount(PasswordPolicyInputBean passwordPolicyInputBean) throws Exception {
        long count = 0;
        try {
            count = passwordPolicyRepository.getDataCount(passwordPolicyInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<PasswordPolicy> getPasswordPolicySearchResults(PasswordPolicyInputBean passwordPolicyInputBean) throws Exception {
        List<PasswordPolicy> passwordPolicyList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.PASSWORDPOLICY_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get password policy search list.");
            //Get department search list.
            passwordPolicyList = passwordPolicyRepository.getPasswordPolicySearchResults(passwordPolicyInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return passwordPolicyList;
    }

    public long getDataCountDual(PasswordPolicyInputBean passwordPolicyInputBean) throws Exception {
        long count = 0;
        try {
            count = passwordPolicyRepository.getDataCountDual(passwordPolicyInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getPasswordPolicySearchResultsDual(PasswordPolicyInputBean policyInputBean) throws Exception {
        List<TempAuthRec> passwordPolicyDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.PASSWORDPOLICY_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get password policy dual authentication search list.");
            //Get department dual authentication search list
            passwordPolicyDualList = passwordPolicyRepository.getPasswordPolicySearchResultsDual(policyInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return passwordPolicyDualList;
    }

    public PasswordPolicy getWebPasswordPolicy(int passwordPolicyCode) {
        PasswordPolicy passwordPolicy = null;
        try {
            passwordPolicy = passwordPolicyRepository.getWebPasswordPolicy(passwordPolicyCode);
            //set password policy to session bean
            sessionBean.setPasswordPolicy(passwordPolicy);
        } catch (Exception e) {
            throw e;
        }
        return passwordPolicy;
    }

    public String updatePasswordPolicy(PasswordPolicyInputBean passwordPolicyInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        PasswordPolicy existingPasswordPolicy = null;
        try {
            existingPasswordPolicy = passwordPolicyRepository.getPasswordPolicy(passwordPolicyInputBean.getPasswordPolicyId());
            if (existingPasswordPolicy != null) {
                //check changed values
                String oldValueAsString = this.getPasswordPolicyAsString(existingPasswordPolicy, true);
                String newValueAsString = this.getPasswordPolicyAsString(passwordPolicyInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    passwordPolicyInputBean.setLastUpdatedTime(currentDate);
                    passwordPolicyInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.PASSWORDPOLICY_MGT_PAGE)) {
                        auditDescription = "Requested to update password policy (code: " + passwordPolicyInputBean.getPasswordPolicyId() + ")";
                        message = this.insertDualAuthRecord(passwordPolicyInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Password policy (code: " + passwordPolicyInputBean.getPasswordPolicyId() + ") updated by " + sessionBean.getUsername();
                        message = passwordPolicyRepository.updatePasswordPolicy(passwordPolicyInputBean);
                    }
                }
            } else {
                message = MessageVarList.PASSWORD_POLICY_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.DEPARTMENT_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.PASSWORD_POLICY_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.PASSWORDPOLICY_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getPasswordPolicyAsString(existingPasswordPolicy, false));
                audittrace.setNewvalue(this.getPasswordPolicyAsString(passwordPolicyInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmPasswordPolicy(String id) throws Exception {
        String message = "";
        String auditDescription = "";
        PasswordPolicyInputBean passwordPolicyInputBean = null;
        PasswordPolicy existingPasswordPolicy = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                passwordPolicyInputBean = new PasswordPolicyInputBean();
                passwordPolicyInputBean.setPasswordPolicyId(tempAuthRecBean.getKey1());
                passwordPolicyInputBean.setMinimumLength(tempAuthRecBean.getKey2());
                passwordPolicyInputBean.setMaximumLength(tempAuthRecBean.getKey3());
                passwordPolicyInputBean.setMinimumSpecialCharacters(tempAuthRecBean.getKey4());
                passwordPolicyInputBean.setMinimumUpperCaseCharacters(tempAuthRecBean.getKey5());
                passwordPolicyInputBean.setMinimumNumericalCharacters(tempAuthRecBean.getKey6());
                passwordPolicyInputBean.setMinimumLowerCaseCharacters(tempAuthRecBean.getKey7());
                passwordPolicyInputBean.setNoOfInvalidLoginAttempt(tempAuthRecBean.getKey8());
                passwordPolicyInputBean.setRepeatCharactersAllow(tempAuthRecBean.getKey9());
                passwordPolicyInputBean.setInitialPasswordExpiryStatus(tempAuthRecBean.getKey10());
                passwordPolicyInputBean.setPasswordExpiryPeriod(tempAuthRecBean.getKey11());
                passwordPolicyInputBean.setNoOfHistoryPassword(tempAuthRecBean.getKey12());
                passwordPolicyInputBean.setMinimumPasswordChangePeriod(tempAuthRecBean.getKey13());
                passwordPolicyInputBean.setIdleAccountExpiryPeriod(tempAuthRecBean.getKey14());
                passwordPolicyInputBean.setDescription(tempAuthRecBean.getKey15());
                passwordPolicyInputBean.setCreatedTime(commonRepository.getCurrentDate());
                passwordPolicyInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                passwordPolicyInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing department
                try {
                    String passwordPolicyId = passwordPolicyInputBean.getPasswordPolicyId();
                    existingPasswordPolicy = passwordPolicyRepository.getPasswordPolicy(passwordPolicyId);
                } catch (EmptyResultDataAccessException ex) {
                    existingPasswordPolicy = null;
                }

                if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingPasswordPolicy != null) {
                        message = passwordPolicyRepository.updatePasswordPolicy(passwordPolicyInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                }

                //if password policy db operation success, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success , insert the audit
                    if (message.isEmpty()) {
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on password policy (password policy id: ").append(passwordPolicyInputBean.getPasswordPolicyId())
                                .append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" approved by ")
                                .append(sessionBean.getUsername());

                        auditDescription = auditDesBuilder.toString();
                    } else {
                        message = MessageVarList.COMMON_ERROR_PROCESS;
                    }
                } else {
                    message = MessageVarList.COMMON_ERROR_PROCESS;
                }
            } else {
                message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
            }

        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.PASSWORD_POLICY_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
            message = MessageVarList.COMMON_ERROR_ALRADY_USE;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.PASSWORDPOLICY_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getPasswordPolicyAsString(passwordPolicyInputBean, false));
                audittrace.setOldvalue(this.getPasswordPolicyAsString(existingPasswordPolicy, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectPasswordPolicy(String id) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            //get temp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on password policy (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
                    auditDescription = auditDesBuilder.toString();
                } else {
                    message = MessageVarList.COMMON_ERROR_PROCESS;
                }
            } else {
                message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
            }
        } catch (Exception ex) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.PASSWORDPOLICY_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String insertDualAuthRecord(PasswordPolicyInputBean passwordPolicyInputBean, String task) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(passwordPolicyInputBean.getPasswordPolicyId(), PageVarList.PASSWORDPOLICY_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.PASSWORDPOLICY_MGT_PAGE);
                tempAuthRecBean.setTask(task);
                tempAuthRecBean.setKey1(passwordPolicyInputBean.getPasswordPolicyId());
                tempAuthRecBean.setKey2(passwordPolicyInputBean.getMinimumLength());
                tempAuthRecBean.setKey3(passwordPolicyInputBean.getMaximumLength());
                tempAuthRecBean.setKey4(passwordPolicyInputBean.getMinimumSpecialCharacters());
                tempAuthRecBean.setKey5(passwordPolicyInputBean.getMinimumUpperCaseCharacters());
                tempAuthRecBean.setKey6(passwordPolicyInputBean.getMinimumNumericalCharacters());
                tempAuthRecBean.setKey7(passwordPolicyInputBean.getMinimumLowerCaseCharacters());
                tempAuthRecBean.setKey8(passwordPolicyInputBean.getNoOfInvalidLoginAttempt());
                tempAuthRecBean.setKey9(passwordPolicyInputBean.getRepeatCharactersAllow());
                tempAuthRecBean.setKey10(passwordPolicyInputBean.getInitialPasswordExpiryStatus());
                tempAuthRecBean.setKey11(passwordPolicyInputBean.getPasswordExpiryPeriod());
                tempAuthRecBean.setKey12(passwordPolicyInputBean.getNoOfHistoryPassword());
                tempAuthRecBean.setKey13(passwordPolicyInputBean.getMinimumPasswordChangePeriod());
                tempAuthRecBean.setKey14(passwordPolicyInputBean.getIdleAccountExpiryPeriod());
                tempAuthRecBean.setKey15(passwordPolicyInputBean.getDescription().trim());
                //insert dual auth record
                message = commonRepository.insertDualAuthRecordSQL(tempAuthRecBean);
            }
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
            throw cve;
        } catch (Exception e) {
            throw e;
        }
        return message;
    }

    private String getPasswordPolicyAsString(PasswordPolicyInputBean passwordPolicyInputBean, boolean checkChanges) {
        StringBuilder passwordPolicyStringBuilder = new StringBuilder();
        try {
            if (passwordPolicyInputBean != null) {
                if (passwordPolicyInputBean.getPasswordPolicyId() != null) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getPasswordPolicyId());
                } else {
                    passwordPolicyStringBuilder.append("error");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getMinimumLength() != null && !passwordPolicyInputBean.getMinimumLength().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getMinimumLength());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getMaximumLength() != null && !passwordPolicyInputBean.getMaximumLength().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getMaximumLength());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getMinimumSpecialCharacters() != null && !passwordPolicyInputBean.getMinimumSpecialCharacters().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getMinimumSpecialCharacters());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getMinimumUpperCaseCharacters() != null && !passwordPolicyInputBean.getMinimumUpperCaseCharacters().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getMinimumUpperCaseCharacters());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getMinimumUpperCaseCharacters() != null && !passwordPolicyInputBean.getMinimumUpperCaseCharacters().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getMinimumUpperCaseCharacters());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getMinimumLowerCaseCharacters() != null && !passwordPolicyInputBean.getMinimumLowerCaseCharacters().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getMinimumLowerCaseCharacters());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getNoOfInvalidLoginAttempt() != null && !passwordPolicyInputBean.getNoOfInvalidLoginAttempt().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getNoOfInvalidLoginAttempt());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getRepeatCharactersAllow() != null && !passwordPolicyInputBean.getRepeatCharactersAllow().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getRepeatCharactersAllow());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getInitialPasswordExpiryStatus() != null && !passwordPolicyInputBean.getInitialPasswordExpiryStatus().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getInitialPasswordExpiryStatus());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getPasswordExpiryPeriod() != null && !passwordPolicyInputBean.getPasswordExpiryPeriod().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getPasswordExpiryPeriod());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getNoOfHistoryPassword() != null && !passwordPolicyInputBean.getNoOfHistoryPassword().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getNoOfHistoryPassword());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getMinimumPasswordChangePeriod() != null && !passwordPolicyInputBean.getMinimumPasswordChangePeriod().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getMinimumPasswordChangePeriod());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getIdleAccountExpiryPeriod() != null && !passwordPolicyInputBean.getIdleAccountExpiryPeriod().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getIdleAccountExpiryPeriod());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicyInputBean.getDescription() != null && !passwordPolicyInputBean.getDescription().isEmpty()) {
                    passwordPolicyStringBuilder.append(passwordPolicyInputBean.getDescription());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                if (!checkChanges) {
                    passwordPolicyStringBuilder.append("|");
                    if (passwordPolicyInputBean.getCreatedTime() != null) {
                        passwordPolicyStringBuilder.append(common.formatDateToString(passwordPolicyInputBean.getCreatedTime()));
                    } else {
                        passwordPolicyStringBuilder.append("--");
                    }

                    passwordPolicyStringBuilder.append("|");
                    if (passwordPolicyInputBean.getCreatedUser() != null) {
                        passwordPolicyStringBuilder.append(passwordPolicyInputBean.getCreatedUser());
                    } else {
                        passwordPolicyStringBuilder.append("--");
                    }

                    passwordPolicyStringBuilder.append("|");
                    if (passwordPolicyInputBean.getLastUpdatedTime() != null) {
                        passwordPolicyStringBuilder.append(common.formatDateToString(passwordPolicyInputBean.getLastUpdatedTime()));
                    } else {
                        passwordPolicyStringBuilder.append("--");
                    }

                    passwordPolicyStringBuilder.append("|");
                    if (passwordPolicyInputBean.getLastUpdatedUser() != null) {
                        passwordPolicyStringBuilder.append(passwordPolicyInputBean.getLastUpdatedUser());
                    } else {
                        passwordPolicyStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return passwordPolicyStringBuilder.toString();
    }

    private String getPasswordPolicyAsString(PasswordPolicy passwordPolicy, boolean checkChanges) {
        StringBuilder passwordPolicyStringBuilder = new StringBuilder();
        try {
            if (passwordPolicy != null) {
                if (passwordPolicy.getPasswordPolicyId() != 0) {
                    passwordPolicyStringBuilder.append(passwordPolicy.getPasswordPolicyId());
                } else {
                    passwordPolicyStringBuilder.append("error");
                }

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getMinimumLength());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getMaximumLength());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getMinimumSpecialCharacters());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getMinimumUpperCaseCharacters());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getMinimumUpperCaseCharacters());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getMinimumLowerCaseCharacters());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getNoOfInvalidLoginAttempt());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getRepeatCharactersAllow());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getInitialPasswordExpiryStatus());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getPasswordExpiryPeriod());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getNoOfHistoryPassword());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getMinimumPasswordChangePeriod());

                passwordPolicyStringBuilder.append("|");
                passwordPolicyStringBuilder.append(passwordPolicy.getIdleAccountExpiryPeriod());

                passwordPolicyStringBuilder.append("|");
                if (passwordPolicy.getDescription() != null) {
                    passwordPolicyStringBuilder.append(passwordPolicy.getDescription());
                } else {
                    passwordPolicyStringBuilder.append("--");
                }

                if (!checkChanges) {
                    passwordPolicyStringBuilder.append("|");
                    if (passwordPolicy.getCreatedTime() != null) {
                        passwordPolicyStringBuilder.append(common.formatDateToString(passwordPolicy.getCreatedTime()));
                    } else {
                        passwordPolicyStringBuilder.append("--");
                    }

                    passwordPolicyStringBuilder.append("|");
                    if (passwordPolicy.getCreatedUser() != null) {
                        passwordPolicyStringBuilder.append(passwordPolicy.getCreatedUser());
                    } else {
                        passwordPolicyStringBuilder.append("--");
                    }

                    passwordPolicyStringBuilder.append("|");
                    if (passwordPolicy.getLastUpdatedTime() != null) {
                        passwordPolicyStringBuilder.append(common.formatDateToString(passwordPolicy.getLastUpdatedTime()));
                    } else {
                        passwordPolicyStringBuilder.append("--");
                    }

                    passwordPolicyStringBuilder.append("|");
                    if (passwordPolicy.getLastUpdatedUser() != null) {
                        passwordPolicyStringBuilder.append(passwordPolicy.getLastUpdatedUser());
                    } else {
                        passwordPolicyStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return passwordPolicyStringBuilder.toString();
    }
}
