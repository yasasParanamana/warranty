package com.oxcentra.rdbsms.controller.usermgt.task;

import com.oxcentra.rdbsms.annotation.accesscontrol.AccessControl;
import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.usermgt.task.TaskInputBean;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.usermgt.Task;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.service.common.CommonService;
import com.oxcentra.rdbsms.service.usermgt.task.TaskService;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.common.DataTablesResponse;
import com.oxcentra.rdbsms.util.common.ResponseBean;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.StatusVarList;
import com.oxcentra.rdbsms.util.varlist.TaskVarList;
import com.oxcentra.rdbsms.validators.RequestBeanValidation;
import com.oxcentra.rdbsms.validators.usermgt.task.TaskValidator;
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
public class TaskController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonService commonService;

    @Autowired
    TaskService taskService;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    TaskValidator taskValidator;

    @Autowired
    Common common;

    @GetMapping("/viewTask")
    @AccessControl(pageCode = PageVarList.TASK_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getTaskPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  TASK PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("taskview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("taskview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listTask", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.TASK_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Task> searchTask(@RequestBody TaskInputBean taskInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  TASK SEARCH");
        DataTablesResponse<Task> responseBean = new DataTablesResponse<>();
        try {
            System.out.println("taskInputBean > " + taskInputBean);
            long count = taskService.getDataCount(taskInputBean);
            if (count > 0) {
                List<Task> list = taskService.getTaskSearchResults(taskInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = taskInputBean.echo;
                responseBean.columns = taskInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = taskInputBean.echo;
                responseBean.columns = taskInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @RequestMapping(value = "/listDualTask", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.TASK_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualTask(@RequestBody TaskInputBean taskInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  TASK SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = taskService.getDataCountDual(taskInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = taskService.getTaskSearchResultsDual(taskInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = taskInputBean.echo;
                responseBean.columns = taskInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = taskInputBean.echo;
                responseBean.columns = taskInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addTask", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.TASK_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addTask(@ModelAttribute("task") TaskInputBean taskInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  TASK ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(taskInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = taskService.insertTask(taskInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TASK_MGT_SUCCESS_ADD, null, locale), null);
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

    @GetMapping(value = "/getTask")
    @AccessControl(pageCode = PageVarList.TASK_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Task getTask(@RequestParam String taskCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  TASK GET");
        Task task = new Task();
        try {
            if (taskCode != null && !taskCode.trim().isEmpty()) {
                task = taskService.getTask(taskCode);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return task;
    }

    @PostMapping(value = "/updateTask", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.TASK_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateTask(@ModelAttribute("task") TaskInputBean taskInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  TASK UPDATE");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(taskInputBean);
            if (bindingResult.hasErrors()) {
                responseBean.setErrorMessage(messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = taskService.updateTask(taskInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TASK_MGT_SUCCESS_UPDATE, null, locale), null);
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

    @PostMapping(value = "/deleteTask", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.TASK_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteTask(@RequestParam String taskCode, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  TASK DELETE");
        ResponseBean responseBean = null;
        try {
            String message = taskService.deleteTask(taskCode);
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

    @PostMapping(value = "/confirmTask", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.TASK_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmTask(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  TASK CONFIRM");
        ResponseBean responseBean = null;
        try {
            String message = taskService.confirmTask(id);
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
    }

    @PostMapping(value = "/rejectTask", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.TASK_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectTask(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  TASK REJECT");
        ResponseBean responseBean = null;
        try {
            String message = taskService.rejectTask(id);
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
    }

    @ModelAttribute
    public void getTaskBean(Model map) throws Exception {
        TaskInputBean taskInputBean = new TaskInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        taskInputBean.setStatusList(statusList);
        taskInputBean.setStatusActList(statusActList);
        //set privileges
        this.applyUserPrivileges(taskInputBean);
        //add values to model map
        map.addAttribute("task", taskInputBean);
    }

    private void applyUserPrivileges(TaskInputBean taskInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.TASK_MGT_PAGE, sessionBean);
            taskInputBean.setVadd(false);
            taskInputBean.setVupdate(false);
            taskInputBean.setVdelete(false);
            taskInputBean.setVconfirm(false);
            taskInputBean.setVreject(false);
            taskInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.TASK_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        taskInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        taskInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        taskInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        taskInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        taskInputBean.setVreject(true);
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
        dataBinder.setValidator(taskValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
