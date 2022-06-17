package com.epic.rdbsms.controller.sysconfigmgt.userparam;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.bean.common.UserParamCategory;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.userparam.UserParamInputBean;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.mapping.userparam.UserParam;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.sysconfigmgt.userparam.UserParamService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.CommonVarList;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.TaskVarList;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.sysconfigmgt.userparam.UserParamValidator;
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
public class UserParamController implements RequestBeanValidation<Object> {
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
    UserParamService userParamService;

    @Autowired
    UserParamValidator userParamValidator;


    @GetMapping(value = "/viewUserParam")
    @AccessControl(pageCode = PageVarList.USERPARAMETER_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getUserParamPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERPARAM PAGE VIEW");
        ModelAndView modelAndView = null;

        try {
            //redirect to home page
            modelAndView = new ModelAndView("viewuserparam", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("viewuserparam", modelMap);
        }
        return modelAndView;

    }

    @PostMapping(value = "/listUserParam", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USERPARAMETER_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    @ResponseBody
    public DataTablesResponse<UserParam> searchUserParam(@RequestBody UserParamInputBean userParamInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERPARAM  SEARCH");
        DataTablesResponse<UserParam> responseBean = new DataTablesResponse<>();

        try {
            long count = userParamService.getCount(userParamInputBean);
            if (count > 0) {
                List<UserParam> list = userParamService.getUserParamSearchResultList(userParamInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = userParamInputBean.echo;
                responseBean.columns = userParamInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = userParamInputBean.echo;
                responseBean.columns = userParamInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualUserParam", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USERPARAMETER_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualUserParam(@RequestBody UserParamInputBean userParamInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  USER PARAM SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = userParamService.getDataCountDual(userParamInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = userParamService.getUserParamSearchResultsDual(userParamInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = userParamInputBean.echo;
                responseBean.columns = userParamInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = userParamInputBean.echo;
                responseBean.columns = userParamInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }


    @PostMapping(value = "/addUserParam", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERPARAMETER_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addUserParam(@ModelAttribute("userParam") UserParamInputBean userParamInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USER PARAM ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(userParamInputBean);
            if (bindingResult.hasErrors()) {
                //String errorMessage = bindingResult.getFieldErrors().stream().findFirst().get().getDefaultMessage();
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = userParamService.insertUserParam(userParamInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERPARAM_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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

    @GetMapping(value = "/getUserParam")
    @AccessControl(pageCode = PageVarList.USERPARAMETER_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    UserParam getUserParam(@RequestParam String code) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET USER PARAM");
        UserParam userParam = new UserParam();
        try {
            if (code != null && !code.isEmpty()) {
                userParam = userParamService.getUserParam(code);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return userParam;
    }


    @PostMapping(value = "/updateUserParam", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERPARAMETER_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateUserParam(@ModelAttribute("userParam") UserParamInputBean userParamInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE USER PARAM");
        ResponseBean responseBean;
        try {
            BindingResult bindingResult = validateRequestBean(userParamInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = userParamService.updateUserParam(userParamInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERPARAM_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/deleteUserParam", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERPARAMETER_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteUserParam(@RequestParam String code, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE USER PARAM");
        ResponseBean responseBean;
        try {
            String message = userParamService.deleteUserParam(code);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERPARAM_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }


    @PostMapping(value = "/confirmUserParam", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERPARAMETER_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmUserParam(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USER PARAM CONFIRM");
        ResponseBean responseBean;
        try {
            String message = userParamService.confirmUserParam(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERPARAM_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }


    @PostMapping(value = "/rejectUserParam", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERPARAMETER_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectUserParam(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT USER PARAM");
        ResponseBean responseBean;
        try {
            String message = userParamService.rejectUserParam(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERPARAM_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
    public void getUserParamBean(Model map) throws Exception {
        UserParamInputBean userParamInputBean = new UserParamInputBean();
        //get status list
        List<Status> statusList = commonRepository.getActiveDeActiveStatusList();
        List<Status> statusActList = common.getActiveStatusList();
        // get user param category list
        List<UserParamCategory> categoryList = commonRepository.getUserParamCategoryList();
        userParamInputBean.setCategoryList(categoryList);
        //set values to task bean
        userParamInputBean.setStatusList(statusList);
        userParamInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(userParamInputBean);
        //add values to model map
        map.addAttribute("userParam", userParamInputBean);
    }

    private void applyUserPrivileges(UserParamInputBean userParamInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.USERPARAMETER_MGT_PAGE, sessionBean);
            userParamInputBean.setVadd(false);
            userParamInputBean.setVupdate(false);
            userParamInputBean.setVdelete(false);
            userParamInputBean.setVconfirm(false);
            userParamInputBean.setVreject(false);
            userParamInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.USERPARAMETER_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        userParamInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        userParamInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        userParamInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        userParamInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        userParamInputBean.setVreject(true);
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
        dataBinder.setValidator(userParamValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
