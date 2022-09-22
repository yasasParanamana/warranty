package com.oxcentra.warranty.controller.warrenty.inhouse;

import com.oxcentra.warranty.annotation.accesscontrol.AccessControl;
import com.oxcentra.warranty.bean.common.CommonKeyVal;
import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.sysconfigmgt.failurearea.FailureArea;
import com.oxcentra.warranty.bean.sysconfigmgt.failuretype.FailureType;
import com.oxcentra.warranty.bean.sysconfigmgt.model.Model;
import com.oxcentra.warranty.bean.sysconfigmgt.repairtype.RepairType;
import com.oxcentra.warranty.bean.sysconfigmgt.state.State;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.bean.warranty.inhouse.InHouseInputBean;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.mapping.warranty.SpareParts;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.mapping.warranty.WarrantyAttachments;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.service.common.CommonService;
import com.oxcentra.warranty.service.warranty.critical.CriticalService;
import com.oxcentra.warranty.service.warranty.inhouse.InHouseService;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.common.DataTablesResponse;
import com.oxcentra.warranty.util.common.ResponseBean;
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
import org.springframework.http.MediaType;
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
public class InHouseController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonService commonService;

    @Autowired
    InHouseService inHouseService;

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

    @GetMapping("/viewInHouse")
    @AccessControl(pageCode = PageVarList.IN_HOUSE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getInHouse(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  IN_HOUSE PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("inhouseview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("inhouseview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listInHouse", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.IN_HOUSE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Claim> searchInHouse(@RequestBody InHouseInputBean inHouseInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  IN_HOUSE SEARCH");
        DataTablesResponse<Claim> responseBean = new DataTablesResponse<>();
        try {
            System.out.println("inHouseInputBean > " + inHouseInputBean);
            long count = inHouseService.getDataCount(inHouseInputBean);
            if (count > 0) {
                List<Claim> list = inHouseService.getCriticalSearchResults(inHouseInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = inHouseInputBean.echo;
                responseBean.columns = inHouseInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = inHouseInputBean.echo;
                responseBean.columns = inHouseInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }


    @GetMapping(value = "/getInHouse")
    @AccessControl(pageCode = PageVarList.IN_HOUSE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Claim getClaim(@RequestParam String id) {
        logger.info("[" + sessionBean.getSessionid() + "]  IN_HOUSE GET");
        Claim claim= new Claim();
        SpareParts spareParts = new SpareParts();
        WarrantyAttachments warrantyAttachments = new WarrantyAttachments();
        try {
            if (id != null && !id.trim().isEmpty()) {
                System.out.println("WARRANTY ID : " +id);
                claim = inHouseService.getClaim(id);

                //get model
                List<SpareParts> sparePartsList = inHouseService.getSpareParts(id);
                claim.setSparePartList(sparePartsList);

                //get pdf files
                List<WarrantyAttachments> attachmentsPDFList = inHouseService.getPdfFiles(id);
                claim.setPdfFileList(attachmentsPDFList);

                //get jpeg

            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return claim;
    }

    @ModelAttribute
    public void getClaimBean(org.springframework.ui.Model map) throws Exception {
        InHouseInputBean inHouseInputBean = new InHouseInputBean();
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
        List<CommonKeyVal> costTypeList = inHouseService.getCostTypeList();
        //Failing Area
        List<CommonKeyVal> failingAreaList = inHouseService.getFailingAreaList();
        //Purchasing Status
        List<CommonKeyVal> claimTypeList = inHouseService.getClaimTypeList();


        //set values to inHouseInputBean bean
        inHouseInputBean.setStatusList(statusList);
        inHouseInputBean.setStatusActList(statusActList);
        inHouseInputBean.setModelActList(modelActList);
        inHouseInputBean.setStateActList(stateActList);
        inHouseInputBean.setDealership(sessionBean.getUser().getDealership());

        inHouseInputBean.setFailureTypeActList(failureTypeActList);
        inHouseInputBean.setFailureAreaActList(failureAreaActList);
        inHouseInputBean.setRepairTypeActList(RepairTypeActList);
        inHouseInputBean.setSupplierActList(SupplierActList);
        inHouseInputBean.setCostTypeList(costTypeList);
        inHouseInputBean.setFailingAreaList(failingAreaList);
        inHouseInputBean.setClaimTypeList(claimTypeList);

        //set privileges
        this.applyUserPrivileges(inHouseInputBean);
        //add values to model map
        map.addAttribute("inHouse", inHouseInputBean);
    }

    private void applyUserPrivileges(InHouseInputBean inHouseInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.IN_HOUSE_MGT_PAGE, sessionBean);
            inHouseInputBean.setVadd(false);
            inHouseInputBean.setVupdate(false);
            inHouseInputBean.setVdelete(false);
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        inHouseInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        inHouseInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        inHouseInputBean.setVdelete(true);
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

    @PostMapping(value = "/updateInHouse", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.IN_HOUSE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateClaim(@ModelAttribute("inHouse") InHouseInputBean inHouseInputBean , Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM UPDATE");
        ResponseBean responseBean = null;
        try {
            String message = inHouseService.updateRequestClaim(inHouseInputBean, locale);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CLAIM_MGT_SUCCESS_UPDATE, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

}
