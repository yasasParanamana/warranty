package com.oxcentra.warranty.service.warranty.critical;

import com.oxcentra.warranty.bean.common.CommonKeyVal;
import com.oxcentra.warranty.bean.session.SessionBean;;
import com.oxcentra.warranty.bean.warranty.critical.CriticalInputBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.mapping.warranty.*;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.repository.warranty.critical.CriticalRepository;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.varlist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
public class CriticalService {

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
    CriticalRepository criticalRepository;

    private final String fields = "Task Code|Description|Status|Created Time|Last Updated Time|Last Updated User";

    public long getDataCount(CriticalInputBean criticalInputBean) throws Exception {
        long count = 0;
        try {
            count = criticalRepository.getDataCount(criticalInputBean);
        } catch (EmptyResultDataAccessException ere) {
            ere.printStackTrace();
            throw ere;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return count;
    }

    public List<Claim> getCriticalSearchResults(CriticalInputBean criticalInputBean) throws Exception {
        List<Claim> claimList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_USER_MGT);
            audittrace.setPage(PageVarList.CLAIMS_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get Critical search list.");
            //Get Critical search list.
            claimList = criticalRepository.getClaimSearchResults(criticalInputBean);
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
            claim = criticalRepository.getClaim(id);
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
            sparePartBeanList = criticalRepository.getSparePartList(id);
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
            attachmentBeanList = criticalRepository.getFileList(id,attachmentType);
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
            supplier = criticalRepository.getSupplierDetails(supplierId);
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

    public List<Claim> getClaimSearchResultListForReport(CriticalInputBean criticalInputBean) throws Exception {
        List<Claim> claimList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_WARRANTY_MGT);
            audittrace.setPage(PageVarList.CRITICAL_MGT_PAGE);
            audittrace.setTask(TaskVarList.DOWNLOAD_TASK);
            audittrace.setDescription("Get claim search list.");
            //get sms outbox search list for report
            claimList = criticalRepository.getClaimSearchResultListForReport(criticalInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return claimList;
    }

    public StringBuffer makeCsvReport(CriticalInputBean  criticalInputBean) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            List<Claim> claimList = this.getClaimSearchResultListForReport(criticalInputBean);
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
                stringBuffer.append(criticalInputBean.getFromDate());
                stringBuffer.append('\n');

                stringBuffer.append("To Date : ");
                stringBuffer.append(criticalInputBean.getToDate());
                stringBuffer.append('\n');

                stringBuffer.append('\n');
            }
        } catch (Exception e) {
            throw e;
        }
        return stringBuffer;
    }


}
