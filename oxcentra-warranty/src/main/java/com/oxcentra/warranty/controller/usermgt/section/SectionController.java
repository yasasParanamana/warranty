package com.oxcentra.warranty.controller.usermgt.section;

import com.oxcentra.warranty.annotation.accesscontrol.AccessControl;
import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.usermgt.section.SectionInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.usermgt.Section;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.service.usermgt.section.SectionService;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.common.DataTablesResponse;
import com.oxcentra.warranty.util.common.ResponseBean;
import com.oxcentra.warranty.validators.RequestBeanValidation;
import com.oxcentra.warranty.validators.usermgt.section.SectionValidator;
import com.oxcentra.warranty.util.varlist.*;
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
public class SectionController implements RequestBeanValidation<Object> {
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
    SectionService sectionService;

    @Autowired
    SectionValidator sectionValidator;

    @GetMapping(value = "/viewSection")
    @AccessControl(pageCode = PageVarList.SECTION_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getSytemUserPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SECTION PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("sectionview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("sectionview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listSection", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.SECTION_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Section> searchSection(@RequestBody SectionInputBean sectionInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SECTION SEARCH");
        DataTablesResponse<Section> responseBean = new DataTablesResponse<>();
        try {
            long count = sectionService.getCount(sectionInputBean);
            if (count > 0) {
                List<Section> list = sectionService.getSectionSearchResultList(sectionInputBean);
                //set data set to response bean
                responseBean.data.addAll(list);
                responseBean.echo = sectionInputBean.echo;
                responseBean.columns = sectionInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = sectionInputBean.echo;
                responseBean.columns = sectionInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/listDualSection", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.SECTION_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<TempAuthRec> searchDualSection(@RequestBody SectionInputBean sectionInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SECTION SEARCH DUAL");
        DataTablesResponse<TempAuthRec> responseBean = new DataTablesResponse<>();
        try {
            long count = sectionService.getDataCountDual(sectionInputBean);
            if (count > 0) {
                List<TempAuthRec> dualList = sectionService.getSectionSearchResultsDual(sectionInputBean);
                //set values to response bean
                responseBean.data.addAll(dualList);
                responseBean.echo = sectionInputBean.echo;
                responseBean.columns = sectionInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = sectionInputBean.echo;
                responseBean.columns = sectionInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @PostMapping(value = "/addSection", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SECTION_MGT_PAGE, taskCode = TaskVarList.ADD_TASK)
    public @ResponseBody
    ResponseBean addSection(@ModelAttribute("section") SectionInputBean sectionInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SECTION ADD");
        ResponseBean responseBean = null;
        try {
            BindingResult bindingResult = validateRequestBean(sectionInputBean);
            if (bindingResult.hasErrors()) {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = sectionService.insertSection(sectionInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SECTION_MGT_ADDED_SUCCESSFULLY, null, locale), null);
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

    @GetMapping(value = "/getSection")
    @AccessControl(pageCode = PageVarList.SECTION_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    Section getSection(@RequestParam String sectionCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  GET SECTION");
        Section section = new Section();
        try {
            if (sectionCode != null && !sectionCode.isEmpty()) {
                section = sectionService.getSection(sectionCode);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return section;
    }

    @PostMapping(value = "/updateSection", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SECTION_MGT_PAGE, taskCode = TaskVarList.UPDATE_TASK)
    public @ResponseBody
    ResponseBean updateSection(@ModelAttribute("section") SectionInputBean sectionInputBean, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] UPDATE SECTION");
        ResponseBean responseBean = new ResponseBean();
        try {
            BindingResult bindingResult = validateRequestBean(sectionInputBean);
            if (bindingResult.hasErrors()) {
                responseBean.setErrorMessage(messageSource.getMessage(bindingResult.getAllErrors().get(0).getCode(), new Object[]{bindingResult.getAllErrors().get(0).getDefaultMessage()}, locale));
            } else {
                String message = sectionService.updateSection(sectionInputBean, locale);
                if (message.isEmpty()) {
                    responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SECTION_MGT_UPDATE_SUCCESSFULLY, null, locale), null);
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

    @PostMapping(value = "/deleteSection", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SECTION_MGT_PAGE, taskCode = TaskVarList.DELETE_TASK)
    public @ResponseBody
    ResponseBean deleteSection(@RequestParam String sectionCode, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "] DELETE SECTION");
        ResponseBean responseBean;
        try {
            String message = sectionService.deleteSection(sectionCode);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SECTION_MGT_DELETE_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/confirmSection", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SECTION_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_CONFIRM_TASK)
    public @ResponseBody
    ResponseBean confirmSection(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SECTION CONFIRM");
        ResponseBean responseBean;
        try {
            String message = sectionService.confirmSection(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SECTION_MGT_CONFIRM_SUCCESSFULLY, null, locale), null);
            } else {
                responseBean = new ResponseBean(false, null, messageSource.getMessage(message, null, locale));
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            responseBean = new ResponseBean(false, null, messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
        }
        return responseBean;
    }

    @PostMapping(value = "/rejectSection", produces = {MediaType.APPLICATION_JSON_VALUE})
    @AccessControl(pageCode = PageVarList.SECTION_MGT_PAGE, taskCode = TaskVarList.DUAL_AUTH_REJECT_TASK)
    public @ResponseBody
    ResponseBean rejectSection(@RequestParam(value = "id") String id, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  REJECT SECTION");
        ResponseBean responseBean;
        try {
            String message = sectionService.rejectSection(id);
            if (message.isEmpty()) {
                responseBean = new ResponseBean(true, messageSource.getMessage(MessageVarList.SECTION_MGT_REJECT_SUCCESSFULLY, null, locale), null);
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
        SectionInputBean sectionInputBean = new SectionInputBean();
        //get status list
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DEFAULT);
        List<Status> statusActList = common.getActiveStatusList();
        //set values to task bean
        sectionInputBean.setStatusList(statusList);
        sectionInputBean.setStatusActList(statusActList);
        //add privileges to input bean
        this.applyUserPrivileges(sectionInputBean);
        //add values to model map
        map.addAttribute("section", sectionInputBean);
    }

    private void applyUserPrivileges(SectionInputBean sectionInputBean) {
        try {
            List<Task> taskList = common.getUserTaskListByPage(PageVarList.SECTION_MGT_PAGE, sessionBean);
            sectionInputBean.setVadd(false);
            sectionInputBean.setVupdate(false);
            sectionInputBean.setVdelete(false);
            sectionInputBean.setVconfirm(false);
            sectionInputBean.setVreject(false);
            sectionInputBean.setVdualauth(commonRepository.checkPageIsDualAuthenticate(PageVarList.SECTION_MGT_PAGE));
            //check task list one by one
            if (taskList != null && !taskList.isEmpty()) {
                taskList.forEach(task -> {
                    if (task.getTaskCode().equalsIgnoreCase(TaskVarList.ADD_TASK)) {
                        sectionInputBean.setVadd(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.UPDATE_TASK)) {
                        sectionInputBean.setVupdate(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DELETE_TASK)) {
                        sectionInputBean.setVdelete(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_CONFIRM_TASK)) {
                        sectionInputBean.setVconfirm(true);
                    } else if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DUAL_AUTH_REJECT_TASK)) {
                        sectionInputBean.setVreject(true);
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
        dataBinder.setValidator(sectionValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
