package com.epic.rdbsms.service.usermgt.systemuser;

import com.epic.rdbsms.bean.common.TempAuthRecBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.usermgt.systemuser.SystemUserInputBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.SystemUser;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.repository.usermgt.systemuser.SystemUserRepository;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.security.SHA256Algorithm;
import com.epic.rdbsms.util.varlist.*;
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
public class SystemUserService {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    Common common;

    @Autowired
    SystemUserRepository systemUserRepository;

    @Autowired
    Audittrace audittrace;

    @Autowired
    MessageSource messageSource;

    @Autowired
    SHA256Algorithm sha256Algorithm;

    private final String fields = "SystemUserInputBean User Name|Full Name|Email|User Role Code|Status|Mobile Number|Created Time|Last Updated Time|Last Updated User";

    public long getCount(SystemUserInputBean systemUserInputBean) {
        long count = 0;
        try {
            count = systemUserRepository.getDataCount(systemUserInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<SystemUser> getSystemUserSearchResultList(SystemUserInputBean systemUserInputBean) {
        List<SystemUser> systemUserList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_USER_MGT);
            audittrace.setPage(PageVarList.USER_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get system user search list.");
            //Get system user search list.
            systemUserList = systemUserRepository.getSystemUserSearchList(systemUserInputBean);
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
        return systemUserList;
    }

    public long getDataCountDual(SystemUserInputBean systemUserInputBean) {
        long count = 0;
        try {
            count = systemUserRepository.getDataCountDual(systemUserInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getSystemUserSearchResultsDual(SystemUserInputBean systemUserInputBean) {
        List<TempAuthRec> systemUserDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_USER_MGT);
            audittrace.setPage(PageVarList.USER_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get system user dual authentication search list.");
            //Get system user dual authentication search list
            systemUserDualList = systemUserRepository.getSystemUserSearchResultsDual(systemUserInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return systemUserDualList;
    }

    public String insertSystemUser(SystemUserInputBean systemUserInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            SystemUser existingUser = systemUserRepository.getSystemUser(systemUserInputBean.getUserName().trim());
            if (existingUser == null) {
                //set the other values to input bean
                SystemUser nic = systemUserRepository.getSystemUserbyNIC(systemUserInputBean.getUserName(), systemUserInputBean.getNic().trim());
                if(nic == null) {
                    SystemUser serviceID = systemUserRepository.getSystemUserbyServiceID(systemUserInputBean.getUserName(), systemUserInputBean.getServiceId().trim());
                    if(serviceID == null) {
                        Date currentDate = commonRepository.getCurrentDate();
                        String lastUpdatedUser = sessionBean.getUsername();

                        systemUserInputBean.setCreatedTime(currentDate);
                        systemUserInputBean.setLastUpdatedTime(currentDate);
                        systemUserInputBean.setLastUpdatedUser(lastUpdatedUser);
                        systemUserInputBean.setCreatedUser(lastUpdatedUser);

                        //check the page dual auth enable or disable
                        if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USER_MGT_PAGE)) {
                            auditDescription = "Requested to add system user (user name: " + systemUserInputBean.getUserName().trim().toUpperCase() + ")";
                            message = this.insertDualAuthRecord(systemUserInputBean, TaskVarList.ADD_TASK);
                        } else {
                            auditDescription = "System user (user name: " + systemUserInputBean.getUserName().trim().toUpperCase() + ") added by " + sessionBean.getUsername();
                            message = systemUserRepository.insertSystemUser(systemUserInputBean);
                        }
                    } else {
                        message = MessageVarList.SYSTEMUSER_MGT_SERVICEID_EXISTS;
                        auditDescription = messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_SERVICEID_EXISTS, null, locale);
                    }
                } else {
                    message = MessageVarList.SYSTEMUSER_MGT_NIC_EXISTS;
                    auditDescription = messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_NIC_EXISTS, null, locale);
                }
            } else {
                message = MessageVarList.SYSTEMUSER_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.SYSTEMUSER_MGT_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.USER_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSystemUserAsString(systemUserInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public SystemUser getSystemUser(String userName) throws Exception {
        SystemUser systemUser;
        try {
            systemUser = systemUserRepository.getSystemUser(userName);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return systemUser;
    }

    public String updateSystemUser(SystemUserInputBean systemUserInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        SystemUser existingSystemUser = null;
        try {
            existingSystemUser = systemUserRepository.getSystemUser(systemUserInputBean.getUserName());
            if (existingSystemUser != null) {
                SystemUser nic = systemUserRepository.getSystemUserbyNIC(systemUserInputBean.getUserName(), systemUserInputBean.getNic().trim());
                if(nic == null) {
                    SystemUser serviceID = systemUserRepository.getSystemUserbyServiceID(systemUserInputBean.getUserName(), systemUserInputBean.getServiceId().trim());
                    if(serviceID == null) {
                        //check changed values
                        String oldValueAsString = this.getSystemUserAsString(existingSystemUser, true);
                        String newValueAsString = this.getSystemUserAsString(systemUserInputBean, true);
                        //check the old value and new value
                        if (oldValueAsString.equals(newValueAsString)) {
                            message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                        } else {
                            //set the other values to input bean
                            Date currentDate = commonRepository.getCurrentDate();
                            String lastUpdatedUser = sessionBean.getUsername();

                            systemUserInputBean.setLastUpdatedTime(currentDate);
                            systemUserInputBean.setLastUpdatedUser(lastUpdatedUser);

                            //check the page dual auth enable or disable
                            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USER_MGT_PAGE)) {
                                auditDescription = "Requested to update system user (username: " + systemUserInputBean.getUserName().trim().toUpperCase() + ")";
                                message = this.insertDualAuthRecord(systemUserInputBean, TaskVarList.UPDATE_TASK);

                            } else {
                                auditDescription = "System user (user name: " + systemUserInputBean.getUserName().trim().toUpperCase() + ") updated by " + sessionBean.getUsername();
                                message = systemUserRepository.updateSystemUser(systemUserInputBean);
                            }
                        }
                    } else {
                        message = MessageVarList.SYSTEMUSER_MGT_SERVICEID_EXISTS;
                        auditDescription = messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_SERVICEID_EXISTS, null, locale);
                    }
                } else {
                    message = MessageVarList.SYSTEMUSER_MGT_NIC_EXISTS;
                    auditDescription = messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_NIC_EXISTS, null, locale);
                }
            } else {
                message = MessageVarList.SYSTEMUSER_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.SYSTEMUSER_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.USER_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getSystemUserAsString(existingSystemUser, false));
                audittrace.setNewvalue(this.getSystemUserAsString(systemUserInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteSystemUser(String userName) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USER_MGT_PAGE)) {
                //get the existing system user
                SystemUser systemUser = systemUserRepository.getSystemUser(userName);
                if (systemUser != null) {
                    SystemUserInputBean systemUserInputBean = new SystemUserInputBean();
                    //set the values to input bean
                    systemUserInputBean.setUserName(systemUser.getUserName());
                    systemUserInputBean.setFullName(systemUser.getFullName());
                    systemUserInputBean.setEmail(systemUser.getEmail());
                    systemUserInputBean.setUserRoleCode(systemUser.getUserRoleCode());
                    systemUserInputBean.setStatus(systemUser.getStatus());
                    systemUserInputBean.setMobileNumber(systemUser.getMobileNumber());
                    systemUserInputBean.setPassword(systemUser.getPassword());
                    systemUserInputBean.setNic(systemUser.getNic());
                    systemUserInputBean.setServiceId(systemUser.getServiceid());
                    systemUserInputBean.setCreatedTime(systemUser.getCreatedTime());
                    systemUserInputBean.setLastUpdatedTime(systemUser.getLastUpdatedTime());
                    systemUserInputBean.setLastUpdatedUser(systemUser.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted system user (username: " + userName.trim().toUpperCase() + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(systemUserInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.SYSTEMUSER_MGT_NORECORD_FOUND;
                }
            } else {
                message = systemUserRepository.deleteSystemUser(userName);
                auditDescription = "System User (User name: " + userName.trim().toUpperCase() + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.SYSTEMUSER_MGT_NORECORD_FOUND;
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
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.USER_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmSystemUser(String id) {
        String message = "";
        String auditDescription = "";
        SystemUserInputBean systemUserInputBean = null;
        SystemUser existingSystemUser = null;
        try {
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                systemUserInputBean = new SystemUserInputBean();
                systemUserInputBean.setUserName(tempAuthRecBean.getKey1());
                systemUserInputBean.setFullName(tempAuthRecBean.getKey2());
                systemUserInputBean.setEmail(tempAuthRecBean.getKey3());
                systemUserInputBean.setUserRoleCode(tempAuthRecBean.getKey4());
                systemUserInputBean.setStatus(tempAuthRecBean.getKey5());
                systemUserInputBean.setMobileNumber(tempAuthRecBean.getKey6());
                systemUserInputBean.setPassword(tempAuthRecBean.getKey7());
                systemUserInputBean.setNic(tempAuthRecBean.getKey8());
                systemUserInputBean.setServiceId(tempAuthRecBean.getKey9());
                systemUserInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                systemUserInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing system user
                try {
                    String userName = systemUserInputBean.getUserName();
                    existingSystemUser = systemUserRepository.getSystemUser(userName);
                } catch (EmptyResultDataAccessException e) {
                    existingSystemUser = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSystemUser == null) {
                        systemUserInputBean.setCreatedUser(sessionBean.getUsername());
                        systemUserInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        message = systemUserRepository.insertSystemUser(systemUserInputBean);
                    } else {
                        message = MessageVarList.SYSTEMUSER_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSystemUser != null) {
                        message = systemUserRepository.updateSystemUser(systemUserInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSystemUser != null) {
                        message = systemUserRepository.deleteSystemUser(systemUserInputBean.getUserName().trim());
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                }

                //if task db operation sucess, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success,insert the audit
                    if (message.isEmpty()) {
                        //create audit description
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on task (task code: ").append(systemUserInputBean.getUserName())
                                .append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" approved by ")
                                .append(sessionBean.getUsername());
                        //set the audit trace description
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
            message = MessageVarList.SYSTEMUSER_MGT_NORECORD_FOUND;
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
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.USER_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSystemUserAsString(systemUserInputBean, false));
                audittrace.setOldvalue(this.getSystemUserAsString(existingSystemUser, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectSystemUser(String id) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on system (username: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.USER_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String insertDualAuthRecord(SystemUserInputBean systemUserInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(systemUserInputBean.getUserName(), PageVarList.USER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.USER_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(systemUserInputBean.getUserName());
                tempAuthRecBean.setKey2(systemUserInputBean.getFullName());
                tempAuthRecBean.setKey3(systemUserInputBean.getEmail());
                tempAuthRecBean.setKey4(systemUserInputBean.getUserRoleCode());
                tempAuthRecBean.setKey5(systemUserInputBean.getStatus());
                tempAuthRecBean.setKey6(systemUserInputBean.getMobileNumber());
                tempAuthRecBean.setKey7(sha256Algorithm.makeHash(systemUserInputBean.getPassword()));
                tempAuthRecBean.setKey8(systemUserInputBean.getNic());
                tempAuthRecBean.setKey9(systemUserInputBean.getServiceId());
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

    private String getSystemUserAsString(SystemUserInputBean systemUserInputBean, boolean checkChanges) {
        StringBuilder systemUserStringBuilder = new StringBuilder();
        try {
            if (systemUserInputBean != null) {

                if (systemUserInputBean.getUserName() != null && !systemUserInputBean.getUserName().isEmpty()) {
                    systemUserStringBuilder.append(systemUserInputBean.getUserName());
                } else {
                    systemUserStringBuilder.append("error");
                }

                systemUserStringBuilder.append("|");
                if (systemUserInputBean.getFullName() != null && !systemUserInputBean.getFullName().isEmpty()) {
                    systemUserStringBuilder.append(systemUserInputBean.getFullName());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUserInputBean.getEmail() != null && !systemUserInputBean.getEmail().isEmpty()) {
                    systemUserStringBuilder.append(systemUserInputBean.getEmail());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUserInputBean.getUserRoleCode() != null && !systemUserInputBean.getUserRoleCode().isEmpty()) {
                    systemUserStringBuilder.append(systemUserInputBean.getUserRoleCode());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUserInputBean.getStatus() != null && !systemUserInputBean.getStatus().isEmpty()) {
                    systemUserStringBuilder.append(systemUserInputBean.getStatus());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUserInputBean.getMobileNumber() != null && !systemUserInputBean.getMobileNumber().isEmpty()) {
                    systemUserStringBuilder.append(systemUserInputBean.getMobileNumber());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUserInputBean.getNic() != null && !systemUserInputBean.getNic().isEmpty()) {
                    systemUserStringBuilder.append(systemUserInputBean.getNic());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUserInputBean.getServiceId() != null && !systemUserInputBean.getServiceId().isEmpty()) {
                    systemUserStringBuilder.append(systemUserInputBean.getServiceId());
                } else {
                    systemUserStringBuilder.append("--");
                }

                if (!checkChanges) {
                    systemUserStringBuilder.append("|");
                    if (systemUserInputBean.getCreatedTime() != null) {
                        systemUserStringBuilder.append(common.formatDateToString(systemUserInputBean.getCreatedTime()));
                    } else {
                        systemUserStringBuilder.append("--");
                    }

                    systemUserStringBuilder.append("|");
                    if (systemUserInputBean.getLastUpdatedTime() != null) {
                        systemUserStringBuilder.append(common.formatDateToString(systemUserInputBean.getLastUpdatedTime()));
                    } else {
                        systemUserStringBuilder.append("--");
                    }

                    systemUserStringBuilder.append("|");
                    if (systemUserInputBean.getLastUpdatedUser() != null) {
                        systemUserStringBuilder.append(systemUserInputBean.getLastUpdatedUser());
                    } else {
                        systemUserStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return systemUserStringBuilder.toString();
    }

    private String getSystemUserAsString(SystemUser systemUser, boolean checkChanges) {
        StringBuilder systemUserStringBuilder = new StringBuilder();
        try {
            if (systemUser != null) {

                if (systemUser.getUserName() != null && !systemUser.getUserName().isEmpty()) {
                    systemUserStringBuilder.append(systemUser.getUserName());
                } else {
                    systemUserStringBuilder.append("error");
                }

                systemUserStringBuilder.append("|");
                if (systemUser.getFullName() != null && !systemUser.getFullName().isEmpty()) {
                    systemUserStringBuilder.append(systemUser.getFullName());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUser.getEmail() != null && !systemUser.getEmail().isEmpty()) {
                    systemUserStringBuilder.append(systemUser.getEmail());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUser.getUserRoleCode() != null && !systemUser.getUserRoleCode().isEmpty()) {
                    systemUserStringBuilder.append(systemUser.getUserRoleCode());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUser.getStatus() != null && !systemUser.getStatus().isEmpty()) {
                    systemUserStringBuilder.append(systemUser.getStatus());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUser.getMobileNumber() != null && !systemUser.getMobileNumber().isEmpty()) {
                    systemUserStringBuilder.append(systemUser.getMobileNumber());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUser.getNic() != null && !systemUser.getNic().isEmpty()) {
                    systemUserStringBuilder.append(systemUser.getNic());
                } else {
                    systemUserStringBuilder.append("--");
                }

                systemUserStringBuilder.append("|");
                if (systemUser.getServiceid() != null && !systemUser.getServiceid().isEmpty()) {
                    systemUserStringBuilder.append(systemUser.getServiceid());
                } else {
                    systemUserStringBuilder.append("--");
                }

                if (!checkChanges) {
                    systemUserStringBuilder.append("|");
                    if (systemUser.getCreatedTime() != null) {
                        systemUserStringBuilder.append(common.formatDateToString(systemUser.getCreatedTime()));
                    } else {
                        systemUserStringBuilder.append("--");
                    }

                    systemUserStringBuilder.append("|");
                    if (systemUser.getLastUpdatedTime() != null) {
                        systemUserStringBuilder.append(common.formatDateToString(systemUser.getLastUpdatedTime()));
                    } else {
                        systemUserStringBuilder.append("--");
                    }

                    systemUserStringBuilder.append("|");
                    if (systemUser.getLastUpdatedUser() != null) {
                        systemUserStringBuilder.append(systemUser.getLastUpdatedUser());
                    } else {
                        systemUserStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return systemUserStringBuilder.toString();
    }
}
