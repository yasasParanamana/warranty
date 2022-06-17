package com.epic.rdbsms.controller.sysconfigmgt.passwordpolicy;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.passwordpolicy.PasswordPolicyInputBean;
import com.epic.rdbsms.mapping.passwordpolicy.PasswordPolicy;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.common.CommonService;
import com.epic.rdbsms.service.sysconfigmgt.passwordpolicy.PasswordPolicyService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.TaskVarList;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.sysconfigmgt.passwordpolicy.PasswordPolicyValidator;
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
public class PasswordPolicyController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonService commonService;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    PasswordPolicyService passwordPolicyService;

    @Autowired
    PasswordPolicyValidator passwordPolicyValidator;

    @Autowired
    Common common;

    @GetMapping("/viewPasswordPolicy")
    @AccessControl(pageCode = PageVarList.PASSWORDPOLICY_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getPasswordPolicyPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD POLICY PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("passwordpolicyview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("passwordpolicyview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listPasswordPolicy", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.PASSWORDPOLICY_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<PasswordPolicy> searchPasswordPolicy(@RequestBody PasswordPolicyInputBean passwordPolicyInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD POLICY SEARCH");
        DataTablesResponse<PasswordPolicy> responseBean = new DataTablesResponse<>();
        try {
            long count = passwordPolicyService.getDataCount(passwordPolicyInputBean);
            if (count > 0) {
                List<PasswordPolicy> passwordPolicyList = passwordPolicyService.getPasswordPolicySearchResults(passwordPolicyInputBean);
                //set values to response bean
                responseBean.data.addAll(passwordPolicyList);
                responseBean.echo = passwordPolicyInputBean.echo;
                responseBean.columns = passwordPolicyInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = passwordPolicyInputBean.echo;
                responseBean.columns = passwordPolicyInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualPasswordPolicy", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.PASSWORDPOLICY_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualPasswordPolicy(@RequestBody PasswordPolicyInputBean passwordPolicyInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD POLICY SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = passwordPolicyService.getDataCountDual(passwordPolicyInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = passwordPolicyService.getPasswordPolicySearchResultsDual(passwordPolicyInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = passwordPolicyInputBean.echo;
                responseBean.columns = passwordPolicyInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = passwordPolicyInputBean.echo;
                responseBean.columns = passwordPolicyInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @GetMapping(value = "/getPasswordPolicy")
    @AccessControl(pageCode = PageVarList.PASSWORDPOLICY_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    PasswordPolicy getPasswordPolicy(@RequestParam String policyid) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD POLICY GET");
        PasswordPolicy passwordPolicy = new PasswordPolicy();
        try {
            if (policyid != null && !policyid.trim().isEmpty()) {
                int id = Integer.parseInt(policyid);
                passwordPolicy = passwordPolicyService.getWebPasswordPolicy(id);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return passwordPolicy;
    }

    @PostMapping(value = "/updatePasswordPolicy", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.PASSWORDPOLICY_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updatePasswordPolicy(@ModelAttribute("passwordPolicy") PasswordPolicyInputBean passwordPolicyInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD POLICY UPDATE");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(passwordPolicyInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = passwordPolicyService.updatePasswordPolicy(passwordPolicyInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.PASSWORD_POLICY_MGT_SUCCESS_UPDATE, null, locale), null);
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

    @PostMapping(value = "/confirmPasswordPolicy", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.PASSWORDPOLICY_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmPasswordPolicy(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD POLICY CONFIRM");
        ResponseBean responseBean = null;
        try {
            String message = passwordPolicyService.confirmPasswordPolicy(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.PASSWORD_POLICY_MGT_SUCCESS_CONFIRM, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectPasswordPolicy", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.PASSWORDPOLICY_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    @ResponseBody
    public ResponseBean rejectPasswordPolicy(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD POLICY REJECT");
        ResponseBean responseBean = null;
        try {
            String message = passwordPolicyService.rejectPasswordPolicy(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.PASSWORD_POLICY_MGT_SUCCESS_REJECT, null, locale), null);
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
    public void getPasswordPolicyBean(Model map) throws Exception {
        //set values to password parameter bean
        PasswordPolicyInputBean passwordPolicyInputBean = new PasswordPolicyInputBean();
        //add privileges to input bean
        this.applyUserPrivileges(passwordPolicyInputBean);
        //add values to model map
        map.addAttribute("passwordPolicy", passwordPolicyInputBean);
    }

    private void applyUserPrivileges(PasswordPolicyInputBean passwordPolicyInputBean) {
        List<Task> taskList = common.getUserTaskListByPage(PageVarList.PASSWORDPOLICY_MGT_PAGE, sessionBean);
        passwordPolicyInputBean.setVadd(false);
        passwordPolicyInputBean.setVupdate(false);
        passwordPolicyInputBean.setVdelete(false);
        passwordPolicyInputBean.setVconfirm(false);
        passwordPolicyInputBean.setVreject(false);
        passwordPolicyInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.PASSWORDPOLICY_MGT_PAGE));
        //check task list one by one
        if (taskList != null && !taskList.isEmpty()) {
            taskList.forEach(task -> {
                if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                    passwordPolicyInputBean.setVadd(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                    passwordPolicyInputBean.setVupdate(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                    passwordPolicyInputBean.setVdelete(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                    passwordPolicyInputBean.setVconfirm(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                    passwordPolicyInputBean.setVreject(true);
                }
            });
        }
    }

    @Override
    public BindingResult validateRequestBean(Object o) {
        DataBinder dataBinder = new DataBinder(o);
        dataBinder.setValidator(passwordPolicyValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
