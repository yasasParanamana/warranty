package com.oxcentra.rdbsms.controller.sysconfigmgt.channeltxntype;

import com.oxcentra.rdbsms.annotation.accesscontrol.AccessControl;
import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.channeltxntype.ChannelTxnTypeInputBean;
import com.oxcentra.rdbsms.mapping.channeltxntype.ChannelTxnType;
import com.oxcentra.rdbsms.mapping.smschannel.SmsChannel;
import com.oxcentra.rdbsms.mapping.smstemplate.SmsOutputTemplate;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.txntype.TxnType;
import com.oxcentra.rdbsms.mapping.usermgt.Task;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.service.sysconfigmgt.channeltxntype.ChannelTxnTypeService;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.common.DataTablesResponse;
import com.oxcentra.rdbsms.util.common.ResponseBean;
import com.oxcentra.rdbsms.validators.RequestBeanValidation;
import com.oxcentra.rdbsms.validators.sysconfigmgt.channeltxntype.ChannelTxnTypeValidator;
import com.oxcentra.rdbsms.util.varlist.*;
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

/**
 * @author Namila Withanage on 11/19/2021
 */
@Controller
@Scope("request")
public class ChannelTxnTypeController implements RequestBeanValidation<Object> {

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
    ChannelTxnTypeService channelTxnTypeService;

    @Autowired
    ChannelTxnTypeValidator channelTxnTypeValidator;

    @GetMapping("/viewChannelTxnType")
    @AccessControl(pageCode = PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getChannelTxnTypePage(ModelMap modelMap, Locale locale) throws Exception {
        logger.info("[" + sessionBean.getSessionid() + "]   CHANNEL TXNTYPE PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("channeltxntypeview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("channeltxntypeview", modelMap);
        }
        return modelAndView;
    }

    @GetMapping(value = "/getChannelTxnType")
    @AccessControl(pageCode = PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ChannelTxnType getChannelTxnType(@RequestParam String txntype, @RequestParam String channel) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET CHANNEL TXNTYPE");
        ChannelTxnType channelTxnType = new ChannelTxnType();
        try {
            if (txntype != null && !txntype.isEmpty() && channel != null && !channel.isEmpty()) {
                channelTxnType = channelTxnTypeService.getChannelTxnType(txntype, channel);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return channelTxnType;
    }

    @PostMapping(value = "/updateChannelTxnType", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateChannelTxnType(@ModelAttribute("channelTxnType") ChannelTxnTypeInputBean channelTxnTypeInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE CHANNEL TXNTYPE");
        ResponseBean responseBean;
        try {
            BindingResult bindingResult = validateRequestBean(channelTxnTypeInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = channelTxnTypeService.updateChannelTxnType(channelTxnTypeInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CHANNEL_TXN_TYPE_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/listDualChannelTxnType", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualChannelTxnType(@RequestBody ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  CHANNEL TXNTYPE SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = channelTxnTypeService.getDataCountDual(channelTxnTypeInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = channelTxnTypeService.getChannelTxnTypeSearchResultsDual(channelTxnTypeInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = channelTxnTypeInputBean.echo;
                responseBean.columns = channelTxnTypeInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = channelTxnTypeInputBean.echo;
                responseBean.columns = channelTxnTypeInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/deleteChannelTxnType", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteChannelTxnType(@RequestParam String txntype, @RequestParam String channel, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE CHANNEL TXNTYPE");
        ResponseBean responseBean;
        try {
            String message = channelTxnTypeService.deleteChannelTxnType(txntype, channel);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CHANNEL_TXN_TYPE_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/addChannelTxnType", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addChannelTxnType(@Valid @ModelAttribute("channelTxnType") ChannelTxnTypeInputBean channelTxnTypeInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CHANNEL TXNTYPE ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(channelTxnTypeInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, Locale.US));
            } else {
                String message = channelTxnTypeService.insertChannelTxnType(channelTxnTypeInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CHANNEL_TXN_TYPE_ADDED_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/confirmChannelTxnType", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmChannelTxnType(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CHANNEL TXNTYPE CONFIRM");
        ResponseBean responseBean;
        try {
            String message = channelTxnTypeService.confirmChannelTxnType(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CHANNEL_TXN_TYPE_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectChannelTxnType", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectChannelTxnType(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT CHANNEL TXNTYPE");
        ResponseBean responseBean;
        try {
            String message = channelTxnTypeService.rejectChannelTxnType(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CHANNEL_TXN_TYPE_REJECT_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/listChannelTxnType", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<ChannelTxnType> searchChannelTxnType(@RequestBody ChannelTxnTypeInputBean channelTxnTypeInputBean) {

        logger.info("[" + sessionBean.getSessionid() + "]  CHANNEL TXNTYPE SEARCH");
        DataTablesResponse<ChannelTxnType> responseBean = new DataTablesResponse<>();

        try {
            long count = channelTxnTypeService.getCount(channelTxnTypeInputBean);

            if (count > 0) {

                List<ChannelTxnType> list = channelTxnTypeService.getChannelTxnTypeResultList(channelTxnTypeInputBean);

                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = channelTxnTypeInputBean.echo;
                responseBean.columns = channelTxnTypeInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;

            } else {

                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = channelTxnTypeInputBean.echo;
                responseBean.columns = channelTxnTypeInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @ModelAttribute
    public void getChannelTxnTypeBean(Model map) throws Exception {
        ChannelTxnTypeInputBean channelTxnTypeInputBean = new ChannelTxnTypeInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        List<TxnType> txnTypeList = commonRepository.getTxnTypeList(commonVarList.STATUS_ACTIVE);
        List<SmsChannel> smsChannelList = commonRepository.getSmsChannelList(commonVarList.STATUS_ACTIVE);
        List<SmsOutputTemplate> templateList = commonRepository.getTemplateList(commonVarList.STATUS_ACTIVE);
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        channelTxnTypeInputBean.setStatusList(statusList);
        channelTxnTypeInputBean.setTxnTypeList(txnTypeList);
        channelTxnTypeInputBean.setChannelList(smsChannelList);
        channelTxnTypeInputBean.setTemplateList(templateList);
        channelTxnTypeInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(channelTxnTypeInputBean);
        //add values to model map
        map.addAttribute("channelTxnType", channelTxnTypeInputBean);
    }

    private void applyUserPrivileges(ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, sessionBean);
            channelTxnTypeInputBean.setVadd(false);
            channelTxnTypeInputBean.setVupdate(false);
            channelTxnTypeInputBean.setVdelete(false);
            channelTxnTypeInputBean.setVconfirm(false);
            channelTxnTypeInputBean.setVreject(false);
            channelTxnTypeInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        channelTxnTypeInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        channelTxnTypeInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        channelTxnTypeInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        channelTxnTypeInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        channelTxnTypeInputBean.setVreject(true);
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
        dataBinder.setValidator(channelTxnTypeValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }

}
