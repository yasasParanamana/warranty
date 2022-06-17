package com.epic.rdbsms.controller.sysconfigmgt.department;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.department.DepartmentInputBean;
import com.epic.rdbsms.mapping.department.Department;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.sysconfigmgt.department.DepartmentService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.CommonVarList;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.TaskVarList;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.sysconfigmgt.department.DepartmentValidator;
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
public class DepartmentController implements RequestBeanValidation<Object> {
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
    DepartmentService departmentService;

    @Autowired
    DepartmentValidator departmentValidator;

    @GetMapping(value = "/viewDepartment")
    @AccessControl(pageCode = PageVarList.DEPARTMENT_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getSytemUserPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  DEPARTMENT PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("departmentview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("departmentview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listDepartment", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.DEPARTMENT_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Department> searchDepartment(@RequestBody DepartmentInputBean departmentInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  DEPARTMENT SEARCH");
        DataTablesResponse<Department> responseBean = new DataTablesResponse<>();
        try {
            long count = departmentService.getCount(departmentInputBean);
            if (count > 0) {
                List<Department> list = departmentService.getDepartmentSearchResultList(departmentInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = departmentInputBean.echo;
                responseBean.columns = departmentInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = departmentInputBean.echo;
                responseBean.columns = departmentInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualDepartment", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.DEPARTMENT_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualDepartment(@RequestBody DepartmentInputBean departmentInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  DEPARTMENT SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = departmentService.getDataCountDual(departmentInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = departmentService.getDepartmentSearchResultsDual(departmentInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = departmentInputBean.echo;
                responseBean.columns = departmentInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = departmentInputBean.echo;
                responseBean.columns = departmentInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addDepartment", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.DEPARTMENT_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addDepartment(@ModelAttribute("department") DepartmentInputBean departmentInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  DEPARTMENT ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(departmentInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = departmentService.insertDepartment(departmentInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.DEPARTMENT_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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

    @GetMapping(value = "/getDepartment")
    @AccessControl(pageCode = PageVarList.DEPARTMENT_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Department getDepartment(@RequestParam String code) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET DEPARTMENT");
        Department department = new Department();
        try {
            if (code != null && !code.isEmpty()) {
                department = departmentService.getDepartment(code);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return department;
    }

    @PostMapping(value = "/updateDepartment", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.DEPARTMENT_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateDepartment(@ModelAttribute("department") DepartmentInputBean departmentInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE DEPARTMENT");
        ResponseBean responseBean;
        try {
            BindingResult bindingResult = validateRequestBean(departmentInputBean);

            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));

            } else {
                String message = departmentService.updateDepartment(departmentInputBean, locale);

                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.DEPARTMENT_MGT_UPDATE_SUCCESSFULLY, null, locale), null);

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

    @PostMapping(value = "/deleteDepartment", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.DEPARTMENT_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteDepartment(@RequestParam String code, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE DEPARTMENT");
        ResponseBean responseBean;
        try {
            String message = departmentService.deleteDepartment(code);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.DEPARTMENT_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmDepartment", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.DEPARTMENT_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmDepartment(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  DEPARTMENT CONFIRM");
        ResponseBean responseBean;
        try {
            String message = departmentService.confirmDepartment(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.DEPARTMENT_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectDepartment", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.DEPARTMENT_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectDepartment(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT DEPARTMENT");
        ResponseBean responseBean;
        try {
            String message = departmentService.rejectDepartment(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.DEPARTMENT_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
    public void getDepartmentBean(Model map) throws Exception {
        DepartmentInputBean departmentInputBean = new DepartmentInputBean();
        //get status list
        List<Status> statusList = commonRepository.getActiveDeActiveStatusList();
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        departmentInputBean.setStatusList(statusList);
        departmentInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(departmentInputBean);
        //add values to model map
        map.addAttribute("department", departmentInputBean);
    }

    private void applyUserPrivileges(DepartmentInputBean departmentInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.DEPARTMENT_MGT_PAGE, sessionBean);
            departmentInputBean.setVadd(false);
            departmentInputBean.setVupdate(false);
            departmentInputBean.setVdelete(false);
            departmentInputBean.setVconfirm(false);
            departmentInputBean.setVreject(false);
            departmentInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.DEPARTMENT_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        departmentInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        departmentInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        departmentInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        departmentInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        departmentInputBean.setVreject(true);
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
        dataBinder.setValidator(departmentValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
