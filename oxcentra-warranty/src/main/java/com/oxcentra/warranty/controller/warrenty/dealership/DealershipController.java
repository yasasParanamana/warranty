package com.oxcentra.warranty.controller.warrenty.dealership;

import com.oxcentra.warranty.annotation.accesscontrol.AccessControl;
import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.dealership.DealershipInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.mapping.warranty.Dealership;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.service.warranty.dealership.DealershipService;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.common.DataTablesResponse;
import com.oxcentra.warranty.util.common.ResponseBean;
import com.oxcentra.warranty.util.varlist.CommonVarList;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import com.oxcentra.warranty.util.varlist.TaskVarList;
import com.oxcentra.warranty.validators.RequestBeanValidation;
import com.oxcentra.warranty.validators.warranty.dealership.DealershipValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.oxcentra.warranty.util.varlist.PageVarList.DEALERSHIP_MGT_PAGE;
import static com.oxcentra.warranty.util.varlist.TaskVarList.ADD_TASK;
import static com.oxcentra.warranty.util.varlist.TaskVarList.VIEW_TASK;


@Controller
@Scope("request")
public class DealershipController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    Common common;

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    DealershipService dealershipService;

    @Autowired
    DealershipValidator dealershipValidator;

    @GetMapping(value = "/viewDealership")
    @AccessControl(pageCode = DEALERSHIP_MGT_PAGE, taskCode = VIEW_TASK)
    public ModelAndView getDealershipPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  DEALERSHIP PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("dealershipview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("dealershipview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listDealership", headers = {"content-type=application/json"})
    @AccessControl(pageCode = DEALERSHIP_MGT_PAGE, taskCode = VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Dealership> searchSection(@RequestBody DealershipInputBean dealershipInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  DEALERSHIP SEARCH");
        DataTablesResponse<Dealership> responseBean = new DataTablesResponse<>();
        try {
            long count = dealershipService.getCount(dealershipInputBean);
            if (count > 0) {
                List<Dealership> list = dealershipService.getDealershipSearchResultList(dealershipInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = dealershipInputBean.echo;
                responseBean.columns = dealershipInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = dealershipInputBean.echo;
                responseBean.columns = dealershipInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualDealership", headers = {"content-type=application/json"})
    @AccessControl(pageCode = DEALERSHIP_MGT_PAGE, taskCode = VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualSection(@RequestBody DealershipInputBean dealershipInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  DEALERSHIP SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = dealershipService.getDataCountDual(dealershipInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = dealershipService.getDealershipSearchResultsDual(dealershipInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = dealershipInputBean.echo;
                responseBean.columns = dealershipInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = dealershipInputBean.echo;
                responseBean.columns = dealershipInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addDealership", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = DEALERSHIP_MGT_PAGE, taskCode = ADD_TASK)
    public @ResponseBody
    ResponseBean addSection(@ModelAttribute("dealership") DealershipInputBean dealershipInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  DEALERSHIP ADD");
        ResponseBean responseBean = null;
        dealershipInputBean.setDealershipCode("DEAL-" + System.currentTimeMillis() / 100);
        try {
            BindingResult bindingResult = validateRequestBean(dealershipInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = dealershipService.insertDealership(dealershipInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.DEALERSHIP_MGT_ADDED_SUCCESSFULLY, null, locale), null);
                } else {
                    responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
                }
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @GetMapping(value = "/getDealership")
    @AccessControl(pageCode = DEALERSHIP_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Dealership getSection(@RequestParam String dealershipCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET DEALERSHIP");
        Dealership dealership = new Dealership();
        try {
            if (dealershipCode != null && !dealershipCode.isEmpty()) {
                dealership = dealershipService.getDealership(dealershipCode);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        logger.info(dealership);
        return dealership;
    }

    @PostMapping(value = "/updateDealership", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = DEALERSHIP_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateSection(@ModelAttribute("dealership") DealershipInputBean dealershipInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE DEALERSHIP");
        ResponseBean responseBean = new ResponseBean();
        try {
            BindingResult bindingResult = validateRequestBean(dealershipInputBean);
            if (bindingResult.hasErrors()) {
                responseBean.setErrorMessage(messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = dealershipService.updateDealership(dealershipInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.DEALERSHIP_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
                } else {
                    responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
                }
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/deleteDealership", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = DEALERSHIP_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteSection(@RequestParam String dealershipCode, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE DEALERSHIP");
        ResponseBean responseBean;
        try {
            String message = dealershipService.deleteDealership(dealershipCode);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.DEALERSHIP_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmDealership", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = DEALERSHIP_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmSection(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  DEALERSHIP CONFIRM");
        ResponseBean responseBean;
        try {
            String message = dealershipService.confirmDealership(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.DEALERSHIP_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectDealership", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = DEALERSHIP_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectSection(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT DEALERSHIP");
        ResponseBean responseBean;
        try {
            String message = dealershipService.rejectDealership(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.DEALERSHIP_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
    public void getSectionBean(Model map) throws Exception {
        DealershipInputBean dealershipInputBean = new DealershipInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        dealershipInputBean.setStatusList(statusList);
        dealershipInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(dealershipInputBean);
        //add values to model map
        map.addAttribute("dealership", dealershipInputBean);
    }

    private void applyUserPrivileges(DealershipInputBean dealershipInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(DEALERSHIP_MGT_PAGE, sessionBean);
            dealershipInputBean.setVadd(false);
            dealershipInputBean.setVupdate(false);
            dealershipInputBean.setVdelete(false);
            dealershipInputBean.setVconfirm(false);
            dealershipInputBean.setVreject(false);
            dealershipInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(DEALERSHIP_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(ADD_TASK)) {
                        dealershipInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        dealershipInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        dealershipInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        dealershipInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        dealershipInputBean.setVreject(true);
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
        dataBinder.setValidator(dealershipValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
