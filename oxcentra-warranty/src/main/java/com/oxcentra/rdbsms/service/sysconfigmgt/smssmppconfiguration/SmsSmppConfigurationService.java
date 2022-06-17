package com.oxcentra.rdbsms.service.sysconfigmgt.smssmppconfiguration;

import com.oxcentra.rdbsms.bean.common.TempAuthRecBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.smssmppconfiguration.SmsSmppConfigurationInputBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.smppconfig.SmppConfiguration;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.smssmppconfiguration.SmppConfigurationRepository;
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
public class SmsSmppConfigurationService {

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
    SmppConfigurationRepository smppConfigurationRepository;

    private final String fields = "SmsSmppConfigurationInputBean SMPPCode|Description|Status|Max TPS|Primary IP|Secondary IP|System ID|Password|Bind Port|Bind Mode|MT Port|MO Port|Max Bulk TPS|Created Time|Created User|Last Updated Time|Last Updated User";

    public long getCount(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        long count = 0;
        try {
            count = smppConfigurationRepository.getDataCount(smsSmppConfigurationInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<SmppConfiguration> getSmppConfigSearchResultList(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        List<SmppConfiguration> smppConfigurationList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SMPPCONFIG_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get SMPP configuration search list.");
            //Get SMPP configuration search list.
            smppConfigurationList = smppConfigurationRepository.getSmppConfigurationSearchList(smsSmppConfigurationInputBean);
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
        return smppConfigurationList;
    }

    public long getDataCountDual(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        long count = 0;
        try {
            count = smppConfigurationRepository.getDataCountDual(smsSmppConfigurationInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getSmppConfigurationSearchResultsDual(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        List<TempAuthRec> smppConfigurationDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SMPPCONFIG_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get smpp dual authentication search list.");
            //Get smpp configuration dual authentication search list
            smppConfigurationDualList = smppConfigurationRepository.getSmppConfigurationSearchResultsDual(smsSmppConfigurationInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return smppConfigurationDualList;
    }

    public String insertSmppConfiguration(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            SmppConfiguration smppConfiguration = smppConfigurationRepository.getSmppConfiguration(smsSmppConfigurationInputBean.getSmppCode().trim());
            if (smppConfiguration == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                smsSmppConfigurationInputBean.setCreatedUser(lastUpdatedUser);
                smsSmppConfigurationInputBean.setCreatedTime(currentDate);
                smsSmppConfigurationInputBean.setLastUpdatedTime(currentDate);
                smsSmppConfigurationInputBean.setLastUpdatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SMPPCONFIG_MGT_PAGE)) {
                    auditDescription = "Requested to add smpp configuration (Smpp code: " + smsSmppConfigurationInputBean.getSmppCode() + ")";
                    message = this.insertDualAuthRecord(smsSmppConfigurationInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Smpp Configuration (Smpp code: " + smsSmppConfigurationInputBean.getSmppCode() + ") added by " + sessionBean.getUsername();
                    message = smppConfigurationRepository.insertSmppConfiguration(smsSmppConfigurationInputBean);
                }
            } else {
                message = MessageVarList.SMPP_CONFIGURATION_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.SMPP_CONFIGURATION_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.SMPP_CONFIGURATION_MGT_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.SMPPCONFIG_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSmppConfigurationAsString(smsSmppConfigurationInputBean, false));
            }
        }
        return message;
    }

    public SmppConfiguration getSmppConfiguration(String smppCode) {
        SmppConfiguration smppConfiguration;
        try {
            smppConfiguration = smppConfigurationRepository.getSmppConfiguration(smppCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return smppConfiguration;
    }

    public String updateSmppConfiguration(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        SmppConfiguration existingSmppConfiguration = null;
        try {
            existingSmppConfiguration = smppConfigurationRepository.getSmppConfiguration(smsSmppConfigurationInputBean.getSmppCode());
            if (existingSmppConfiguration != null) {
                //check changed values
                String oldValueAsString = this.getSmppConfigurationAsString(existingSmppConfiguration, true);
                String newValueAsString = this.getSmppConfigurationAsString(smsSmppConfigurationInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    smsSmppConfigurationInputBean.setLastUpdatedTime(currentDate);
                    smsSmppConfigurationInputBean.setLastUpdatedUser(lastUpdatedUser);
                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SMPPCONFIG_MGT_PAGE)) {
                        auditDescription = "Requested to update smpp configuration (Smpp code: " + smsSmppConfigurationInputBean.getSmppCode() + ")";
                        message = this.insertDualAuthRecord(smsSmppConfigurationInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Smpp configuration (Smpp code: " + smsSmppConfigurationInputBean.getSmppCode() + ") updated by " + sessionBean.getUsername();
                        message = smppConfigurationRepository.updateSmppConfiguration(smsSmppConfigurationInputBean);
                    }
                }
            } else {
                message = MessageVarList.SMPP_CONFIGURATION_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.SMPP_CONFIGURATION_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.SMPP_CONFIGURATION_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.SMPPCONFIG_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getSmppConfigurationAsString(existingSmppConfiguration, false));
                audittrace.setNewvalue(this.getSmppConfigurationAsString(smsSmppConfigurationInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteSmppConfiguration(String smppCode) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SMPPCONFIG_MGT_PAGE)) {
                //get the existing smpp configuration
                SmppConfiguration smppConfiguration = smppConfigurationRepository.getSmppConfiguration(smppCode);
                if (smppConfiguration != null) {
                    SmsSmppConfigurationInputBean smsSmppConfigurationInputBean = new SmsSmppConfigurationInputBean();
                    //set the values to input bean
                    smsSmppConfigurationInputBean.setSmppCode(smppConfiguration.getSmppCode());
                    smsSmppConfigurationInputBean.setDescription(smppConfiguration.getDescription());
                    smsSmppConfigurationInputBean.setStatus(smppConfiguration.getStatus());
                    smsSmppConfigurationInputBean.setMaxTps(String.valueOf(smppConfiguration.getMaxTps()));
                    smsSmppConfigurationInputBean.setPrimaryIp(smppConfiguration.getPrimaryIp());
                    smsSmppConfigurationInputBean.setSecondaryIp(smppConfiguration.getSecondaryIp());
                    smsSmppConfigurationInputBean.setSystemId(smppConfiguration.getSystemId());
                    smsSmppConfigurationInputBean.setPassword(smppConfiguration.getPassword());
                    smsSmppConfigurationInputBean.setBindMode(smppConfiguration.getBindMode());
                    smsSmppConfigurationInputBean.setMtPort(smppConfiguration.getMtPort());
                    smsSmppConfigurationInputBean.setMoPort(smppConfiguration.getMoPort());
                    smsSmppConfigurationInputBean.setMaxBulkTps(String.valueOf(smppConfiguration.getMaxBulkTps()));
                    smsSmppConfigurationInputBean.setCreatedTime(smppConfiguration.getCreatedTime());
                    smsSmppConfigurationInputBean.setCreatedUser(smppConfiguration.getCreatedUser());
                    smsSmppConfigurationInputBean.setLastUpdatedTime(smppConfiguration.getLastUpdatedTime());
                    smsSmppConfigurationInputBean.setLastUpdatedUser(smppConfiguration.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted Smpp configuration (Smpp code: " + smppCode + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(smsSmppConfigurationInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.SMPP_CONFIGURATION_MGT_NORECORD_FOUND;
                }
            } else {
                message = smppConfigurationRepository.deleteSmppConfiguration(smppCode);
                auditDescription = "Smpp Configuration (Smpp code: " + smppCode + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.SMPP_CONFIGURATION_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.SMPPCONFIG_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmSmppConfiguration(String id) {
        String message = "";
        String auditDescription = "";
        SmsSmppConfigurationInputBean smsSmppConfigurationInputBean = null;
        SmppConfiguration existingSmppConfiguration = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                smsSmppConfigurationInputBean = new SmsSmppConfigurationInputBean();
                smsSmppConfigurationInputBean.setSmppCode(tempAuthRecBean.getKey1());
                smsSmppConfigurationInputBean.setDescription(tempAuthRecBean.getKey2());
                smsSmppConfigurationInputBean.setStatus(tempAuthRecBean.getKey3());
                smsSmppConfigurationInputBean.setMaxTps(tempAuthRecBean.getKey4());
                smsSmppConfigurationInputBean.setPrimaryIp(tempAuthRecBean.getKey5());
                smsSmppConfigurationInputBean.setSecondaryIp(tempAuthRecBean.getKey6());
                smsSmppConfigurationInputBean.setSystemId(tempAuthRecBean.getKey7());
                smsSmppConfigurationInputBean.setPassword(tempAuthRecBean.getKey8());
                smsSmppConfigurationInputBean.setBindPort(tempAuthRecBean.getKey9());
                smsSmppConfigurationInputBean.setBindMode(tempAuthRecBean.getKey10());
                smsSmppConfigurationInputBean.setMtPort(tempAuthRecBean.getKey11());
                smsSmppConfigurationInputBean.setMoPort(tempAuthRecBean.getKey12());
                smsSmppConfigurationInputBean.setMaxBulkTps(tempAuthRecBean.getKey13());
                smsSmppConfigurationInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                smsSmppConfigurationInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing smpp configuration
                try {
                    String code = smsSmppConfigurationInputBean.getSmppCode();
                    existingSmppConfiguration = smppConfigurationRepository.getSmppConfiguration(code);
                } catch (EmptyResultDataAccessException e) {
                    existingSmppConfiguration = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSmppConfiguration == null) {
                        smsSmppConfigurationInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        smsSmppConfigurationInputBean.setCreatedUser(sessionBean.getUsername());
                        message = smppConfigurationRepository.insertSmppConfiguration(smsSmppConfigurationInputBean);
                    } else {
                        message = MessageVarList.SMPP_CONFIGURATION_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSmppConfiguration != null) {
                        message = smppConfigurationRepository.updateSmppConfiguration(smsSmppConfigurationInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSmppConfiguration != null) {
                        message = smppConfigurationRepository.deleteSmppConfiguration(smsSmppConfigurationInputBean.getSmppCode());
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
                                .append("' operation on task (task code: ").append(smsSmppConfigurationInputBean.getSmppCode())
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
            message = MessageVarList.SMPP_CONFIGURATION_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.DEPARTMENT_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSmppConfigurationAsString(smsSmppConfigurationInputBean, false));
                audittrace.setOldvalue(this.getSmppConfigurationAsString(existingSmppConfiguration, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectSmppConfiguration(String code) {
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
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on smpp configuration (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.SMPPCONFIG_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String insertDualAuthRecord(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(smsSmppConfigurationInputBean.getSmppCode(), PageVarList.SMPPCONFIG_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.SMPPCONFIG_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(smsSmppConfigurationInputBean.getSmppCode());
                tempAuthRecBean.setKey2(smsSmppConfigurationInputBean.getDescription());
                tempAuthRecBean.setKey3(smsSmppConfigurationInputBean.getStatus());
                tempAuthRecBean.setKey4(String.valueOf(smsSmppConfigurationInputBean.getMaxTps()));
                tempAuthRecBean.setKey5(smsSmppConfigurationInputBean.getPrimaryIp());
                tempAuthRecBean.setKey6(smsSmppConfigurationInputBean.getSecondaryIp());
                tempAuthRecBean.setKey7(smsSmppConfigurationInputBean.getSystemId());
                tempAuthRecBean.setKey8(smsSmppConfigurationInputBean.getPassword());
                tempAuthRecBean.setKey9(String.valueOf(smsSmppConfigurationInputBean.getBindPort()));
                tempAuthRecBean.setKey10(smsSmppConfigurationInputBean.getBindMode());
                tempAuthRecBean.setKey11(smsSmppConfigurationInputBean.getMtPort());
                tempAuthRecBean.setKey12(smsSmppConfigurationInputBean.getMoPort());
                tempAuthRecBean.setKey13(String.valueOf(smsSmppConfigurationInputBean.getMaxBulkTps()));
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

    private String getSmppConfigurationAsString(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean, boolean checkChanges) {
        StringBuilder smppConfigurationStringBuilder = new StringBuilder();
        try {
            if (smppConfigurationStringBuilder != null) {
                if (smsSmppConfigurationInputBean.getSmppCode() != null && !smsSmppConfigurationInputBean.getSmppCode().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getSmppCode());
                } else {
                    smppConfigurationStringBuilder.append("error");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getDescription() != null && !smsSmppConfigurationInputBean.getDescription().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getDescription());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getStatus() != null && !smsSmppConfigurationInputBean.getStatus().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getStatus());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getMaxTps() != null && !smsSmppConfigurationInputBean.getMaxTps().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getMaxTps());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getPrimaryIp() != null && !smsSmppConfigurationInputBean.getPrimaryIp().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getPrimaryIp());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getSecondaryIp() != null && !smsSmppConfigurationInputBean.getSecondaryIp().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getSecondaryIp());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getSystemId() != null && !smsSmppConfigurationInputBean.getSystemId().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getSystemId());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getPassword() != null && !smsSmppConfigurationInputBean.getPassword().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getPassword());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getBindPort() != null && !smsSmppConfigurationInputBean.getBindPort().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getBindPort());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getBindMode() != null && !smsSmppConfigurationInputBean.getBindMode().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getBindMode());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getMtPort() != null && !smsSmppConfigurationInputBean.getMtPort().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getMtPort());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getMoPort() != null && !smsSmppConfigurationInputBean.getMoPort().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getMoPort());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smsSmppConfigurationInputBean.getMaxBulkTps() != null && !smsSmppConfigurationInputBean.getMaxBulkTps().isEmpty()) {
                    smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getMaxBulkTps());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                if (!checkChanges) {
                    smppConfigurationStringBuilder.append("|");
                    if (smsSmppConfigurationInputBean.getCreatedTime() != null) {
                        smppConfigurationStringBuilder.append(common.formatDateToString(smsSmppConfigurationInputBean.getCreatedTime()));
                    } else {
                        smppConfigurationStringBuilder.append("--");
                    }

                    smppConfigurationStringBuilder.append("|");
                    if (smsSmppConfigurationInputBean.getCreatedUser() != null) {
                        smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getCreatedUser());
                    } else {
                        smppConfigurationStringBuilder.append("--");
                    }

                    smppConfigurationStringBuilder.append("|");
                    if (smsSmppConfigurationInputBean.getLastUpdatedTime() != null) {
                        smppConfigurationStringBuilder.append(common.formatDateToString(smsSmppConfigurationInputBean.getLastUpdatedTime()));
                    } else {
                        smppConfigurationStringBuilder.append("--");
                    }

                    smppConfigurationStringBuilder.append("|");
                    if (smsSmppConfigurationInputBean.getLastUpdatedUser() != null) {
                        smppConfigurationStringBuilder.append(smsSmppConfigurationInputBean.getLastUpdatedUser());
                    } else {
                        smppConfigurationStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return smppConfigurationStringBuilder.toString();
    }

    private String getSmppConfigurationAsString(SmppConfiguration smppConfiguration, boolean checkChanges) {
        StringBuilder smppConfigurationStringBuilder = new StringBuilder();
        try {
            if (smppConfiguration != null) {

                if (smppConfiguration.getSmppCode() != null && !smppConfiguration.getSmppCode().isEmpty()) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getSmppCode());
                } else {
                    smppConfigurationStringBuilder.append("error");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getDescription() != null && !smppConfiguration.getDescription().isEmpty()) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getDescription());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getStatus() != null && !smppConfiguration.getStatus().isEmpty()) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getStatus());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getMaxTps() != 0) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getMaxTps());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getPrimaryIp() != null && !smppConfiguration.getPrimaryIp().isEmpty()) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getPrimaryIp());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getSecondaryIp() != null && !smppConfiguration.getSecondaryIp().isEmpty()) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getSecondaryIp());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getSystemId() != null && !smppConfiguration.getSystemId().isEmpty()) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getSystemId());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getPassword() != null && !smppConfiguration.getPassword().isEmpty()) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getPassword());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getBindPort() != 0) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getBindPort());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getBindMode() != null && !smppConfiguration.getBindMode().isEmpty()) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getBindMode());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getMtPort() != null && !smppConfiguration.getMtPort().isEmpty()) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getMtPort());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getMoPort() != null && !smppConfiguration.getMoPort().isEmpty()) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getMoPort());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                smppConfigurationStringBuilder.append("|");
                if (smppConfiguration.getMaxBulkTps() != 0) {
                    smppConfigurationStringBuilder.append(smppConfiguration.getMaxBulkTps());
                } else {
                    smppConfigurationStringBuilder.append("--");
                }

                if (!checkChanges) {
                    smppConfigurationStringBuilder.append("|");
                    if (smppConfiguration.getCreatedTime() != null) {
                        smppConfigurationStringBuilder.append(common.formatDateToString(smppConfiguration.getCreatedTime()));
                    } else {
                        smppConfigurationStringBuilder.append("--");
                    }

                    smppConfigurationStringBuilder.append("|");
                    if (smppConfiguration.getCreatedUser() != null) {
                        smppConfigurationStringBuilder.append(smppConfiguration.getCreatedUser());
                    } else {
                        smppConfigurationStringBuilder.append("--");
                    }

                    smppConfigurationStringBuilder.append("|");
                    if (smppConfiguration.getLastUpdatedTime() != null) {
                        smppConfigurationStringBuilder.append(common.formatDateToString(smppConfiguration.getLastUpdatedTime()));
                    } else {
                        smppConfigurationStringBuilder.append("--");
                    }

                    smppConfigurationStringBuilder.append("|");
                    if (smppConfiguration.getLastUpdatedUser() != null) {
                        smppConfigurationStringBuilder.append(smppConfiguration.getLastUpdatedUser());
                    } else {
                        smppConfigurationStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return smppConfigurationStringBuilder.toString();
    }
}
