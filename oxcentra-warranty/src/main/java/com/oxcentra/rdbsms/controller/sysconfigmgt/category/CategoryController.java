package com.oxcentra.rdbsms.controller.sysconfigmgt.category;

import com.oxcentra.rdbsms.annotation.accesscontrol.AccessControl;
import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.category.CategoryInputBean;
import com.oxcentra.rdbsms.mapping.category.Category;
import com.oxcentra.rdbsms.mapping.department.Priority;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.usermgt.Task;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.service.sysconfigmgt.category.CategoryService;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.common.DataTablesResponse;
import com.oxcentra.rdbsms.util.common.ResponseBean;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.TaskVarList;
import com.oxcentra.rdbsms.validators.RequestBeanValidation;
import com.oxcentra.rdbsms.validators.sysconfigmgt.category.CategoryValidator;
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
public class CategoryController implements RequestBeanValidation<Object> {
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
    CategoryValidator categoryValidator;

    @Autowired
    CategoryService categoryService;

    @GetMapping(value = "/viewCategory")
    @AccessControl(pageCode = PageVarList.CATEGORY_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getCategoryPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CATEGORY PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("categoryview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("categoryview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listCategory", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CATEGORY_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Category> searchCategory(@RequestBody CategoryInputBean categoryInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  CATEGORY SEARCH");
        DataTablesResponse<Category> responseBean = new DataTablesResponse<>();
        try {
            long count = categoryService.getCount(categoryInputBean);
            if (count > 0) {
                List<Category> list = categoryService.getCategorySearchResultList(categoryInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = categoryInputBean.echo;
                responseBean.columns = categoryInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = categoryInputBean.echo;
                responseBean.columns = categoryInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualCategory", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CATEGORY_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualCategory(@RequestBody CategoryInputBean categoryInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  CATEGORY SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = categoryService.getDataCountDual(categoryInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = categoryService.getCategorySearchResultsDual(categoryInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = categoryInputBean.echo;
                responseBean.columns = categoryInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = categoryInputBean.echo;
                responseBean.columns = categoryInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addCategory", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CATEGORY_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addCategory(@ModelAttribute("category") CategoryInputBean categoryInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CATEGORY ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(categoryInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = categoryService.insertCategory(categoryInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CATEGORY_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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

    @GetMapping(value = "/getCategory")
    @AccessControl(pageCode = PageVarList.CATEGORY_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Category getCategory(@RequestParam String categoryCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET CATEGORY");
        Category category = new Category();
        try {
            if (categoryCode != null && !categoryCode.isEmpty()) {
                category = categoryService.getCategory(categoryCode);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return category;
    }

    @PostMapping(value = "/updateCategory", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CATEGORY_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateCategory(@ModelAttribute("category") CategoryInputBean categoryInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE CATEGORY");
        ResponseBean responseBean;
        try {
            BindingResult bindingResult = validateRequestBean(categoryInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = categoryService.updateCategory(categoryInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CATEGORY_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/deleteCategory", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CATEGORY_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteCategory(@RequestParam String code, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE CATEGORY");
        ResponseBean responseBean;
        try {
            String message = categoryService.deleteCategory(code);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CATEGORY_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmCategory", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CATEGORY_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmCategory(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CATEGORY CONFIRM");
        ResponseBean responseBean;
        try {
            String message = categoryService.confirmCategory(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CATEGORY_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/rejectCategory", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CATEGORY_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectCategory(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT CATEGORY");
        ResponseBean responseBean;
        try {
            String message = categoryService.rejectCategory(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CATEGORY_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
    public void getCategoryBean(Model map) throws Exception {
        CategoryInputBean categoryInputBean = new CategoryInputBean();
        //get status list
        List<Status> statusList = commonRepository.getActiveDeActiveStatusList();
        List<Status> statusActList = common.getActiveStatusList();
        List<Priority> priorityList = commonRepository.getActivePriorityList();
        //set values to task bean
        categoryInputBean.setStatusList(statusList);
        categoryInputBean.setStatusActList(statusActList);
        categoryInputBean.setPriorityList(priorityList);
        //add privileges to input bean
        this.applyUserPrivileges(categoryInputBean);
        //add values to model map
        map.addAttribute("category", categoryInputBean);
    }

    private void applyUserPrivileges(CategoryInputBean categoryInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.CATEGORY_MGT_PAGE, sessionBean);
            categoryInputBean.setVadd(false);
            categoryInputBean.setVupdate(false);
            categoryInputBean.setVdelete(false);
            categoryInputBean.setVconfirm(false);
            categoryInputBean.setVreject(false);
            categoryInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.CATEGORY_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        categoryInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        categoryInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        categoryInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        categoryInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        categoryInputBean.setVreject(true);
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
        dataBinder.setValidator(categoryValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
