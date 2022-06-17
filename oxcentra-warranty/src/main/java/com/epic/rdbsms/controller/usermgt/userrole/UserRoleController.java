package com.epic.rdbsms.controller.usermgt.userrole;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.usermgt.userrole.UserRoleInputBean;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.*;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.common.CommonService;
import com.epic.rdbsms.service.usermgt.userrole.UserRoleService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.StatusVarList;
import com.epic.rdbsms.util.varlist.TaskVarList;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.usermgt.userrole.UserRoleValidator;
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
public class UserRoleController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonService commonService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    UserRoleValidator userRoleValidator;

    @Autowired
    Common common;

    @GetMapping("/viewUserRole")
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView viewUserRole(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("userroleview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("pageview", modelMap);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/listUserRole", method = RequestMethod.POST, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<UserRole> searchUserRole(@RequestBody UserRoleInputBean userRoleInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE SEARCH");
        DataTablesResponse<UserRole> responseBean = new DataTablesResponse<>();
        try {
            long count = userRoleService.getDataCount(userRoleInputBean);
            if (count > 0) {
                List<UserRole> userRoleList = userRoleService.getUserRoleSearchResults(userRoleInputBean);
                //set values to response bean
                responseBean.data.addAll(userRoleList);
                responseBean.echo = userRoleInputBean.echo;
                responseBean.columns = userRoleInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = userRoleInputBean.echo;
                responseBean.columns = userRoleInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @RequestMapping(value = "/listDualUserRole")
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualUserRole(@RequestBody UserRoleInputBean userRoleInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = userRoleService.getDataCountDual(userRoleInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = userRoleService.getUserRoleSearchResultsDual(userRoleInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = userRoleInputBean.echo;
                responseBean.columns = userRoleInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = userRoleInputBean.echo;
                responseBean.columns = userRoleInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addUserRole", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addUserRole(@ModelAttribute("userrole") UserRoleInputBean userRoleInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(userRoleInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = userRoleService.insertUserRole(userRoleInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERROLE_MGT_SUCCESS_ADD, null, locale), null);
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

    @RequestMapping(value = "/getUserRole", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    UserRole getUserRole(@RequestParam String userRoleCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE GET");
        UserRole userRole = new UserRole();
        try {
            if (userRoleCode != null && !userRoleCode.trim().isEmpty()) {
                userRole = userRoleService.getUserRole(userRoleCode);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return userRole;
    }

    @PostMapping(value = "/updateUserRole", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateUserRole(@ModelAttribute("userrole") UserRoleInputBean userRoleInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE UPDATE");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(userRoleInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = userRoleService.updateUserRole(userRoleInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERROLE_MGT_SUCCESS_UPDATE, null, locale), null);
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

    @PostMapping(value = "/deleteUserRole", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    @ResponseBody
    public ResponseBean deleteUserRole(@RequestParam String userRoleCode, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE DELETE");
        ResponseBean responseBean;
        try {
            String message = userRoleService.deleteUserRole(userRoleCode);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERROLE_MGT_SUCCESS_DELETE, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @RequestMapping(value = "/getAllSection", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.ASSIGN_PAGE)
    public @ResponseBody
    List<Section> getAllSection() {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE LOAD SECTION");
        List<Section> list = new ArrayList<>();
        try {
            list = userRoleService.getAllSection();
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return list;
    }

    @RequestMapping(value = "/getAssignedSection", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.ASSIGN_PAGE)
    public @ResponseBody
    List<Section> getAssignedSection(@RequestParam("userroleCode") String userroleCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE LOAD ASSIGNED SECTION");
        List<Section> list = new ArrayList<>();
        try {
            list = userRoleService.getAssignedSection(userroleCode);
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return list;
    }

    @RequestMapping(value = "/getAssignedPages", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.ASSIGN_PAGE)
    public @ResponseBody
    List<Page> getAssignedPages(@RequestParam("userroleCode") String userroleCode, @RequestParam("sectionCode") String sectionCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE LOAD ASSIGNED PAGES");
        List<Page> list = new ArrayList<>();
        try {
            list = userRoleService.getAssignedPages(userroleCode, sectionCode);
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return list;
    }

    @RequestMapping(value = "/getUnAssignedPages", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.ASSIGN_PAGE)
    public @ResponseBody
    List<Page> getUnAssignedPages(@RequestParam("userroleCode") String userroleCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE LOAD UNASSIGNED PAGES");
        List<Page> list = new ArrayList<>();
        try {
            list = userRoleService.getUnAssignedPages(userroleCode);
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return list;
    }

    @PostMapping(value = "/assignPages", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.ASSIGN_PAGE)
    public @ResponseBody
    ResponseBean assignPages(@ModelAttribute("assignPage") UserRoleInputBean userRoleInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE ASSIGN PAGE");
        ResponseBean responseBean;
        try {
            String message = userRoleService.assignPages(userRoleInputBean, locale);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERROLE_MGT_SUCCESS_ASSIGNED_PAGE, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @RequestMapping(value = "/getAssignedTasks", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.ASSIGN_TASK)
    public @ResponseBody
    List<Task> getAssignedTasks(@RequestParam("userroleCode") String userroleCode, @RequestParam("page") String pageCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE LOAD ASSIGNED TASKS");
        List<Task> list = new ArrayList<>();
        try {
            list = userRoleService.getAssignedTasks(userroleCode, pageCode);
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return list;
    }

    @RequestMapping(value = "/getUnAssignedTasks", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.ASSIGN_TASK)
    public @ResponseBody
    List<Task> getUnAssignedTasks(@RequestParam("userroleCode") String userroleCode, @RequestParam("page") String pageCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE LOAD UNASSIGNED TASKS");
        List<Task> list = new ArrayList<>();
        try {
            list = userRoleService.getUnAssignedTasks(userroleCode, pageCode);
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return list;
    }

    @PostMapping(value = "/assignTasks", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.ASSIGN_TASK)
    public @ResponseBody
    ResponseBean assignTasks(@ModelAttribute("assignTask") UserRoleInputBean userRoleInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE ASSIGN TASK");
        ResponseBean responseBean = null;
        try {
            String message = userRoleService.assignTasks(userRoleInputBean, locale);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERROLE_MGT_SUCCESS_ASSIGNED_TASK, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmUserRole", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    @ResponseBody
    public ResponseBean confirmUserRole(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE CONFIRM");
        ResponseBean responseBean = null;
        try {
            String message = userRoleService.confirmUserRole(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERROLE_MGT_SUCCESS_CONFIRM, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectUserRole", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.USERROLE_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    @ResponseBody
    public ResponseBean rejectUserRole(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  USERROLE PAGE REJECT");
        ResponseBean responseBean = null;
        try {
            String message = userRoleService.rejectUserRole(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.USERROLE_MGT_SUCCESS_REJECT, null, locale), null);
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
    public void getUserRoleBean(Model map) throws Exception {
        UserRoleInputBean inputBean = new UserRoleInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        List<Status> statusActList = common.getActiveStatusList();

        List<UserRoleType> userroleTypeList = commonRepository.getUserroleTypeList();
        List<UserRoleType> userroleTypeActList = commonRepository.getActUserroleTypeList();

        //set values to page bean
        inputBean.setStatusList(statusList);
        inputBean.setStatusActList(statusActList);
        inputBean.setUserroleTypeList(userroleTypeList);
        inputBean.setUserroleTypeActList(userroleTypeActList);

        //set privileges
        this.applyUserPrivileges(inputBean);
        //add values to model map
        map.addAttribute("userrole", inputBean);
    }

    private void applyUserPrivileges(UserRoleInputBean inputBean) {
        List<Task> tasklist = common.getUserTaskListByPage(PageVarList.USERROLE_MGT_PAGE, sessionBean);

        inputBean.setVadd(false);
        inputBean.setVupdate(false);
        inputBean.setVdelete(false);
        inputBean.setVassignpage(false);
        inputBean.setVassigntask(false);
        inputBean.setVconfirm(false);
        inputBean.setVreject(false);
        inputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.USERROLE_MGT_PAGE));

        if (tasklist != null && !tasklist.isEmpty()) {
            tasklist.forEach(task -> {
                if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                    inputBean.setVadd(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                    inputBean.setVupdate(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                    inputBean.setVdelete(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ASSIGN_PAGE)) {
                    inputBean.setVassignpage(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ASSIGN_TASK)) {
                    inputBean.setVassigntask(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                    inputBean.setVconfirm(true);
                } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                    inputBean.setVreject(true);
                }
            });
        }
    }

    @Override
    public BindingResult validateRequestBean(Object object) {
        DataBinder dataBinder = new DataBinder(object);
        dataBinder.setValidator(userRoleValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }


}
