package com.epic.rdbsms.service.sysconfigmgt.channel;

import com.epic.rdbsms.bean.channel.ChannelInputBean;
import com.epic.rdbsms.bean.common.TempAuthRecBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.channel.Channel;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.repository.sysconfigmgt.channel.ChannelRepository;
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

@Service
@Scope("prototype")
public class ChannelService {

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
    ChannelRepository channelRepository;

    private final String fields = "ChannelInputBean Code|Description|Status|Created Time|Created User|Last Updated Time|Last Updated User";

    public long getCount(ChannelInputBean channelInputBean) {
        long count = 0;
        try {
            count = channelRepository.getDataCount(channelInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Channel> getChannelSearchResultList(ChannelInputBean channelInputBean) {
        List<Channel> channelList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CHANNEL_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get channel search list.");
            //Get channel search list.
            channelList = channelRepository.getChannelSearchList(channelInputBean);
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
        return channelList;
    }

    public long getDataCountDual(ChannelInputBean channelInputBean) {
        long count = 0;
        try {
            count = channelRepository.getDataCountDual(channelInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getChannelSearchResultsDual(ChannelInputBean channelInputBean) {
        List<TempAuthRec> channelDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CHANNEL_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get channel dual authentication search list.");
            //Get channel dual authentication search list
            channelDualList = channelRepository.getChannelSearchResultsDual(channelInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return channelDualList;
    }

    public String insertChannel(ChannelInputBean channelInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            Channel channel = channelRepository.getChannel(channelInputBean.getChannelCode().trim());
            if (channel == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                channelInputBean.setCreatedUser(lastUpdatedUser);
                channelInputBean.setCreatedTime(currentDate);
                channelInputBean.setLastUpdatedTime(currentDate);
                channelInputBean.setLastUpdatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CHANNEL_MGT_PAGE)) {
                    auditDescription = "Requested to add channel (code: " + channelInputBean.getChannelCode() + ")";
                    message = this.insertDualAuthRecord(channelInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Channel (code: " + channelInputBean.getChannelCode() + ") added by " + sessionBean.getUsername();
                    message = channelRepository.insertChannel(channelInputBean);
                }
            } else {
                message = MessageVarList.CHANNEL_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.CHANNEL_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.CHANNEL_MGT_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.CHANNEL_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getChannelAsString(channelInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }


    public Channel getChannel(String code) {
        Channel channel;
        try {
            channel = channelRepository.getChannel(code);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return channel;
    }

    public String updateChannel(ChannelInputBean channelInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        Channel existingChannel = null;
        try {
            existingChannel = channelRepository.getChannel(channelInputBean.getChannelCode());
            if (existingChannel != null) {
                //check changed values
                String oldValueAsString = this.getChannelAsString(existingChannel, true);
                String newValueAsString = this.getChannelAsString(channelInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    channelInputBean.setLastUpdatedTime(currentDate);
                    channelInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CHANNEL_MGT_PAGE)) {
                        auditDescription = "Requested to update channel (code: " + channelInputBean.getChannelCode() + ")";
                        message = this.insertDualAuthRecord(channelInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Channel (code: " + channelInputBean.getChannelCode() + ") updated by " + sessionBean.getUsername();
                        message = channelRepository.updateChannel(channelInputBean);
                    }
                }
            } else {
                message = MessageVarList.DEPARTMENT_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.DEPARTMENT_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.DEPARTMENT_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CHANNEL_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getChannelAsString(existingChannel, false));
                audittrace.setNewvalue(this.getChannelAsString(channelInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteChannel(String channelCode) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CHANNEL_MGT_PAGE)) {
                //get the existing channel
                Channel channel = channelRepository.getChannel(channelCode);
                if (channel != null) {
                    ChannelInputBean channelInputBean = new ChannelInputBean();
                    //set the values to input bean
                    channelInputBean.setChannelCode(channel.getChannelCode());
                    channelInputBean.setDescription(channel.getDescription());
                    channelInputBean.setStatus(channel.getStatus());
                    channelInputBean.setCreatedTime(channel.getCreatedTime());
                    channelInputBean.setCreatedUser(channel.getCreatedUser());
                    channelInputBean.setLastUpdatedTime(channel.getLastUpdatedTime());
                    channelInputBean.setLastUpdatedUser(channel.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted channel (channel code: " + channelCode + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(channelInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.CHANNEL_MGT_NORECORD_FOUND;
                }
            } else {
                message = channelRepository.deleteChannel(channelCode);
                auditDescription = "Channel (Channel Code: " + channelCode + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CHANNEL_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CHANNEL_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmChannel(String id) {
        String message = "";
        String auditDescription = "";
        ChannelInputBean channelInputBean = null;
        Channel existingChannel = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                channelInputBean = new ChannelInputBean();
                channelInputBean.setChannelCode(tempAuthRecBean.getKey1());
                channelInputBean.setDescription(tempAuthRecBean.getKey2());
                channelInputBean.setPassword(tempAuthRecBean.getKey7());
                channelInputBean.setStatus(tempAuthRecBean.getKey3());

                channelInputBean.setLastUpdatedUser(sessionBean.getUsername());
                channelInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());

                //get the existing channel
                try {
                    String code = channelInputBean.getChannelCode();
                    existingChannel = channelRepository.getChannel(code);
                } catch (EmptyResultDataAccessException e) {
                    existingChannel = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingChannel == null) {
                        channelInputBean.setCreatedUser(sessionBean.getUsername());
                        channelInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        message = channelRepository.insertChannel(channelInputBean);
                    } else {
                        message = MessageVarList.DEPARTMENT_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingChannel != null) {
                        message = channelRepository.updateChannel(channelInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingChannel != null) {
                        message = channelRepository.deleteChannel(channelInputBean.getChannelCode());
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
                                .append("' operation on task (task code: ").append(channelInputBean.getChannelCode())
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
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.CHANNEL_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getChannelAsString(channelInputBean, false));
                audittrace.setOldvalue(this.getChannelAsString(existingChannel, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectChannel(String code) {
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
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on channel (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.CHANNEL_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String insertDualAuthRecord(ChannelInputBean channelInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(channelInputBean.getChannelCode(), PageVarList.CHANNEL_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.CHANNEL_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(channelInputBean.getChannelCode());
                tempAuthRecBean.setKey2(channelInputBean.getDescription());
                tempAuthRecBean.setKey7(channelInputBean.getPassword());
                tempAuthRecBean.setKey3(channelInputBean.getStatus());
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

    private String getChannelAsString(ChannelInputBean channelInputBean, boolean checkChanges) {
        StringBuilder channelStringBuilder = new StringBuilder();
        try {
            if (channelInputBean != null) {

                if (channelInputBean.getChannelCode() != null && !channelInputBean.getChannelCode().isEmpty()) {
                    channelStringBuilder.append(channelInputBean.getChannelCode());
                } else {
                    channelStringBuilder.append("error");
                }

                channelStringBuilder.append("|");
                if (channelInputBean.getDescription() != null && !channelInputBean.getDescription().isEmpty()) {
                    channelStringBuilder.append(channelInputBean.getDescription());
                } else {
                    channelStringBuilder.append("--");
                }

                channelStringBuilder.append("|");
                if (channelInputBean.getStatus() != null && !channelInputBean.getStatus().isEmpty()) {
                    channelStringBuilder.append(channelInputBean.getStatus());
                } else {
                    channelStringBuilder.append("--");
                }

                if (!checkChanges) {
                    channelStringBuilder.append("|");
                    if (channelInputBean.getCreatedTime() != null) {
                        channelStringBuilder.append(common.formatDateToString(channelInputBean.getCreatedTime()));
                    } else {
                        channelStringBuilder.append("--");
                    }

                    channelStringBuilder.append("|");
                    if (channelInputBean.getCreatedUser() != null) {
                        channelStringBuilder.append(channelInputBean.getCreatedUser());
                    } else {
                        channelStringBuilder.append("--");
                    }

                    channelStringBuilder.append("|");
                    if (channelInputBean.getLastUpdatedTime() != null) {
                        channelStringBuilder.append(common.formatDateToString(channelInputBean.getLastUpdatedTime()));
                    } else {
                        channelStringBuilder.append("--");
                    }

                    channelStringBuilder.append("|");
                    if (channelInputBean.getLastUpdatedUser() != null) {
                        channelStringBuilder.append(channelInputBean.getLastUpdatedUser());
                    } else {
                        channelStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return channelStringBuilder.toString();
    }

    private String getChannelAsString(Channel channel, boolean checkChanges) {
        StringBuilder channelStringBuilder = new StringBuilder();
        try {
            if (channel != null) {
                if (channel.getChannelCode() != null && !channel.getChannelCode().isEmpty()) {
                    channelStringBuilder.append(channel.getChannelCode());
                } else {
                    channelStringBuilder.append("error");
                }

                channelStringBuilder.append("|");
                if (channel.getDescription() != null && !channel.getDescription().isEmpty()) {
                    channelStringBuilder.append(channel.getDescription());
                } else {
                    channelStringBuilder.append("--");
                }

                channelStringBuilder.append("|");
                if (channel.getStatus() != null && !channel.getStatus().isEmpty()) {
                    channelStringBuilder.append(channel.getStatus());
                } else {
                    channelStringBuilder.append("--");
                }

                if (!checkChanges) {
                    channelStringBuilder.append("|");
                    if (channel.getCreatedTime() != null) {
                        channelStringBuilder.append(common.formatDateToString(channel.getCreatedTime()));
                    } else {
                        channelStringBuilder.append("--");
                    }

                    channelStringBuilder.append("|");
                    if (channel.getCreatedUser() != null) {
                        channelStringBuilder.append(channel.getCreatedUser());
                    } else {
                        channelStringBuilder.append("--");
                    }

                    channelStringBuilder.append("|");
                    if (channel.getLastUpdatedTime() != null) {
                        channelStringBuilder.append(common.formatDateToString(channel.getLastUpdatedTime()));
                    } else {
                        channelStringBuilder.append("--");
                    }

                    channelStringBuilder.append("|");
                    if (channel.getLastUpdatedUser() != null) {
                        channelStringBuilder.append(channel.getLastUpdatedUser());
                    } else {
                        channelStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return channelStringBuilder.toString();
    }

}
