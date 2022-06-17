package com.epic.rdbsms.controller.usermgt.page;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.usermgt.page.PageInputBean;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Page;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.common.CommonService;
import com.epic.rdbsms.service.usermgt.page.PageService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.StatusVarList;
import com.epic.rdbsms.util.varlist.TaskVarList;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.usermgt.page.PageValidator;
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
@Scope("prototype")
public class PageController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonService commonService;

    @Autowired
    PageService pageService;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    PageValidator pageValidator;

    @Autowired
    Common common;

    @GetMapping("/viewPage")
    public ModelAndView viewPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("pageview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("pageview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listPage", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.PAGE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Page> searchPage(@RequestBody PageInputBean pageInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  PAGE SEARCH");
        DataTablesResponse<Page> responseBean = new DataTablesResponse<>();
        try {
            long count = pageService.getDataCount(pageInputBean);
            if (count > 0) {
                List<Page> pageList = pageService.getPageSearchResults(pageInputBean);
                //set values to response bean
                responseBean.data.addAll(pageList);
                responseBean.echo = pageInputBean.echo;
                responseBean.columns = pageInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = pageInputBean.echo;
                responseBean.columns = pageInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualPage", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.PAGE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualPage(@RequestBody PageInputBean pageInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  PAGE SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = pageService.getDataCountDual(pageInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = pageService.getPageSearchResultsDual(pageInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = pageInputBean.echo;
                responseBean.columns = pageInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = pageInputBean.echo;
                responseBean.columns = pageInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @GetMapping(value = "/getPage")
    @AccessControl(pageCode = PageVarList.PAGE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Page getPage(@RequestParam String pageCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  PAGE GET");
        Page page = new Page();
        try {
            if (pageCode != null && !pageCode.trim().isEmpty()) {
                page = pageService.getPage(pageCode);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return page;
    }

    @PostMapping(value = "/updatePage", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.PAGE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updatePage(@ModelAttribute("page") PageInputBean pageInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PAGE UPDATE");
        ResponseBean responseBean = new ResponseBean();
        try {
            BindingResult bindingResult = validateRequestBean(pageInputBean);
            if (bindingResult.hasErrors()) {
                responseBean.setErrorMessage(messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = pageService.updatePage(pageInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.PAGE_MGT_SUCCESS_UPDATE, null, locale), null);
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

    @PostMapping(value = "/confirmPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.PAGE_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    @ResponseBody
    public ResponseBean confirmPage(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PAGE CONFIRM");
        ResponseBean responseBean;
        try {
            String message = pageService.confirmPage(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.PAGE_MGT_SUCCESS_CONFIRM, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.PAGE_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    @ResponseBody
    public ResponseBean rejectPage(@RequestParam String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PAGE REJECT");
        ResponseBean responseBean;
        try {
            String message = pageService.rejectPage(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.PAGE_MGT_SUCCESS_REJECT, null, locale), null);
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
    public void getPageBean(Model map) throws Exception {
        PageInputBean pageInputBean = new PageInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        //set values to task bean
        pageInputBean.setStatusList(statusList);
        //add privileges to input bean
        this.applyUserPrivileges(pageInputBean);
        //add values to model map
        map.addAttribute("page", pageInputBean);
    }

    private void applyUserPrivileges(PageInputBean inputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.PAGE_MGT_PAGE, sessionBean);
            inputBean.setVupdate(false);
            inputBean.setVconfirm(false);
            inputBean.setVreject(false);
            inputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.PAGE_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        inputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        inputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        inputBean.setVreject(true);
                    }
                });
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public BindingResult validateRequestBean(Object object) {
        DataBinder dataBinder = new DataBinder(object);
        dataBinder.setValidator(pageValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
