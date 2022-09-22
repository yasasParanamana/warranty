package com.oxcentra.warranty.controller.warrenty.critical;

import com.oxcentra.warranty.annotation.accesscontrol.AccessControl;
import com.oxcentra.warranty.bean.common.CommonKeyVal;
import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.sysconfigmgt.failurearea.FailureArea;
import com.oxcentra.warranty.bean.sysconfigmgt.failuretype.FailureType;
import com.oxcentra.warranty.bean.sysconfigmgt.model.Model;
import com.oxcentra.warranty.bean.sysconfigmgt.repairtype.RepairType;
import com.oxcentra.warranty.bean.sysconfigmgt.state.State;
import com.oxcentra.warranty.bean.warranty.critical.CriticalInputBean;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.mapping.warranty.*;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.service.common.CommonService;
import com.oxcentra.warranty.service.warranty.critical.CriticalService;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.common.DataTablesResponse;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import com.oxcentra.warranty.util.varlist.TaskVarList;
import com.oxcentra.warranty.validators.RequestBeanValidation;
import com.oxcentra.warranty.validators.warranty.claim.ClaimValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@Scope("request")
public class CriticalController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonService commonService;

    @Autowired
    CriticalService criticalService;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    ClaimValidator claimValidator;

    @Autowired
    Common common;

    @GetMapping("/viewCritical")
    @AccessControl(pageCode = PageVarList.CRITICAL_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getCritical(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CRITICAL PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("criticalview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("criticalview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listCritical", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CRITICAL_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Claim> searchCritical(@RequestBody CriticalInputBean criticalInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  CRITICAL SEARCH");
        DataTablesResponse<Claim> responseBean = new DataTablesResponse<>();
        try {
            System.out.println("criticalInputBean > " + criticalInputBean);
            long count = criticalService.getDataCount(criticalInputBean);
            if (count > 0) {
                List<Claim> list = criticalService.getCriticalSearchResults(criticalInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = criticalInputBean.echo;
                responseBean.columns = criticalInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = criticalInputBean.echo;
                responseBean.columns = criticalInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }


    @GetMapping(value = "/getCritical")
    @AccessControl(pageCode = PageVarList.CRITICAL_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Claim getClaim(@RequestParam String id) {
        logger.info("[" + sessionBean.getSessionid() + "]  CRITICAL GET");
        Claim claim= new Claim();
        SpareParts spareParts = new SpareParts();
        WarrantyAttachments warrantyAttachments = new WarrantyAttachments();
        try {
            if (id != null && !id.trim().isEmpty()) {
                System.out.println("WARRANTY ID : " +id);
                claim = criticalService.getClaim(id);

                //get model
                List<SpareParts> sparePartsList = criticalService.getSpareParts(id);
                claim.setSparePartList(sparePartsList);

                //get pdf files
//                List<WarrantyAttachments> attachmentsPDFList = criticalService.getPdfFiles(id);
//                claim.setPdfFileList(attachmentsPDFList);

                //get jpeg

            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return claim;
    }

    @ModelAttribute
    public void getClaimBean(org.springframework.ui.Model map) throws Exception {
        CriticalInputBean criticalInputBean = new CriticalInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        List<Status> statusActList = common.getActiveStatusList();
        //get model
        List<Model> modelActList = commonRepository.getActiveModelList(StatusVarList.STATUS_DFLT_ACT);
        //get state
        List<State> stateActList = commonRepository.getActiveStateList(StatusVarList.STATUS_DFLT_ACT);
        //Failure Type
        List<FailureType> failureTypeActList = commonRepository.getActiveFailureTypeList(StatusVarList.STATUS_DFLT_ACT);
        //Failure Area
        List<FailureArea> failureAreaActList = commonRepository.getActiveFailureAreaList(StatusVarList.STATUS_DFLT_ACT);
        //Repair Type
        List<RepairType> RepairTypeActList = commonRepository.getActiveRepairTypeList(StatusVarList.STATUS_DFLT_ACT);
        //Supplier List
        List<Supplier> SupplierActList = commonRepository.getActiveSupplierList(StatusVarList.STATUS_DFLT_ACT);

        //Cost Type
        List<CommonKeyVal> costTypeList = criticalService.getCostTypeList();
        //Failing Area
        List<CommonKeyVal> failingAreaList = criticalService.getFailingAreaList();
        //Purchasing Status
        List<CommonKeyVal> claimTypeList = criticalService.getClaimTypeList();


        //set values to criticalInputBean bean
        criticalInputBean.setStatusList(statusList);
        criticalInputBean.setStatusActList(statusActList);
        criticalInputBean.setModelActList(modelActList);
        criticalInputBean.setStateActList(stateActList);
        criticalInputBean.setDealership(sessionBean.getUser().getDealership());

        criticalInputBean.setFailureTypeActList(failureTypeActList);
        criticalInputBean.setFailureAreaActList(failureAreaActList);
        criticalInputBean.setRepairTypeActList(RepairTypeActList);
        criticalInputBean.setSupplierActList(SupplierActList);
        criticalInputBean.setCostTypeList(costTypeList);
        criticalInputBean.setFailingAreaList(failingAreaList);
        criticalInputBean.setClaimTypeList(claimTypeList);

        //set privileges
        this.applyUserPrivileges(criticalInputBean);
        //add values to model map
        map.addAttribute("critical", criticalInputBean);
    }

    private void applyUserPrivileges(CriticalInputBean criticalInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.CRITICAL_MGT_PAGE, sessionBean);
            criticalInputBean.setVadd(false);
            criticalInputBean.setVupdate(false);
            criticalInputBean.setVdelete(false);
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        criticalInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        criticalInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        criticalInputBean.setVdelete(true);
                    }
                });
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public BindingResult validateRequestBean(Object object) {
        DataBinder dataBinder = new DataBinder(object);
        dataBinder.setValidator(claimValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }

}
