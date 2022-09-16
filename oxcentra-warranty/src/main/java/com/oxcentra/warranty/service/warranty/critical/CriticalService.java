package com.oxcentra.warranty.service.warranty.critical;

import com.oxcentra.warranty.bean.common.CommonKeyVal;
import com.oxcentra.warranty.bean.common.TempAuthRecBean;
import com.oxcentra.warranty.bean.session.SessionBean;;
import com.oxcentra.warranty.bean.warranty.critical.CriticalInputBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.mapping.warranty.*;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.repository.warranty.claim.ClaimRepository;
import com.oxcentra.warranty.repository.warranty.critical.CriticalRepository;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public List<WarrantyAttachments> getPdfFiles(String id) throws Exception {
        List<WarrantyAttachments> attachmentBeanList;
        try {
            attachmentBeanList = criticalRepository.getPdfFileList(id);
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

}
