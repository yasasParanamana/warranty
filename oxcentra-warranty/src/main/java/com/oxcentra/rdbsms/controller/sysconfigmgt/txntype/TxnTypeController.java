package com.oxcentra.rdbsms.controller.sysconfigmgt.txntype;

import com.oxcentra.rdbsms.annotation.accesscontrol.AccessControl;
import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.txntype.TxnTypeInputBean;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.txntype.TxnType;
import com.oxcentra.rdbsms.mapping.usermgt.Task;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.service.sysconfigmgt.txntype.TxnTypeService;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.common.DataTablesResponse;
import com.oxcentra.rdbsms.util.common.ResponseBean;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.TaskVarList;
import com.oxcentra.rdbsms.validators.RequestBeanValidation;
import com.oxcentra.rdbsms.validators.sysconfigmgt.txntype.TxnTypeValidator;
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

/**
 * @author Namila Withanage on 11/15/2021
 */
@Controller
@Scope("request")
public class TxnTypeController implements RequestBeanValidation<Object> {

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
    TxnTypeService txnTypeService;

    @Autowired
    TxnTypeValidator txnTypeValidator;

    @GetMapping(value = "/viewTxnType")
    @AccessControl(pageCode = PageVarList.TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getSytemUserPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  TXN TYPE PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("txntypeview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("txntypeview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listTxnType", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TxnType> searchTxnType(@RequestBody TxnTypeInputBean txnTypeInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  TXN TYPE SEARCH");
        DataTablesResponse<TxnType> responseBean = new DataTablesResponse<>();
        try {
            long count = txnTypeService.getCount(txnTypeInputBean);
            if (count > 0) {
                List<TxnType> list = txnTypeService.getTxnTypeSearchResultList(txnTypeInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = txnTypeInputBean.echo;
                responseBean.columns = txnTypeInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = txnTypeInputBean.echo;
                responseBean.columns = txnTypeInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualTxnType", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualTxnType(@RequestBody TxnTypeInputBean txnTypeInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  TXN TYPE SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = txnTypeService.getDataCountDual(txnTypeInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = txnTypeService.getTxnTypeSearchResultsDual(txnTypeInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = txnTypeInputBean.echo;
                responseBean.columns = txnTypeInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = txnTypeInputBean.echo;
                responseBean.columns = txnTypeInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addTxnType", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addTxnType(@ModelAttribute("txntype") TxnTypeInputBean txnTypeInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  TXN TYPE ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(txnTypeInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = txnTypeService.insertTxnType(txnTypeInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TXN_TYPE_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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

    @GetMapping(value = "/getTxnType")
    @AccessControl(pageCode = PageVarList.TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    TxnType getTxnType(@RequestParam String txntype) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET TXN TYPE");
        TxnType txnType = new TxnType();
        try {
            if (txntype != null && !txntype.isEmpty()) {
                txnType = txnTypeService.getTxnType(txntype);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return txnType;
    }

    @PostMapping(value = "/updateTxnType", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateTxnType(@ModelAttribute("txntype") TxnTypeInputBean txnTypeInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE TXN TYPE");
        ResponseBean responseBean;
        try {
            BindingResult bindingResult = validateRequestBean(txnTypeInputBean);

            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));

            } else {
                String message = txnTypeService.updateTxnType(txnTypeInputBean, locale);

                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TXN_TYPE_MGT_UPDATE_SUCCESSFULLY, null, locale), null);

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

    @PostMapping(value = "/deleteTxnType", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteTxnType(@RequestParam String txntype, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE TXN TYPE");
        ResponseBean responseBean;
        try {
            String message = txnTypeService.deleteTxnType(txntype);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TXN_TYPE_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmTxnType", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmTxnType(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  TXN TYPE CONFIRM");
        ResponseBean responseBean;
        try {
            String message = txnTypeService.confirmTxnType(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TXN_TYPE_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectTxnType", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectTxnType(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT TXN TYPE");
        ResponseBean responseBean;
        try {
            String message = txnTypeService.rejectTxnType(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.TXN_TYPE_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
    public void getTxnTypeBean(Model map) throws Exception {
        TxnTypeInputBean txnTypeInputBean = new TxnTypeInputBean();
        //get status list
        List<Status> statusList = commonRepository.getActiveDeActiveStatusList();
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        txnTypeInputBean.setStatusList(statusList);
        txnTypeInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(txnTypeInputBean);
        //add values to model map
        map.addAttribute("txntype", txnTypeInputBean);
    }

    private void applyUserPrivileges(TxnTypeInputBean txnTypeInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.TXN_TYPE_MGT_PAGE, sessionBean);
            txnTypeInputBean.setVadd(false);
            txnTypeInputBean.setVupdate(false);
            txnTypeInputBean.setVdelete(false);
            txnTypeInputBean.setVconfirm(false);
            txnTypeInputBean.setVreject(false);
            txnTypeInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.TXN_TYPE_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        txnTypeInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        txnTypeInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        txnTypeInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        txnTypeInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        txnTypeInputBean.setVreject(true);
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
        dataBinder.setValidator(txnTypeValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }

}
