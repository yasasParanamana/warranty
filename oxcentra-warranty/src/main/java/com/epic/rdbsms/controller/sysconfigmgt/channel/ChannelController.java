package com.epic.rdbsms.controller.sysconfigmgt.channel;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.channel.ChannelInputBean;
import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.mapping.channel.Channel;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.sysconfigmgt.channel.ChannelService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.CommonVarList;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.TaskVarList;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.sysconfigmgt.channel.ChannelValidator;
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
public class ChannelController implements RequestBeanValidation<Object> {
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
    ChannelService channelService;

    @Autowired
    ChannelValidator channelValidator;

    @GetMapping(value = "/viewChannel")
    @AccessControl(pageCode = PageVarList.CHANNEL_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getSytemUserPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CHANNEL PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("channelview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("channelview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listChannel", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CHANNEL_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Channel> searchChannel(@RequestBody ChannelInputBean channelInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  CHANNEL SEARCH");
        DataTablesResponse<Channel> responseBean = new DataTablesResponse<>();
        try {
            long count = channelService.getCount(channelInputBean);
            if (count > 0) {
                List<Channel> list = channelService.getChannelSearchResultList(channelInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = channelInputBean.echo;
                responseBean.columns = channelInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = channelInputBean.echo;
                responseBean.columns = channelInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualChannel", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.CHANNEL_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualChannel(@RequestBody ChannelInputBean channelInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  CHANNEL SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = channelService.getDataCountDual(channelInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = channelService.getChannelSearchResultsDual(channelInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = channelInputBean.echo;
                responseBean.columns = channelInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = channelInputBean.echo;
                responseBean.columns = channelInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addChannel", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CHANNEL_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addChannel(@ModelAttribute("channel") ChannelInputBean channelInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CHANNEL ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(channelInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = channelService.insertChannel(channelInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CHANNEL_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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

    @GetMapping(value = "/getChannel")
    @AccessControl(pageCode = PageVarList.CHANNEL_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Channel getChannel(@RequestParam String channelCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET CHANNEL");
        Channel channel = new Channel();
        try {
            if (channelCode != null && !channelCode.isEmpty()) {
                channel = channelService.getChannel(channelCode);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return channel;
    }

    @PostMapping(value = "/updateChannel", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CHANNEL_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateChannel(@ModelAttribute("channel") ChannelInputBean channelInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE CHANNEL");
        ResponseBean responseBean;
        try {
            BindingResult bindingResult = validateRequestBean(channelInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = channelService.updateChannel(channelInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CHANNEL_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/deleteChannel", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CHANNEL_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteChannel(@RequestParam String channelCode, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE CHANNEL");
        ResponseBean responseBean;
        try {
            String message = channelService.deleteChannel(channelCode);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CHANNEL_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmChannel", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CHANNEL_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmChannel(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  CHANNEL CONFIRM");
        ResponseBean responseBean;
        try {
            String message = channelService.confirmChannel(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CHANNEL_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectChannel", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.CHANNEL_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectChannel(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT CHANNEL");
        ResponseBean responseBean;
        try {
            String message = channelService.rejectChannel(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.CHANNEL_MGT_REJECT_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @Override
    public BindingResult validateRequestBean(Object object) {
        DataBinder dataBinder = new DataBinder(object);
        dataBinder.setValidator(channelValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }

    @ModelAttribute
    public void getChannelBean(Model map) throws Exception {
        ChannelInputBean channelInputBean = new ChannelInputBean();
        //get status list
        List<Status> statusList = commonRepository.getActiveDeActiveStatusList();
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        channelInputBean.setStatusList(statusList);
        channelInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(channelInputBean);
        //add values to model map
        map.addAttribute("channel", channelInputBean);
    }

    private void applyUserPrivileges(ChannelInputBean channelInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.CHANNEL_MGT_PAGE, sessionBean);
            channelInputBean.setVadd(false);
            channelInputBean.setVupdate(false);
            channelInputBean.setVdelete(false);
            channelInputBean.setVconfirm(false);
            channelInputBean.setVreject(false);
            channelInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.CHANNEL_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        channelInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        channelInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        channelInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        channelInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        channelInputBean.setVreject(true);
                    }
                });
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
