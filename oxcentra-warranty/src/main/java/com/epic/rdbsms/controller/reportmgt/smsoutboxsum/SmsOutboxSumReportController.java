package com.epic.rdbsms.controller.reportmgt.smsoutboxsum;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.common.CommonCategoryBean;
import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.smsoutbox.SmsOutboxReportInputBean;
import com.epic.rdbsms.mapping.department.Department;
import com.epic.rdbsms.mapping.smschannel.SmsChannel;
import com.epic.rdbsms.mapping.smsoutboxsum.SmsOutboxsum;
import com.epic.rdbsms.mapping.telco.Telco;
import com.epic.rdbsms.mapping.txntype.TxnType;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.reportmgt.smsoutboxsum.SmsOutboxsumService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.common.DataTablesResponse;
import com.epic.rdbsms.util.varlist.*;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.smsoutput.SmsOutputValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
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
public class SmsOutboxSumReportController implements RequestBeanValidation<Object> {
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
    SmsOutputValidator smsOutputValidator;

    @Autowired
    SmsOutboxsumService smsOutboxsumService;

    @GetMapping(value = "/viewsmsoutboxsumreport")
    @AccessControl(pageCode = PageVarList.SMSOUTBOX_SUM_REPORT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getSytemUserPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS OUTBOX SUMMARY REPORT PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("smsoutboxsumreportview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("smsoutboxsumreportview", modelMap);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/listSmsOutBoxSum", method = RequestMethod.POST, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.SMSOUTBOX_SUM_REPORT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<SmsOutboxsum> searchSmsOutboxSum(@RequestBody SmsOutboxReportInputBean smsOutboxReportInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS OUTBOX SUM REPORT SEARCH");
        DataTablesResponse<SmsOutboxsum> responseBean = new DataTablesResponse<>();
        try {
            List<SmsOutboxsum> smsOutboxsumList = smsOutboxsumService.getSmsOutBoxSumSearchResultList(smsOutboxReportInputBean);
            if (smsOutboxsumList.size() > 0 && smsOutboxsumList.get(0).getTotalSMS() > 0) {
                //set data set to response bean
                responseBean.data.addAll(smsOutboxsumList);
                responseBean.echo = smsOutboxReportInputBean.echo;
                responseBean.columns = smsOutboxReportInputBean.columns;
                responseBean.totalRecords = 1;
                responseBean.totalDisplayRecords = 1;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = smsOutboxReportInputBean.echo;
                responseBean.columns = smsOutboxReportInputBean.columns;
                responseBean.totalRecords = 0;
                responseBean.totalDisplayRecords = 0;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @RequestMapping(value = "/downloadCsvReportSmsOutBoxSum", method = RequestMethod.POST)
    @AccessControl(pageCode = PageVarList.SMSOUTBOX_SUM_REPORT_PAGE, taskCode = TaskVarList.DOWNLOAD_TASK)
    public @ResponseBody
    void downloadCsvSmsOutputReport(@ModelAttribute("smsoutputreport") SmsOutboxReportInputBean smsOutboxReportInputBean, HttpServletResponse httpServletResponse) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS OUTBOX SUM REPORT PDF");
        OutputStream outputStream = null;
        try {
            StringBuffer csvContent = smsOutboxsumService.makeCsvReport(smsOutboxReportInputBean);
            //set response to
            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.setHeader("Content-disposition", "inline; filename=SmsOutBoxSum_Report.csv");
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

    @ModelAttribute
    public void getSmsOutBoxReportBean(Model map) throws Exception {
        SmsOutboxReportInputBean smsOutboxReportInputBean = new SmsOutboxReportInputBean();
        //get telco list , department list , sms channel list
        List<Telco> telcoList = commonRepository.getTelcoList(commonVarList.STATUS_ACTIVE);
        List<Department> departmentList = commonRepository.getDepartmentList(commonVarList.STATUS_ACTIVE);
        List<SmsChannel> smsChannelList = commonRepository.getSmsChannelList(commonVarList.STATUS_ACTIVE);
        List<CommonCategoryBean> categoryList = commonRepository.getCategoryList(commonVarList.STATUS_ACTIVE);
        List<Status> statusList = commonRepository.getStatusList(StatusVarList.STATUS_CATEGORY_DELIVERY);
        List<TxnType> txnTypeList = commonRepository.getTxnTypeList(commonVarList.STATUS_ACTIVE);

        //set values to bean
        smsOutboxReportInputBean.setTelcoList(telcoList);
        smsOutboxReportInputBean.setDepartmentList(departmentList);
        smsOutboxReportInputBean.setSmsChannelList(smsChannelList);
        smsOutboxReportInputBean.setStatusList(statusList);
        smsOutboxReportInputBean.setCategoryList(categoryList);
        smsOutboxReportInputBean.setTxnTypeList(txnTypeList);
        //add privileges to input bean
        this.applyUserPrivileges(smsOutboxReportInputBean);
        //add values to model map
        map.addAttribute("smsoutboxsumreport", smsOutboxReportInputBean);
    }

    private void applyUserPrivileges(SmsOutboxReportInputBean smsOutboxReportInputBean) {
        List<Task> taskList = common.getUserTaskListByPage(PageVarList.SMSOUTBOX_SUM_REPORT_PAGE, sessionBean);
        smsOutboxReportInputBean.setVdownload(false);
        //check task list one by one
        if (taskList != null && !taskList.isEmpty()) {
            taskList.forEach(task -> {
                if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DOWNLOAD_TASK)) {
                    smsOutboxReportInputBean.setVdownload(true);
                }
            });
        }
    }

    @Override
    public BindingResult validateRequestBean(Object o) {
        DataBinder dataBinder = new DataBinder(o);
        dataBinder.setValidator(smsOutputValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
