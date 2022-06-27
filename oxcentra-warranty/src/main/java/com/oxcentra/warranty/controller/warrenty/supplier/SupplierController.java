package com.oxcentra.warranty.controller.warrenty.supplier;

import com.oxcentra.warranty.annotation.accesscontrol.AccessControl;
import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.supplier.SupplierInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.service.warranty.supplier.SupplierService;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.common.DataTablesResponse;
import com.oxcentra.warranty.util.common.ResponseBean;
import com.oxcentra.warranty.util.varlist.*;
import com.oxcentra.warranty.validators.RequestBeanValidation;
import com.oxcentra.warranty.validators.warranty.supplier.SupplierValidator;
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

import static com.oxcentra.warranty.util.varlist.PageVarList.SUPPLIER_MGT_PAGE;
import static com.oxcentra.warranty.util.varlist.TaskVarList.ADD_TASK;
import static com.oxcentra.warranty.util.varlist.TaskVarList.VIEW_TASK;


@Controller
@Scope("request")
public class SupplierController implements RequestBeanValidation<Object> {
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
    SupplierService supplierService;

    @Autowired
    SupplierValidator supplierValidator;

    @GetMapping(value = "/viewSupplier")
    @AccessControl(pageCode = SUPPLIER_MGT_PAGE, taskCode = VIEW_TASK)
    public ModelAndView getSupplierPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SUPPLIER PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("supplierview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("supplierview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listSupplier", headers = {"content-type=application/json"})
    @AccessControl(pageCode = SUPPLIER_MGT_PAGE, taskCode = VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Supplier> searchSection(@RequestBody SupplierInputBean supplierInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SUPPLIER SEARCH");
        DataTablesResponse<Supplier> responseBean = new DataTablesResponse<>();
        try {
            long count = supplierService.getCount(supplierInputBean);
            if (count > 0) {
                List<Supplier> list = supplierService.getSupplierSearchResultList(supplierInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = supplierInputBean.echo;
                responseBean.columns = supplierInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = supplierInputBean.echo;
                responseBean.columns = supplierInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualSupplier", headers = {"content-type=application/json"})
    @AccessControl(pageCode = SUPPLIER_MGT_PAGE, taskCode = VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualSection(@RequestBody SupplierInputBean supplierInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SUPPLIER SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = supplierService.getDataCountDual(supplierInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = supplierService.getSupplierSearchResultsDual(supplierInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = supplierInputBean.echo;
                responseBean.columns = supplierInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = supplierInputBean.echo;
                responseBean.columns = supplierInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addSupplier", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = SUPPLIER_MGT_PAGE, taskCode = ADD_TASK)
    public @ResponseBody
    ResponseBean addSection(@ModelAttribute("supplier") SupplierInputBean supplierInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SUPPLIER ADD");
        ResponseBean responseBean = null;
        supplierInputBean.setSupplierCode("SUP-" + System.currentTimeMillis() / 100);
        try {
            BindingResult bindingResult = validateRequestBean(supplierInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = supplierService.insertSupplier(supplierInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SUPPLIER_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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

    @GetMapping(value = "/getSupplier")
    @AccessControl(pageCode = SUPPLIER_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Supplier getSection(@RequestParam String supplierCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET SUPPLIER");
        Supplier supplier = new Supplier();
        try {
            if (supplierCode != null && !supplierCode.isEmpty()) {
                supplier = supplierService.getSupplier(supplierCode);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        logger.info(supplier);
        return supplier;
    }

    @PostMapping(value = "/updateSupplier", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = SUPPLIER_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateSection(@ModelAttribute("supplier") SupplierInputBean supplierInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE SUPPLIER");
        ResponseBean responseBean = new ResponseBean();
        try {
            BindingResult bindingResult = validateRequestBean(supplierInputBean);
            if (bindingResult.hasErrors()) {
                responseBean.setErrorMessage(messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = supplierService.updateSupplier(supplierInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SUPPLIER_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/deleteSupplier", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = SUPPLIER_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteSection(@RequestParam String supplierCode, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE SUPPLIER");
        ResponseBean responseBean;
        try {
            String message = supplierService.deleteSupplier(supplierCode);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SUPPLIER_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmSupplier", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = SUPPLIER_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmSection(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SUPPLIER CONFIRM");
        ResponseBean responseBean;
        try {
            String message = supplierService.confirmSupplier(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SUPPLIER_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectSupplier", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = SUPPLIER_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectSection(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT SUPPLIER");
        ResponseBean responseBean;
        try {
            String message = supplierService.rejectSupplier(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SUPPLIER_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
    public void getSectionBean(Model map) throws Exception {
        SupplierInputBean supplierInputBean = new SupplierInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        supplierInputBean.setStatusList(statusList);
        supplierInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(supplierInputBean);
        //add values to model map
        map.addAttribute("supplier", supplierInputBean);
    }

    private void applyUserPrivileges(SupplierInputBean supplierInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(SUPPLIER_MGT_PAGE, sessionBean);
            supplierInputBean.setVadd(false);
            supplierInputBean.setVupdate(false);
            supplierInputBean.setVdelete(false);
            supplierInputBean.setVconfirm(false);
            supplierInputBean.setVreject(false);
            supplierInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(SUPPLIER_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(ADD_TASK)) {
                        supplierInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        supplierInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        supplierInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        supplierInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        supplierInputBean.setVreject(true);
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
        dataBinder.setValidator(supplierValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
