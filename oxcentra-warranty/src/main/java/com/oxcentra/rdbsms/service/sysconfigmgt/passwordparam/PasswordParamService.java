package com.oxcentra.rdbsms.service.sysconfigmgt.passwordparam;

import com.oxcentra.rdbsms.bean.common.TempAuthRecBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.passwordparam.PasswordParamInputBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.sectionmgt.PasswordParam;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.passwordparam.PasswordParamRepository;
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
public class PasswordParamService {

    @Autowired
    PasswordParamRepository passwordParamRepository;

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

    private final String fields = "PasswordParamInputBean PasswordParam|UserRoleType|Value|Created Time|Last Updated Time|Last Updated User";

    public long getDataCount(PasswordParamInputBean paramBean) throws Exception {
        long count = 0;
        try {
            count = passwordParamRepository.getDataCount(paramBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<PasswordParam> getPasswordParamSearchResults(PasswordParamInputBean passwordParamInputBean) throws Exception {
        List<PasswordParam> paramList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.PASSWORD_PARAMETER_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get password param search list.");
            //get category search list
            paramList = passwordParamRepository.getPasswordParamSearchResults(passwordParamInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return paramList;
    }

    public long getDataCountDual(PasswordParamInputBean paramBean) throws Exception {
        long count = 0;
        try {
            count = passwordParamRepository.getDataCountDual(paramBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getPasswordParamSearchResultsDual(PasswordParamInputBean passwordParamInputBean) throws Exception {
        List<TempAuthRec> passwordParamDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.PASSWORD_PARAMETER_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get password param dual authentication search list.");
            //get category dual authentication search list
            passwordParamDualList = passwordParamRepository.getPasswordParamSearchResultsDual(passwordParamInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return passwordParamDualList;
    }

    public PasswordParam getPasswordParam(String paramCode) throws Exception {
        PasswordParam passwordParam;
        try {
            passwordParam = passwordParamRepository.getPasswordParam(paramCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return passwordParam;
    }

    public String updatePasswordParam(PasswordParamInputBean passwordParamInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        PasswordParam existingPasswordParam = null;
        try {
            existingPasswordParam = passwordParamRepository.getPasswordParam(passwordParamInputBean.getPasswordparam());
            if (existingPasswordParam != null) {
                //check changed values
                String oldValueAsString = this.getPasswordParamAsString(existingPasswordParam, true);
                String newValueAsString = this.getPasswordParamAsString(passwordParamInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    passwordParamInputBean.setLastUpdatedTime(currentDate);
                    passwordParamInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.PASSWORD_PARAMETER_MGT_PAGE)) {
                        auditDescription = "Requested to update password param (code: " + passwordParamInputBean.getPasswordparam() + ")";
                        message = this.insertDualAuthRecord(passwordParamInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Password param (code: " + passwordParamInputBean.getPasswordparam() + ") updated by " + sessionBean.getUsername();
                        message = passwordParamRepository.updatePasswordParam(passwordParamInputBean);
                    }
                }
            } else {
                message = MessageVarList.PASSWORD_PARAM_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.CATEGORY_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.PASSWORD_PARAM_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception ex) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.PASSWORD_PARAMETER_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getPasswordParamAsString(existingPasswordParam, false));
                audittrace.setNewvalue(this.getPasswordParamAsString(passwordParamInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }


    public String confirmPasswordParam(String id) throws Exception {
        String message = "";
        String auditDescription = "";
        PasswordParamInputBean passwordParamInputBean = null;
        PasswordParam existingPasswordParam = null;
        try {
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                passwordParamInputBean = new PasswordParamInputBean();
                passwordParamInputBean.setPasswordparam(tempAuthRecBean.getKey1());
                passwordParamInputBean.setUserroletype(tempAuthRecBean.getKey2());
                passwordParamInputBean.setValue(tempAuthRecBean.getKey3());
                passwordParamInputBean.setCreatedTime(commonRepository.getCurrentDate());
                passwordParamInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                passwordParamInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing department
                try {
                    String code = passwordParamInputBean.getPasswordparam();
                    existingPasswordParam = passwordParamRepository.getPasswordParam(code);
                } catch (EmptyResultDataAccessException e) {
                    existingPasswordParam = null;
                }

                if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingPasswordParam != null) {
                        message = passwordParamRepository.updatePasswordParam(passwordParamInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                }

                //if password param db operation success, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success,insert the audit
                    if (message.isEmpty()) {
                        //if temp auth db operation success,insert the audit
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on password param (password param code: ").append(passwordParamInputBean.getPasswordparam())
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
            message = MessageVarList.PASSWORD_PARAM_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.PASSWORD_PARAMETER_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getPasswordParamAsString(passwordParamInputBean, false));
                audittrace.setOldvalue(this.getPasswordParamAsString(existingPasswordParam, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectPasswordParam(String id) throws Exception {
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
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on password param (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.PASSWORD_PARAMETER_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String insertDualAuthRecord(PasswordParamInputBean passwordParamInputBean, String task) throws Exception {
        TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
        String message = "";
        long count = 0;
        try {
            count = commonRepository.getTempAuthRecordCount(passwordParamInputBean.getPasswordparam().trim(), PageVarList.PASSWORD_PARAMETER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                tempAuthRecBean.setPage(PageVarList.PASSWORD_PARAMETER_MGT_PAGE);
                tempAuthRecBean.setTask(task);
                tempAuthRecBean.setKey1(passwordParamInputBean.getPasswordparam().trim());
                tempAuthRecBean.setKey2(passwordParamInputBean.getUserroletype().trim());
                tempAuthRecBean.setKey3(passwordParamInputBean.getValue().trim());
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

    private String getPasswordParamAsString(PasswordParam passwordParam, boolean checkChanges) {
        StringBuilder passwordParamStringBuilder = new StringBuilder();
        try {
            if (passwordParam != null) {
                if (passwordParam.getPasswordparam() != null) {
                    passwordParamStringBuilder.append(passwordParam.getPasswordparam());
                } else {
                    passwordParamStringBuilder.append("error");
                }

                passwordParamStringBuilder.append("|");
                if (passwordParam.getUserroletype() != null) {
                    passwordParamStringBuilder.append(passwordParam.getUserroletype());
                } else {
                    passwordParamStringBuilder.append("--");
                }

                passwordParamStringBuilder.append("|");
                if (passwordParam.getValue() != null) {
                    passwordParamStringBuilder.append(passwordParam.getValue());
                } else {
                    passwordParamStringBuilder.append("--");
                }

                if (!checkChanges) {
                    passwordParamStringBuilder.append("|");
                    if (passwordParam.getCreatedTime() != null) {
                        passwordParamStringBuilder.append(common.formatDateToString(passwordParam.getCreatedTime()));
                    } else {
                        passwordParamStringBuilder.append("--");
                    }

                    passwordParamStringBuilder.append("|");
                    if (passwordParam.getCreatedUser() != null) {
                        passwordParamStringBuilder.append(passwordParam.getCreatedUser());
                    } else {
                        passwordParamStringBuilder.append("--");
                    }

                    passwordParamStringBuilder.append("|");
                    if (passwordParam.getLastUpdatedTime() != null) {
                        passwordParamStringBuilder.append(common.formatDateToString(passwordParam.getLastUpdatedTime()));
                    } else {
                        passwordParamStringBuilder.append("--");
                    }

                    passwordParamStringBuilder.append("|");
                    if (passwordParam.getLastUpdatedUser() != null) {
                        passwordParamStringBuilder.append(passwordParam.getLastUpdatedUser());
                    } else {
                        passwordParamStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return passwordParamStringBuilder.toString();
    }

    private String getPasswordParamAsString(PasswordParamInputBean passwordParamInputBean, boolean checkChanges) {
        StringBuilder passwordParamStringBuilder = new StringBuilder();
        try {
            if (passwordParamInputBean != null) {
                if (passwordParamInputBean.getPasswordparam() != null) {
                    passwordParamStringBuilder.append(passwordParamInputBean.getPasswordparam());
                } else {
                    passwordParamStringBuilder.append("error");
                }

                passwordParamStringBuilder.append("|");
                if (passwordParamInputBean.getUserroletype() != null) {
                    passwordParamStringBuilder.append(passwordParamInputBean.getUserroletype());
                } else {
                    passwordParamStringBuilder.append("--");
                }

                passwordParamStringBuilder.append("|");
                if (passwordParamInputBean.getValue() != null) {
                    passwordParamStringBuilder.append(passwordParamInputBean.getValue());
                } else {
                    passwordParamStringBuilder.append("--");
                }

                if (!checkChanges) {
                    passwordParamStringBuilder.append("|");
                    if (passwordParamInputBean.getCreatedTime() != null) {
                        passwordParamStringBuilder.append(common.formatDateToString(passwordParamInputBean.getCreatedTime()));
                    } else {
                        passwordParamStringBuilder.append("--");
                    }

                    passwordParamStringBuilder.append("|");
                    if (passwordParamInputBean.getCreatedUser() != null) {
                        passwordParamStringBuilder.append(passwordParamInputBean.getCreatedUser());
                    } else {
                        passwordParamStringBuilder.append("--");
                    }

                    passwordParamStringBuilder.append("|");
                    if (passwordParamInputBean.getLastUpdatedTime() != null) {
                        passwordParamStringBuilder.append(common.formatDateToString(passwordParamInputBean.getLastUpdatedTime()));
                    } else {
                        passwordParamStringBuilder.append("--");
                    }

                    passwordParamStringBuilder.append("|");
                    if (passwordParamInputBean.getLastUpdatedUser() != null) {
                        passwordParamStringBuilder.append(passwordParamInputBean.getLastUpdatedUser());
                    } else {
                        passwordParamStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return passwordParamStringBuilder.toString();
    }
}
