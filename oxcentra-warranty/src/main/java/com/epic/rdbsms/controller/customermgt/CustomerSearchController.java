package com.epic.rdbsms.controller.customermgt;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.bean.customermgt.customersearch.CustomerSearchInputBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.usermgt.task.TaskInputBean;
import com.epic.rdbsms.mapping.customermgt.CustomerSearch;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.customermgt.customersearch.CustomerSearchService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.*;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.customermgt.customerregistration.CustomerSearchValidator;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@Scope("request")
public class CustomerSearchController implements RequestBeanValidation<Object> {
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
    CustomerSearchService customerSearchService;

    @Autowired
    CustomerSearchValidator customerSearchValidator;

    @GetMapping(value = "/viewCustomerSearch")
    @AccessControl(pageCode = PageVarList.CUSTOMER_SEARCH_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getCustomerSearchPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CUSTOMER SEARCH PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("cusserachview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("cusserachview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listCustomerInfo", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CUSTOMER_SEARCH_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<CustomerSearch> searchCustomer(@RequestBody CustomerSearchInputBean customerSearchInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  CUSTOMER SEARCH");
        DataTablesResponse<CustomerSearch> responseBean = new DataTablesResponse<>();
        try {
            long count = customerSearchService.getCount(customerSearchInputBean);
            if (count > 0) {
                List<CustomerSearch> list = customerSearchService.getCustomerSearchResultList(customerSearchInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = customerSearchInputBean.echo;
                responseBean.columns = customerSearchInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = customerSearchInputBean.echo;
                responseBean.columns = customerSearchInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmCustomer", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CUSTOMER_SEARCH_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmCustomer(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CUSTOMER CONFIRM");
        ResponseBean responseBean;
        try {
            String message = customerSearchService.confirmCustomer(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CUSTOMER_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectCustomer", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CUSTOMER_SEARCH_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectCustomer(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT CUSTOMER");
        ResponseBean responseBean;
        try {
            String message = customerSearchService.rejectCustomer(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CUSTOMER_MGT_REJECT_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/updateCustomer", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CUSTOMER_SEARCH_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateCustomer(@ModelAttribute("cussearch") CustomerSearchInputBean customerSearchInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE CUSTOMER");
        ResponseBean responseBean = new ResponseBean();
        try {
            BindingResult bindingResult = validateRequestBean(customerSearchInputBean);
            if (bindingResult.hasErrors()) {
                responseBean.setErrorMessage(messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = customerSearchService.updateCustomer(customerSearchInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CUSTOMER_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/listDualCustomerSearch", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CUSTOMER_SEARCH_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualSection(@RequestBody CustomerSearchInputBean customerSearchInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  CUSTOMER SEARCH SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = customerSearchService.getDataCountDual(customerSearchInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = customerSearchService.getSectionSearchResultsDual(customerSearchInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = customerSearchInputBean.echo;
                responseBean.columns = customerSearchInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = customerSearchInputBean.echo;
                responseBean.columns = customerSearchInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @GetMapping(value = "/getCustomerSearch")
    @AccessControl(pageCode = PageVarList.CUSTOMER_SEARCH_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    CustomerSearch getCustomerSearch(@RequestParam String customerId) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET CUSTOMER SEARCH");
        CustomerSearch customerSearch = new CustomerSearch();
        try {
            if (customerId != null && !customerId.isEmpty()) {
                customerSearch = customerSearchService.getCustomerSearch(customerId);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return customerSearch;
    }

    @ModelAttribute
    public void getCustomerSearchBean(Model map) throws Exception {
        CustomerSearchInputBean customerSearchInputBean = new CustomerSearchInputBean();
        //add privileges to input bean
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        customerSearchInputBean.setStatusList(statusList);
        this.applyUserPrivileges(customerSearchInputBean);
        //add values to model map
        map.addAttribute("cussearch", customerSearchInputBean);
    }

    private void applyUserPrivileges(CustomerSearchInputBean customerSearchInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.CUSTOMER_SEARCH_MGT_PAGE, sessionBean);
            customerSearchInputBean.setVadd(false);
            customerSearchInputBean.setVupdate(false);
            customerSearchInputBean.setVdelete(false);
            customerSearchInputBean.setVconfirm(false);
            customerSearchInputBean.setVreject(false);
            customerSearchInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.CUSTOMER_SEARCH_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        customerSearchInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DOWNLOAD_TASK)) {
                        customerSearchInputBean.setVdownload(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        customerSearchInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        customerSearchInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        customerSearchInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        customerSearchInputBean.setVreject(true);
                    }
                });
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @RequestMapping(value = "/csvCustomerSearch", method = RequestMethod.POST)
    @AccessControl(pageCode = PageVarList.CUSTOMER_SEARCH_MGT_PAGE, taskCode = TaskVarList.DOWNLOAD_TASK)
    @ResponseBody
    public void csvReportCustomerSearch(@ModelAttribute("cussearch") CustomerSearchInputBean customerSearchInputBean, HttpServletResponse httpServletResponse) {
        logger.info("[" + sessionBean.getSessionid() + "]  CUSTOMER SEARCH CSV");
        OutputStream outputStream = null;
        try {
            StringBuffer csvContent = customerSearchService.makeCsvReport(customerSearchInputBean);
            //set response to
            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.setHeader("Content-disposition", "inline; filename=Customer_Report.csv");
            httpServletResponse.getOutputStream().print(csvContent.toString());
        } catch (Exception ex) {
            logger.error("Exception  :  ", ex);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ex) {

            }
        }
    }
    @Override
    public BindingResult validateRequestBean(Object object) {
        DataBinder dataBinder = new DataBinder(object);
        dataBinder.setValidator(customerSearchValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}

