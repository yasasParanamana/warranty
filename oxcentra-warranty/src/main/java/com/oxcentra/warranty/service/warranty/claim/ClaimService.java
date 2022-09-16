package com.oxcentra.warranty.service.warranty.claim;

import com.oxcentra.warranty.bean.common.CommonKeyVal;
import com.oxcentra.warranty.bean.common.TempAuthRecBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.sysconfigmgt.model.Model;
import com.oxcentra.warranty.bean.usermgt.task.TaskInputBean;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.mapping.warranty.SpareParts;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.mapping.warranty.WarrantyAttachments;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.repository.warranty.claim.ClaimRepository;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Scope("prototype")
public class ClaimService {

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
    ClaimRepository claimRepository;

    private final String fields = "Task Code|Description|Status|Created Time|Last Updated Time|Last Updated User";

    public long getDataCount(ClaimInputBean claimInputBean) throws Exception {
        long count = 0;
        try {
            count = claimRepository.getDataCount(claimInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Claim> getClaimSearchResults(ClaimInputBean claimInputBean) throws Exception {
        List<Claim> claimList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_USER_MGT);
            audittrace.setPage(PageVarList.CLAIMS_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get Claim search list.");
            //Get Claim search list.
            claimList = claimRepository.getClaimSearchResults(claimInputBean);
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
        return claimList;
    }

    public String insertClaim(ClaimInputBean claimInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            //check claimId is already exist or not
            Claim existingClaim = claimRepository.getClaim(claimInputBean.getId().trim());
            if (existingClaim == null) {
                //set the other values to input bean

                Date currentDate = commonRepository.getCurrentDate();
                String user = sessionBean.getUsername();

                claimInputBean.setCreatedTime(currentDate);
                claimInputBean.setCreatedUser(user);
                claimInputBean.setLastUpdatedTime(currentDate);
                claimInputBean.setLastUpdatedUser(user);
                claimInputBean.setStatus(StatusVarList.STATUS_CLAIM_PENDING);
                claimInputBean.setTotalCost(claimInputBean.getTotalCost());
                claimInputBean.setPurchasingDate(claimInputBean.getPurchasingDate());

                ArrayList<SpareParts> sparePartsList = new ArrayList<SpareParts>();
                SpareParts spareParts1 = new SpareParts();
                SpareParts spareParts2 = new SpareParts();

                if (claimInputBean.getSparePartRequired1() != null && !claimInputBean.getSparePartRequired1().isEmpty()) {
                    spareParts1.setSparePartType(claimInputBean.getSparePartRequired1());
                    spareParts1.setQty(claimInputBean.getQuantity1());
                    sparePartsList.add(spareParts1);
                }
                if (claimInputBean.getSparePartRequired2() != null && !claimInputBean.getSparePartRequired2().isEmpty()) {
                    spareParts2.setSparePartType(claimInputBean.getSparePartRequired2());
                    spareParts2.setQty(claimInputBean.getQuantity2());
                    sparePartsList.add(spareParts2);
                }
                SpareParts spareParts3 = new SpareParts();
                if (claimInputBean.getSparePartRequired3() != null && !claimInputBean.getSparePartRequired3().isEmpty()) {
                    spareParts3.setSparePartType(claimInputBean.getSparePartRequired3());
                    spareParts3.setQty(claimInputBean.getQuantity3());
                    sparePartsList.add(spareParts3);
                }
                SpareParts spareParts4 = new SpareParts();
                if (claimInputBean.getSparePartRequired4() != null && !claimInputBean.getSparePartRequired4().isEmpty()) {
                    spareParts4.setSparePartType(claimInputBean.getSparePartRequired4());
                    spareParts4.setQty(claimInputBean.getQuantity4());
                    sparePartsList.add(spareParts4);
                }
                SpareParts spareParts5 = new SpareParts();
                if (claimInputBean.getSparePartRequired5() != null && !claimInputBean.getSparePartRequired5().isEmpty()) {
                    spareParts5.setSparePartType(claimInputBean.getSparePartRequired5());
                    spareParts5.setQty(claimInputBean.getQuantity5());
                    sparePartsList.add(spareParts5);
                }
                SpareParts spareParts6 = new SpareParts();
                if (claimInputBean.getSparePartRequired6() != null && !claimInputBean.getSparePartRequired6().isEmpty()) {
                    spareParts6.setSparePartType(claimInputBean.getSparePartRequired6());
                    spareParts6.setQty(claimInputBean.getQuantity6());
                    sparePartsList.add(spareParts6);
                }
                SpareParts spareParts7 = new SpareParts();
                if (claimInputBean.getSparePartRequired7() != null && !claimInputBean.getSparePartRequired7().isEmpty()) {
                    spareParts7.setSparePartType(claimInputBean.getSparePartRequired7());
                    spareParts7.setQty(claimInputBean.getQuantity7());
                    sparePartsList.add(spareParts7);
                }
                SpareParts spareParts8 = new SpareParts();
                if (claimInputBean.getSparePartRequired8() != null && !claimInputBean.getSparePartRequired8().isEmpty()) {
                    spareParts8.setSparePartType(claimInputBean.getSparePartRequired8());
                    spareParts8.setQty(claimInputBean.getQuantity8());
                    sparePartsList.add(spareParts8);
                }
                SpareParts spareParts9 = new SpareParts();
                if (claimInputBean.getSparePartRequired9() != null && !claimInputBean.getSparePartRequired9().isEmpty()) {
                    spareParts9.setSparePartType(claimInputBean.getSparePartRequired9());
                    spareParts9.setQty(claimInputBean.getQuantity9());
                    sparePartsList.add(spareParts9);
                }
                SpareParts spareParts10 = new SpareParts();
                if (claimInputBean.getSparePartRequired10() != null && !claimInputBean.getSparePartRequired10().isEmpty()) {
                    spareParts10.setSparePartType(claimInputBean.getSparePartRequired10());
                    spareParts10.setQty(claimInputBean.getQuantity10());
                    sparePartsList.add(spareParts10);
                }

                claimInputBean.setSpareParts(sparePartsList);

                auditDescription = "Claim (ID: " + claimInputBean.getId() + ") added by " + sessionBean.getUsername();
                message = claimRepository.insertClaim(claimInputBean);

            } else {
                message = MessageVarList.CLAIM_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.CLAIM_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.CLAIM_MGT_ALREADY_EXISTS;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception x) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.USER_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getClaimAsString(claimInputBean, false));
            }
        }
        return message;
    }

