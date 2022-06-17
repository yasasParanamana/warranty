package com.epic.rdbsms.controller.sysconfigmgt.smssmppconfiguration;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.smssmppconfiguration.SmsSmppConfigurationInputBean;
import com.epic.rdbsms.mapping.smppconfig.SmppConfiguration;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.sysconfigmgt.smssmppconfiguration.SmsSmppConfigurationService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.CommonVarList;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.TaskVarList;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.smssmppconfiguration.SmsSmppConfigurationValidator;
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
public class SmsSmppConfigurationController implements RequestBeanValidation<Object> {
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
    SmsSmppConfigurationService smsSmppConfigurationService;

    @Autowired
    SmsSmppConfigurationValidator smsSmppConfigurationValidator;

    @GetMapping(value = "/viewSmppConfiguration")
    @AccessControl(pageCode = PageVarList.SMPPCONFIG_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getSmppConfigPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS SMPP CONFIGURATION PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("smppconfigview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("smppconfigview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listSmppConfiguration", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.SMPPCONFIG_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<SmppConfiguration> searchSmppConfig(@RequestBody SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS SMPP CONFIGURATION SEARCH");
        DataTablesResponse<SmppConfiguration> responseBean = new DataTablesResponse<>();
        try {
            long count = smsSmppConfigurationService.getCount(smsSmppConfigurationInputBean);
            if (count > 0) {
                List<SmppConfiguration> list = smsSmppConfigurationService.getSmppConfigSearchResultList(smsSmppConfigurationInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = smsSmppConfigurationInputBean.echo;
                responseBean.columns = smsSmppConfigurationInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = smsSmppConfigurationInputBean.echo;
                responseBean.columns = smsSmppConfigurationInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualSmppConfiguration", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.SMPPCONFIG_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualSmppConfig(@RequestBody SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS SMPP CONFIGURATION SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = smsSmppConfigurationService.getDataCountDual(smsSmppConfigurationInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = smsSmppConfigurationService.getSmppConfigurationSearchResultsDual(smsSmppConfigurationInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = smsSmppConfigurationInputBean.echo;
                responseBean.columns = smsSmppConfigurationInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = smsSmppConfigurationInputBean.echo;
                responseBean.columns = smsSmppConfigurationInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addSmppConfiguration", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMPPCONFIG_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addSmppConfiguration(@ModelAttribute("smppconfig") SmsSmppConfigurationInputBean smsSmppConfigurationInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS SMPP CONFIGURATION ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(smsSmppConfigurationInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = smsSmppConfigurationService.insertSmppConfiguration(smsSmppConfigurationInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SMPP_CONFIGURATION_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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

    @GetMapping(value = "/getSmppConfiguration")
    @AccessControl(pageCode = PageVarList.SMPPCONFIG_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    SmppConfiguration getSmppConfiguration(@RequestParam String smppCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET SMS SMPP CONFIGURATION");
        SmppConfiguration smppConfiguration = new SmppConfiguration();
        try {
            if (smppCode != null && !smppCode.isEmpty()) {
                smppConfiguration = smsSmppConfigurationService.getSmppConfiguration(smppCode);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return smppConfiguration;
    }

    @PostMapping(value = "/updateSmppConfiguration", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMPPCONFIG_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateSmppConfiguration(@ModelAttribute("smppconfig") SmsSmppConfigurationInputBean smsSmppConfigurationInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE SMS SMPP CONFIGURATION");
        ResponseBean responseBean;
        try {
            BindingResult bindingResult = validateRequestBean(smsSmppConfigurationInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = smsSmppConfigurationService.updateSmppConfiguration(smsSmppConfigurationInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SMPP_CONFIGURATION_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/deleteSmppConfiguration", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMPPCONFIG_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteSmppConfiguration(@RequestParam String smppCode, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE SMS SMPP CONFIGURATION");
        ResponseBean responseBean;
        try {
            String message = smsSmppConfigurationService.deleteSmppConfiguration(smppCode);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SMPP_CONFIGURATION_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmSmppConfiguration", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMPPCONFIG_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmSmppConfiguration(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS SMPP CONFIGURATION CONFIRM");
        ResponseBean responseBean;
        try {
            String message = smsSmppConfigurationService.confirmSmppConfiguration(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SMPP_CONFIGURATION_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectSmppConfiguration", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMPPCONFIG_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectSmppConfiguration(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT SMS SMPP CONFIGURATION");
        ResponseBean responseBean;
        try {
            String message = smsSmppConfigurationService.rejectSmppConfiguration(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SMPP_CONFIGURATION_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
    public void getSmppConfigBean(Model map) throws Exception {
        SmsSmppConfigurationInputBean smsSmppConfigurationInputBean = new SmsSmppConfigurationInputBean();
        //get status list
        List<Status> statusList = commonRepository.getActiveDeActiveStatusList();
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        smsSmppConfigurationInputBean.setStatusList(statusList);
        smsSmppConfigurationInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(smsSmppConfigurationInputBean);
        //add values to model map
        map.addAttribute("smppconfig", smsSmppConfigurationInputBean);
    }

    private void applyUserPrivileges(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.SMPPCONFIG_MGT_PAGE, sessionBean);
            smsSmppConfigurationInputBean.setVadd(false);
            smsSmppConfigurationInputBean.setVupdate(false);
            smsSmppConfigurationInputBean.setVdelete(false);
            smsSmppConfigurationInputBean.setVconfirm(false);
            smsSmppConfigurationInputBean.setVreject(false);
            smsSmppConfigurationInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.SMPPCONFIG_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        smsSmppConfigurationInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        smsSmppConfigurationInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        smsSmppConfigurationInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        smsSmppConfigurationInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        smsSmppConfigurationInputBean.setVreject(true);
                    }
                });
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public BindingResult validateRequestBean(Object o) {
        DataBinder dataBinder = new DataBinder(o);
        dataBinder.setValidator(smsSmppConfigurationValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
