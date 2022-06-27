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

import static com.oxcentra.warranty.util.varlist.MessageVarList.*;
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
    SupplierRepository supplierRepository;

    private final String fields = "SupplierInputBean Supplier Code|Description|Status|Sort Key|Created Time|Last Updated Time|Last Updated User";

    public long getCount(SupplierInputBean supplierInputBean) {
        long count = 0;
        try {
            count = supplierRepository.getDataCount(supplierInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Supplier> getSupplierSearchResultList(SupplierInputBean supplierInputBean) {
        List<Supplier> supplierList;
        try {
            //set audit trace values
            audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(SUPPLIER_MGT_PAGE);
            audittrace.setTask(VIEW_TASK);
            audittrace.setDescription("Get supplier search list.");
            //Get supplier search list.
            supplierList = supplierRepository.getSupplierSearchList(supplierInputBean);
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
        return supplierList;
    }

    public long getDataCountDual(SupplierInputBean supplierInputBean) {
        long count = 0;
        try {
            count = supplierRepository.getDataCountDual(supplierInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getSupplierSearchResultsDual(SupplierInputBean supplierInputBean) {
        List<TempAuthRec> supplierDualList = null;
        try {
            //set audit trace values
            audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(SUPPLIER_MGT_PAGE);
            audittrace.setTask(VIEW_TASK);
            audittrace.setDescription("Get supplier dual authentication search list.");
            //Get supplier dual authentication search list
//            supplierDualList = supplierRepository.getSupplierSearchResultsDual(supplierInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return supplierDualList;
    }

    public String insertSupplier(SupplierInputBean supplierInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            Supplier supplier = supplierRepository.getSupplier(supplierInputBean.getSupplierCode());
            if (supplier == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                supplierInputBean.setCreatedTime(currentDate);
                supplierInputBean.setLastUpdatedTime(currentDate);
                supplierInputBean.setLastUpdatedUser(lastUpdatedUser);
                supplierInputBean.setCreatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(SUPPLIER_MGT_PAGE)) {
                    auditDescription = "Requested to add supplier (code: " + supplierInputBean.getSupplierCode() + ")";
//                    message = this.insertDualAuthRecord(SupplierInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Supplier (code: " + supplierInputBean.getSupplierCode()+ ") added by " + sessionBean.getUsername();
                    message = supplierRepository.insertSupplier(supplierInputBean);
                }
            } else {
                message = SUPPLIER_MGT_ADDED_SUCCESSFULLY;
                auditDescription = messageSource.getMessage(MessageVarList.SUPPLIER_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = SUPPLIER_MGT_ALREADY_EXISTS;
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
                audittrace.setPage(SUPPLIER_MGT_PAGE);
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
        Supplier supplier;
        try {
            supplier = supplierRepository.getSupplier(supplierCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return supplier;
    }

    public String updateSupplier(SupplierInputBean supplierInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        Supplier existingSupplier = null;
        try {
            existingSupplier = supplierRepository.getSupplier(supplierInputBean.getSupplierCode());
            if (existingSupplier != null) {
                //check changed values
                String oldValueAsString = this.getSupplierAsString(existingSupplier, true);
                String newValueAsString = this.getSupplierAsString(supplierInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();
                    supplierInputBean.setLastUpdatedTime(currentDate);
                    supplierInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(SUPPLIER_MGT_PAGE)) {
                        auditDescription = "Requested to update supplier (code: " + supplierInputBean.getSupplierCode() + ")";
//                        message = this.insertDualAuthRecord(SupplierInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Supplier (code: " + supplierInputBean.getSupplierCode() + ") updated by " + sessionBean.getUsername();
                        message = supplierRepository.updateSupplier(supplierInputBean);
                    }
                }
            } else {
                message = MessageVarList.SUPPLIER_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.SUPPLIER_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.SUPPLIER_MGT_NORECORD_FOUND;
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
                audittrace.setPage(SUPPLIER_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getSupplierAsString(existingSupplier, false));
                audittrace.setNewvalue(this.getSupplierAsString(supplierInputBean, false));
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
            if (commonRepository.checkPageIsDualAuthenticate(SUPPLIER_MGT_PAGE)) {
                //get the existing supplier
                Supplier supplier = supplierRepository.getSupplier(supplierCode);
                if (supplier != null) {
                    SupplierInputBean supplierInputBean = new SupplierInputBean();
                    //set the values to input bean
                    supplierInputBean.setSupplierCode(supplier.getSupplierCode());
                    supplierInputBean.setSupplierName(supplier.getSupplierName());
                    supplierInputBean.setStatus(supplier.getStatus());
                    supplierInputBean.setCreatedTime(supplier.getCreatedTime());
                    supplierInputBean.setLastUpdatedTime(supplier.getLastUpdatedTime());
                    supplierInputBean.setLastUpdatedUser(supplier.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted supplier (code: " + supplierCode + ")";
                    //insert the record to dual auth table
//                    message = this.insertDualAuthRecord(SupplierInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = SUPPLIER_MGT_NORECORD_FOUND;
                }
            } else {
                message = supplierRepository.deleteSupplier(supplierCode);
                auditDescription = "Supplier (Code: " + supplierCode + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = SUPPLIER_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
            message = MessageVarList.COMMON_ERROR_ALRADY_USE;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
//                audittrace.setSection(SectionVarList.SUPPLIER_SYS_CONFIGURATION_MGT);
                audittrace.setPage(SUPPLIER_MGT_PAGE);
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
        SupplierInputBean supplierInputBean = null;
        Supplier existingSupplier = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(supplierCode);
            if (tempAuthRecBean != null) {
                supplierInputBean = new SupplierInputBean();
//                supplierInputBean.setId(tempAuthRecBean.getKey1());
//                supplierInputBean.setDescription(tempAuthRecBean.getKey2());
//                supplierInputBean.setStatus(tempAuthRecBean.getKey3());
//                supplierInputBean.setSortKey(tempAuthRecBean.getKey4());
                supplierInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                supplierInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing session
                try {
//                    String code = SupplierInputBean.getId();
                    existingSupplier = supplierRepository.getSupplier(supplierCode);
                } catch (EmptyResultDataAccessException e) {
                    existingSupplier = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSupplier == null) {
                        supplierInputBean.setCreatedUser(sessionBean.getUsername());
                        supplierInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        message = supplierRepository.insertSupplier(supplierInputBean);
                    } else {
                        message = MessageVarList.SUPPLIER_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSupplier != null) {
                        message = supplierRepository.updateSupplier(supplierInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingSupplier != null) {
                        message = supplierRepository.deleteSupplier(supplierInputBean.getSupplierCode());
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
                                .append("' operation on task (task code: ").append(supplierInputBean.getSupplierCode())
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
            message = MessageVarList.SUPPLIER_MGT_NORECORD_FOUND;
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
                audittrace.setPage(SUPPLIER_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getSupplierAsString(supplierInputBean, false));
                audittrace.setOldvalue(this.getSupplierAsString(existingSupplier, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectSupplier(String supplierCode) {
        String message = "";
        String auditDescription = "";
        try {
            //get temp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(supplierCode);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(supplierCode, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on supplier (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(SUPPLIER_MGT_PAGE);
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
//            long count = commonRepository.getTempAuthRecordCount(SupplierInputBean.getId(), SUPPLIER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
//            if (count > 0) {
//                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
//            } else {
//                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
//                tempAuthRecBean.setPage(SUPPLIER_MGT_PAGE);
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

    private String getSupplierAsString(SupplierInputBean supplierInputBean, boolean checkChanges) {
        StringBuilder supplierStringBuilder = new StringBuilder();
        try {
            if (supplierInputBean != null) {

                if (supplierInputBean.getSupplierName()!= null && !supplierInputBean.getSupplierName().isEmpty()) {
                    supplierStringBuilder.append(supplierInputBean.getSupplierName());
                } else {
                    supplierStringBuilder.append("error");
                }

                supplierStringBuilder.append("|");
                if (supplierInputBean.getSupplierPhone() != null && !supplierInputBean.getSupplierPhone().isEmpty()) {
                    supplierStringBuilder.append(supplierInputBean.getSupplierPhone());
                } else {
                    supplierStringBuilder.append("--");
                }

                supplierStringBuilder.append("|");
                if (supplierInputBean.getStatus() != null && !supplierInputBean.getStatus().isEmpty()) {
                    supplierStringBuilder.append(supplierInputBean.getStatus());
                } else {
                    supplierStringBuilder.append("--");
                }

                if (!checkChanges) {
                    supplierStringBuilder.append("|");
                    if (supplierInputBean.getCreatedTime() != null) {
                        supplierStringBuilder.append(common.formatDateToString(supplierInputBean.getCreatedTime()));
                    } else {
                        supplierStringBuilder.append("--");
                    }

                    supplierStringBuilder.append("|");
                    if (supplierInputBean.getLastUpdatedTime() != null) {
                        supplierStringBuilder.append(common.formatDateToString(supplierInputBean.getLastUpdatedTime()));
                    } else {
                        supplierStringBuilder.append("--");
                    }

                    supplierStringBuilder.append("|");
                    if (supplierInputBean.getLastUpdatedUser() != null) {
                        supplierStringBuilder.append(supplierInputBean.getLastUpdatedUser());
                    } else {
                        supplierStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return supplierStringBuilder.toString();
    }

    private String getSupplierAsString(Supplier supplier, boolean checkChanges) {
        StringBuilder supplierStringBuilder = new StringBuilder();
        try {
            if (supplier != null) {
                if (supplier.getSupplierName()!= null && !supplier.getSupplierName().isEmpty()) {
                    supplierStringBuilder.append(supplier.getSupplierName());
                } else {
                    supplierStringBuilder.append("error");
                }

                supplierStringBuilder.append("|");
                if (supplier.getSupplierPhone() != null && !supplier.getSupplierPhone().isEmpty()) {
                    supplierStringBuilder.append(supplier.getSupplierPhone());
                } else {
                    supplierStringBuilder.append("--");
                }

                supplierStringBuilder.append("|");
                if (supplier.getStatus() != null && !supplier.getStatus().isEmpty()) {
                    supplierStringBuilder.append(supplier.getStatus());
                } else {
                    supplierStringBuilder.append("--");
                }


                if (!checkChanges) {
                    supplierStringBuilder.append("|");
                    if (supplier.getCreatedTime() != null) {
                        supplierStringBuilder.append(common.formatDateToString(supplier.getCreatedTime()));
                    } else {
                        supplierStringBuilder.append("--");
                    }


                    supplierStringBuilder.append("|");
                    if (supplier.getLastUpdatedTime() != null) {
                        supplierStringBuilder.append(common.formatDateToString(supplier.getLastUpdatedTime()));
                    } else {
                        supplierStringBuilder.append("--");
                    }

                    supplierStringBuilder.append("|");
                    if (supplier.getLastUpdatedUser() != null) {
                        supplierStringBuilder.append(supplier.getLastUpdatedUser());
                    } else {
                        supplierStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return supplierStringBuilder.toString();
    }


}