    public Claim getClaim(String id) throws Exception {
        Claim claim;
        try {
            claim = claimRepository.getClaim(id);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return claim;
    }

//    public SpareParts getSpareParts(String id) throws Exception {
//        SpareParts spareParts;
//        try {
//            spareParts = claimRepository.getSparePart(id);
//        } catch (EmptyResultDataAccessException ere) {
//            throw ere;
//        } catch (Exception e) {
//            throw e;
//        }
//        return spareParts;
//    }

    public List<SpareParts> getSpareParts(String id) throws Exception {
        List<SpareParts> sparePartBeanList;
        try {
            sparePartBeanList = claimRepository.getSparePartList(id);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return sparePartBeanList;
    }

    public List<WarrantyAttachments> getPdfFiles(String id) throws Exception {
        List<WarrantyAttachments> attachmentBeanList;
        try {
            attachmentBeanList = claimRepository.getPdfFileList(id);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return attachmentBeanList;
    }

    public Supplier getSupplierDetails(String supplierId) throws Exception {
        Supplier supplier;
        try {
            supplier = claimRepository.getSupplierDetails(supplierId);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return supplier;
    }


    public String updateClaim(ClaimInputBean claimInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        Claim existingClaim = null;
        try {
            existingClaim = claimRepository.getClaim(claimInputBean.getId());
            if (existingClaim != null) {
                //check changed values
                String oldValueAsString = this.getClaimAsString(existingClaim, true);
                String newValueAsString = this.getClaimAsString(claimInputBean, true);
                //check the old value and new value
                /*if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else
                {*/
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                    /*claimInputBean.setLastUpdatedTime(currentDate);
                    claimInputBean.setLastUpdatedUser(lastUpdatedUser);*/
                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.TASK_MGT_PAGE)) {
                    auditDescription = "Requested to update task (Task code: " + claimInputBean.getId() + ")";
                    message = this.insertDualAuthRecord(claimInputBean, TaskVarList.UPDATE_TASK);
                } else {
                    auditDescription = "Task Mgt (Claim id: " + claimInputBean.getId() + ") updated by " + sessionBean.getUsername();
                    message = claimRepository.updateClaim(claimInputBean);
                }
//                }
            } else {
                message = MessageVarList.TASK_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.TASK_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.TASK_MGT_NORECORD_FOUND;
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
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getClaimAsString(existingClaim, false));
                audittrace.setNewvalue(this.getClaimAsString(claimInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteClaim(String id) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            message = claimRepository.deleteClaim(id);
            auditDescription = "Claim (WARRANTY ID: " + id + ") deleted by " + sessionBean.getUsername();

        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
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
                audittrace.setPage(PageVarList.CLAIMS_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmClaim(String id) throws Exception {
        String message = "";
        String auditDescription = "";
        ClaimInputBean claimInputBean = null;
        Claim existingClaim = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                claimInputBean = new ClaimInputBean();
                claimInputBean.setId(tempAuthRecBean.getKey1());
                claimInputBean.setDescription(tempAuthRecBean.getKey2());
                claimInputBean.setStatus(tempAuthRecBean.getKey3());
                /*claimInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                claimInputBean.setLastUpdatedUser(sessionBean.getUsername());*/

                //get the existing task
                try {
                    String code = claimInputBean.getId();
                    existingClaim = claimRepository.getClaim(id);
                } catch (EmptyResultDataAccessException e) {
                    existingClaim = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingClaim == null) {
                       /* claimInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        claimInputBean.setCreatedUser(sessionBean.getUsername());*/
                        message = claimRepository.insertClaim(claimInputBean);
                    } else {
                        message = MessageVarList.TASK_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingClaim != null) {
                        message = claimRepository.updateClaim(claimInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingClaim != null) {
                        message = claimRepository.deleteClaim(claimInputBean.getId());
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
                                .append("' operation on task (ID: ").append(claimInputBean.getId())
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
            message = MessageVarList.TASK_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.TASK_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getClaimAsString(claimInputBean, false));
                audittrace.setOldvalue(this.getClaimAsString(existingClaim, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectClaim(String code) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(code);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(code, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on task mgt (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
                    auditDescription = auditDesBuilder.toString();
                } else {
                    message = MessageVarList.COMMON_ERROR_PROCESS;
                }
            } else {
                message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.TASK_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;

    }

    public String insertDualAuthRecord(ClaimInputBean claimInputBean, String id) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(claimInputBean.getId().trim(), PageVarList.CLAIMS_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.CLAIMS_MGT_PAGE);
                tempAuthRecBean.setId(id);
                tempAuthRecBean.setKey1(claimInputBean.getId().trim().toUpperCase());
                tempAuthRecBean.setKey2(claimInputBean.getDescription().trim());
                tempAuthRecBean.setKey3(claimInputBean.getStatus().trim());
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

    private String getClaimAsString(Claim claim, boolean checkChanges) {
        StringBuilder claimStringBuilder = new StringBuilder();
        try {
            if (claim != null) {
                if (claim.getId() != null) {
                    claimStringBuilder.append(claim.getId());
                } else {
                    claimStringBuilder.append("error");
                }

                /*taskStringBuilder.append("|");
                if (task.getDescription() != null) {
                    taskStringBuilder.append(task.getDescription());
                } else {
                    taskStringBuilder.append("--");
                }

                taskStringBuilder.append("|");
                if (task.getStatus() != null) {
                    taskStringBuilder.append(task.getStatus());
                } else {
                    taskStringBuilder.append("--");
                }

                if (!checkChanges) {
                    taskStringBuilder.append("|");
                    if (task.getCreatedUser() != null) {
                        taskStringBuilder.append(task.getCreatedUser());
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (task.getCreatedTime() != null) {
                        taskStringBuilder.append(common.formatDateToString(task.getCreatedTime()));
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (task.getLastUpdatedUser() != null) {
                        taskStringBuilder.append(task.getLastUpdatedUser());
                    } else {
                        taskStringBuilder.append("--");
                    }*/

                  /*  taskStringBuilder.append("|");
                    if (task.getLastUpdatedTime() != null) {
                        taskStringBuilder.append(common.formatDateToString(task.getLastUpdatedTime()));
                    } else {
                        taskStringBuilder.append("--");
                    }*/
                /*    }*/
            }
        } catch (Exception e) {
            throw e;
        }
        return claimStringBuilder.toString();
    }

    private String getClaimAsString(ClaimInputBean claimInputBean, boolean checkChanges) {
        StringBuilder claimStringBuilder = new StringBuilder();
        try {
            if (claimInputBean != null) {
                if (claimInputBean.getId() != null) {
                    claimStringBuilder.append(claimInputBean.getId());
                } else {
                    claimStringBuilder.append("error");
                }

               /* taskStringBuilder.append("|");
                if (taskInputBean.getDescription() != null) {
                    taskStringBuilder.append(taskInputBean.getDescription());
                } else {
                    taskStringBuilder.append("--");
                }

                taskStringBuilder.append("|");
                if (taskInputBean.getStatus() != null) {
                    taskStringBuilder.append(taskInputBean.getStatus());
                } else {
                    taskStringBuilder.append("--");
                }

                if (!checkChanges) {
                    taskStringBuilder.append("|");
                    if (taskInputBean.getCreatedUser() != null) {
                        taskStringBuilder.append(taskInputBean.getCreatedUser());
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (taskInputBean.getCreatedTime() != null) {
                        taskStringBuilder.append(common.formatDateToString(taskInputBean.getCreatedTime()));
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (taskInputBean.getLastUpdatedUser() != null) {
                        taskStringBuilder.append(taskInputBean.getLastUpdatedUser());
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (taskInputBean.getLastUpdatedTime() != null) {
                        taskStringBuilder.append(common.formatDateToString(taskInputBean.getLastUpdatedTime()));
                    } else {
                        taskStringBuilder.append("--");
                    }*/
                /* }*/
            }
        } catch (Exception e) {
            throw e;
        }
        return claimStringBuilder.toString();
    }

    public String approveRequestClaim(ClaimInputBean claimInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        Claim existingClaim = null;
        try {
            existingClaim = claimRepository.getClaim(claimInputBean.getId());
            if (existingClaim != null) {

                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                claimInputBean.setStatus(StatusVarList.STATUS_CLAIM_APPROVE);
                claimInputBean.setIsInHouse("1");
                claimInputBean.setLastUpdatedTime(currentDate);
                claimInputBean.setLastUpdatedUser(lastUpdatedUser);

                System.out.println(claimInputBean.getCostType());


                if(!claimInputBean.getCostType().equalsIgnoreCase("LABOUR")){
                    claimInputBean.setHours(null);
                    claimInputBean.setLabourRate(null);
                }

                ArrayList<SpareParts> sparePartsList = new ArrayList<SpareParts>();
                SpareParts spareParts1 = new SpareParts();
                SpareParts spareParts2 = new SpareParts();

                if (claimInputBean.getSparePartRequired1() != null && !claimInputBean.getSparePartRequired1().isEmpty()) {
                    spareParts1.setSparePartType(claimInputBean.getSparePartRequired1());
                    spareParts1.setQty(claimInputBean.getQuantity1());
                    sparePartsList.add(spareParts1);
                }
                if (claimInputBean.getSparePartRequired2() != null && !claimInputBean.getSparePartRequired2().isEmpty()) {
                    spareParts2.setSparePartType(claimInputBean.getSparePartRequired2());
                    spareParts2.setQty(claimInputBean.getQuantity2());
                    sparePartsList.add(spareParts2);
                }
                SpareParts spareParts3 = new SpareParts();
                if (claimInputBean.getSparePartRequired3() != null && !claimInputBean.getSparePartRequired3().isEmpty()) {
                    spareParts3.setSparePartType(claimInputBean.getSparePartRequired3());
                    spareParts3.setQty(claimInputBean.getQuantity3());
                    sparePartsList.add(spareParts3);
                }
                SpareParts spareParts4 = new SpareParts();
                if (claimInputBean.getSparePartRequired4() != null && !claimInputBean.getSparePartRequired4().isEmpty()) {
                    spareParts4.setSparePartType(claimInputBean.getSparePartRequired4());
                    spareParts4.setQty(claimInputBean.getQuantity4());
                    sparePartsList.add(spareParts4);
                }
                SpareParts spareParts5 = new SpareParts();
                if (claimInputBean.getSparePartRequired5() != null && !claimInputBean.getSparePartRequired5().isEmpty()) {
                    spareParts5.setSparePartType(claimInputBean.getSparePartRequired5());
                    spareParts5.setQty(claimInputBean.getQuantity5());
                    sparePartsList.add(spareParts5);
                }
                SpareParts spareParts6 = new SpareParts();
                if (claimInputBean.getSparePartRequired6() != null && !claimInputBean.getSparePartRequired6().isEmpty()) {
                    spareParts6.setSparePartType(claimInputBean.getSparePartRequired6());
                    spareParts6.setQty(claimInputBean.getQuantity6());
                    sparePartsList.add(spareParts6);
                }
                SpareParts spareParts7 = new SpareParts();
                if (claimInputBean.getSparePartRequired7() != null && !claimInputBean.getSparePartRequired7().isEmpty()) {
                    spareParts7.setSparePartType(claimInputBean.getSparePartRequired7());
                    spareParts7.setQty(claimInputBean.getQuantity7());
                    sparePartsList.add(spareParts7);
                }
                SpareParts spareParts8 = new SpareParts();
                if (claimInputBean.getSparePartRequired8() != null && !claimInputBean.getSparePartRequired8().isEmpty()) {
                    spareParts8.setSparePartType(claimInputBean.getSparePartRequired8());
                    spareParts8.setQty(claimInputBean.getQuantity8());
                    sparePartsList.add(spareParts8);
                }
                SpareParts spareParts9 = new SpareParts();
                if (claimInputBean.getSparePartRequired9() != null && !claimInputBean.getSparePartRequired9().isEmpty()) {
                    spareParts9.setSparePartType(claimInputBean.getSparePartRequired9());
                    spareParts9.setQty(claimInputBean.getQuantity9());
                    sparePartsList.add(spareParts9);
                }
                SpareParts spareParts10 = new SpareParts();
                if (claimInputBean.getSparePartRequired10() != null && !claimInputBean.getSparePartRequired10().isEmpty()) {
                    spareParts10.setSparePartType(claimInputBean.getSparePartRequired10());
                    spareParts10.setQty(claimInputBean.getQuantity10());
                    sparePartsList.add(spareParts10);
                }

                claimInputBean.setSpareParts(sparePartsList);

                auditDescription = "Claim (ID: " + claimInputBean.getId() + ") approved by " + sessionBean.getUsername();
                message = claimRepository.approveRequestClaim(claimInputBean);

            } else {
                message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.CLAIM_MGT_NO_RECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
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
                audittrace.setPage(PageVarList.CLAIMS_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getClaimAsString(existingClaim, false));
                audittrace.setNewvalue(this.getClaimAsString(claimInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectRequestClaim(ClaimInputBean claimInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        Claim existingClaim = null;
        try {
            existingClaim = claimRepository.getClaim(claimInputBean.getId());
            if (existingClaim != null) {

                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                claimInputBean.setStatus(StatusVarList.STATUS_CLAIM_HEAD_OFFICE_REJECTED);
                claimInputBean.setIsInHouse("1");
                claimInputBean.setLastUpdatedTime(currentDate);
                claimInputBean.setLastUpdatedUser(lastUpdatedUser);

                if(!claimInputBean.getCostType().equalsIgnoreCase("LABOUR")){
                    claimInputBean.setHours(null);
                    claimInputBean.setLabourRate(null);
                }

                ArrayList<SpareParts> sparePartsList = new ArrayList<SpareParts>();
                SpareParts spareParts1 = new SpareParts();
                SpareParts spareParts2 = new SpareParts();

                if (claimInputBean.getSparePartRequired1() != null && !claimInputBean.getSparePartRequired1().isEmpty()) {
                    spareParts1.setSparePartType(claimInputBean.getSparePartRequired1());
                    spareParts1.setQty(claimInputBean.getQuantity1());
                    sparePartsList.add(spareParts1);
                }
                if (claimInputBean.getSparePartRequired2() != null && !claimInputBean.getSparePartRequired2().isEmpty()) {
                    spareParts2.setSparePartType(claimInputBean.getSparePartRequired2());
                    spareParts2.setQty(claimInputBean.getQuantity2());
                    sparePartsList.add(spareParts2);
                }
                SpareParts spareParts3 = new SpareParts();
                if (claimInputBean.getSparePartRequired3() != null && !claimInputBean.getSparePartRequired3().isEmpty()) {
                    spareParts3.setSparePartType(claimInputBean.getSparePartRequired3());
                    spareParts3.setQty(claimInputBean.getQuantity3());
                    sparePartsList.add(spareParts3);
                }
                SpareParts spareParts4 = new SpareParts();
                if (claimInputBean.getSparePartRequired4() != null && !claimInputBean.getSparePartRequired4().isEmpty()) {
                    spareParts4.setSparePartType(claimInputBean.getSparePartRequired4());
                    spareParts4.setQty(claimInputBean.getQuantity4());
                    sparePartsList.add(spareParts4);
                }
                SpareParts spareParts5 = new SpareParts();
                if (claimInputBean.getSparePartRequired5() != null && !claimInputBean.getSparePartRequired5().isEmpty()) {
                    spareParts5.setSparePartType(claimInputBean.getSparePartRequired5());
                    spareParts5.setQty(claimInputBean.getQuantity5());
                    sparePartsList.add(spareParts5);
                }
                SpareParts spareParts6 = new SpareParts();
                if (claimInputBean.getSparePartRequired6() != null && !claimInputBean.getSparePartRequired6().isEmpty()) {
                    spareParts6.setSparePartType(claimInputBean.getSparePartRequired6());
                    spareParts6.setQty(claimInputBean.getQuantity6());
                    sparePartsList.add(spareParts6);
                }
                SpareParts spareParts7 = new SpareParts();
                if (claimInputBean.getSparePartRequired7() != null && !claimInputBean.getSparePartRequired7().isEmpty()) {
                    spareParts7.setSparePartType(claimInputBean.getSparePartRequired7());
                    spareParts7.setQty(claimInputBean.getQuantity7());
                    sparePartsList.add(spareParts7);
                }
                SpareParts spareParts8 = new SpareParts();
                if (claimInputBean.getSparePartRequired8() != null && !claimInputBean.getSparePartRequired8().isEmpty()) {
                    spareParts8.setSparePartType(claimInputBean.getSparePartRequired8());
                    spareParts8.setQty(claimInputBean.getQuantity8());
                    sparePartsList.add(spareParts8);
                }
                SpareParts spareParts9 = new SpareParts();
                if (claimInputBean.getSparePartRequired9() != null && !claimInputBean.getSparePartRequired9().isEmpty()) {
                    spareParts9.setSparePartType(claimInputBean.getSparePartRequired9());
                    spareParts9.setQty(claimInputBean.getQuantity9());
                    sparePartsList.add(spareParts9);
                }
                SpareParts spareParts10 = new SpareParts();
                if (claimInputBean.getSparePartRequired10() != null && !claimInputBean.getSparePartRequired10().isEmpty()) {
                    spareParts10.setSparePartType(claimInputBean.getSparePartRequired10());
                    spareParts10.setQty(claimInputBean.getQuantity10());
                    sparePartsList.add(spareParts10);
                }

                claimInputBean.setSpareParts(sparePartsList);

                auditDescription = "Claim (ID: " + claimInputBean.getId() + ") rejected by " + sessionBean.getUsername();
                message = claimRepository.rejectRequestClaim(claimInputBean);

            } else {
                message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.CLAIM_MGT_NO_RECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
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
                audittrace.setPage(PageVarList.CLAIMS_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getClaimAsString(existingClaim, false));
                audittrace.setNewvalue(this.getClaimAsString(claimInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String SendEmail(ClaimInputBean claimInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        Claim existingClaim = null;
        try {
            existingClaim = claimRepository.getClaim(claimInputBean.getId());
            if (existingClaim != null) {

                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                claimInputBean.setLastUpdatedTime(currentDate);
                claimInputBean.setLastUpdatedUser(lastUpdatedUser);
                claimInputBean.setStatus(StatusVarList.STATUS_CLAIM_PRE_APPROVED);
                claimInputBean.setIsInHouse("0");

                auditDescription = "Claim (ID: " + claimInputBean.getId() + ") acknowledge by " + sessionBean.getUsername();
                message = claimRepository.sendEmail(claimInputBean);

            } else {
                message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.CLAIM_MGT_NO_RECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
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
                audittrace.setPage(PageVarList.CLAIMS_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getClaimAsString(existingClaim, false));
                audittrace.setNewvalue(this.getClaimAsString(claimInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String notedRequestClaim(ClaimInputBean claimInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        Claim existingClaim = null;
        try {
            existingClaim = claimRepository.getClaim(claimInputBean.getId());
            if (existingClaim != null) {

                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                claimInputBean.setStatus(StatusVarList.STATUS_CLAIM_NOTED);
                claimInputBean.setIsInHouse("1");
                claimInputBean.setLastUpdatedTime(currentDate);
                claimInputBean.setLastUpdatedUser(lastUpdatedUser);

                if(!claimInputBean.getCostType().equalsIgnoreCase("LABOUR")){
                    claimInputBean.setHours(null);
                    claimInputBean.setLabourRate(null);
                }

                ArrayList<SpareParts> sparePartsList = new ArrayList<SpareParts>();
                SpareParts spareParts1 = new SpareParts();
                SpareParts spareParts2 = new SpareParts();

                if (claimInputBean.getSparePartRequired1() != null && !claimInputBean.getSparePartRequired1().isEmpty()) {
                    spareParts1.setSparePartType(claimInputBean.getSparePartRequired1());
                    spareParts1.setQty(claimInputBean.getQuantity1());
                    sparePartsList.add(spareParts1);
                }
                if (claimInputBean.getSparePartRequired2() != null && !claimInputBean.getSparePartRequired2().isEmpty()) {
                    spareParts2.setSparePartType(claimInputBean.getSparePartRequired2());
                    spareParts2.setQty(claimInputBean.getQuantity2());
                    sparePartsList.add(spareParts2);
                }
                SpareParts spareParts3 = new SpareParts();
                if (claimInputBean.getSparePartRequired3() != null && !claimInputBean.getSparePartRequired3().isEmpty()) {
                    spareParts3.setSparePartType(claimInputBean.getSparePartRequired3());
                    spareParts3.setQty(claimInputBean.getQuantity3());
                    sparePartsList.add(spareParts3);
                }
                SpareParts spareParts4 = new SpareParts();
                if (claimInputBean.getSparePartRequired4() != null && !claimInputBean.getSparePartRequired4().isEmpty()) {
                    spareParts4.setSparePartType(claimInputBean.getSparePartRequired4());
                    spareParts4.setQty(claimInputBean.getQuantity4());
                    sparePartsList.add(spareParts4);
                }
                SpareParts spareParts5 = new SpareParts();
                if (claimInputBean.getSparePartRequired5() != null && !claimInputBean.getSparePartRequired5().isEmpty()) {
                    spareParts5.setSparePartType(claimInputBean.getSparePartRequired5());
                    spareParts5.setQty(claimInputBean.getQuantity5());
                    sparePartsList.add(spareParts5);
                }
                SpareParts spareParts6 = new SpareParts();
                if (claimInputBean.getSparePartRequired6() != null && !claimInputBean.getSparePartRequired6().isEmpty()) {
                    spareParts6.setSparePartType(claimInputBean.getSparePartRequired6());
                    spareParts6.setQty(claimInputBean.getQuantity6());
                    sparePartsList.add(spareParts6);
                }
                SpareParts spareParts7 = new SpareParts();
                if (claimInputBean.getSparePartRequired7() != null && !claimInputBean.getSparePartRequired7().isEmpty()) {
                    spareParts7.setSparePartType(claimInputBean.getSparePartRequired7());
                    spareParts7.setQty(claimInputBean.getQuantity7());
                    sparePartsList.add(spareParts7);
                }
                SpareParts spareParts8 = new SpareParts();
                if (claimInputBean.getSparePartRequired8() != null && !claimInputBean.getSparePartRequired8().isEmpty()) {
                    spareParts8.setSparePartType(claimInputBean.getSparePartRequired8());
                    spareParts8.setQty(claimInputBean.getQuantity8());
                    sparePartsList.add(spareParts8);
                }
                SpareParts spareParts9 = new SpareParts();
                if (claimInputBean.getSparePartRequired9() != null && !claimInputBean.getSparePartRequired9().isEmpty()) {
                    spareParts9.setSparePartType(claimInputBean.getSparePartRequired9());
                    spareParts9.setQty(claimInputBean.getQuantity9());
                    sparePartsList.add(spareParts9);
                }
                SpareParts spareParts10 = new SpareParts();
                if (claimInputBean.getSparePartRequired10() != null && !claimInputBean.getSparePartRequired10().isEmpty()) {
                    spareParts10.setSparePartType(claimInputBean.getSparePartRequired10());
                    spareParts10.setQty(claimInputBean.getQuantity10());
                    sparePartsList.add(spareParts10);
                }

                claimInputBean.setSpareParts(sparePartsList);

                auditDescription = "Claim (ID: " + claimInputBean.getId() + ") noted by " + sessionBean.getUsername();
                message = claimRepository.notedRequestClaim(claimInputBean);

            } else {
                message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.CLAIM_MGT_NO_RECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            e.printStackTrace();
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.CLAIMS_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getClaimAsString(existingClaim, false));
                audittrace.setNewvalue(this.getClaimAsString(claimInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public List<CommonKeyVal> getCostTypeList() {

        List<CommonKeyVal> list = new ArrayList<CommonKeyVal>();

        CommonKeyVal costTYpe1 = new CommonKeyVal();
        costTYpe1.setKey("LABOUR");
        costTYpe1.setValue("Labour");
        list.add(costTYpe1);

        CommonKeyVal costTYpe2 = new CommonKeyVal();
        costTYpe2.setKey("MATERIALS");
        costTYpe2.setValue("Materials");
        list.add(costTYpe2);

        CommonKeyVal costTYpe3 = new CommonKeyVal();
        costTYpe3.setKey("SUBLET");
        costTYpe3.setValue("Sublet");
        list.add(costTYpe3);

        return list;

    }

    public List<CommonKeyVal> getFailingAreaList() {

        List<CommonKeyVal> list = new ArrayList<CommonKeyVal>();

        CommonKeyVal failingArea1 = new CommonKeyVal();
        failingArea1.setKey("FAILURE_1");
        failingArea1.setValue("FAILURE_1");
        list.add(failingArea1);

        CommonKeyVal failingArea2 = new CommonKeyVal();
        failingArea2.setKey("FAILURE_2");
        failingArea2.setValue("FAILURE_2");
        list.add(failingArea2);

        CommonKeyVal failingArea3 = new CommonKeyVal();
        failingArea3.setKey("FAILURE_3");
        failingArea3.setValue("FAILURE_3");
        list.add(failingArea3);

        return list;

    }

    public List<CommonKeyVal> getClaimTypeList() {

        List<CommonKeyVal> list = new ArrayList<CommonKeyVal>();

        CommonKeyVal puchasingStatus1 = new CommonKeyVal();
        puchasingStatus1.setKey("STOCK_VAN");
        puchasingStatus1.setValue("stock van(Consignment)");
        list.add(puchasingStatus1);

        CommonKeyVal puchasingStatus2 = new CommonKeyVal();
        puchasingStatus2.setKey("TO_BE_DELIVERED");
        puchasingStatus2.setValue("to be delivered");
        list.add(puchasingStatus2);

        CommonKeyVal puchasingStatus3 = new CommonKeyVal();
        puchasingStatus3.setKey("SOLD");
        puchasingStatus3.setValue("Sold");
        list.add(puchasingStatus3);

        return list;

    }

  public long getRequestCount(String Status) throws Exception {
        long count;
        try {
            count = claimRepository.getRequestCount(Status);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }



}
