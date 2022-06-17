package com.epic.rdbsms.controller.sysconfigmgt.passwordparam;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.passwordparam.PasswordParamInputBean;
import com.epic.rdbsms.mapping.common.CommonPasswordParam;
import com.epic.rdbsms.mapping.sectionmgt.PasswordParam;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.mapping.usermgt.UserRoleType;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.common.CommonService;
import com.epic.rdbsms.service.sysconfigmgt.passwordparam.PasswordParamService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.TaskVarList;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.sysconfigmgt.passwordparam.PasswordParamValidator;
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
public class PasswordParamController implements RequestBeanValidation<Object> {
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
    PasswordParamService passwordParamService;

    @Autowired
    PasswordParamValidator passwordParamValidator;

    @Autowired
    Common common;

    @GetMapping("/viewPasswordParam")
    @AccessControl(pageCode = PageVarList.PASSWORD_PARAMETER_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getPasswordParamPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD PARAM PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //add values to model map
            modelAndView = new ModelAndView("passwordparamview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("passwordparamview", modelMap);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/listPasswordParam", method = RequestMethod.POST, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.PASSWORD_PARAMETER_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<PasswordParam> searchPasswordParam(@RequestBody PasswordParamInputBean passwordParamInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD PARAM SEARCH");
        DataTablesResponse<PasswordParam> responseBean = new DataTablesResponse<>();
        try {
            long count = passwordParamService.getDataCount(passwordParamInputBean);
            if (count > 0) {
                List<PasswordParam> list = passwordParamService.getPasswordParamSearchResults(passwordParamInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = passwordParamInputBean.echo;
                responseBean.columns = passwordParamInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = passwordParamInputBean.echo;
                responseBean.columns = passwordParamInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @RequestMapping(value = "/listDualPasswordParam")
    @AccessControl(pageCode = PageVarList.PASSWORD_PARAMETER_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualPasswordParam(@RequestBody PasswordParamInputBean passwordParamInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD PARAM SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = passwordParamService.getDataCountDual(passwordParamInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = passwordParamService.getPasswordParamSearchResultsDual(passwordParamInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = passwordParamInputBean.echo;
                responseBean.columns = passwordParamInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = passwordParamInputBean.echo;
                responseBean.columns = passwordParamInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @RequestMapping(value = "/getPasswordParam", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.PASSWORD_PARAMETER_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    PasswordParam getPasswordParam(@RequestParam String passwordParam) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD PARAM GET");
        PasswordParam pwdParam = new PasswordParam();
        try {
            if (passwordParam != null && !passwordParam.trim().isEmpty()) {
                pwdParam = passwordParamService.getPasswordParam(passwordParam);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return pwdParam;
    }

    @PostMapping(value = "/updatePasswordParam", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.PASSWORD_PARAMETER_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updatePasswordParam(@ModelAttribute("passwordparam") PasswordParamInputBean passwordParamInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD PARAM UPDATE");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(passwordParamInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = passwordParamService.updatePasswordParam(passwordParamInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.PASSWORD_PARAM_MGT_SUCCESS_UPDATE, null, locale), null);
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

    @PostMapping(value = "/confirmPasswordParam", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.PASSWORD_PARAMETER_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmPasswordParam(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD PARAM CONFIRM");
        ResponseBean responseBean = null;
        try {
            String message = passwordParamService.confirmPasswordParam(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.PASSWORD_PARAM_MGT_SUCCESS_CONFIRM, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectPasswordParam", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.PASSWORD_PARAMETER_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectPasswordParam(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD PARAM REJECT");
        ResponseBean responseBean = null;
        try {
            String message = passwordParamService.rejectPasswordParam(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.PASSWORD_PARAM_MGT_SUCCESS_REJECT, null, locale), null);
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
    public void getPasswordParam(Model map) {
        //set values to password parameter bean
        PasswordParamInputBean paramInputBean = new PasswordParamInputBean();
        List<UserRoleType> userRoleTypeList = commonRepository.getActiveUserRoleTypeList();
        List<CommonPasswordParam> passwordParamList = commonRepository.getCommonPasswordParamList();
        // retrieving user role and common password param list
        paramInputBean.setUserRoleTypeBeanList(userRoleTypeList);
        paramInputBean.setPasswordParamBeanList(passwordParamList);
        //set privileges
        this.applyUserPrivileges(paramInputBean);
        //add values to model map
        map.addAttribute("passwordparam", paramInputBean);
    }

    private void applyUserPrivileges(PasswordParamInputBean passwordParamInputBean) {
        List<Task> tasklist = common.getUserTaskListByPage(PageVarList.PASSWORD_PARAMETER_MGT_PAGE, sessionBean);
        passwordParamInputBean.setVadd(false);
        passwordParamInputBean.setVupdate(false);
        passwordParamInputBean.setVdelete(false);
        passwordParamInputBean.setVconfirm(false);
        passwordParamInputBean.setVreject(false);
        passwordParamInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.PASSWORD_PARAMETER_MGT_PAGE));
        if (tasklist != null && !tasklist.isEmpty()) {
            tasklist.forEach(task -> {
                if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                    passwordParamInputBean.setVadd(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                    passwordParamInputBean.setVupdate(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                    passwordParamInputBean.setVdelete(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                    passwordParamInputBean.setVconfirm(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                    passwordParamInputBean.setVreject(true);
                }
            });
        }
    }

    @Override
    public BindingResult validateRequestBean(Object o) {
        DataBinder dataBinder = new DataBinder(o);
        dataBinder.setValidator(passwordParamValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
