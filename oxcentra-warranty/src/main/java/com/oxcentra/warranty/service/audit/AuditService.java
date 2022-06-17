package com.oxcentra.warranty.service.audit;

import com.oxcentra.warranty.bean.audit.AuditTraceInputBean;
import com.oxcentra.warranty.bean.common.CommonBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.repository.audit.AuditRepository;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.repository.usermgt.page.PageRepository;
import com.oxcentra.warranty.repository.usermgt.section.SectionRepository;
import com.oxcentra.warranty.repository.usermgt.task.TaskRepository;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.common.ExcelCommon;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.SectionVarList;
import com.oxcentra.warranty.util.varlist.TaskVarList;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Scope("prototype")
public class AuditService {

    @Autowired
    AuditRepository auditRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    ExcelCommon excelCommon;

    @Autowired
    Common common;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    Audittrace audittrace;

    private final int auditTraceReportColumnCount = 11;
    private final int auditTraceReportHeaderRowCount = 13;

    public long getDataCount(AuditTraceInputBean auditInputBean) throws Exception {
        long count = 0;
        try {
            count = auditRepository.getDataCount(auditInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Audittrace> getAuditSearchResultList(AuditTraceInputBean auditInputBean) throws Exception {
        List<Audittrace> auditTraceList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_AUDIT_MGT);
            audittrace.setPage(PageVarList.AUDITTRACE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get audit trace search list.");
            //get sms outbox search list
            auditTraceList = auditRepository.getAuditSearchResultList(auditInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return auditTraceList;
    }

    public Audittrace getAudit(String id) throws Exception {
        Audittrace auditTrace;
        try {
            auditTrace = auditRepository.getAuditTrace(id);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return auditTrace;
    }

    public List<Audittrace> getAuditTraceSearchResultListForReport(AuditTraceInputBean auditTraceInputBean) throws Exception {
        List<Audittrace> auditTraceList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_AUDIT_MGT);
            audittrace.setPage(PageVarList.AUDITTRACE_MGT_PAGE);
            audittrace.setTask(TaskVarList.DOWNLOAD_TASK);
            audittrace.setDescription("Get audit trace search list.");
            //get sms outbox search list for report
            auditTraceList = auditRepository.getAuditTraceSearchResultListForReport(auditTraceInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return auditTraceList;
    }

    public Object generateExcelReport(HttpServletRequest request, AuditTraceInputBean auditInputBean) throws Exception {
        System.out.println("@@@@ -1");
        Object returnObject = null;
        long count = 0;
        try {
            //check the temporary file path and create file path
            String directory = request.getServletContext().getInitParameter("tmpreportpath");
            File file = new File(directory);
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
            //get count
            count = auditRepository.getDataCount(auditInputBean);
            if (count > 0) {
                long maxRow = Long.parseLong(request.getServletContext().getInitParameter("numberofrowsperexcel"));
                SXSSFWorkbook workbook = this.createExcelTopSection(auditInputBean);
                Sheet sheet = workbook.getSheetAt(0);

                int fileCount = 0;
                int currRow = auditTraceReportHeaderRowCount;
                currRow = this.createExcelTableHeaderSection(workbook, currRow);

                int selectRow = Integer.parseInt(request.getServletContext().getInitParameter("numberofselectrows"));
                long numberOfTimes = count / selectRow;
                if ((count % selectRow) > 0) {
                    numberOfTimes += 1;
                }

                int from = 0;
                int listrownumber = 1;
                for (int i = 0; i < numberOfTimes; i++) {
                    List<Audittrace> dataBeanList = auditRepository.getAuditExcelResults(auditInputBean);
                    if (dataBeanList.size() > 0) {
                        for (Audittrace audittrace : dataBeanList) {
                            if (currRow + 1 > maxRow) {
                                fileCount++;
                                this.writeTemporaryFile(workbook, fileCount, directory);
                                workbook = this.createExcelTopSection(auditInputBean);
                                sheet = workbook.getSheetAt(0);
                                currRow = auditTraceReportHeaderRowCount;
                                this.createExcelTableHeaderSection(workbook, currRow);
                            }
                            currRow = this.createExcelTableBodySection(workbook, audittrace, currRow, listrownumber);
                            listrownumber++;
                            if (currRow % 100 == 0) {
                                // retain 100 last rows and flush all others
                                ((SXSSFSheet) sheet).flushRows(100);
                            }
                        }
                    }
                    from = from + selectRow;
                }

                Date createdTime = commonRepository.getCurrentDate();
                this.createExcelBotomSection(workbook, currRow, count, createdTime);

                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
                sheet.autoSizeColumn(2);
                sheet.autoSizeColumn(3);
                sheet.autoSizeColumn(4);
                sheet.autoSizeColumn(5);
                sheet.autoSizeColumn(6);
                //sheet.autoSizeColumn(7);
                sheet.autoSizeColumn(8);
                sheet.autoSizeColumn(9);
                sheet.autoSizeColumn(10);

                returnObject = workbook;
            }
        } catch (Exception e) {
            throw e;
        }
        return returnObject;
    }

    private SXSSFWorkbook createExcelTopSection(AuditTraceInputBean auditInputBean) throws Exception {
        System.out.println("@@@@ 1");
        SXSSFWorkbook workbook = null;
        try {
            workbook = new SXSSFWorkbook(-1);
            Sheet sheet = workbook.createSheet("Audit_Report");

            CellStyle fontBoldedUnderlinedCell = ExcelCommon.getFontBoldedUnderlinedCell(workbook);

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("RDB BANK SMS SOLUTION");
            cell.setCellStyle(fontBoldedUnderlinedCell);

            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("Audit Report");
            cell.setCellStyle(fontBoldedUnderlinedCell);

            row = sheet.createRow(4);
            cell = row.createCell(0);
            cell.setCellValue("From Date");
            cell = row.createCell(1);
            cell.setCellValue(common.replaceEmptyorNullStringToALL(auditInputBean.getFromDate()));
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(5);
            cell = row.createCell(0);
            cell.setCellValue("To Date");
            cell = row.createCell(1);
            cell.setCellValue(common.replaceEmptyorNullStringToALL(auditInputBean.getToDate()));
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(6);
            cell = row.createCell(0);
            cell.setCellValue("Username");
            cell = row.createCell(1);
            cell.setCellValue(common.replaceEmptyorNullStringToALL(auditInputBean.getUserName()));
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(7);
            cell = row.createCell(0);
            cell.setCellValue("Section");
            cell = row.createCell(1);
            if (!auditInputBean.getSection().isEmpty() && auditInputBean.getSection() != null) {
                cell.setCellValue(common.replaceEmptyorNullStringToALL(sectionRepository.getSection(auditInputBean.getSection()).getDescription()));
            } else {
                cell.setCellValue("-ALL-");
            }
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(8);
            cell = row.createCell(0);
            cell.setCellValue("Page");
            cell = row.createCell(1);
            if (!auditInputBean.getPage().isEmpty() && auditInputBean.getPage() != null) {
                cell.setCellValue(common.replaceEmptyorNullStringToALL(pageRepository.getPage(auditInputBean.getPage()).getDescription()));
            } else {
                cell.setCellValue("-ALL-");
            }
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(9);
            cell = row.createCell(0);
            cell.setCellValue("Task");
            cell = row.createCell(1);
            if (!auditInputBean.getTask().isEmpty() && auditInputBean.getTask() != null) {
                cell.setCellValue(common.replaceEmptyorNullStringToALL(taskRepository.getTask(auditInputBean.getTask()).getDescription()));
            } else {
                cell.setCellValue("-ALL-");
            }
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(10);
            cell = row.createCell(0);
            cell.setCellValue("Description");
            cell = row.createCell(1);
            cell.setCellValue(common.replaceEmptyorNullStringToALL(auditInputBean.getDescription()));
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));
        } catch (Exception e) {
            throw e;
        }
        return workbook;
    }

    private int createExcelTableHeaderSection(SXSSFWorkbook workbook, int currrow) throws Exception {
        System.out.println("@@@@ 2");
        try {
            CellStyle columnHeaderCell = ExcelCommon.getColumnHeadeCell(workbook);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.createRow(currrow++);

            Cell cell = row.createCell(0);
            cell.setCellValue("ID");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(1);
            cell.setCellValue("Audit ID");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(2);
            cell.setCellValue("Username");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(3);
            cell.setCellValue("Description");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(4);
            cell.setCellValue("Section");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(5);
            cell.setCellValue("Page");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(6);
            cell.setCellValue("Task");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(7);
            cell.setCellValue("Remarks");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(8);
            cell.setCellValue("IP");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(9);
            cell.setCellValue("User Role");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(10);
            cell.setCellValue("Last Updated Date and Time");
            cell.setCellStyle(columnHeaderCell);
        } catch (Exception e) {
            throw e;
        }
        return currrow;
    }

    private void writeTemporaryFile(SXSSFWorkbook workbook, int fileCount, String directory) throws Exception {
        File file;
        FileOutputStream outputStream = null;
        try {
            file = new File(directory);
            if (!file.exists()) {
                file.mkdirs();
            }

            if (fileCount > 0) {
                file = new File(directory + File.separator + "AuditReport_" + fileCount + ".xlsx");
            } else {
                file = new File(directory + File.separator + "AuditReport.xlsx");
            }
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);

        } catch (IOException e) {
            throw e;
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    private int createExcelTableBodySection(SXSSFWorkbook workbook, Audittrace audittrace, int currrow, int rownumber) throws Exception {
        //System.out.println("@@@@ 3");
        try {
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle rowColumnCell = ExcelCommon.getRowColumnCell(workbook);
            Row row = sheet.createRow(currrow++);

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_LEFT);
            style.setBorderBottom(XSSFCellStyle.BORDER_THIN);

            Cell cell = row.createCell(0);
            cell.setCellValue(rownumber);
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(audittrace.getAuditid().toString());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(2);
            cell.setCellValue(audittrace.getLastupdateduser());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(3);
            cell.setCellValue(audittrace.getDescription());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(4);
            cell.setCellValue(audittrace.getSection());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(5);
            cell.setCellValue(audittrace.getPage());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(6);
            cell.setCellValue(audittrace.getTask());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(7);
            cell.setCellValue(audittrace.getRemarks());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(8);
            cell.setCellValue(audittrace.getIp());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(9);
            cell.setCellValue(audittrace.getUserrole());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(10);
            Date date = audittrace.getLastupdatedtime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = formatter.format(date);
            cell.setCellValue(strDate);
            cell.setCellStyle(rowColumnCell);
        } catch (Exception e) {
            throw e;
        }
        return currrow;
    }

    private void createExcelBotomSection(SXSSFWorkbook workbook, int currrow, long count, Date date) throws Exception {
        try {
            CellStyle fontBoldedCell = ExcelCommon.getFontBoldedCell(workbook);
            Sheet sheet = workbook.getSheetAt(0);

            currrow++;
            Row row = sheet.createRow(currrow++);
            Cell cell = row.createCell(0);
            cell.setCellValue("Summary");
            cell.setCellStyle(fontBoldedCell);

            row = sheet.createRow(currrow++);
            cell = row.createCell(0);
            cell.setCellValue("Total Record Count");
            cell = row.createCell(1);
            cell.setCellValue(count);
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(currrow++);
            cell = row.createCell(0);
            cell.setCellValue("Report Created Time");
            cell = row.createCell(1);

            if (date != null && !date.toString().isEmpty()) {
                cell.setCellValue(date.toString().substring(0, 19));
            } else {
                cell.setCellValue("--");
            }
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));
        } catch (Exception e) {
            throw e;
        }
    }

    public List<CommonBean> getPagesforSection(String sectionCode) throws Exception {
        List<CommonBean> assignedPageList;
        try {
            assignedPageList = auditRepository.getAssignedPages(sectionCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return assignedPageList;
    }

    public List<CommonBean> getTasksforPage(String pageCode) throws SQLException {
        List<CommonBean> assignedPageList;
        try {
            assignedPageList = auditRepository.getAssignedTasks(pageCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return assignedPageList;
    }

    public StringBuffer makeCsvReport(AuditTraceInputBean auditTraceInputBean) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            List<Audittrace> audittraceList = this.getAuditTraceSearchResultListForReport(auditTraceInputBean);
            if (!audittraceList.isEmpty()) {
                stringBuffer.append("ID");
                stringBuffer.append(',');

                stringBuffer.append("Audit ID");
                stringBuffer.append(',');

                stringBuffer.append("Username");
                stringBuffer.append(',');

                stringBuffer.append("Description");
                stringBuffer.append(',');

                stringBuffer.append("Section");
                stringBuffer.append(',');

                stringBuffer.append("Page");
                stringBuffer.append(',');

                stringBuffer.append("Task");
                stringBuffer.append(',');

                stringBuffer.append("Remarks");
                stringBuffer.append(',');

                stringBuffer.append("IP");
                stringBuffer.append(',');

                stringBuffer.append("User Role");
                stringBuffer.append(',');

                stringBuffer.append("Last Updated Date And Time");
                stringBuffer.append('\n');

                int i = 0;
                for (Audittrace audittrace : audittraceList) {
                    i++;
                    try {
                        stringBuffer.append(i);
                        stringBuffer.append(",");
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (audittrace.getAuditid() != null) {
                            stringBuffer.append(audittrace.getAuditid());
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (audittrace.getLastupdateduser() != null && !audittrace.getLastupdateduser().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(audittrace.getLastupdateduser()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (audittrace.getDescription() != null && !audittrace.getDescription().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(audittrace.getDescription()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (audittrace.getSection() != null && !audittrace.getSection().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(audittrace.getSection()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (audittrace.getPage() != null && !audittrace.getPage().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(audittrace.getPage()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (audittrace.getTask() != null && !audittrace.getTask().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(audittrace.getTask()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (audittrace.getRemarks() != null && !audittrace.getRemarks().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(audittrace.getRemarks()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (audittrace.getIp() != null && !audittrace.getIp().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(audittrace.getIp()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (audittrace.getUserrole() != null && !audittrace.getUserrole().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(audittrace.getUserrole()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (audittrace.getLastupdatedtime() != null) {
                            String createdDateTime = common.formatDateToStringCsvFile(audittrace.getLastupdatedtime());
                            stringBuffer.append(common.appendSpecialCharacterToCsv(createdDateTime));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    stringBuffer.append('\n');

                }
                stringBuffer.append('\n');

                stringBuffer.append("From Date : ");
                stringBuffer.append(auditTraceInputBean.getFromDate());
                stringBuffer.append('\n');

                stringBuffer.append("To Date : ");
                stringBuffer.append(auditTraceInputBean.getToDate());
                stringBuffer.append('\n');

                stringBuffer.append("Username : ");
                stringBuffer.append(common.replaceEmptyorNullStringToALL(auditTraceInputBean.getUserName()));
                stringBuffer.append('\n');

                stringBuffer.append("Section : ");
                if (!auditTraceInputBean.getSection().isEmpty() && auditTraceInputBean.getSection() != null) {
                    stringBuffer.append(common.replaceEmptyorNullStringToALL(sectionRepository.getSection(auditTraceInputBean.getSection()).getDescription()));
                } else {
                    stringBuffer.append("-ALL-");
                }
                stringBuffer.append('\n');

                stringBuffer.append("Page : ");
                if (!auditTraceInputBean.getPage().isEmpty() && auditTraceInputBean.getPage() != null) {
                    stringBuffer.append(common.replaceEmptyorNullStringToALL(pageRepository.getPage(auditTraceInputBean.getPage()).getDescription()));
                } else {
                    stringBuffer.append("-ALL-");
                }
                stringBuffer.append('\n');

                stringBuffer.append("Task : ");
                if (!auditTraceInputBean.getTask().isEmpty() && auditTraceInputBean.getTask() != null) {
                    stringBuffer.append(common.replaceEmptyorNullStringToALL(taskRepository.getTask(auditTraceInputBean.getTask()).getDescription()));
                } else {
                    stringBuffer.append("-ALL-");
                }
                stringBuffer.append('\n');

                stringBuffer.append("Description : ");
                stringBuffer.append(common.replaceEmptyorNullStringToALL(auditTraceInputBean.getDescription()));
                stringBuffer.append('\n');

                stringBuffer.append('\n');
            }
        } catch (Exception e) {
            throw e;
        }
        return stringBuffer;
    }
}
