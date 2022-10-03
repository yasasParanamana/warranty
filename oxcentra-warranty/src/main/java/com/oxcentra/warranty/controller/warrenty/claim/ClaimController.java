package com.oxcentra.warranty.controller.warrenty.claim;

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
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.mapping.warranty.SpareParts;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.mapping.warranty.WarrantyAttachments;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.service.common.CommonService;
import com.oxcentra.warranty.service.warranty.claim.ClaimService;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.common.DataTablesResponse;
import com.oxcentra.warranty.util.common.ResponseBean;
import com.oxcentra.warranty.util.common.UploadedFile;
import com.oxcentra.warranty.util.varlist.*;
import com.oxcentra.warranty.validators.RequestBeanValidation;
import com.oxcentra.warranty.validators.warranty.claim.ClaimValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@Scope("request")
public class ClaimController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonService commonService;

    @Autowired
    ClaimService claimService;

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

    @Autowired
    CommonVarList commonVarList;


    @GetMapping("/viewWarrantyClaims")
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getWarrantyClaims(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  WARRANTY CLAIMS PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("claimview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("claimview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listWarrantyClaims", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Claim> searchClaim(@RequestBody ClaimInputBean claimInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  WARRANTY CLAIMS SEARCH");
        DataTablesResponse<Claim> responseBean = new DataTablesResponse<>();
        try {
            long count = claimService.getDataCount(claimInputBean);
            if (count > 0) {
                List<Claim> list = claimService.getClaimSearchResults(claimInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = claimInputBean.echo;
                responseBean.columns = claimInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = claimInputBean.echo;
                responseBean.columns = claimInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addWarrantyClaims", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addClaim(@ModelAttribute("claim") ClaimInputBean claimInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM ADD");
        ResponseBean responseBean = null;
        try {
            claimInputBean.setId("WDC-" + common.getFormattedDate());
            System.out.println("Claim ID    : " + claimInputBean.getId() );

            BindingResult bindingResult = validateRequestBean(claimInputBean);

            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = claimService.insertClaim(claimInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CLAIM_MGT_SUCCESS_ADD, null, locale), null);
                } else {
                    responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @GetMapping(value = "/getWarrantyClaims")
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Claim getClaim(@RequestParam String id) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM GET");
        Claim claim= new Claim();
        SpareParts spareParts = new SpareParts();
        WarrantyAttachments warrantyAttachments = new WarrantyAttachments();
        try {
            if (id != null && !id.trim().isEmpty()) {
                System.out.println("WARRANTY ID : " +id);
                claim = claimService.getClaim(id);

                //get model
                List<SpareParts> sparePartsList = claimService.getSpareParts(id);
                claim.setSparePartList(sparePartsList);

                //get repair files
                List<WarrantyAttachments> attachmentsRepairFileList = claimService.getFiles(id, commonVarList.ATTACHMENT_FILE_TYPE_REPAIR);
                claim.setRepairFileList(attachmentsRepairFileList);

                //get cost type files
                List<WarrantyAttachments> attachmentsCostFileList = claimService.getFiles(id, commonVarList.ATTACHMENT_FILE_TYPE_DEALER);
                claim.setClaimTypeFileList(attachmentsCostFileList);


            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return claim;
    }

    @GetMapping(value = "/getSupplierDetailsWarrantyClaims")
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Supplier getSupplierDetails(@RequestParam String supplierId) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM SUPPLIER DETAILS");
        Supplier supplier= new Supplier();
        try {
            if (supplierId != null && !supplierId.trim().isEmpty()) {
                System.out.println("SUPPLIER ID : " +supplierId);
                supplier = claimService.getSupplierDetails(supplierId);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return supplier;
    }

    @PostMapping(value = "/updateWarrantyClaims", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateClaim(@ModelAttribute("claim") ClaimInputBean claimInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM UPDATE");
        ResponseBean responseBean = null;
        try {
          /*  BindingResult bindingResult = validateRequestBean(claimInputBean);
            if (bindingResult.hasErrors()) {
                responseBean.setErrorMessage(messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {*/
                String message = claimService.updateClaim(claimInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TASK_MGT_SUCCESS_UPDATE, null, locale), null);
                } else {
                    responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
                }
            /*}*//**/
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/deleteWarrantyClaims", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteClaim(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM DELETE");
        ResponseBean responseBean = null;
        try {
            String message = claimService.deleteClaim(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CLAIM_MGT_SUCCESS_DELETE, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/approveWarrantyClaims", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean approveRequestClaim(@ModelAttribute("claim") ClaimInputBean claimInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM APPROVE");
        ResponseBean responseBean = null;
        try {
            String message = claimService.approveRequestClaim(claimInputBean, locale);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CLAIM_MGT_SUCCESS_APPROVE, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectWarrantyClaims", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean rejectRequestClaim(@ModelAttribute("claim") ClaimInputBean claimInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM REJECT");
        ResponseBean responseBean = null;
        try {
            String message = claimService.rejectRequestClaim(claimInputBean, locale);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CLAIM_MGT_SUCCESS_REJECT, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/sendEmailWarrantyClaims", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean sendEmailClaim(@ModelAttribute("claim") ClaimInputBean claimInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM SEND EMAIL");
        ResponseBean responseBean = null;
        try {
            String message = claimService.SendEmail(claimInputBean, locale);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CLAIM_MGT_SUCCESS_APPROVE, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/notedWarrantyClaims", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean notedRequestClaim(@ModelAttribute("claim") ClaimInputBean claimInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM NOTED");
        ResponseBean responseBean = null;
        try {
            String message = claimService.notedRequestClaim(claimInputBean, locale);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CLAIM_MGT_SUCCESS_APPROVE, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @ModelAttribute
    public void getClaimBean(org.springframework.ui.Model map) throws Exception {
        ClaimInputBean claimInputBean = new ClaimInputBean();
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
        List<CommonKeyVal> costTypeList = claimService.getCostTypeList();
        //Failing Area
        List<CommonKeyVal> failingAreaList = claimService.getFailingAreaList();
        //Purchasing Status
        List<CommonKeyVal> claimTypeList = claimService.getClaimTypeList();

        //pending request Count
        long pendingRequestCount = claimService.getRequestCount(StatusVarList.STATUS_CLAIM_PENDING);
        long inPurchaseRequestCount = claimService.getRequestCount(StatusVarList.STATUS_CLAIM_PRE_APPROVED);
        long notedRequestCount = claimService.getRequestCount(StatusVarList.STATUS_CLAIM_NOTED);

        //set values to claimInputBean bean
        claimInputBean.setStatusList(statusList);
        claimInputBean.setStatusActList(statusActList);
        claimInputBean.setModelActList(modelActList);
        claimInputBean.setStateActList(stateActList);
        claimInputBean.setDealership(sessionBean.getUser().getDealership());

        claimInputBean.setFailureTypeActList(failureTypeActList);
        claimInputBean.setFailureAreaActList(failureAreaActList);
        claimInputBean.setRepairTypeActList(RepairTypeActList);
        claimInputBean.setSupplierActList(SupplierActList);
        claimInputBean.setCostTypeList(costTypeList);
        claimInputBean.setFailingAreaList(failingAreaList);
        claimInputBean.setClaimTypeList(claimTypeList);

        claimInputBean.setCountPending(Long.toString(pendingRequestCount));
        claimInputBean.setCountInPurchase(Long.toString(inPurchaseRequestCount));
        claimInputBean.setCountNoted(Long.toString(notedRequestCount));

        //set privileges
        this.applyUserPrivileges(claimInputBean);
        //add values to model map
        map.addAttribute("claim", claimInputBean);
    }

    private void applyUserPrivileges(ClaimInputBean claimInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.CLAIMS_MGT_PAGE, sessionBean);
            claimInputBean.setVadd(false);
            claimInputBean.setVupdate(false);
            claimInputBean.setVdelete(false);

            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        claimInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        claimInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        claimInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.VIEW_TASK)) {
                        claimInputBean.setVview(true);
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
