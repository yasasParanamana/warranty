package com.oxcentra.warranty.service.sysconfigmgt.userparam;

import com.oxcentra.warranty.bean.common.TempAuthRecBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.sysconfigmgt.userparam.UserParamInputBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.userparam.UserParam;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.repository.sysconfigmgt.userparam.UserParamRepository;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.varlist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Scope("prototype")
public class UserParamService {
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
    UserParamRepository userParamRepository;

    private final String fields = "UserParamInputBean ParamCode|Description|Category|Status|Last Updated User|Last Updated Time|Created Time";

    public long getCount(UserParamInputBean userParamInputBean) {
        long count = 0;
        try {
            count = userParamRepository.getDataCount(userParamInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<UserParam> getUserParamSearchResultList(UserParamInputBean userParamInputBean) {
        List<UserParam> userParamList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.USERPARAMETER_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get userparam search list.");
            //Get User param search list.
            userParamList = userParamRepository.getUserParamSearchList(userParamInputBean);
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
        return userParamList;
    }

    public long getDataCountDual(UserParamInputBean userParamInputBean) {
        long count = 0;
        try {
            count = userParamRepository.getDataCountDual(userParamInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getUserParamSearchResultsDual(UserParamInputBean userParamInputBean) {
        List<TempAuthRec> userParamtDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.USERPARAMETER_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get department dual authentication search list.");
            //Get department dual authentication search list
            userParamtDualList = userParamRepository.getUserParamSearchResultsDual(userParamInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return userParamtDualList;
    }

    public String insertUserParam(UserParamInputBean userParamInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            UserParam userParam = userParamRepository.getUserParam(userParamInputBean.getParamCode().trim());
            if (userParam == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                userParamInputBean.setCreatedTime(currentDate);
                userParamInputBean.setCreatedUser(lastUpdatedUser);
                userParamInputBean.setLastUpdatedTime(currentDate);
                userParamInputBean.setLastUpdatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USERPARAMETER_MGT_PAGE)) {
                    auditDescription = "Requested to add userparam (paramcode: " + userParamInputBean.getParamCode() + ")";
                    message = this.insertDualAuthRecord(userParamInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Department (code: " + userParamInputBean.getParamCode() + ") added by " + sessionBean.getUsername();
                    message = userParamRepository.insertUserParam(userParamInputBean);
                }
            } else {
                message = MessageVarList.USERPARAM_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.USERPARAM_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.USERPARAM_MGT_ALREADY_EXISTS;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception x) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.USERPARAMETER_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getUserParamAsString(userParamInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public UserParam getUserParam(String code) {
        UserParam userParam;
        try {
            userParam = userParamRepository.getUserParam(code);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return userParam;
    }


    public String updateUserParam(UserParamInputBean userParamInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        UserParam existingUserParam = null;
        try {

            existingUserParam = userParamRepository.getUserParam(userParamInputBean.getParamCode());
            if (existingUserParam != null) {
                //check changed values
                String oldValueAsString = this.getUserParamAsString(existingUserParam, true);
                String newValueAsString = this.getUserParamAsString(userParamInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    userParamInputBean.setLastUpdatedTime(currentDate);
                    userParamInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USERPARAMETER_MGT_PAGE)) {
                        auditDescription = "Requested to update userparam (code: " + userParamInputBean.getParamCode() + ")";
                        message = this.insertDualAuthRecord(userParamInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "userparam (code: " + userParamInputBean.getParamCode() + ") updated by " + sessionBean.getUsername();
                        message = userParamRepository.updateUserParam(userParamInputBean);
                    }
                }
            } else {
                message = MessageVarList.USERPARAMT_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.USERPARAMT_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.USERPARAMT_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.USERPARAMETER_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getUserParamAsString(existingUserParam, false));
                audittrace.setNewvalue(this.getUserParamAsString(existingUserParam, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteUserParam(String code) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USERPARAMETER_MGT_PAGE)) {
                //get the existing department
                UserParam userParam = userParamRepository.getUserParam(code);
                if (userParam != null) {
                    UserParamInputBean userParamInputBean = new UserParamInputBean();
                    //set the values to input bean
                    userParamInputBean.setParamCode(userParam.getParamCode());
                    userParamInputBean.setDescription(userParam.getDescription());
                    userParamInputBean.setCategory(userParam.getCategory());
                    userParamInputBean.setStatus(userParam.getStatus());
                    userParamInputBean.setCreatedTime(userParam.getCreatedTime());
                    userParamInputBean.setLastUpdatedTime(userParam.getLastUpdatedTime());
                    userParamInputBean.setLastUpdatedUser(userParam.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted userparam (paramcode: " + code + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(userParamInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.USERPARAMT_MGT_NORECORD_FOUND;
                }
            } else {
                message = userParamRepository.deleteUserParam(code);
                auditDescription = "Userparam (paramCode: " + code + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.USERPARAMT_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.USER_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmUserParam(String id) {
        String message = "";
        String auditDescription = "";
        UserParamInputBean userParamInputBean = null;
        UserParam existingUserParam = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                userParamInputBean = new UserParamInputBean();
                userParamInputBean.setParamCode(tempAuthRecBean.getKey1());
                userParamInputBean.setDescription(tempAuthRecBean.getKey2());
                userParamInputBean.setCategory(tempAuthRecBean.getKey3());
                userParamInputBean.setStatus(tempAuthRecBean.getKey4());

                userParamInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                userParamInputBean.setLastUpdatedUser(sessionBean.getUsername());
                //userParamInputBean.setUserTask(tempAuthRecBean.getTask());
                //get the existing user param
                try {
                    String code = userParamInputBean.getParamCode();
                    existingUserParam = userParamRepository.getUserParam(code);
                } catch (EmptyResultDataAccessException e) {
                    existingUserParam = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingUserParam == null) {
                        userParamInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        userParamInputBean.setCreatedUser(sessionBean.getUsername());
                        message = userParamRepository.insertUserParam(userParamInputBean);
                    } else {
                        message = MessageVarList.USERPARAM_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingUserParam != null) {
                        message = userParamRepository.updateUserParam(userParamInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingUserParam != null) {
                        message = userParamRepository.deleteUserParam(userParamInputBean.getParamCode());
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                }

                //if task db operation success, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success,insert the audit
                    if (message.isEmpty()) {
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on task (task code: ").append(userParamInputBean.getParamCode())
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
            message = MessageVarList.USERPARAMT_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.USERPARAMETER_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getUserParamAsString(userParamInputBean, false));
                audittrace.setOldvalue(this.getUserParamAsString(existingUserParam, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }


    public String rejectUserParam(String code) {
        String message = "";
        String auditDescription = "";
        try {
            //get temp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(code);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(code, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on userparam (paramcode: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.USERPARAMETER_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }


    private String getUserParamAsString(UserParam userParam, boolean checkChanges) {
        StringBuilder userParamStringBuilder = new StringBuilder();
        try {
            if (userParam != null) {
                if (userParam.getParamCode() != null && !userParam.getParamCode().isEmpty()) {
                    userParamStringBuilder.append(userParam.getParamCode());
                } else {
                    userParamStringBuilder.append("error");
                }

                userParamStringBuilder.append("|");
                if (userParam.getDescription() != null && !userParam.getDescription().isEmpty()) {
                    userParamStringBuilder.append(userParam.getDescription());
                } else {
                    userParamStringBuilder.append("--");
                }


                userParamStringBuilder.append("|");
                if (userParam.getCategory() != null && !userParam.getCategory().isEmpty()) {
                    userParamStringBuilder.append(userParam.getCategory());
                } else {
                    userParamStringBuilder.append("--");
                }


                userParamStringBuilder.append("|");
                if (userParam.getStatus() != null && !userParam.getStatus().isEmpty()) {
                    userParamStringBuilder.append(userParam.getStatus());
                } else {
                    userParamStringBuilder.append("--");
                }

                if (!checkChanges) {

                    userParamStringBuilder.append("|");
                    if (userParam.getLastUpdatedUser() != null) {
                        userParamStringBuilder.append(userParam.getLastUpdatedUser());
                    } else {
                        userParamStringBuilder.append("--");
                    }

                    userParamStringBuilder.append("|");
                    if (userParam.getLastUpdatedTime() != null) {
                        userParamStringBuilder.append(common.formatDateToString(userParam.getLastUpdatedTime()));
                    } else {
                        userParamStringBuilder.append("--");
                    }

                    userParamStringBuilder.append("|");
                    if (userParam.getCreatedTime() != null) {
                        userParamStringBuilder.append(common.formatDateToString(userParam.getCreatedTime()));
                    } else {
                        userParamStringBuilder.append("--");
                    }

                }
            }
        } catch (Exception e) {
            throw e;
        }
        return userParamStringBuilder.toString();
    }

    private String getUserParamAsString(UserParamInputBean userParamInputBean, boolean checkChanges) {
        StringBuilder userParamStringBuilder = new StringBuilder();
        try {
            if (userParamInputBean != null) {

                if (userParamInputBean.getParamCode() != null && !userParamInputBean.getParamCode().isEmpty()) {
                    userParamStringBuilder.append(userParamInputBean.getParamCode());
                } else {
                    userParamStringBuilder.append("error");
                }

                userParamStringBuilder.append("|");
                if (userParamInputBean.getDescription() != null && !userParamInputBean.getDescription().isEmpty()) {
                    userParamStringBuilder.append(userParamInputBean.getDescription());
                } else {
                    userParamStringBuilder.append("--");
                }

                userParamStringBuilder.append("|");
                if (userParamInputBean.getCategory() != null && !userParamInputBean.getCategory().isEmpty()) {
                    userParamStringBuilder.append(userParamInputBean.getCategory());
                } else {
                    userParamStringBuilder.append("--");
                }

                userParamStringBuilder.append("|");
                if (userParamInputBean.getStatus() != null && !userParamInputBean.getStatus().isEmpty()) {
                    userParamStringBuilder.append(userParamInputBean.getStatus());
                } else {
                    userParamStringBuilder.append("--");
                }

                if (!checkChanges) {

                    userParamStringBuilder.append("|");
                    if (userParamInputBean.getLastUpdatedUser() != null) {
                        userParamStringBuilder.append(userParamInputBean.getLastUpdatedUser());
                    } else {
                        userParamStringBuilder.append("--");
                    }

                    userParamStringBuilder.append("|");
                    if (userParamInputBean.getLastUpdatedTime() != null) {
                        userParamStringBuilder.append(common.formatDateToString(userParamInputBean.getLastUpdatedTime()));
                    } else {
                        userParamStringBuilder.append("--");
                    }

                    userParamStringBuilder.append("|");
                    if (userParamInputBean.getCreatedTime() != null) {
                        userParamStringBuilder.append(common.formatDateToString(userParamInputBean.getCreatedTime()));
                    } else {
                        userParamStringBuilder.append("--");
                    }

                }
            }
        } catch (Exception e) {
            throw e;
        }
        return userParamStringBuilder.toString();
    }

    private String insertDualAuthRecord(UserParamInputBean userParamInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(userParamInputBean.getParamCode(), PageVarList.USERPARAMETER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.USERPARAMETER_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(userParamInputBean.getParamCode());
                tempAuthRecBean.setKey2(userParamInputBean.getDescription());
                tempAuthRecBean.setKey3(userParamInputBean.getCategory());
                tempAuthRecBean.setKey4(userParamInputBean.getStatus());
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

}
