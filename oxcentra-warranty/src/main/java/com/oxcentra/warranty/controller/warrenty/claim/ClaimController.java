package com.oxcentra.warranty.controller.warrenty.claim;

import com.oxcentra.warranty.annotation.accesscontrol.AccessControl;
import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.sysconfigmgt.model.Model;
import com.oxcentra.warranty.bean.sysconfigmgt.state.State;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.service.common.CommonService;
import com.oxcentra.warranty.service.warranty.claim.ClaimService;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.common.DataTablesResponse;
import com.oxcentra.warranty.util.common.ResponseBean;
import com.oxcentra.warranty.util.common.UploadedFile;
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
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
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
            System.out.println("claimInputBean > " + claimInputBean);
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

    @RequestMapping(value = "/listDualWarrantyClaims", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualClaim(@RequestBody ClaimInputBean claimInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = claimService.getDataCountDual(claimInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = claimService.getClaimSearchResultsDual(claimInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = claimInputBean.echo;
                responseBean.columns = claimInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
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
            claimInputBean.setId("WDC-" + System.currentTimeMillis() / 100);

//            for (String file: claimInputBean.getFile()) {
//                System.out.println("File >>>>>>>>>>>>" +file);
//            }

            System.out.println("File >>>> " + claimInputBean.getFile().size() );

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
        try {
            if (id != null && !id.trim().isEmpty()) {
                System.out.println("ID  " +id);
                claim = claimService.getClaim(id);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return claim;
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
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TASK_MGT_SUCCESS_DELETE, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

   /* @PostMapping(value = "/confirmWarrantyClaims", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmClaim(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  TASK CONFIRM");
        ResponseBean responseBean = null;
        try {
            String message = claimService.confirmClaim(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TASK_MGT_SUCCESS_CONFIRM, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }*/

/*    @PostMapping(value = "/rejectWarrantyClaims", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CLAIMS_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectClaim(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CLAIM REJECT");
        ResponseBean responseBean = null;
        try {
            String message = claimService.rejectClaim(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TASK_MGT_SUCCESS_REJECT, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }*/

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
        //set values to task bean
        claimInputBean.setStatusList(statusList);
        claimInputBean.setStatusActList(statusActList);
        claimInputBean.setModelActList(modelActList);
        claimInputBean.setStateActList(stateActList);
        claimInputBean.setDealership(sessionBean.getUser().getDealership());
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
            claimInputBean.setVconfirm(false);
            claimInputBean.setVreject(false);
            claimInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.CLAIMS_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        claimInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        claimInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        claimInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        claimInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        claimInputBean.setVreject(true);
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
}
