package com.oxcentra.rdbsms.controller.usermgt.systemuser;

import com.oxcentra.rdbsms.annotation.accesscontrol.AccessControl;
import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.usermgt.systemuser.SystemUserInputBean;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.usermgt.SystemUser;
import com.oxcentra.rdbsms.mapping.usermgt.Task;
import com.oxcentra.rdbsms.mapping.usermgt.UserRole;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.service.usermgt.systemuser.SystemUserService;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.common.DataTablesResponse;
import com.oxcentra.rdbsms.util.common.ResponseBean;
import com.oxcentra.rdbsms.validators.RequestBeanValidation;
import com.oxcentra.rdbsms.validators.usermgt.systemuser.SystemUserValidator;
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
public class SystemUserController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    SystemUserValidator systemUserValidator;

    @Autowired
    Common common;

    @Autowired
    SystemUserService systemUserService;

    @Autowired
    CommonVarList commonVarList;

    @GetMapping(value = "/viewSystemUser")
    @AccessControl(pageCode = PageVarList.USER_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getSytemUserPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SYSTEM USER PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("systemuserview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("systemuserview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listSystemUser", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USER_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<SystemUser> searchSystemUser(@RequestBody SystemUserInputBean systemUserInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SYSTEM USER SEARCH");
        DataTablesResponse<SystemUser> responseBean = new DataTablesResponse<>();
        try {
            long count = systemUserService.getCount(systemUserInputBean);
            if (count > 0) {
                List<SystemUser> list = systemUserService.getSystemUserSearchResultList(systemUserInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = systemUserInputBean.echo;
                responseBean.columns = systemUserInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = systemUserInputBean.echo;
                responseBean.columns = systemUserInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualSystemUser", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USER_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualSystemUser(@RequestBody SystemUserInputBean systemUserInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SYSTEM USER SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = systemUserService.getDataCountDual(systemUserInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = systemUserService.getSystemUserSearchResultsDual(systemUserInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = systemUserInputBean.echo;
                responseBean.columns = systemUserInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = systemUserInputBean.echo;
                responseBean.columns = systemUserInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addSystemUser", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USER_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addSystemUser(@ModelAttribute("systemuser") SystemUserInputBean systemUserInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SYSTEM USER ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(systemUserInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = systemUserService.insertSystemUser(systemUserInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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

    @GetMapping(value = "/getSystemUser")
    @AccessControl(pageCode = PageVarList.USER_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    SystemUser getSystemUser(@RequestParam String userName) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET SYSTEM USER");
        SystemUser systemUser = new SystemUser();
        try {
            if (userName != null && !userName.isEmpty()) {
                systemUser = systemUserService.getSystemUser(userName);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return systemUser;
    }

    @PostMapping(value = "/updateSystemUser", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USER_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateSystemUser(@ModelAttribute("systemuser") SystemUserInputBean systemUserInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE SYSTEM USER");
        ResponseBean responseBean;
        try {
            BindingResult bindingResult = validateRequestBean(systemUserInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, Locale.US));
            } else {
                String message = systemUserService.updateSystemUser(systemUserInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/deleteSystemUser", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USER_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteSystemUser(@RequestParam String userName, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE SYSTEM USER");
        ResponseBean responseBean;
        try {
            String message = systemUserService.deleteSystemUser(userName);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmSystemUser", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USER_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmSystemUser(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SYSTEM USER CONFIRM");
        ResponseBean responseBean;
        try {
            String message = systemUserService.confirmSystemUser(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectSystemUser", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USER_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    @ResponseBody
    public ResponseBean rejectSystemUser(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT SYSTEM USER");
        ResponseBean responseBean;
        try {
            String message = systemUserService.rejectSystemUser(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SYSTEMUSER_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
    public void getSystemUserBean(Model map) throws Exception {
        SystemUserInputBean systemUserInputBean = new SystemUserInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        List<Status> statusActList = common.getActiveStatusList();
        List<UserRole> userRoleList = commonRepository.getUserRoleListByUserRoleTypeCode(commonVarList.USERROLE_TYPE_WEB);
        //set values to task bean
        systemUserInputBean.setStatusList(statusList);
        systemUserInputBean.setStatusActList(statusActList);
        systemUserInputBean.setUserRoleList(userRoleList);
        //add privileges to input bean
        this.applyUserPrivileges(systemUserInputBean);
        //add values to model map
        map.addAttribute("systemuser", systemUserInputBean);
    }

    private void applyUserPrivileges(SystemUserInputBean systemUserInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.USER_MGT_PAGE, sessionBean);
            systemUserInputBean.setVadd(false);
            systemUserInputBean.setVupdate(false);
            systemUserInputBean.setVdelete(false);
            systemUserInputBean.setVconfirm(false);
            systemUserInputBean.setVreject(false);
            systemUserInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.USER_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        systemUserInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        systemUserInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        systemUserInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        systemUserInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        systemUserInputBean.setVreject(true);
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
        dataBinder.setValidator(systemUserValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
