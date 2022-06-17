package com.oxcentra.rdbsms.controller.sysconfigmgt.templatemgt;

import com.oxcentra.rdbsms.annotation.accesscontrol.AccessControl;
import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.templatemgt.SMSTemplateInputBean;
import com.oxcentra.rdbsms.mapping.comparisonfield.Comparisonfield;
import com.oxcentra.rdbsms.mapping.templatemgt.TemplateMgt;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.usermgt.Task;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.service.sysconfigmgt.templatemgt.TemplateMgtService;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.common.DataTablesResponse;
import com.oxcentra.rdbsms.util.common.ResponseBean;
import com.oxcentra.rdbsms.validators.RequestBeanValidation;
import com.oxcentra.rdbsms.validators.sysconfigmgt.templateValidator.SMSTemplateValidator;
import com.oxcentra.rdbsms.util.varlist.*;
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
public class TemplateController implements RequestBeanValidation<Object> {
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
    TemplateMgtService templateMgtService;

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    SMSTemplateValidator smsTemplateValidator;

    @GetMapping(value = "/viewSMSTemplate")
    @AccessControl(pageCode = PageVarList.SMS_TEMPLATE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getSMSTemplate(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS TEMPLATE PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("templatemgtview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("templatemgtview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listSMSTemplateMgt", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.SMS_TEMPLATE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TemplateMgt> searchSMSTemplate(@RequestBody SMSTemplateInputBean smsTemplateInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "] SMS TEMPLATE MGT SEARCH");
        DataTablesResponse<TemplateMgt> responseBean = new DataTablesResponse<>();
        try {
            long count = templateMgtService.getCount(smsTemplateInputBean);
            if (count > 0) {
                List<TemplateMgt> list = templateMgtService.getTemplateSearchResultList(smsTemplateInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = smsTemplateInputBean.echo;
                responseBean.columns = smsTemplateInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = smsTemplateInputBean.echo;
                responseBean.columns = smsTemplateInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualSMSTemplateMgt", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.SMS_TEMPLATE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualSMSTemplate(@RequestBody SMSTemplateInputBean smsTemplateInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS TEMPLATE SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = templateMgtService.getDataCountDual(smsTemplateInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = templateMgtService.getSmsTemplateSearchResultsDual(smsTemplateInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = smsTemplateInputBean.echo;
                responseBean.columns = smsTemplateInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = smsTemplateInputBean.echo;
                responseBean.columns = smsTemplateInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addSMSTemplateMgt", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMS_TEMPLATE_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addCategory(@ModelAttribute("templatemgt") SMSTemplateInputBean smsTemplateInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS TEMPLATE ADD");
        ResponseBean responseBean = null;
        try {

            BindingResult bindingResult = validateRequestBean(smsTemplateInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = templateMgtService.insertSMSTemplate(smsTemplateInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TEMPLATE_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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


    @GetMapping(value = "/getSMSTemplateMgt")
    @AccessControl(pageCode = PageVarList.SMS_TEMPLATE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    TemplateMgt getSMSTemplate(@RequestParam String code) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET SMS TEMPLATE");
        TemplateMgt templateMgt = new TemplateMgt();
        try {
            if (code != null && !code.isEmpty()) {
                templateMgt = templateMgtService.getSMSTemplate(code);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return templateMgt;
    }

    @PostMapping(value = "/updateSMSTemplateMgt", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMS_TEMPLATE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateSMSTemplate(@ModelAttribute("templatemgt") SMSTemplateInputBean smsTemplateInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE SMS TEMPLATE");
        ResponseBean responseBean;
        try {
            BindingResult bindingResult = validateRequestBean(smsTemplateInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = templateMgtService.updateSMSTemplate(smsTemplateInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TEMPLATE_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/deleteSMSTemplateMgt", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMS_TEMPLATE_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteSMSTemplate(@RequestParam String code, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE SMS TEMPLATE");
        ResponseBean responseBean;
        try {
            String message = templateMgtService.deleteSMSTemplate(code);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TEMPLATE_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmSMSTemplateMgt", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMS_TEMPLATE_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmSMSTemplate(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS TEMPLATE CONFIRM");
        ResponseBean responseBean;
        try {
            String message = templateMgtService.confirmSMSTemplate(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TEMPLATE_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        System.out.println("]]]]]]]]] return responseBean > " + responseBean);
        return responseBean;
    }

    @PostMapping(value = "/rejectSMSTemplateMgt", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SMS_TEMPLATE_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectCategory(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT SMS TEMPLATE");
        ResponseBean responseBean;
        try {
            String message = templateMgtService.rejectSmsTemplate(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TEMPLATE_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
    public void getTemplateBean(Model map) throws Exception {
        SMSTemplateInputBean smsTemplateInputBean = new SMSTemplateInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        List<Status> statusActList = common.getActiveStatusList();
        List<Comparisonfield> comparisonfieldList = commonRepository.getComparisonfieldList();
        //set values to task bean
        smsTemplateInputBean.setStatusList(statusList);
        smsTemplateInputBean.setStatusActList(statusActList);
        smsTemplateInputBean.setComparisonfieldList(comparisonfieldList);
        //add privileges to input bean
        this.applyUserPrivileges(smsTemplateInputBean);
        //add values to model map
        map.addAttribute("templatemgt", smsTemplateInputBean);
    }

    private void applyUserPrivileges(SMSTemplateInputBean smsTemplateInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.SMS_TEMPLATE_MGT_PAGE, sessionBean);
            smsTemplateInputBean.setVadd(false);
            smsTemplateInputBean.setVupdate(false);
            smsTemplateInputBean.setVdelete(false);
            smsTemplateInputBean.setVconfirm(false);
            smsTemplateInputBean.setVreject(false);
            smsTemplateInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.SMS_TEMPLATE_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        smsTemplateInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        smsTemplateInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        smsTemplateInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        smsTemplateInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        smsTemplateInputBean.setVreject(true);
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
        dataBinder.setValidator(smsTemplateValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
