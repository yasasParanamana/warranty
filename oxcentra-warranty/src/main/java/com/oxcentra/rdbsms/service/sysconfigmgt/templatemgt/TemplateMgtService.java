package com.oxcentra.rdbsms.service.sysconfigmgt.templatemgt;

import com.oxcentra.rdbsms.bean.common.TempAuthRecBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.templatemgt.SMSTemplateInputBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.templatemgt.TemplateMgt;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.templatemgt.TemplateMgtRepository;
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
public class TemplateMgtService {
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
    TemplateMgtRepository templateMgtRepository;

    private final String fields = "UserParamInputBean ParamCode|Description|Category|Status|Last Updated User|Last Updated Time|Created Time";

    public long getCount(SMSTemplateInputBean smsTemplateInputBean) {
        long count = 0;
        try {
            count = templateMgtRepository.getDataCount(smsTemplateInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TemplateMgt> getTemplateSearchResultList(SMSTemplateInputBean smsTemplateInputBean) {
        List<TemplateMgt> templateMgtList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.USERPARAMETER_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get template mgt search list.");
            //Get User param search list.
            templateMgtList = templateMgtRepository.getTemplateSearchResultList(smsTemplateInputBean);
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
        return templateMgtList;
    }

    public String deleteSMSTemplate(String code) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SMS_TEMPLATE_MGT_PAGE)) {
                //get the existing department
                TemplateMgt templateMgt = templateMgtRepository.getSMSTemplate(code);
                if (templateMgt != null) {
                    SMSTemplateInputBean smsTemplateInputBean = new SMSTemplateInputBean();
                    smsTemplateInputBean.setCode(templateMgt.getCode());
                    smsTemplateInputBean.setDescription(templateMgt.getDescription());
                    smsTemplateInputBean.setMessageformat(templateMgt.getMessageformat());
                    smsTemplateInputBean.setStatus(templateMgt.getStatus());
                    smsTemplateInputBean.setCreatedtime(templateMgt.getCreatedtime());
                    smsTemplateInputBean.setCreateduser(templateMgt.getCreateduser());
                    smsTemplateInputBean.setLastupdatedtime(templateMgt.getLastupdatedtime());
                    smsTemplateInputBean.setLastupdateduser(templateMgt.getLastupdateduser());
                    //set audit description
                    auditDescription = "Requested to deleted sms template (code: " + code + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(smsTemplateInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.TEMPLATE_MGT_NORECORD_FOUND;
                }
            } else {
                message = templateMgtRepository.deleteSMSTemplate(code);
                auditDescription = "SMS Template (Code: " + code + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.TEMPLATE_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.SMS_TEMPLATE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String insertDualAuthRecord(SMSTemplateInputBean smsTemplateInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(smsTemplateInputBean.getCode(), PageVarList.SMS_TEMPLATE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.SMS_TEMPLATE_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(smsTemplateInputBean.getCode());
                tempAuthRecBean.setKey2(smsTemplateInputBean.getDescription());
                tempAuthRecBean.setKey3(smsTemplateInputBean.getStatus());
                tempAuthRecBean.setTmpRecord(smsTemplateInputBean.getMessageformat());
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

    public long getDataCountDual(SMSTemplateInputBean smsTemplateInputBean) {
        long count = 0;
        try {
            count = templateMgtRepository.getDataCountDual(smsTemplateInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getSmsTemplateSearchResultsDual(SMSTemplateInputBean smsTemplateInputBean) {
        List<TempAuthRec> tempAuthRecList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SMS_TEMPLATE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get sms template dual authentication search list.");
            tempAuthRecList = templateMgtRepository.getSmsTemplateSearchResultsDual(smsTemplateInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return tempAuthRecList;
    }

    public String confirmSMSTemplate(String id) {
        String message = "";
        String auditDescription = "";
        SMSTemplateInputBean smsTemplateInputBean = null;
        TemplateMgt existingTemplateMgt = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                smsTemplateInputBean = new SMSTemplateInputBean();
                smsTemplateInputBean.setCode(tempAuthRecBean.getKey1());
                smsTemplateInputBean.setDescription(tempAuthRecBean.getKey2());
                smsTemplateInputBean.setMessageformat(tempAuthRecBean.getTmpRecord());
                smsTemplateInputBean.setStatus(tempAuthRecBean.getKey3());

                smsTemplateInputBean.setLastupdatedtime(commonRepository.getCurrentDate());
                smsTemplateInputBean.setLastupdateduser(sessionBean.getUsername());


                try {
                    String code = smsTemplateInputBean.getCode();
                    existingTemplateMgt = templateMgtRepository.getSMSTemplate(code);
                } catch (EmptyResultDataAccessException e) {
                    existingTemplateMgt = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingTemplateMgt == null) {
                        smsTemplateInputBean.setCreatedtime(commonRepository.getCurrentDate());
                        smsTemplateInputBean.setCreateduser(sessionBean.getUsername());
                        message = templateMgtRepository.insertSMSTemplate(smsTemplateInputBean);
                    } else {
                        message = MessageVarList.TEMPLATE_MGT_ALREADY_EXISTS;
                    }

                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingTemplateMgt != null) {
                        message = templateMgtRepository.updateSMSTemplate(smsTemplateInputBean);
                        System.out.println("message @templateService UPDATE_TASK= " + message);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }

                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingTemplateMgt != null) {
                        message = templateMgtRepository.deleteSMSTemplate(smsTemplateInputBean.getCode());
                        System.out.println("message @templateService DELETE_TASK= " + message);
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
                                .append("' operation on sms template (sms template code: ").append(smsTemplateInputBean.getCode())
                                .append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" approved by ")
                                .append(sessionBean.getUsername());

                        auditDescription = auditDesBuilder.toString();
                        System.out.println("auditDescription == " + auditDescription);
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
                audittrace.setPage(PageVarList.SMS_TEMPLATE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSMSTemplateAsString(smsTemplateInputBean, false));
                audittrace.setOldvalue(this.getSMSTemplateAsString(existingTemplateMgt, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        System.out.println("return message; >>>>>>>>>>>>>>>> " + message);
        return message;
    }

    private String getSMSTemplateAsString(SMSTemplateInputBean smsTemplateInputBean, boolean checkChanges) {
        StringBuilder categoryStringBuilder = new StringBuilder();
        try {
            if (smsTemplateInputBean != null) {
                if (smsTemplateInputBean.getCode() != null && !smsTemplateInputBean.getCode().isEmpty()) {
                    categoryStringBuilder.append(smsTemplateInputBean.getCode());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                if (smsTemplateInputBean.getDescription() != null && !smsTemplateInputBean.getDescription().isEmpty()) {
                    categoryStringBuilder.append(smsTemplateInputBean.getDescription());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                if (smsTemplateInputBean.getMessageformat() != null && !smsTemplateInputBean.getMessageformat().isEmpty()) {
                    categoryStringBuilder.append(smsTemplateInputBean.getMessageformat());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                if (smsTemplateInputBean.getStatus() != null && !smsTemplateInputBean.getStatus().isEmpty()) {
                    categoryStringBuilder.append(smsTemplateInputBean.getStatus());
                } else {
                    categoryStringBuilder.append("--");
                }


                if (!checkChanges) {
                    categoryStringBuilder.append("|");
                    if (smsTemplateInputBean.getCreatedtime() != null) {
                        categoryStringBuilder.append(common.formatDateToString(smsTemplateInputBean.getCreatedtime()));
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (smsTemplateInputBean.getCreateduser() != null) {
                        categoryStringBuilder.append(smsTemplateInputBean.getCreateduser());
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (smsTemplateInputBean.getLastupdatedtime() != null) {
                        categoryStringBuilder.append(common.formatDateToString(smsTemplateInputBean.getLastupdatedtime()));
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (smsTemplateInputBean.getLastupdateduser() != null) {
                        categoryStringBuilder.append(smsTemplateInputBean.getLastupdateduser());
                    } else {
                        categoryStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return categoryStringBuilder.toString();
    }

    private String getSMSTemplateAsString(TemplateMgt templateMgt, boolean checkChanges) {
        StringBuilder categoryStringBuilder = new StringBuilder();
        try {
            if (templateMgt != null) {
                if (templateMgt.getCode() != null && !templateMgt.getCode().isEmpty()) {
                    categoryStringBuilder.append(templateMgt.getCode());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                if (templateMgt.getDescription() != null && !templateMgt.getDescription().isEmpty()) {
                    categoryStringBuilder.append(templateMgt.getDescription());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                if (templateMgt.getMessageformat() != null && !templateMgt.getMessageformat().isEmpty()) {
                    categoryStringBuilder.append(templateMgt.getMessageformat());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                if (templateMgt.getStatus() != null && !templateMgt.getStatus().isEmpty()) {
                    categoryStringBuilder.append(templateMgt.getStatus());
                } else {
                    categoryStringBuilder.append("--");
                }

                if (!checkChanges) {
                    categoryStringBuilder.append("|");
                    if (templateMgt.getCreatedtime() != null) {
                        categoryStringBuilder.append(common.formatDateToString(templateMgt.getCreatedtime()));
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (templateMgt.getCreateduser() != null) {
                        categoryStringBuilder.append(templateMgt.getCreateduser());
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (templateMgt.getLastupdatedtime() != null) {
                        categoryStringBuilder.append(common.formatDateToString(templateMgt.getLastupdatedtime()));
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (templateMgt.getLastupdateduser() != null) {
                        categoryStringBuilder.append(templateMgt.getLastupdateduser());
                    } else {
                        categoryStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return categoryStringBuilder.toString();
    }

    public String rejectSmsTemplate(String id) {
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
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on sms template (sms template code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.SMS_TEMPLATE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String updateSMSTemplate(SMSTemplateInputBean smsTemplateInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        TemplateMgt existingTemplate = null;
        try {
            existingTemplate = templateMgtRepository.getSMSTemplate(smsTemplateInputBean.getCode());
            if (existingTemplate != null) {
                //check changed values
                smsTemplateInputBean.setMessageformat(makeSmsFormat(smsTemplateInputBean));
                String oldValueAsString = this.getSMSTemplateAsString(existingTemplate, true);
                String newValueAsString = this.getSMSTemplateAsString(smsTemplateInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    smsTemplateInputBean.setLastupdatedtime(currentDate);
                    smsTemplateInputBean.setLastupdateduser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SMS_TEMPLATE_MGT_PAGE)) {
                        auditDescription = "Requested to update sms template (code: " + smsTemplateInputBean.getCode() + ")";
                        message = this.insertDualAuthRecord(smsTemplateInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "SMS Template (code: " + smsTemplateInputBean.getCode() + ") updated by " + sessionBean.getUsername();
                        message = templateMgtRepository.updateSMSTemplate(smsTemplateInputBean);
                    }
                }
            } else {
                message = MessageVarList.TEMPLATE_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.TEMPLATE_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.TEMPLATE_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.SMS_TEMPLATE_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getSMSTemplateAsString(existingTemplate, false));
                audittrace.setNewvalue(this.getSMSTemplateAsString(smsTemplateInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String insertSMSTemplate(SMSTemplateInputBean smsTemplateInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            TemplateMgt templateMgt = templateMgtRepository.getSMSTemplate(smsTemplateInputBean.getCode().trim());
            if (templateMgt == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                smsTemplateInputBean.setMessageformat(makeSmsFormat(smsTemplateInputBean));
                smsTemplateInputBean.setCreatedtime(currentDate);
                smsTemplateInputBean.setCreateduser(lastUpdatedUser);
                smsTemplateInputBean.setLastupdatedtime(currentDate);
                smsTemplateInputBean.setLastupdateduser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SMS_TEMPLATE_MGT_PAGE)) {
                    auditDescription = "Requested to add sms template (code: " + smsTemplateInputBean.getCode() + ")";
                    message = this.insertDualAuthRecord(smsTemplateInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "SMS Template (code: " + smsTemplateInputBean.getCode() + ") added by " + sessionBean.getUsername();
                    message = templateMgtRepository.insertSMSTemplate(smsTemplateInputBean);
                }
            } else {
                message = MessageVarList.TEMPLATE_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.TEMPLATE_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.TEMPLATE_MGT_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.SMS_TEMPLATE_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSMSTemplateAsString(smsTemplateInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String makeSmsFormat(SMSTemplateInputBean inputBean) throws Exception {
        String smsFormat = "";
        try {
            smsFormat = inputBean.getDes1() + "|" + inputBean.getField1() + "|" + inputBean.getDes2() + "|" + inputBean.getField2()
                    + "|" + inputBean.getDes3() + "|" + inputBean.getField3() + "|" + inputBean.getDes4() + "|" + inputBean.getField4()
                    + "|" + inputBean.getDes5() + "|" + inputBean.getField5() + "|" + inputBean.getDes6() + "|" + inputBean.getField6()
                    + "|" + inputBean.getDes7() + "|" + inputBean.getField7();
        } catch (Exception e) {
            throw e;
        }

        return smsFormat;
    }

    public TemplateMgt getSMSTemplate(String code) {
        TemplateMgt templateMgt;
        try {
            templateMgt = templateMgtRepository.getSMSTemplate(code);
            String[] fieldArray = templateMgt.getMessageformat().split("\\|");

            if (fieldArray.length > 0) {
                templateMgt.setDes1(fieldArray[0]);
            }
            if (fieldArray.length > 1) {
                templateMgt.setField1(fieldArray[1]);
            }
            if (fieldArray.length > 2) {
                templateMgt.setDes2(fieldArray[2]);
            }
            if (fieldArray.length > 3) {
                templateMgt.setField2(fieldArray[3]);
            }
            if (fieldArray.length > 4) {
                templateMgt.setDes3(fieldArray[4]);
            }
            if (fieldArray.length > 5) {
                templateMgt.setField3(fieldArray[5]);
            }
            if (fieldArray.length > 6) {
                templateMgt.setDes4(fieldArray[6]);
            }
            if (fieldArray.length > 7) {
                templateMgt.setField4(fieldArray[7]);
            }
            if (fieldArray.length > 8) {
                templateMgt.setDes5(fieldArray[8]);
            }
            if (fieldArray.length > 9) {
                templateMgt.setField5(fieldArray[9]);
            }
            if (fieldArray.length > 10) {
                templateMgt.setDes6(fieldArray[10]);
            }
            if (fieldArray.length > 11) {
                templateMgt.setField6(fieldArray[11]);
            }
            if (fieldArray.length > 12) {
                templateMgt.setDes7(fieldArray[12]);
            }
            if (fieldArray.length > 13) {
                templateMgt.setField7(fieldArray[13]);
            }
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return templateMgt;
    }
}
