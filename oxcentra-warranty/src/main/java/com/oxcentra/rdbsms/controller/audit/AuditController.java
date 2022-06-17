package com.oxcentra.rdbsms.controller.audit;

import com.oxcentra.rdbsms.annotation.accesscontrol.AccessControl;
import com.oxcentra.rdbsms.bean.audit.AuditTraceInputBean;
import com.oxcentra.rdbsms.bean.common.CommonBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.usermgt.Task;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.usermgt.page.PageRepository;
import com.oxcentra.rdbsms.repository.usermgt.section.SectionRepository;
import com.oxcentra.rdbsms.repository.usermgt.task.TaskRepository;
import com.oxcentra.rdbsms.service.audit.AuditService;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.common.DataTablesResponse;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.TaskVarList;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
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
public class AuditController {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    MessageSource messageSource;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    AuditService auditService;

    @Autowired
    Common common;

    @GetMapping(value = "/viewAudit")
    @AccessControl(pageCode = PageVarList.AUDITTRACE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getAuditTracePage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  AUDIT PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("auditview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("auditview", modelMap);
        }
        return modelAndView;
    }

    @PostMapping(value = "/listAudit", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.AUDITTRACE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    DataTablesResponse<Audittrace> searchAudit(@RequestBody AuditTraceInputBean auditTraceInputBean, HttpServletResponse response, HttpServletRequest request) {
        logger.info("[" + sessionBean.getSessionid() + "]  AUDIT SEARCH");
        DataTablesResponse<Audittrace> responseBean = new DataTablesResponse<>();
        try {
            long count = auditService.getDataCount(auditTraceInputBean);
            if (count > 0) {
                List<Audittrace> auditTraceList = auditService.getAuditSearchResultList(auditTraceInputBean);
                //set data set to response bean
                responseBean.data.addAll(auditTraceList);
                responseBean.echo = auditTraceInputBean.echo;
                responseBean.columns = auditTraceInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            } else {
                //set data set to response bean
                responseBean.data.addAll(new ArrayList<>());
                responseBean.echo = auditTraceInputBean.echo;
                responseBean.columns = auditTraceInputBean.columns;
                responseBean.totalRecords = count;
                responseBean.totalDisplayRecords = count;
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return responseBean;
    }

    @GetMapping(value = "/getAudit", headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.AUDITTRACE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    Audittrace getAudit(@RequestParam String auditId) {
        logger.info("[" + sessionBean.getSessionid() + "]  AUDIT GET");
        Audittrace audittrace = new Audittrace();
        try {
            if (auditId != null && !auditId.isEmpty()) {
                audittrace = auditService.getAudit(auditId);
            }
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return audittrace;
    }

    @PostMapping(value = "/pdfAudit")
    @AccessControl(pageCode = PageVarList.AUDITTRACE_MGT_PAGE, taskCode = TaskVarList.DOWNLOAD_TASK)
    public void pdfReportAudit(@ModelAttribute("audittrace") AuditTraceInputBean auditTraceInputBean, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logger.info("[" + sessionBean.getSessionid() + "]  AUDIT PDF");

        try {
            List<Audittrace> auditTraceList = auditService.getAuditTraceSearchResultListForReport(auditTraceInputBean);
            if (auditTraceList != null && !auditTraceList.isEmpty() && auditTraceList.size() > 0) {
                //generate the printed date
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm a");

                InputStream jasperStream = this.getClass().getResourceAsStream("/reports/audittrace/audit_report.jasper");
                Map<String, Object> parameterMap = new HashMap<>();
                //set parameters to map
                parameterMap.put("fromdate", common.replaceEmptyorNullStringToALL(auditTraceInputBean.getFromDate()));
                parameterMap.put("todate", common.replaceEmptyorNullStringToALL(auditTraceInputBean.getToDate()));
                parameterMap.put("username", common.replaceEmptyorNullStringToALL(auditTraceInputBean.getUserName()));

                if (!auditTraceInputBean.getSection().isEmpty() && auditTraceInputBean.getSection() != null) {
                    parameterMap.put("section", common.replaceEmptyorNullStringToALL(sectionRepository.getSection(auditTraceInputBean.getSection()).getDescription()));
                } else {
                    parameterMap.put("section", "-ALL-");
                }

                if (!auditTraceInputBean.getPage().isEmpty() && auditTraceInputBean.getPage() != null) {
                    parameterMap.put("page", common.replaceEmptyorNullStringToALL(pageRepository.getPage(auditTraceInputBean.getPage()).getDescription()));
                } else {
                    parameterMap.put("page", "-ALL-");
                }

                if (!auditTraceInputBean.getTask().isEmpty() && auditTraceInputBean.getTask() != null) {
                    parameterMap.put("task", common.replaceEmptyorNullStringToALL(taskRepository.getTask(auditTraceInputBean.getTask()).getDescription()));
                } else {
                    parameterMap.put("task", "-ALL-");
                }

                parameterMap.put("description", common.replaceEmptyorNullStringToALL(auditTraceInputBean.getDescription()));
                parameterMap.put("printeddate", simpleDateFormat.format(calendar.getTime()));

                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, new JRBeanCollectionDataSource(auditTraceList));

                httpServletResponse.setHeader("Content-Disposition", "attachment; filename=AuditTrace-Report.pdf");
                httpServletResponse.setContentType("application/download");
                OutputStream outputStream = httpServletResponse.getOutputStream();

                // Export report to PDF
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            }
        } catch (Exception ex) {
            logger.error("Exception  :  ", ex);
        }
    }

    @RequestMapping(value = "/getSections", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.AUDITTRACE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    List<CommonBean> getSections() {
        logger.info("[" + sessionBean.getSessionid() + "]  AUDITTRACE PAGE LOAD SECTIONS");
        List<CommonBean> list = new ArrayList<>();
        try {
            list = commonRepository.getSectionList();
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return list;
    }

    @RequestMapping(value = "/getPagesforSection", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.AUDITTRACE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    List<CommonBean> getAssignedPages(@RequestParam("sectionCode") String sectionCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  AUDITTRACE PAGE LOAD PAGES");
        List<CommonBean> list = new ArrayList<>();
        try {
            if (!sectionCode.equals("")) {
                list = auditService.getPagesforSection(sectionCode);
            } else {
                list = commonRepository.getPageList();
            }

        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return list;
    }

    @RequestMapping(value = "/getTasksforPage", method = RequestMethod.GET, headers = {"content-type=application/json"})
    @AccessControl(pageCode = PageVarList.AUDITTRACE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public @ResponseBody
    List<CommonBean> getAssignedTasks(@RequestParam("pageCode") String pageCode) {
        logger.info("[" + sessionBean.getSessionid() + "]  AUDITTRACE PAGE LOAD TASKS");
        List<CommonBean> list = new ArrayList<>();
        try {
            if (!pageCode.equals("")) {
                list = auditService.getTasksforPage(pageCode);
            } else {
                list = commonRepository.getTaskList();
            }

        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return list;
    }


    @PostMapping(value = "/excelAudit")
    @AccessControl(pageCode = PageVarList.AUDITTRACE_MGT_PAGE, taskCode = TaskVarList.DOWNLOAD_TASK)
    public void excelReportAudit(@ModelAttribute("audittrace") AuditTraceInputBean auditTraceInputBean, HttpServletRequest request, HttpServletResponse response) {
        logger.info("[" + sessionBean.getSessionid() + "]  AUDIT EXCEL");
        OutputStream outputStream = null;
        try {
            Object object = auditService.generateExcelReport(request, auditTraceInputBean);
            if (object instanceof SXSSFWorkbook) {
                SXSSFWorkbook workbook = (SXSSFWorkbook) object;
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-disposition", "attachment; filename=AuditTrace_Report.xlsx");
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

    @RequestMapping(value = "/csvAudit", method = RequestMethod.POST)
    @AccessControl(pageCode = PageVarList.AUDITTRACE_MGT_PAGE, taskCode = TaskVarList.DOWNLOAD_TASK)
    @ResponseBody
    public void csvReportAudit(@ModelAttribute("audittrace") AuditTraceInputBean auditTraceInputBean, HttpServletResponse httpServletResponse) {
        logger.info("[" + sessionBean.getSessionid() + "]  AUDIT CSV");
        OutputStream outputStream = null;
        try {
            StringBuffer csvContent = auditService.makeCsvReport(auditTraceInputBean);
            //set response to
            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.setHeader("Content-disposition", "inline; filename=AuditTrace_Report.csv");
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
    public void getAuditTraceBean(Model map) throws Exception {
        //set values to audit bean
        AuditTraceInputBean auditTraceInputBean = new AuditTraceInputBean();
        auditTraceInputBean.setSectionList(commonRepository.getSectionList());
        auditTraceInputBean.setPageList(commonRepository.getPageList());
        auditTraceInputBean.setTaskList(commonRepository.getTaskList());
        //set privileges
        this.applyUserPrivileges(auditTraceInputBean);
        //add values to model map
        map.addAttribute("audittrace", auditTraceInputBean);
    }

    private void applyUserPrivileges(AuditTraceInputBean inputBean) {
        List<Task> taskList = common.getUserTaskListByPage(PageVarList.AUDITTRACE_MGT_PAGE, sessionBean);
        inputBean.setVdownload(false);
        inputBean.setVdownload(false);
        if (taskList != null && !taskList.isEmpty()) {
            taskList.forEach(task -> {
                if (task.getTaskCode().equalsIgnoreCase(TaskVarList.DOWNLOAD_TASK)) {
                    inputBean.setVdownload(true);
                }
            });
        }
    }
}
