package com.oxcentra.rdbsms.controller.sysconfigmgt.smsmtportmgt;

import com.oxcentra.rdbsms.annotation.accesscontrol.AccessControl;
import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.smsmtportmanagement.SmsMtPortInputBean;
import com.oxcentra.rdbsms.mapping.smsmtportmgt.SmsMtPort;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.usermgt.Task;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.service.sysconfigmgt.smsmtportmgt.SmsMtPortService;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.common.DataTablesResponse;
import com.oxcentra.rdbsms.util.common.ResponseBean;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.TaskVarList;
import com.oxcentra.rdbsms.validators.RequestBeanValidation;
import com.oxcentra.rdbsms.validators.sysconfigmgt.smsmtportmgt.SmsMtPortValidator;
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

@Controller
@Scope("request")
public class SmsMtPortController implements RequestBeanValidation<Object> {
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
    SmsMtPortService smsMtPortService;

    @Autowired
    SmsMtPortValidator smsMtPortValidator;

    @GetMapping(value = "/viewSmsMtPort")
    @AccessControl(pageCode = PageVarList.SMSMTPORT_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getMtPortPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS MT PORT PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("smsmtportview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("smsmtportview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listSmsMtPort", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.SMSMTPORT_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<SmsMtPort> searchMTPort(@RequestBody SmsMtPortInputBean smsMtPortInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS MT PORT SEARCH");
        DataTablesResponse<SmsMtPort> responseBean = new DataTablesResponse<>();
        try {
            long count = smsMtPortService.getCount(smsMtPortInputBean);
            if (count > 0) {
                List<SmsMtPort> list = smsMtPortService.getMTPortSearchResultList(smsMtPortInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = smsMtPortInputBean.echo;
                responseBean.columns = smsMtPortInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = smsMtPortInputBean.echo;
                responseBean.columns = smsMtPortInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualSmsMtPort", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.SMSMTPORT_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualSmsMtPort(@RequestBody SmsMtPortInputBean smsMtPortInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS MT PORT SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = smsMtPortService.getDataCountDual(smsMtPortInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = smsMtPortService.getMTPortSearchResultsDual(smsMtPortInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = smsMtPortInputBean.echo;
                responseBean.columns = smsMtPortInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = smsMtPortInputBean.echo;
                responseBean.columns = smsMtPortInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/updateSmsMtPort", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMSMTPORT_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateSmsMtPort(@ModelAttribute("smsmtport") SmsMtPortInputBean smsMtPortInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE SMS MT PORT");
        ResponseBean responseBean = new ResponseBean();
        try {
            BindingResult bindingResult = validateRequestBean(smsMtPortInputBean);
            if (bindingResult.hasErrors()) {
                responseBean.setErrorMessage(messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = smsMtPortService.updateSmsMtPort(smsMtPortInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SMSMTPORT_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @GetMapping(value = "/getSmsMtPort")
    @AccessControl(pageCode = PageVarList.SMSMTPORT_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    SmsMtPort getSmsMtPort(@RequestParam String mtPort) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET SMS MT PORT");
        SmsMtPort smsMtPort = new SmsMtPort();
        try {
            if (mtPort != null && !mtPort.isEmpty()) {
                smsMtPort = smsMtPortService.getSmsMtPort(mtPort);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return smsMtPort;
    }

    @PostMapping(value = "/addSmsMtPort", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMSMTPORT_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addSmsMtPort(@ModelAttribute("smsmtport") SmsMtPortInputBean smsMtPortInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS MT PORT ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(smsMtPortInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, Locale.US));
            } else {
                String message = smsMtPortService.insertSmsMtPort(smsMtPortInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SMSMTPORT_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/deleteSmsMtPort", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMSMTPORT_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteSmsMtPort(@RequestParam String mtPort, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE SMS MT PORT");
        ResponseBean responseBean;
        try {
            String message = smsMtPortService.deleteSmsMtPort(mtPort);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SMSMTPORT_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmSmsMtPort", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMSMTPORT_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmSmsMtPort(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS MT PORT CONFIRM");
        ResponseBean responseBean;
        try {
            String message = smsMtPortService.confirmSmsMtPort(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SMSMTPORT_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectSmsMtPort", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMSMTPORT_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectSmsMtPort(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT SMS MT PORT");
        ResponseBean responseBean;
        try {
            String message = smsMtPortService.rejectSmsMtPort(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SMSMTPORT_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
    public void getSmsMtPortBean(Model map) throws Exception {
        SmsMtPortInputBean smsMtPortInputBean = new SmsMtPortInputBean();
        //get status list
        List<Status> statusList = commonRepository.getActiveDeActiveStatusList();
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        smsMtPortInputBean.setStatusList(statusList);
        smsMtPortInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(smsMtPortInputBean);
        //add values to model map
        map.addAttribute("smsmtport", smsMtPortInputBean);
    }

    private void applyUserPrivileges(SmsMtPortInputBean smsMtPortInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.SMSMTPORT_MGT_PAGE, sessionBean);
            smsMtPortInputBean.setVadd(false);
            smsMtPortInputBean.setVupdate(false);
            smsMtPortInputBean.setVdelete(false);
            smsMtPortInputBean.setVconfirm(false);
            smsMtPortInputBean.setVreject(false);
            smsMtPortInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.SMSMTPORT_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        smsMtPortInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        smsMtPortInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        smsMtPortInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        smsMtPortInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        smsMtPortInputBean.setVreject(true);
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
        dataBinder.setValidator(smsMtPortValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
