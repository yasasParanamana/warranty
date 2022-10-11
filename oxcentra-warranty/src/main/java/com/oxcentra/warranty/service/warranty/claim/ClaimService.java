package com.oxcentra.warranty.service.warranty.claim;

import com.oxcentra.warranty.bean.common.CommonKeyVal;
import com.oxcentra.warranty.bean.common.TempAuthRecBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.bean.warranty.claim.EmailRequestBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
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
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintViolationException;
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


    private static RestTemplate restTemplate = new RestTemplate();

    private static final String baseURL = "http://localhost:8082/v1.0/service/supplier/email/";

    private final String fields = "Warranty ID|Description|Status|Created Time|Last Updated Time|Last Updated User";

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

    public List<WarrantyAttachments> getFiles(String id ,String attachmentType) throws Exception {
        List<WarrantyAttachments> attachmentBeanList;
        try {
            attachmentBeanList = claimRepository.getFileList(id,attachmentType);
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
        String Token="";
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
                Token = common.GenerateUUID();
                claimInputBean.setSupplierUrlToken(Token);

                auditDescription = "Claim (ID: " + claimInputBean.getId() + ") acknowledge by " + sessionBean.getUsername();
                message = claimRepository.sendEmail(claimInputBean);

                if(message.isEmpty()){

                    EmailRequestBean request = new EmailRequestBean();

                    Claim claim = this.getClaim(claimInputBean.getId());

                    request.setToken(Token);

                    request.setClaimOnSupplier(claim.getTotalCost().toString());
                    request.setModel(claim.getModel());
                    request.setFailureArea(claim.getFailureArea());
                    request.setRepairType(claim.getRepairType());
                    request.setRepairDescription(claim.getRepairDescription());
                    request.setCostDescription(claim.getCostDescription());

                    EmailRequest emailRequest = new EmailRequest();
                    try{
                        message = emailRequest.sentEmail(request);
                        System.out.println(message);
                    }catch (Exception e){
                        e.printStackTrace();
                        //Reverse Status
                        claimInputBean.setStatus(StatusVarList.STATUS_CLAIM_PENDING);
                        message = claimRepository.updateClaimStatus(claimInputBean);
                        message = MessageVarList.CLAIM_MGT_ERROR_MAIL_SEND;
                    }
                }else{
                    message = MessageVarList.CLAIM_MGT_ERROR_MAIL_SEND;
                }
            } else {
                message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.CLAIM_MGT_NO_RECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CLAIM_MGT_NO_RECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
            ere.printStackTrace();
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
            e.printStackTrace();
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_WARRANTY_MGT);
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
        costTYpe1.setKey("Labour");
        costTYpe1.setValue("Labour");
        list.add(costTYpe1);

        CommonKeyVal costTYpe2 = new CommonKeyVal();
        costTYpe2.setKey("Materials");
        costTYpe2.setValue("Materials");
        list.add(costTYpe2);

        CommonKeyVal costTYpe3 = new CommonKeyVal();
        costTYpe3.setKey("Sublet");
        costTYpe3.setValue("Sublet");
        list.add(costTYpe3);

        return list;

    }

    public List<CommonKeyVal> getFailingAreaList() {

        List<CommonKeyVal> list = new ArrayList<CommonKeyVal>();

        CommonKeyVal failingArea1 = new CommonKeyVal();
        failingArea1.setKey("Missing");
        failingArea1.setValue("Missing");
        list.add(failingArea1);

        CommonKeyVal failingArea2 = new CommonKeyVal();
        failingArea2.setKey("Rejected");
        failingArea2.setValue("Rejected");
        list.add(failingArea2);

        CommonKeyVal failingArea3 = new CommonKeyVal();
        failingArea3.setKey("Wrong Parts");
        failingArea3.setValue("Wrong Parts");
        list.add(failingArea3);

        return list;

    }

    public List<CommonKeyVal> getClaimTypeList() {

        List<CommonKeyVal> list = new ArrayList<CommonKeyVal>();

        CommonKeyVal puchasingStatus1 = new CommonKeyVal();
        puchasingStatus1.setKey("Stock Van(Consignment)");
        puchasingStatus1.setValue("Stock Van(Consignment)");
        list.add(puchasingStatus1);

        CommonKeyVal puchasingStatus2 = new CommonKeyVal();
        puchasingStatus2.setKey("To Be Delivered");
        puchasingStatus2.setValue("To Be Delivered");
        list.add(puchasingStatus2);

        CommonKeyVal puchasingStatus3 = new CommonKeyVal();
        puchasingStatus3.setKey("Sold");
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
