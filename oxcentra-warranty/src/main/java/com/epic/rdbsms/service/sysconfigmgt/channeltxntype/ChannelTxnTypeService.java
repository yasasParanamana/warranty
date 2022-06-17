package com.epic.rdbsms.service.sysconfigmgt.channeltxntype;

import com.epic.rdbsms.bean.common.TempAuthRecBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.channeltxntype.ChannelTxnTypeInputBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.channeltxntype.ChannelTxnType;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.repository.sysconfigmgt.channeltxntype.ChannelTxnTypeRepository;
import com.epic.rdbsms.util.common.Common;
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

/**
 * @author Namila Withanage on 11/19/2021
 */
@Service
@Scope("prototype")
public class ChannelTxnTypeService {

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
    ChannelTxnTypeRepository channelTxnTypeRepository;

    private final String fields = "ChannelTxnType TxnType|Channel|Template|Status|Created Time|Created User|Last Updated Time|Last Updated User";


    public long getCount(ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        long count = 0;
        try {
            count = channelTxnTypeRepository.getDataCount(channelTxnTypeInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public long getDataCountDual(ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        long count = 0;
        try {
            count = channelTxnTypeRepository.getDataCountDual(channelTxnTypeInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getChannelTxnTypeSearchResultsDual(ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        List<TempAuthRec> channelTxnTypeDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get channel txn type dual authentication search list.");
            //Get channel txntype dual authentication search list
            channelTxnTypeDualList = channelTxnTypeRepository.getChannelTxnTypeSearchResultsDual(channelTxnTypeInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return channelTxnTypeDualList;
    }

    public String updateChannelTxnType(ChannelTxnTypeInputBean channelTxnTypeInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        ChannelTxnType existingChannelTxnType = null;
        try {
            existingChannelTxnType = channelTxnTypeRepository.getChannelTxnType(channelTxnTypeInputBean.getTxntype(), channelTxnTypeInputBean.getChannel());
            if (existingChannelTxnType != null) {
                //check changed values
                String oldValueAsString = this.getChannelTxnTypeAsString(existingChannelTxnType, true);
                String newValueAsString = this.getChannelTxnTypeAsString(channelTxnTypeInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    channelTxnTypeInputBean.setLastUpdatedTime(currentDate);
                    channelTxnTypeInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE)) {
                        auditDescription = "Requested to update channel txntype (txntype: " + channelTxnTypeInputBean.getTxntype() + ", channel: " + channelTxnTypeInputBean.getChannel() + ", template: " + channelTxnTypeInputBean.getTemplate() + ")";
                        message = this.insertDualAuthRecord(channelTxnTypeInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Channel txntype (txntype: " + channelTxnTypeInputBean.getTxntype() + ", channel: " + channelTxnTypeInputBean.getChannel() + ", template: " + channelTxnTypeInputBean.getTemplate() + ") updated by " + sessionBean.getUsername();
                        message = channelTxnTypeRepository.updateChannelTxnType(channelTxnTypeInputBean);
                    }
                }
            } else {
                message = MessageVarList.CHANNEL_TXN_TYPE_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.CHANNEL_TXN_TYPE_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CHANNEL_TXN_TYPE_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getChannelTxnTypeAsString(existingChannelTxnType, false));
                audittrace.setNewvalue(this.getChannelTxnTypeAsString(channelTxnTypeInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteChannelTxnType(String txntype, String channel) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE)) {
                //get the existing channel txntype
                ChannelTxnType channelTxnType = channelTxnTypeRepository.getChannelTxnType(txntype, channel);
                if (channelTxnType != null) {
                    ChannelTxnTypeInputBean channelTxnTypeInputBean = new ChannelTxnTypeInputBean();
                    //set the values to input bean
                    channelTxnTypeInputBean.setTxntype(channelTxnType.getTxntype());
                    channelTxnTypeInputBean.setTxntypeDescription(channelTxnType.getTxntypeDescription());
                    channelTxnTypeInputBean.setChannel(channelTxnType.getChannelcode());
                    channelTxnTypeInputBean.setChannelDescription(channelTxnType.getChannelDescription());
                    channelTxnTypeInputBean.setTemplate(channelTxnType.getTemplatecode());
                    channelTxnTypeInputBean.setTemplateDescription(channelTxnType.getTemplateDescription());
                    channelTxnTypeInputBean.setStatus(channelTxnType.getStatus());
                    channelTxnTypeInputBean.setCreatedTime(channelTxnType.getCreatedTime());
                    channelTxnTypeInputBean.setLastUpdatedTime(channelTxnType.getLastUpdatedTime());
                    channelTxnTypeInputBean.setLastUpdatedUser(channelTxnType.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted channel txntype (txntype: " + txntype + ", channel: " + channel + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(channelTxnTypeInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.CHANNEL_TXN_TYPE_NORECORD_FOUND;
                }
            } else {
                message = channelTxnTypeRepository.deleteChannelTxnType(txntype, channel);
                auditDescription = "Channel txntype (txntype: " + txntype + ", channel: " + channel + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CHANNEL_TXN_TYPE_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public ChannelTxnType getChannelTxnType(String txntype, String channel) {
        ChannelTxnType channelTxnType;
        try {
            channelTxnType = channelTxnTypeRepository.getChannelTxnType(txntype, channel);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return channelTxnType;
    }

    public List<ChannelTxnType> getChannelTxnTypeResultList(ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        List<ChannelTxnType> channelTxnTypeList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get channel txn type search list.");
            //Get channel txntype search list.
            channelTxnTypeList = channelTxnTypeRepository.getChannelTxnTypeSearchList(channelTxnTypeInputBean);
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
        return channelTxnTypeList;
    }

    public String insertChannelTxnType(ChannelTxnTypeInputBean channelTxnTypeInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            ChannelTxnType channelTxnType = channelTxnTypeRepository.getChannelTxnType(channelTxnTypeInputBean.getTxntype().trim(), channelTxnTypeInputBean.getChannel().trim());
            if (channelTxnType == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                channelTxnTypeInputBean.setCreatedTime(currentDate);
                channelTxnTypeInputBean.setCreatedUser(lastUpdatedUser);
                channelTxnTypeInputBean.setLastUpdatedTime(currentDate);
                channelTxnTypeInputBean.setLastUpdatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE)) {
                    auditDescription = "Requested to add ChannelTxnType (txntype: " + channelTxnTypeInputBean.getTxntype() + ", channel: " + channelTxnTypeInputBean.getChannel() + ", template: " + channelTxnTypeInputBean.getTemplate() + ")";
                    message = this.insertDualAuthRecord(channelTxnTypeInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "ChannelTxnType (txntype: " + channelTxnTypeInputBean.getTxntype() + ", channel: " + channelTxnTypeInputBean.getChannel() + ", template: " + channelTxnTypeInputBean.getTemplate() + ") added by " + sessionBean.getUsername();
                    message = channelTxnTypeRepository.insertChannelTxnType(channelTxnTypeInputBean);
                }
            } else {
                message = MessageVarList.CHANNEL_TXN_TYPE_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.CHANNEL_TXN_TYPE_NORECORD_FOUND, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.CHANNEL_TXN_TYPE_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getChannelTxnTypeAsString(channelTxnTypeInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectChannelTxnType(String txntype) {
        String message = "";
        String auditDescription = "";
        try {
            //get temp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(txntype);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(txntype, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on channelTxnType (txntype: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmChannelTxnType(String id) {
        String message = "";
        String auditDescription = "";
        ChannelTxnTypeInputBean channelTxnTypeInputBean = null;
        ChannelTxnType existingChannelTxnType = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                channelTxnTypeInputBean = new ChannelTxnTypeInputBean();
                channelTxnTypeInputBean.setTxntype(tempAuthRecBean.getKey1());
                channelTxnTypeInputBean.setChannel(tempAuthRecBean.getKey2());
                channelTxnTypeInputBean.setTemplate(tempAuthRecBean.getKey3());
                channelTxnTypeInputBean.setStatus(tempAuthRecBean.getKey4());

                channelTxnTypeInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                channelTxnTypeInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing ChannelTxnType
                try {
                    existingChannelTxnType = channelTxnTypeRepository.getChannelTxnType(channelTxnTypeInputBean.getTxntype(), channelTxnTypeInputBean.getChannel());
                } catch (EmptyResultDataAccessException e) {
                    existingChannelTxnType = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingChannelTxnType == null) {
                        channelTxnTypeInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        channelTxnTypeInputBean.setCreatedUser(sessionBean.getUsername());
                        message = channelTxnTypeRepository.insertChannelTxnType(channelTxnTypeInputBean);
                    } else {
                        message = MessageVarList.CHANNEL_TXN_TYPE_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingChannelTxnType != null) {
                        message = channelTxnTypeRepository.updateChannelTxnType(channelTxnTypeInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingChannelTxnType != null) {
                        message = channelTxnTypeRepository.deleteChannelTxnType(channelTxnTypeInputBean.getTxntype(), channelTxnTypeInputBean.getChannel());
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
                                .append("' operation on task (task txntype: ").append(channelTxnTypeInputBean.getTxntype())
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
            message = MessageVarList.CHANNEL_TXN_TYPE_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getChannelTxnTypeAsString(channelTxnTypeInputBean, false));
                audittrace.setOldvalue(this.getChannelTxnTypeAsString(channelTxnTypeInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String getChannelTxnTypeAsString(ChannelTxnType channelTxnType, boolean checkChanges) {
        StringBuilder channelTxnTypeStringBuilder = new StringBuilder();
        try {
            if (channelTxnType != null) {
                if (channelTxnType.getTxntype() != null && !channelTxnType.getTxntype().isEmpty()) {
                    channelTxnTypeStringBuilder.append(channelTxnType.getTxntype());
                } else {
                    channelTxnTypeStringBuilder.append("--");
                }

                channelTxnTypeStringBuilder.append("|");
                if (channelTxnType.getChannelcode() != null && !channelTxnType.getChannelcode().isEmpty()) {
                    channelTxnTypeStringBuilder.append(channelTxnType.getChannelcode());
                } else {
                    channelTxnTypeStringBuilder.append("--");
                }

                channelTxnTypeStringBuilder.append("|");
                if (channelTxnType.getTemplatecode() != null && !channelTxnType.getTemplatecode().isEmpty()) {
                    channelTxnTypeStringBuilder.append(channelTxnType.getTemplatecode());
                } else {
                    channelTxnTypeStringBuilder.append("--");
                }

                channelTxnTypeStringBuilder.append("|");
                if (channelTxnType.getStatus() != null && !channelTxnType.getStatus().isEmpty()) {
                    channelTxnTypeStringBuilder.append(channelTxnType.getStatus());
                } else {
                    channelTxnTypeStringBuilder.append("--");
                }

                if (!checkChanges) {
                    channelTxnTypeStringBuilder.append("|");
                    if (channelTxnType.getCreatedTime() != null) {
                        channelTxnTypeStringBuilder.append(common.formatDateToString(channelTxnType.getCreatedTime()));
                    } else {
                        channelTxnTypeStringBuilder.append("--");
                    }

                    channelTxnTypeStringBuilder.append("|");
                    if (channelTxnType.getLastUpdatedTime() != null) {
                        channelTxnTypeStringBuilder.append(common.formatDateToString(channelTxnType.getLastUpdatedTime()));
                    } else {
                        channelTxnTypeStringBuilder.append("--");
                    }

                    channelTxnTypeStringBuilder.append("|");
                    if (channelTxnType.getLastUpdatedUser() != null) {
                        channelTxnTypeStringBuilder.append(channelTxnType.getLastUpdatedUser());
                    } else {
                        channelTxnTypeStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return channelTxnTypeStringBuilder.toString();
    }

    private String getChannelTxnTypeAsString(ChannelTxnTypeInputBean channelTxnTypeInputBean, boolean checkChanges) {
        StringBuilder channelTxnTypeStringBuilder = new StringBuilder();
        try {
            if (channelTxnTypeInputBean != null) {

                if (channelTxnTypeInputBean.getTxntype() != null && !channelTxnTypeInputBean.getTxntype().isEmpty()) {
                    channelTxnTypeStringBuilder.append(channelTxnTypeInputBean.getTxntype());
                } else {
                    channelTxnTypeStringBuilder.append("--");
                }

                channelTxnTypeStringBuilder.append("|");
                if (channelTxnTypeInputBean.getChannel() != null && !channelTxnTypeInputBean.getChannel().isEmpty()) {
                    channelTxnTypeStringBuilder.append(channelTxnTypeInputBean.getChannel());
                } else {
                    channelTxnTypeStringBuilder.append("--");
                }

                channelTxnTypeStringBuilder.append("|");
                if (channelTxnTypeInputBean.getTemplate() != null && !channelTxnTypeInputBean.getTemplate().isEmpty()) {
                    channelTxnTypeStringBuilder.append(channelTxnTypeInputBean.getTemplate());
                } else {
                    channelTxnTypeStringBuilder.append("--");
                }

                channelTxnTypeStringBuilder.append("|");
                if (channelTxnTypeInputBean.getStatus() != null && !channelTxnTypeInputBean.getStatus().isEmpty()) {
                    channelTxnTypeStringBuilder.append(channelTxnTypeInputBean.getStatus());
                } else {
                    channelTxnTypeStringBuilder.append("--");
                }

                if (!checkChanges) {
                    channelTxnTypeStringBuilder.append("|");
                    if (channelTxnTypeInputBean.getCreatedTime() != null) {
                        channelTxnTypeStringBuilder.append(common.formatDateToString(channelTxnTypeInputBean.getCreatedTime()));
                    } else {
                        channelTxnTypeStringBuilder.append("--");
                    }

                    channelTxnTypeStringBuilder.append("|");
                    if (channelTxnTypeInputBean.getLastUpdatedTime() != null) {
                        channelTxnTypeStringBuilder.append(common.formatDateToString(channelTxnTypeInputBean.getLastUpdatedTime()));
                    } else {
                        channelTxnTypeStringBuilder.append("--");
                    }

                    channelTxnTypeStringBuilder.append("|");
                    if (channelTxnTypeInputBean.getLastUpdatedUser() != null) {
                        channelTxnTypeStringBuilder.append(channelTxnTypeInputBean.getLastUpdatedUser());
                    } else {
                        channelTxnTypeStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return channelTxnTypeStringBuilder.toString();
    }

    private String insertDualAuthRecord(ChannelTxnTypeInputBean channelTxnTypeInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(channelTxnTypeInputBean.getTxntype(), PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(channelTxnTypeInputBean.getTxntype());
                tempAuthRecBean.setKey2(channelTxnTypeInputBean.getChannel());
                tempAuthRecBean.setKey3(channelTxnTypeInputBean.getTemplate());
                tempAuthRecBean.setKey4(channelTxnTypeInputBean.getStatus());
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
