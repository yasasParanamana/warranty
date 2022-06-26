package com.oxcentra.warranty.service.warranty.supplier;

import com.oxcentra.warranty.bean.common.TempAuthRecBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.supplier.SupplierInputBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.repository.warranty.supplier.SupplierRepository;
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

import static com.oxcentra.warranty.util.varlist.PageVarList.SUPPLIER_MGT_PAGE;
import static com.oxcentra.warranty.util.varlist.SectionVarList.SECTION_SYS_CONFIGURATION_MGT;
import static com.oxcentra.warranty.util.varlist.TaskVarList.VIEW_TASK;

@Service
@Scope("prototype")
public class SupplierService {

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
    SupplierRepository sectionRepository;

    private final String fields = "SupplierInputBean Supplier Code|Description|Status|Sort Key|Created Time|Last Updated Time|Last Updated User";

    public long getCount(SupplierInputBean SupplierInputBean) {
        long count = 0;
        try {
            count = sectionRepository.getDataCount(SupplierInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Supplier> getSupplierSearchResultList(SupplierInputBean SupplierInputBean) {
        List<Supplier> sectionList;
        try {
            //set audit trace values
            audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(SUPPLIER_MGT_PAGE);
            audittrace.setTask(VIEW_TASK);
            audittrace.setDescription("Get section search list.");
            //Get section search list.
            sectionList = sectionRepository.getSupplierSearchList(SupplierInputBean);
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

    public long getDataCountDual(SupplierInputBean SupplierInputBean) {
        long count = 0;
        try {
            count = sectionRepository.getDataCountDual(SupplierInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getSupplierSearchResultsDual(SupplierInputBean SupplierInputBean) {
        List<TempAuthRec> sectionDualList = null;
        try {
            //set audit trace values
            audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
            audittrace.setTask(VIEW_TASK);
            audittrace.setDescription("Get section dual authentication search list.");
            //Get section dual authentication search list
//            sectionDualList = sectionRepository.getSupplierSearchResultsDual(SupplierInputBean);
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

    public String insertSupplier(SupplierInputBean SupplierInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            Supplier supplier = sectionRepository.getSupplier(SupplierInputBean.getSupplierCode());
            if (supplier == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                SupplierInputBean.setCreatedTime(currentDate);
                SupplierInputBean.setLastUpdatedTime(currentDate);
                SupplierInputBean.setLastUpdatedUser(lastUpdatedUser);
                SupplierInputBean.setCreatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(SUPPLIER_MGT_PAGE)) {
                    auditDescription = "Requested to add section (code: " + SupplierInputBean.getSupplierCode() + ")";
//                    message = this.insertDualAuthRecord(SupplierInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Supplier (code: " + SupplierInputBean.getSupplierCode()+ ") added by " + sessionBean.getUsername();
                    message = sectionRepository.insertSupplier(SupplierInputBean);
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
                audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
//                audittrace.setNewvalue(this.getSupplierAsString(SupplierInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public Supplier getSupplier(String supplierCode) {
        Supplier section;
        try {
            section = sectionRepository.getSupplier(supplierCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return section;
    }

    public String updateSupplier(SupplierInputBean SupplierInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        Supplier existingSupplier = null;
        try {
            existingSupplier = sectionRepository.getSupplier(SupplierInputBean.getSupplierCode());
            if (existingSupplier != null) {
                //check changed values
                String oldValueAsString = this.getSupplierAsString(existingSupplier, true);
                String newValueAsString = this.getSupplierAsString(SupplierInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();
                    SupplierInputBean.setLastUpdatedTime(currentDate);
                    SupplierInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SECTION_MGT_PAGE)) {
                        auditDescription = "Requested to update section (code: " + SupplierInputBean.getSupplierCode() + ")";
//                        message = this.insertDualAuthRecord(SupplierInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Supplier (code: " + SupplierInputBean.getSupplierCode() + ") updated by " + sessionBean.getUsername();
                        message = sectionRepository.updateSupplier(SupplierInputBean);
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
                audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getSupplierAsString(existingSupplier, false));
                audittrace.setNewvalue(this.getSupplierAsString(SupplierInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteSupplier(String supplierCode) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SECTION_MGT_PAGE)) {
                //get the existing section
                Supplier section = sectionRepository.getSupplier(supplierCode);
                if (section != null) {
                    SupplierInputBean SupplierInputBean = new SupplierInputBean();
                    //set the values to input bean
                    SupplierInputBean.setSupplierCode(section.getSupplierCode());
                    SupplierInputBean.setSupplierName(section.getSupplierName());
                    SupplierInputBean.setStatus(section.getStatus());
                    SupplierInputBean.setCreatedTime(section.getCreatedTime());
                    SupplierInputBean.setLastUpdatedTime(section.getLastUpdatedTime());
                    SupplierInputBean.setLastUpdatedUser(section.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted section (code: " + supplierCode + ")";
                    //insert the record to dual auth table
//                    message = this.insertDualAuthRecord(SupplierInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.SECTION_MGT_NORECORD_FOUND;
                }
            } else {
                message = sectionRepository.deleteSupplier(supplierCode);
                auditDescription = "Supplier (Code: " + supplierCode + ") deleted by " + sessionBean.getUsername();
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
//                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmSupplier(String supplierCode) {
        String message = "";
        String auditDescription = "";
        SupplierInputBean SupplierInputBean = null;
        Supplier existingSupplier = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(supplierCode);
            if (tempAuthRecBean != null) {
                SupplierInputBean = new SupplierInputBean();
//                SupplierInputBean.setId(tempAuthRecBean.getKey1());
//                SupplierInputBean.setDescription(tempAuthRecBean.getKey2());
//                SupplierInputBean.setStatus(tempAuthRecBean.getKey3());
//                SupplierInputBean.setSortKey(tempAuthRecBean.getKey4());
                SupplierInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                SupplierInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing session
                try {
//                    String code = SupplierInputBean.getId();
                    existingSupplier = sectionRepository.getSupplier(supplierCode);
                } catch (EmptyResultDataAccessException e) {
                    existingSupplier = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSupplier == null) {
                        SupplierInputBean.setCreatedUser(sessionBean.getUsername());
                        SupplierInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        message = sectionRepository.insertSupplier(SupplierInputBean);
                    } else {
                        message = MessageVarList.SECTION_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSupplier != null) {
                        message = sectionRepository.updateSupplier(SupplierInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSupplier != null) {
                        message = sectionRepository.deleteSupplier(SupplierInputBean.getSupplierCode());
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                }

                //if task db operation success, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(supplierCode, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success,insert the audit
                    if (message.isEmpty()) {
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on task (task code: ").append(SupplierInputBean.getSupplierCode())
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
                audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSupplierAsString(SupplierInputBean, false));
                audittrace.setOldvalue(this.getSupplierAsString(existingSupplier, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectSupplier(String sectionCode) {
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
                audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.SECTION_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

//    private String insertDualAuthRecord(SupplierInputBean SupplierInputBean, String taskCode) throws Exception {
//        String message = "";
//        try {
//            long count = commonRepository.getTempAuthRecordCount(SupplierInputBean.getId(), PageVarList.SECTION_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
//            if (count > 0) {
//                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
//            } else {
//                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
//                tempAuthRecBean.setPage(PageVarList.SECTION_MGT_PAGE);
//                tempAuthRecBean.setTask(taskCode);
//                tempAuthRecBean.setKey1(SupplierInputBean.getSupplierCode());
//                tempAuthRecBean.setKey2(SupplierInputBean.getDescription());
//                tempAuthRecBean.setKey3(SupplierInputBean.getStatus());
//                tempAuthRecBean.setKey4(String.valueOf(SupplierInputBean.getSortKey()));
//                //insert dual auth record
//                message = commonRepository.insertDualAuthRecordSQL(tempAuthRecBean);
//            }
//        } catch (EmptyResultDataAccessException ere) {
//            throw ere;
//        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
//            throw cve;
//        } catch (Exception e) {
//            throw e;
//        }
//        return message;
//    }

    private String getSupplierAsString(SupplierInputBean SupplierInputBean, boolean checkChanges) {
        StringBuilder sectionStringBuilder = new StringBuilder();
        try {
            if (SupplierInputBean != null) {

                if (SupplierInputBean.getSupplierName()!= null && !SupplierInputBean.getSupplierName().isEmpty()) {
                    sectionStringBuilder.append(SupplierInputBean.getSupplierName());
                } else {
                    sectionStringBuilder.append("error");
                }

                sectionStringBuilder.append("|");
                if (SupplierInputBean.getSupplierPhone() != null && !SupplierInputBean.getSupplierPhone().isEmpty()) {
                    sectionStringBuilder.append(SupplierInputBean.getSupplierPhone());
                } else {
                    sectionStringBuilder.append("--");
                }

                sectionStringBuilder.append("|");
                if (SupplierInputBean.getStatus() != null && !SupplierInputBean.getStatus().isEmpty()) {
                    sectionStringBuilder.append(SupplierInputBean.getStatus());
                } else {
                    sectionStringBuilder.append("--");
                }

                if (!checkChanges) {
                    sectionStringBuilder.append("|");
                    if (SupplierInputBean.getCreatedTime() != null) {
                        sectionStringBuilder.append(common.formatDateToString(SupplierInputBean.getCreatedTime()));
                    } else {
                        sectionStringBuilder.append("--");
                    }

                    sectionStringBuilder.append("|");
                    if (SupplierInputBean.getLastUpdatedTime() != null) {
                        sectionStringBuilder.append(common.formatDateToString(SupplierInputBean.getLastUpdatedTime()));
                    } else {
                        sectionStringBuilder.append("--");
                    }

                    sectionStringBuilder.append("|");
                    if (SupplierInputBean.getLastUpdatedUser() != null) {
                        sectionStringBuilder.append(SupplierInputBean.getLastUpdatedUser());
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

    private String getSupplierAsString(Supplier section, boolean checkChanges) {
        StringBuilder sectionStringBuilder = new StringBuilder();
        try {
            if (section != null) {
                if (section.getSupplierName()!= null && !section.getSupplierName().isEmpty()) {
                    sectionStringBuilder.append(section.getSupplierName());
                } else {
                    sectionStringBuilder.append("error");
                }

                sectionStringBuilder.append("|");
                if (section.getSupplierPhone() != null && !section.getSupplierPhone().isEmpty()) {
                    sectionStringBuilder.append(section.getSupplierPhone());
                } else {
                    sectionStringBuilder.append("--");
                }

                sectionStringBuilder.append("|");
                if (section.getStatus() != null && !section.getStatus().isEmpty()) {
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

