package com.oxcentra.warranty.service.warranty.inhouse;

import com.oxcentra.warranty.bean.common.CommonKeyVal;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.bean.warranty.inhouse.InHouseInputBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.mapping.warranty.SpareParts;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.mapping.warranty.WarrantyAttachments;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.repository.warranty.inhouse.InHouseRepository;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.varlist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Scope("prototype")
public class InHouseService {

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
    InHouseRepository inHouseRepository;

    private final String fields = "Task Code|Description|Status|Created Time|Last Updated Time|Last Updated User";

    public long getDataCount(InHouseInputBean inHouseInputBean) throws Exception {
        long count = 0;
        try {
            count = inHouseRepository.getDataCount(inHouseInputBean);
        } catch (EmptyResultDataAccessException ere) {
            ere.printStackTrace();
            throw ere;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return count;
    }
    public List<Claim> getClaimSearchResultListForReport(InHouseInputBean inHouseInputBean) throws Exception {
        List<Claim> claimList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_WARRANTY_MGT);
            audittrace.setPage(PageVarList.IN_HOUSE_MGT_PAGE);
            audittrace.setTask(TaskVarList.DOWNLOAD_TASK);
            audittrace.setDescription("Get audit trace search list.");
            //get sms outbox search list for report
            claimList = inHouseRepository.getClaimSearchResultListForReport(inHouseInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return claimList;
    }


    public List<Claim> getCriticalSearchResults(InHouseInputBean inHouseInputBean) throws Exception {
        List<Claim> claimList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_WARRANTY_MGT);
            audittrace.setPage(PageVarList.IN_HOUSE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get Critical search list.");
            //Get Critical search list.
            claimList = inHouseRepository.getClaimSearchResults(inHouseInputBean);
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



    public Claim getClaim(String id) throws Exception {
        Claim claim;
        try {
            claim = inHouseRepository.getClaim(id);
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
            sparePartBeanList = inHouseRepository.getSparePartList(id);
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
            attachmentBeanList = inHouseRepository.getFileList(id,attachmentType);
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
            supplier = inHouseRepository.getSupplierDetails(supplierId);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return supplier;
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

    public String updateRequestClaim(InHouseInputBean inHouseInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        Claim existingClaim = null;
        try {
            existingClaim = inHouseRepository.getClaim(inHouseInputBean.getId());
            if (existingClaim != null) {

                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                inHouseInputBean.setStatus(StatusVarList.STATUS_CLAIM_COMPLETED);
                inHouseInputBean.setLastUpdatedTime(currentDate);
                inHouseInputBean.setLastUpdatedUser(lastUpdatedUser);

                auditDescription = "Claim (ID: " + inHouseInputBean.getId() + ") updated by " + sessionBean.getUsername();
                message = inHouseRepository.updateRequestClaim(inHouseInputBean);

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
                audittrace.setNewvalue(this.getClaimAsString(inHouseInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
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

    private String getClaimAsString(InHouseInputBean inHouseInputBean, boolean checkChanges) {
        StringBuilder claimStringBuilder = new StringBuilder();
        try {
            if (inHouseInputBean != null) {
                if (inHouseInputBean.getId() != null) {
                    claimStringBuilder.append(inHouseInputBean.getId());
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

    public StringBuffer makeCsvReport(InHouseInputBean inHouseInputBean) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            List<Claim> claimList = this.getClaimSearchResultListForReport(inHouseInputBean);
            if (!claimList.isEmpty()) {
                stringBuffer.append("No");
                stringBuffer.append(',');

                stringBuffer.append("Warranty ID");
                stringBuffer.append(',');

                stringBuffer.append("First Name");
                stringBuffer.append(',');

                stringBuffer.append("Last Name");
                stringBuffer.append(',');

                stringBuffer.append("Phone Number");
                stringBuffer.append(',');

                stringBuffer.append("Email");
                stringBuffer.append(',');

                stringBuffer.append("Dealership");
                stringBuffer.append(',');

                stringBuffer.append("Status");
                stringBuffer.append(',');

                stringBuffer.append("Created USer");
                stringBuffer.append(',');

                stringBuffer.append("Created Date");
                stringBuffer.append(',');

                stringBuffer.append("Last Updated User");
                stringBuffer.append(',');

                stringBuffer.append('\n');

                int i = 0;
                for (Claim claim : claimList) {
                    i++;
                    try {
                        stringBuffer.append(i);
                        stringBuffer.append(",");
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (claim.getId() != null) {
                            stringBuffer.append(claim.getId());
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (claim.getFirstName() != null && !claim.getFirstName().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(claim.getFirstName()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (claim.getLastName() != null && !claim.getLastName().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(claim.getLastName()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (claim.getPhone() != null && !claim.getPhone().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(claim.getPhone()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (claim.getEmail() != null && !claim.getEmail().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(claim.getEmail()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (claim.getDealership() != null && !claim.getDealership().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(claim.getDealership()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (claim.getStatus() != null && !claim.getStatus().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(claim.getStatus()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (claim.getCreatedUser() != null && !claim.getCreatedUser().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(claim.getCreatedUser()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (claim.getCreatedTime() != null) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(claim.getCreatedTime().toString()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (claim.getLastUpdatedUser() != null && !claim.getCreatedUser().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(claim.getCreatedUser()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    stringBuffer.append('\n');

                }
                stringBuffer.append('\n');

                stringBuffer.append("From Date : ");
                stringBuffer.append(inHouseInputBean.getFromDate());
                stringBuffer.append('\n');

                stringBuffer.append("To Date : ");
                stringBuffer.append(inHouseInputBean.getToDate());
                stringBuffer.append('\n');

                stringBuffer.append('\n');
            }
        } catch (Exception e) {
            throw e;
        }
        return stringBuffer;
    }

}
