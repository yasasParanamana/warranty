package com.epic.rdbsms.controller.alerttypetelcomgt.categorytelco;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.alerttypetelcomgt.categorytelco.CategoryTelcoInputBean;
import com.epic.rdbsms.bean.common.CommonCategoryBean;
import com.epic.rdbsms.bean.common.CommonTelcoBean;
import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.mapping.categorytelcomgt.CategoryTelco;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.alerttypetelcomgt.categorytelcomgt.CategoryTelcoService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.*;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.alerttypetelcomgt.categorytelco.CategoryTelcoValidator;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@Scope("request")
public class CategoryTelcoController implements RequestBeanValidation<Object> {
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
    CategoryTelcoService categoryTelcoService;

    @Autowired
    CategoryTelcoValidator categoryTelcoValidator;

    @GetMapping("/viewCategoryTelco")
    @AccessControl(pageCode = PageVarList.CATEGORY_TELCO_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getCategoryTelcoPage(ModelMap modelMap, Locale locale) throws Exception {
        logger.info("[" + sessionBean.getSessionid() + "]  CATEGORY TELCO PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("categorytelcoview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("categorytelcoview", modelMap);
        }
        return modelAndView;
    }

    @GetMapping(value = "/getCategoryTelco")
    @AccessControl(pageCode = PageVarList.CATEGORY_TELCO_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    CategoryTelco getCategoryTelco(@RequestParam String category) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET CATEGORY TELCO");
        CategoryTelco categoryTelco = new CategoryTelco();
        try {
            if (category != null && !category.isEmpty()) {
                categoryTelco = categoryTelcoService.getCategoryTelco(category);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return categoryTelco;
    }

    @PostMapping(value = "/updateCategoryTelco", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CATEGORY_TELCO_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateCategoryTelco(@ModelAttribute("categoryTelco") CategoryTelcoInputBean categoryTelcoInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE CATEGORY TELCO");
        ResponseBean responseBean;
        try {
            BindingResult bindingResult = validateRequestBean(categoryTelcoInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = categoryTelcoService.updateCategoryTelco(categoryTelcoInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CATEGORY_TELCO_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/listDualCategoryTelco", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CATEGORY_TELCO_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualCategoryTelco(@RequestBody CategoryTelcoInputBean categoryTelcoInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  CATEGORY TELCO SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = categoryTelcoService.getDataCountDual(categoryTelcoInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = categoryTelcoService.getCategoryTelcoSearchResultsDual(categoryTelcoInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = categoryTelcoInputBean.echo;
                responseBean.columns = categoryTelcoInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = categoryTelcoInputBean.echo;
                responseBean.columns = categoryTelcoInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/deleteCategoryTelco", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CATEGORY_TELCO_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteCategoryTelco(@RequestParam String category, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE CATEGORY TELCO");
        ResponseBean responseBean;
        try {
            String message = categoryTelcoService.deleteCategory(category);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CATEGORY_TELCO_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/addCategoryTelco", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CATEGORY_TELCO_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addCategoryTelco(@Valid @ModelAttribute("categoryTelco") CategoryTelcoInputBean categoryTelcoInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CATEGORY TELCO ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(categoryTelcoInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, Locale.US));
            } else {
                String message = categoryTelcoService.insertCategoryTelco(categoryTelcoInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CATEGORY_TELCO_ADDED_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/confirmCategoryTelco", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CATEGORY_TELCO_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmCategoryTelco(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CATEGORY TELCO CONFIRM");
        ResponseBean responseBean;
        try {
            String message = categoryTelcoService.confirmCategoryTelco(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CATEGORY_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectCategoryTelco", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CATEGORY_TELCO_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectCategoryTelco(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT CATEGORY TELCO PORT");
        ResponseBean responseBean;
        try {
            String message = categoryTelcoService.rejectCategoryTelco(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CATEGORY_TELCO_REJECT_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/listCategoryTelco", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CATEGORY_TELCO_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<CategoryTelco> searchCategoryTelco(@RequestBody CategoryTelcoInputBean categoryTelcoInputBean) {

        logger.info("[" + sessionBean.getSessionid() + "]  CATEGORY TELCO SEARCH");
        DataTablesResponse<CategoryTelco> responseBean = new DataTablesResponse<>();

        try {
            long count = categoryTelcoService.getCount(categoryTelcoInputBean);

            if (count > 0) {

                List<CategoryTelco> list = categoryTelcoService.getCategoryTelcoResultList(categoryTelcoInputBean);

                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = categoryTelcoInputBean.echo;
                responseBean.columns = categoryTelcoInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;

            } else {

                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = categoryTelcoInputBean.echo;
                responseBean.columns = categoryTelcoInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @ModelAttribute
    public void getCategoryTelcoBean(Model map) throws Exception {
        CategoryTelcoInputBean categoryTelcoInputBean = new CategoryTelcoInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        List<CommonTelcoBean> telcoList = commonRepository.getTelcoList();
        List<CommonCategoryBean> categoryList = commonRepository.getCategoryList(commonVarList.STATUS_ACTIVE);
        List<String> mtPortList = commonRepository.getMtPortList();
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        categoryTelcoInputBean.setStatusList(statusList);
        categoryTelcoInputBean.setTelcoList(telcoList);
        categoryTelcoInputBean.setCategoryList(categoryList);
        categoryTelcoInputBean.setMtPortList(mtPortList);
        categoryTelcoInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(categoryTelcoInputBean);
        //add values to model map
        map.addAttribute("categoryTelco", categoryTelcoInputBean);
    }

    private void applyUserPrivileges(CategoryTelcoInputBean categoryTelcoInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.CATEGORY_TELCO_MGT_PAGE, sessionBean);
            categoryTelcoInputBean.setVadd(false);
            categoryTelcoInputBean.setVupdate(false);
            categoryTelcoInputBean.setVdelete(false);
            categoryTelcoInputBean.setVconfirm(false);
            categoryTelcoInputBean.setVreject(false);
            categoryTelcoInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.CATEGORY_TELCO_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        categoryTelcoInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        categoryTelcoInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        categoryTelcoInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        categoryTelcoInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        categoryTelcoInputBean.setVreject(true);
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
        dataBinder.setValidator(categoryTelcoValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
