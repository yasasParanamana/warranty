package com.oxcentra.rdbsms.controller.reportmgt.smsoutbox;

import com.oxcentra.rdbsms.annotation.accesscontrol.AccessControl;
import com.oxcentra.rdbsms.bean.common.CommonCategoryBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.smsoutbox.SmsOutboxReportInputBean;
import com.oxcentra.rdbsms.mapping.deliverystatus.DeliveryStatus;
import com.oxcentra.rdbsms.mapping.department.Department;
import com.oxcentra.rdbsms.mapping.smschannel.SmsChannel;
import com.oxcentra.rdbsms.mapping.smsoutbox.SmsOutbox;
import com.oxcentra.rdbsms.mapping.telco.Telco;
import com.oxcentra.rdbsms.mapping.txntype.TxnType;
import com.oxcentra.rdbsms.mapping.usermgt.Task;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.category.CategoryRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.channel.ChannelRepository;
import com.oxcentra.rdbsms.service.reportmgt.smsoutbox.SmsOutboxService;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.common.DataTablesResponse;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.TaskVarList;
import com.oxcentra.rdbsms.validators.RequestBeanValidation;
import com.oxcentra.rdbsms.validators.smsoutput.SmsOutputValidator;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Scope("request")
public class SmsOutboxReportController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    CategoryRepository categoryRepository;

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
    SmsOutboxService smsOutboxService;

    @GetMapping(value = "/viewSmsOutBox")
    @AccessControl(pageCode = PageVarList.SMSOUTBOX_REPORT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getSytemUserPage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS OUTBOX REPORT PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            //redirect to home page
            modelAndView = new ModelAndView("smsoutboxreportview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("smsoutboxreportview", modelMap);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/listSmsOutBox", method = RequestMethod.POST, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.SMSOUTBOX_REPORT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<SmsOutbox> searchSmsOutbox(@RequestBody SmsOutboxReportInputBean smsOutboxReportInputBean) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS OUTBOX REPORT SEARCH");
        DataTablesResponse<SmsOutbox> responseBean = new DataTablesResponse<>();
        try {
            long count = smsOutboxService.getCount(smsOutboxReportInputBean);
            if (count > 0) {
                List<SmsOutbox> smsOutboxList = smsOutboxService.getSmsOutBoxSearchResultList(smsOutboxReportInputBean);
                //set data set to response bean
                responseBean.data.addAll(smsOutboxList);
                responseBean.echo = smsOutboxReportInputBean.echo;
                responseBean.columns = smsOutboxReportInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = smsOutboxReportInputBean.echo;
                responseBean.columns = smsOutboxReportInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @RequestMapping(value = "/downloadPdfReportSmsOutBox", method = RequestMethod.POST)
    @AccessControl(pageCode = PageVarList.SMSOUTBOX_REPORT_PAGE, taskCode = TaskVarList.DOWNLOAD_TASK)
    public @ResponseBody
    void downloadPdfSmsOutputReport(@ModelAttribute("smsoutputreport") SmsOutboxReportInputBean smsOutboxReportInputBean, HttpServletResponse httpServletResponse) {

        logger.info("[" + sessionBean.getSessionid() + "]  AUDIT PDF");

        try {
            List<SmsOutbox> smsOutboxList = smsOutboxService.getSmsOutBoxSearchResultListForReport(smsOutboxReportInputBean);
            if (smsOutboxList != null && !smsOutboxList.isEmpty() && smsOutboxList.size() > 0) {
//               //generate the printed date
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm a");

                InputStream jasperStream = this.getClass().getResourceAsStream("/reports/smsoutbox/smsoutbox_report.jasper");
                Map<String, Object> parameterMap = new HashMap<>();
                //set parameters to map
                parameterMap.put("fromdate", common.replaceEmptyorNullStringToALL(smsOutboxReportInputBean.getFromDate()));
                parameterMap.put("todate", common.replaceEmptyorNullStringToALL(smsOutboxReportInputBean.getToDate()));

                if (!smsOutboxReportInputBean.getTelco().isEmpty() && smsOutboxReportInputBean.getTelco() != null) {
                    parameterMap.put("telco", common.replaceEmptyorNullStringToALL(commonRepository.getTelco(smsOutboxReportInputBean.getTelco()).getDescription()));
                } else {
                    parameterMap.put("telco", "-ALL-");
                }

                if (!smsOutboxReportInputBean.getChannel().isEmpty() && smsOutboxReportInputBean.getChannel() != null) {
                    parameterMap.put("channel",common.replaceEmptyorNullStringToALL(channelRepository.getChannel(smsOutboxReportInputBean.getChannel()).getDescription()));
                } else {
                    parameterMap.put("channel", "-ALL-");
                }

                if (!smsOutboxReportInputBean.getCategory().isEmpty() && smsOutboxReportInputBean.getCategory() != null) {
                    parameterMap.put("category", common.replaceEmptyorNullStringToALL(categoryRepository.getCategory(smsOutboxReportInputBean.getCategory()).getDescription()));
                } else {
                    parameterMap.put("category", "-ALL-");
                }

                if (!smsOutboxReportInputBean.getDelstatus().isEmpty() && smsOutboxReportInputBean.getDelstatus() != null) {
                    parameterMap.put("delstatus", common.replaceEmptyorNullStringToALL(commonRepository.getDeliveryStatus(smsOutboxReportInputBean.getDelstatus()).getDescription()));
                } else {
                    parameterMap.put("delstatus", "-ALL-");
                }

                parameterMap.put("mobileno", common.replaceEmptyorNullStringToALL(smsOutboxReportInputBean.getMobileno()));

                if (!smsOutboxReportInputBean.getTxnType().isEmpty() && smsOutboxReportInputBean.getTxnType() != null) {
                    parameterMap.put("txntype", common.replaceEmptyorNullStringToALL(commonRepository.getTxnType(smsOutboxReportInputBean.getTxnType()).getDescription()));
                } else {
                    parameterMap.put("txntype", "-ALL-");
                }

                parameterMap.put("printeddate", simpleDateFormat.format(calendar.getTime()));

                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, new JRBeanCollectionDataSource(smsOutboxList));

                httpServletResponse.setHeader("Content-Disposition", "attachment; filename=SmsOutBox-Report.pdf");
                httpServletResponse.setContentType("application/download");
                OutputStream outputStream = httpServletResponse.getOutputStream();

                // Export report to PDF
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            }
        } catch (Exception ex) {
            logger.error("Exception  :  ", ex);
        }
    }

    @PostMapping(value = "/downloadExcelReportSmsOutBox")
    @AccessControl(pageCode = PageVarList.SMSOUTBOX_REPORT_PAGE, taskCode = TaskVarList.DOWNLOAD_TASK)
    public void downloadExcelSmsOutputReport (@ModelAttribute("smsoutputreport") SmsOutboxReportInputBean smsOutboxReportInputBean, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS OUTBOX REPORT EXCEL");
        OutputStream outputStream = null;
        try {
            Object object = smsOutboxService.generateExcelReport(request, smsOutboxReportInputBean);
            if (object instanceof SXSSFWorkbook) {
                SXSSFWorkbook workbook = (SXSSFWorkbook) object;
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-disposition", "attachment; filename=SMS_OUTBOX_REPORT.xlsx");
                response.setBufferSize(61440);
                outputStream = response.getOutputStream();
                workbook.write(outputStream);
            }
        } catch (Exception ex) {
            logger.error("Exception  :  ", ex);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ex) {
                //do nothing
            }
        }
    }

    @RequestMapping(value = "/downloadCsvReportSmsOutBox", method = RequestMethod.POST)
    @AccessControl(pageCode = PageVarList.SMSOUTBOX_REPORT_PAGE, taskCode = TaskVarList.DOWNLOAD_TASK)
    public @ResponseBody
    void downloadCsvSmsOutputReport(@ModelAttribute("smsoutputreport") SmsOutboxReportInputBean smsOutboxReportInputBean, HttpServletResponse httpServletResponse) {
        logger.info("[" + sessionBean.getSessionid() + "]  SMS OUTBOX REPORT PDF");
        OutputStream outputStream = null;
        try {
            StringBuffer csvContent = smsOutboxService.makeCsvReport(smsOutboxReportInputBean);
            //set response to
            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.setHeader("Content-disposition", "inline; filename=SmsOutBox_Report.csv");
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
        List<DeliveryStatus> delStatusList = commonRepository.getDeliveryStatusList();
        List<TxnType> txnTypeList = commonRepository.getTxnTypeList(commonVarList.STATUS_ACTIVE);

        //set values to bean
        smsOutboxReportInputBean.setTelcoList(telcoList);
        smsOutboxReportInputBean.setDepartmentList(departmentList);
        smsOutboxReportInputBean.setSmsChannelList(smsChannelList);
        smsOutboxReportInputBean.setDelStatusList(delStatusList);
        smsOutboxReportInputBean.setCategoryList(categoryList);
        smsOutboxReportInputBean.setTxnTypeList(txnTypeList);
        //add privileges to input bean
        this.applyUserPrivileges(smsOutboxReportInputBean);
        //add values to model map
        map.addAttribute("smsoutputreport", smsOutboxReportInputBean);
    }

    private void applyUserPrivileges(SmsOutboxReportInputBean smsOutboxReportInputBean) {
        List<Task> taskList = common.getUserTaskListByPage(PageVarList.SMSOUTBOX_REPORT_PAGE, sessionBean);
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
