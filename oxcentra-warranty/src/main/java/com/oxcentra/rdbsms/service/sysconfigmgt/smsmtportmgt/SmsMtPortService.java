package com.oxcentra.rdbsms.service.sysconfigmgt.smsmtportmgt;

import com.oxcentra.rdbsms.bean.common.TempAuthRecBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.smsmtportmanagement.SmsMtPortInputBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.smsmtportmgt.SmsMtPort;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.smsmtportmgt.SmsMtPortRepository;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.varlist.*;
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
public class SmsMtPortService {

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
    SmsMtPortRepository smsMtPortRepository;

    private final String fields = "SmsMtPortInputBean MtPort|Status|Created Time|Created User|Last Updated Time|Last Updated User";

    public long getCount(SmsMtPortInputBean smsMtPortInputBean) {
        long count = 0;
        try {
            count = smsMtPortRepository.getDataCount(smsMtPortInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public long getDataCountDual(SmsMtPortInputBean smsMtPortInputBean) {
        long count = 0;
        try {
            count = smsMtPortRepository.getDataCountDual(smsMtPortInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<SmsMtPort> getMTPortSearchResultList(SmsMtPortInputBean smsMtPortInputBean) {
        List<SmsMtPort> smsMtPortList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SMSMTPORT_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get sms mt port search list.");
            //Get sms mt port search list.
            smsMtPortList = smsMtPortRepository.getSmsMtPortSearchList(smsMtPortInputBean);
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
        return smsMtPortList;
    }

    public List<TempAuthRec> getMTPortSearchResultsDual(SmsMtPortInputBean smsMtPortInputBean) {
        List<TempAuthRec> smsMtPortDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SMSMTPORT_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get sms mt port dual authentication search list.");
            //Get sms mt port dual authentication search list
            smsMtPortDualList = smsMtPortRepository.getSmsMtPortSearchResultsDual(smsMtPortInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return smsMtPortDualList;
    }

    public String rejectSmsMtPort(String mtPort) {
        String message = "";
        String auditDescription = "";
        try {
            //get temp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(mtPort);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(mtPort, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on sms mt port (mt port: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.SMSMTPORT_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String insertSmsMtPort(SmsMtPortInputBean smsMtPortInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            SmsMtPort smsMtPort = smsMtPortRepository.getSmsMtPort(smsMtPortInputBean.getMtPort().trim());
            if (smsMtPort == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                smsMtPortInputBean.setCreatedTime(currentDate);
                smsMtPortInputBean.setLastUpdatedTime(currentDate);
                smsMtPortInputBean.setLastUpdatedUser(lastUpdatedUser);
                smsMtPortInputBean.setCreatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SMSMTPORT_MGT_PAGE)) {
                    auditDescription = "Requested to add sms mt port (mtport: " + smsMtPortInputBean.getMtPort() + ")";
                    message = this.insertDualAuthRecord(smsMtPortInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Sms MT Port (mtport: " + smsMtPortInputBean.getMtPort() + ") added by " + sessionBean.getUsername();
                    message = smsMtPortRepository.insertSmsMtPort(smsMtPortInputBean);
                }
            } else {
                message = MessageVarList.SMSMTPORT_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.SMSMTPORT_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.SMSMTPORT_MGT_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.SMSMTPORT_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSmsMtPortAsString(smsMtPortInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public SmsMtPort getSmsMtPort(String mtport) {
        SmsMtPort smsMtPort;
        try {
            smsMtPort = smsMtPortRepository.getSmsMtPort(mtport);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return smsMtPort;
    }

    private String getSmsMtPortAsString(SmsMtPortInputBean smsMtPortInputBean, boolean checkChanges) {
        StringBuilder smsMtPortStringBuilder = new StringBuilder();
        try {
            if (smsMtPortInputBean != null) {

                if (smsMtPortInputBean.getMtPort() != null && !smsMtPortInputBean.getMtPort().isEmpty()) {
                    smsMtPortStringBuilder.append(smsMtPortInputBean.getMtPort());
                } else {
                    smsMtPortStringBuilder.append("error");
                }

                smsMtPortStringBuilder.append("|");
                if (smsMtPortInputBean.getStatus() != null && !smsMtPortInputBean.getStatus().isEmpty()) {
                    smsMtPortStringBuilder.append(smsMtPortInputBean.getStatus());
                } else {
                    smsMtPortStringBuilder.append("--");
                }

                if (!checkChanges) {
                    smsMtPortStringBuilder.append("|");
                    if (smsMtPortInputBean.getCreatedTime() != null) {
                        smsMtPortStringBuilder.append(common.formatDateToString(smsMtPortInputBean.getCreatedTime()));
                    } else {
                        smsMtPortStringBuilder.append("--");
                    }

                    smsMtPortStringBuilder.append("|");
                    if (smsMtPortInputBean.getCreatedUser() != null) {
                        smsMtPortStringBuilder.append(smsMtPortInputBean.getCreatedUser());
                    } else {
                        smsMtPortStringBuilder.append("--");
                    }

                    smsMtPortStringBuilder.append("|");
                    if (smsMtPortInputBean.getLastUpdatedTime() != null) {
                        smsMtPortStringBuilder.append(common.formatDateToString(smsMtPortInputBean.getLastUpdatedTime()));
                    } else {
                        smsMtPortStringBuilder.append("--");
                    }

                    smsMtPortStringBuilder.append("|");
                    if (smsMtPortInputBean.getLastUpdatedUser() != null) {
                        smsMtPortStringBuilder.append(smsMtPortInputBean.getLastUpdatedUser());
                    } else {
                        smsMtPortStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return smsMtPortStringBuilder.toString();
    }

    public String deleteSmsMtPort(String mtPort) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SMSMTPORT_MGT_PAGE)) {
                //get the existing sms mt port
                SmsMtPort smsMtPort = smsMtPortRepository.getSmsMtPort(mtPort);
                if (smsMtPort != null) {
                    SmsMtPortInputBean smsMtPortInputBean = new SmsMtPortInputBean();
                    //set the values to input bean
                    smsMtPortInputBean.setMtPort(smsMtPort.getMtPort());
                    smsMtPortInputBean.setStatus(smsMtPort.getStatus());
                    smsMtPortInputBean.setCreatedTime(smsMtPort.getCreatedTime());
                    smsMtPortInputBean.setCreatedUser(smsMtPort.getCreatedUser());
                    smsMtPortInputBean.setLastUpdatedTime(smsMtPort.getLastUpdatedTime());
                    smsMtPortInputBean.setLastUpdatedUser(smsMtPort.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted sms mt port (mt port: " + mtPort + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(smsMtPortInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.SMSMTPORT_MGT_NORECORD_FOUND;
                }
            } else {
                message = smsMtPortRepository.deleteSmsMtPort(mtPort);
                auditDescription = "SMS MT Port (MT Port: " + mtPort + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.SMSMTPORT_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.SMSMTPORT_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmSmsMtPort(String id) {
        String message = "";
        String auditDescription = "";
        SmsMtPortInputBean smsMtPortInputBean = null;
        SmsMtPort existingSmsMtPort = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                smsMtPortInputBean = new SmsMtPortInputBean();
                smsMtPortInputBean.setMtPort(tempAuthRecBean.getKey1());
                smsMtPortInputBean.setStatus(tempAuthRecBean.getKey3());
                smsMtPortInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                smsMtPortInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing sms mt port
                try {
                    String mtPort = smsMtPortInputBean.getMtPort();
                    existingSmsMtPort = smsMtPortRepository.getSmsMtPort(mtPort);
                } catch (EmptyResultDataAccessException e) {
                    existingSmsMtPort = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSmsMtPort == null) {
                        smsMtPortInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        smsMtPortInputBean.setCreatedUser(sessionBean.getUsername());
                        message = smsMtPortRepository.insertSmsMtPort(smsMtPortInputBean);
                    } else {
                        message = MessageVarList.SMSMTPORT_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSmsMtPort != null) {
                        message = smsMtPortRepository.updateSmsMtPort(smsMtPortInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSmsMtPort != null) {
                        message = smsMtPortRepository.deleteSmsMtPort(smsMtPortInputBean.getMtPort());
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
                                .append("' operation on task (task mt port: ").append(smsMtPortInputBean.getMtPort())
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
            message = MessageVarList.SMSMTPORT_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.SMSMTPORT_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSmsMtPortAsString(smsMtPortInputBean, false));
                audittrace.setOldvalue(this.getSmsMtPortAsString(smsMtPortInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String updateSmsMtPort(SmsMtPortInputBean smsMtPortInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        SmsMtPort existingSmsMtPort = null;
        try {
            existingSmsMtPort = smsMtPortRepository.getSmsMtPort(smsMtPortInputBean.getMtPort());
            if (existingSmsMtPort != null) {
                //check changed values
                String oldValueAsString = this.getSmsMtPortAsString(existingSmsMtPort, true);
                String newValueAsString = this.getSmsMtPortAsString(smsMtPortInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    smsMtPortInputBean.setLastUpdatedTime(currentDate);
                    smsMtPortInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SMSMTPORT_MGT_PAGE)) {
                        auditDescription = "Requested to update sms mt port (mt port: " + smsMtPortInputBean.getMtPort() + ")";
                        message = this.insertDualAuthRecord(smsMtPortInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Sms Mt Port (mt port: " + smsMtPortInputBean.getMtPort() + ") updated by " + sessionBean.getUsername();
                        message = smsMtPortRepository.updateSmsMtPort(smsMtPortInputBean);
                    }
                }
            } else {
                message = MessageVarList.SMSMTPORT_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.SMSMTPORT_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.SMSMTPORT_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.SMSMTPORT_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getSmsMtPortAsString(existingSmsMtPort, false));
                audittrace.setNewvalue(this.getSmsMtPortAsString(smsMtPortInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String getSmsMtPortAsString(SmsMtPort smsMtPort, boolean checkChanges) {
        StringBuilder smsMtPortStringBuilder = new StringBuilder();
        try {
            if (smsMtPort != null) {
                if (smsMtPort.getMtPort() != null && !smsMtPort.getMtPort().isEmpty()) {
                    smsMtPortStringBuilder.append(smsMtPort.getMtPort());
                } else {
                    smsMtPortStringBuilder.append("error");
                }

                smsMtPortStringBuilder.append("|");
                if (smsMtPort.getStatus() != null && !smsMtPort.getStatus().isEmpty()) {
                    smsMtPortStringBuilder.append(smsMtPort.getStatus());
                } else {
                    smsMtPortStringBuilder.append("--");
                }

                if (!checkChanges) {
                    smsMtPortStringBuilder.append("|");
                    if (smsMtPort.getCreatedTime() != null) {
                        smsMtPortStringBuilder.append(common.formatDateToString(smsMtPort.getCreatedTime()));
                    } else {
                        smsMtPortStringBuilder.append("--");
                    }

                    smsMtPortStringBuilder.append("|");
                    if (smsMtPort.getCreatedUser() != null) {
                        smsMtPortStringBuilder.append(smsMtPort.getCreatedUser());
                    } else {
                        smsMtPortStringBuilder.append("--");
                    }

                    smsMtPortStringBuilder.append("|");
                    if (smsMtPort.getLastUpdatedTime() != null) {
                        smsMtPortStringBuilder.append(common.formatDateToString(smsMtPort.getLastUpdatedTime()));
                    } else {
                        smsMtPortStringBuilder.append("--");
                    }

                    smsMtPortStringBuilder.append("|");
                    if (smsMtPort.getLastUpdatedUser() != null) {
                        smsMtPortStringBuilder.append(smsMtPort.getLastUpdatedUser());
                    } else {
                        smsMtPortStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return smsMtPortStringBuilder.toString();
    }

    private String insertDualAuthRecord(SmsMtPortInputBean smsMtPortInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(smsMtPortInputBean.getMtPort(), PageVarList.SMSMTPORT_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.SMSMTPORT_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(smsMtPortInputBean.getMtPort());
                tempAuthRecBean.setKey3(smsMtPortInputBean.getStatus());
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
