package com.epic.rdbsms.service.usermgt.section;

import com.epic.rdbsms.bean.common.TempAuthRecBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.usermgt.section.SectionInputBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Section;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.repository.usermgt.section.SectionRepository;
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
public class SectionService {

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
    SectionRepository sectionRepository;

    private final String fields = "SectionInputBean Section Code|Description|Status|Sort Key|Created Time|Last Updated Time|Last Updated User";

    public long getCount(SectionInputBean sectionInputBean) {
        long count = 0;
        try {
            count = sectionRepository.getDataCount(sectionInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Section> getSectionSearchResultList(SectionInputBean sectionInputBean) {
        List<Section> sectionList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get section search list.");
            //Get section search list.
            sectionList = sectionRepository.getSectionSearchList(sectionInputBean);
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
        return sectionList;
    }

    public long getDataCountDual(SectionInputBean sectionInputBean) {
        long count = 0;
        try {
            count = sectionRepository.getDataCountDual(sectionInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getSectionSearchResultsDual(SectionInputBean sectionInputBean) {
        List<TempAuthRec> sectionDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get section dual authentication search list.");
            //Get section dual authentication search list
            sectionDualList = sectionRepository.getSectionSearchResultsDual(sectionInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return sectionDualList;
    }

    public String insertSection(SectionInputBean sectionInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            Section section = sectionRepository.getSection(sectionInputBean.getSectionCode().trim());
            if (section == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                sectionInputBean.setCreatedTime(currentDate);
                sectionInputBean.setLastUpdatedTime(currentDate);
                sectionInputBean.setLastUpdatedUser(lastUpdatedUser);
                sectionInputBean.setCreatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SECTION_MGT_PAGE)) {
                    auditDescription = "Requested to add section (code: " + sectionInputBean.getSectionCode() + ")";
                    message = this.insertDualAuthRecord(sectionInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Section (code: " + sectionInputBean.getSectionCode() + ") added by " + sessionBean.getUsername();
                    message = sectionRepository.insertSection(sectionInputBean);
                }
            } else {
                message = MessageVarList.SECTION_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.SECTION_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.SECTION_MGT_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSectionAsString(sectionInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public Section getSection(String sectionCode) {
        Section section;
        try {
            section = sectionRepository.getSection(sectionCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return section;
    }

    public String updateSection(SectionInputBean sectionInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        Section existingSection = null;
        try {
            existingSection = sectionRepository.getSection(sectionInputBean.getSectionCode());
            if (existingSection != null) {
                //check changed values
                String oldValueAsString = this.getSectionAsString(existingSection, true);
                String newValueAsString = this.getSectionAsString(sectionInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();
                    sectionInputBean.setLastUpdatedTime(currentDate);
                    sectionInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SECTION_MGT_PAGE)) {
                        auditDescription = "Requested to update section (code: " + sectionInputBean.getSectionCode() + ")";
                        message = this.insertDualAuthRecord(sectionInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Section (code: " + sectionInputBean.getSectionCode() + ") updated by " + sessionBean.getUsername();
                        message = sectionRepository.updateSection(sectionInputBean);
                    }
                }
            } else {
                message = MessageVarList.SECTION_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.SECTION_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.SECTION_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getSectionAsString(existingSection, false));
                audittrace.setNewvalue(this.getSectionAsString(sectionInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteSection(String sectionCode) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SECTION_MGT_PAGE)) {
                //get the existing section
                Section section = sectionRepository.getSection(sectionCode);
                if (section != null) {
                    SectionInputBean sectionInputBean = new SectionInputBean();
                    //set the values to input bean
                    sectionInputBean.setSectionCode(section.getSectionCode());
                    sectionInputBean.setDescription(section.getDescription());
                    sectionInputBean.setStatus(section.getStatus());
                    sectionInputBean.setSortKey(String.valueOf(section.getSortKey()));
                    sectionInputBean.setCreatedTime(section.getCreatedTime());
                    sectionInputBean.setLastUpdatedTime(section.getLastUpdatedTime());
                    sectionInputBean.setLastUpdatedUser(section.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted section (code: " + sectionCode + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(sectionInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.SECTION_MGT_NORECORD_FOUND;
                }
            } else {
                message = sectionRepository.deleteSection(sectionCode);
                auditDescription = "Section (Code: " + sectionCode + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.SECTION_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmSection(String id) {
        String message = "";
        String auditDescription = "";
        SectionInputBean sectionInputBean = null;
        Section existingSection = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                sectionInputBean = new SectionInputBean();
                sectionInputBean.setSectionCode(tempAuthRecBean.getKey1());
                sectionInputBean.setDescription(tempAuthRecBean.getKey2());
                sectionInputBean.setStatus(tempAuthRecBean.getKey3());
                sectionInputBean.setSortKey(tempAuthRecBean.getKey4());
                sectionInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                sectionInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing session
                try {
                    String code = sectionInputBean.getSectionCode();
                    existingSection = sectionRepository.getSection(code);
                } catch (EmptyResultDataAccessException e) {
                    existingSection = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSection == null) {
                        sectionInputBean.setCreatedUser(sessionBean.getUsername());
                        sectionInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        message = sectionRepository.insertSection(sectionInputBean);
                    } else {
                        message = MessageVarList.SECTION_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSection != null) {
                        message = sectionRepository.updateSection(sectionInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSection != null) {
                        message = sectionRepository.deleteSection(sectionInputBean.getSectionCode());
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
                                .append("' operation on task (task code: ").append(sectionInputBean.getSectionCode())
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
            message = MessageVarList.SECTION_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
            message = MessageVarList.COMMON_ERROR_ALRADY_USE;
            System.out.println("service");
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
                audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSectionAsString(sectionInputBean, false));
                audittrace.setOldvalue(this.getSectionAsString(existingSection, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectSection(String sectionCode) {
        String message = "";
        String auditDescription = "";
        try {
            //get temp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(sectionCode);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(sectionCode, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on section (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String insertDualAuthRecord(SectionInputBean sectionInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(sectionInputBean.getSectionCode(), PageVarList.SECTION_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.SECTION_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(sectionInputBean.getSectionCode());
                tempAuthRecBean.setKey2(sectionInputBean.getDescription());
                tempAuthRecBean.setKey3(sectionInputBean.getStatus());
                tempAuthRecBean.setKey4(String.valueOf(sectionInputBean.getSortKey()));
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

    private String getSectionAsString(SectionInputBean sectionInputBean, boolean checkChanges) {
        StringBuilder sectionStringBuilder = new StringBuilder();
        try {
            if (sectionInputBean != null) {

                if (sectionInputBean.getSectionCode() != null && !sectionInputBean.getSectionCode().isEmpty()) {
                    sectionStringBuilder.append(sectionInputBean.getSectionCode());
                } else {
                    sectionStringBuilder.append("error");
                }

                sectionStringBuilder.append("|");
                if (sectionInputBean.getDescription() != null && !sectionInputBean.getDescription().isEmpty()) {
                    sectionStringBuilder.append(sectionInputBean.getDescription());
                } else {
                    sectionStringBuilder.append("--");
                }

                sectionStringBuilder.append("|");
                if (sectionInputBean.getStatus() != null && !sectionInputBean.getStatus().isEmpty()) {
                    sectionStringBuilder.append(sectionInputBean.getStatus());
                } else {
                    sectionStringBuilder.append("--");
                }

                sectionStringBuilder.append("|");
                if (String.valueOf(sectionInputBean.getSortKey()) != null) {
                    sectionStringBuilder.append(sectionInputBean.getSortKey());
                } else {
                    sectionStringBuilder.append("--");
                }

                if (!checkChanges) {
                    sectionStringBuilder.append("|");
                    if (sectionInputBean.getCreatedTime() != null) {
                        sectionStringBuilder.append(common.formatDateToString(sectionInputBean.getCreatedTime()));
                    } else {
                        sectionStringBuilder.append("--");
                    }

                    sectionStringBuilder.append("|");
                    if (sectionInputBean.getLastUpdatedTime() != null) {
                        sectionStringBuilder.append(common.formatDateToString(sectionInputBean.getLastUpdatedTime()));
                    } else {
                        sectionStringBuilder.append("--");
                    }

                    sectionStringBuilder.append("|");
                    if (sectionInputBean.getLastUpdatedUser() != null) {
                        sectionStringBuilder.append(sectionInputBean.getLastUpdatedUser());
                    } else {
                        sectionStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return sectionStringBuilder.toString();
    }

    private String getSectionAsString(Section section, boolean checkChanges) {
        StringBuilder sectionStringBuilder = new StringBuilder();
        try {
            if (section != null) {
                if (section.getSectionCode() != null && !section.getSectionCode().isEmpty()) {
                    sectionStringBuilder.append(section.getSectionCode());
                } else {
                    sectionStringBuilder.append("error");
                }

                sectionStringBuilder.append("|");
                if (section.getDescription() != null && !section.getDescription().isEmpty()) {
                    sectionStringBuilder.append(section.getDescription());
                } else {
                    sectionStringBuilder.append("--");
                }

                sectionStringBuilder.append("|");
                if (section.getStatus() != null && !section.getStatus().isEmpty()) {
                    sectionStringBuilder.append(section.getStatus());
                } else {
                    sectionStringBuilder.append("--");
                }

                sectionStringBuilder.append("|");
                if (String.valueOf(section.getSortKey()) != null && String.valueOf(section.getSortKey()).isEmpty()) {
                    sectionStringBuilder.append(section.getStatus());
                } else {
                    sectionStringBuilder.append("--");
                }

                if (!checkChanges) {
                    sectionStringBuilder.append("|");
                    if (section.getCreatedTime() != null) {
                        sectionStringBuilder.append(common.formatDateToString(section.getCreatedTime()));
                    } else {
                        sectionStringBuilder.append("--");
                    }


                    sectionStringBuilder.append("|");
                    if (section.getLastUpdatedTime() != null) {
                        sectionStringBuilder.append(common.formatDateToString(section.getLastUpdatedTime()));
                    } else {
                        sectionStringBuilder.append("--");
                    }

                    sectionStringBuilder.append("|");
                    if (section.getLastUpdatedUser() != null) {
                        sectionStringBuilder.append(section.getLastUpdatedUser());
                    } else {
                        sectionStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return sectionStringBuilder.toString();
    }


}

