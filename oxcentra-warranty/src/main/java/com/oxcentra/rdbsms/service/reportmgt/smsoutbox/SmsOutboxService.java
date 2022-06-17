package com.oxcentra.rdbsms.service.reportmgt.smsoutbox;

import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.smsoutbox.SmsOutboxReportInputBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.smsoutbox.SmsOutbox;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.reportmgt.smsoutbox.SmsOutboxRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.category.CategoryRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.channel.ChannelRepository;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.common.ExcelCommon;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.SectionVarList;
import com.oxcentra.rdbsms.util.varlist.TaskVarList;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Scope("prototype")
public class SmsOutboxService {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    Common common;

    @Autowired
    SmsOutboxRepository smsOutboxRepository;

    @Autowired
    Audittrace audittrace;

    @Autowired
    MessageSource messageSource;

    private final int smsoutboxReportHeaderRowCount = 14;

    public long getCount(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        long count = 0;
        try {
            count = smsOutboxRepository.getDataCount(smsOutboxReportInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<SmsOutbox> getSmsOutBoxSearchResultList(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        List<SmsOutbox> smsOutBoxList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SMSOUTBOX_REPORT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get sms outbox search list.");
            //get sms outbox search list
            smsOutBoxList = smsOutboxRepository.getSmsOutBoxSearchResultList(smsOutboxReportInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return smsOutBoxList;
    }

    public List<SmsOutbox> getSmsOutBoxSearchResultListForReport(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        List<SmsOutbox> smsOutBoxList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SMSOUTBOX_REPORT_PAGE);
            audittrace.setTask(TaskVarList.DOWNLOAD_TASK);
            audittrace.setDescription("Get sms outbox search list.");
            //get sms outbox search list for report
            smsOutBoxList = smsOutboxRepository.getSmsOutBoxSearchResultListForReport(smsOutboxReportInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return smsOutBoxList;
    }

    public StringBuffer makeCsvReport(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            List<SmsOutbox> smsOutboxList = this.getSmsOutBoxSearchResultListForReport(smsOutboxReportInputBean);
            if (!smsOutboxList.isEmpty()) {
                stringBuffer.append("ID");
                stringBuffer.append(',');

                stringBuffer.append("Reference No");
                stringBuffer.append(',');

                stringBuffer.append("Mobile Number");
                stringBuffer.append(',');

                stringBuffer.append("Message");
                stringBuffer.append(',');

                stringBuffer.append("Status");
                stringBuffer.append(',');

                stringBuffer.append("Delivery Status");
                stringBuffer.append(',');

                stringBuffer.append("Telco Service Provider");
                stringBuffer.append(',');

                stringBuffer.append("Channel");
                stringBuffer.append(',');

                stringBuffer.append("SMS Category");
                stringBuffer.append(',');

                stringBuffer.append("Transaction Type");
                stringBuffer.append(',');

                stringBuffer.append("Created Date And Time");
                stringBuffer.append('\n');

                for (SmsOutbox smsOutbox : smsOutboxList) {
                    try {
                        if (smsOutbox.getId() != 0) {
                            stringBuffer.append(smsOutbox.getId());
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
                        if (smsOutbox.getReferenceNo() != null && !smsOutbox.getReferenceNo().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(smsOutbox.getReferenceNo()));
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
                        if (smsOutbox.getMobileNumber() != null && !smsOutbox.getMobileNumber().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(smsOutbox.getMobileNumber()));
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
                        if (smsOutbox.getMessage() != null && !smsOutbox.getMessage().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(smsOutbox.getMessage()));
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
                        if (smsOutbox.getStatus() != null && !smsOutbox.getStatus().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(smsOutbox.getStatus()));
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
                        if (smsOutbox.getDeleteStatus() != null && !smsOutbox.getDeleteStatus().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(smsOutbox.getDeleteStatus()));
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
                        if (smsOutbox.getTelco() != null && !smsOutbox.getTelco().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(smsOutbox.getTelco()));
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
                        if (smsOutbox.getChannel() != null && !smsOutbox.getChannel().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(smsOutbox.getChannel()));
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
                        if (smsOutbox.getCategory() != null && !smsOutbox.getCategory().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(smsOutbox.getCategory()));
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
                        if (smsOutbox.getTrnType() != null && !smsOutbox.getTrnType().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(smsOutbox.getTrnType()));
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
                        if (smsOutbox.getCreatedTime() != null) {
                            String createdDateTime = common.formatDateToStringCsvFile(smsOutbox.getCreatedTime());
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
                stringBuffer.append(smsOutboxReportInputBean.getFromDate());
                stringBuffer.append('\n');

                stringBuffer.append("To Date : ");
                stringBuffer.append(smsOutboxReportInputBean.getToDate());
                stringBuffer.append('\n');

                stringBuffer.append("Telco : ");
                if (!smsOutboxReportInputBean.getTelco().isEmpty() && smsOutboxReportInputBean.getTelco() != null) {
                    stringBuffer.append(common.replaceEmptyorNullStringToALL(commonRepository.getTelco(smsOutboxReportInputBean.getTelco()).getDescription()));
                } else {
                    stringBuffer.append("-ALL-");
                }

                stringBuffer.append('\n');

                stringBuffer.append("SMS Channel : ");
                if (!smsOutboxReportInputBean.getChannel().isEmpty() && smsOutboxReportInputBean.getChannel() != null) {
                    stringBuffer.append(common.replaceEmptyorNullStringToALL(channelRepository.getChannel(smsOutboxReportInputBean.getChannel()).getDescription()));
                } else {
                    stringBuffer.append("-ALL-");
                }

                stringBuffer.append('\n');

                stringBuffer.append("SMS Category : ");
                if (!smsOutboxReportInputBean.getCategory().isEmpty() && smsOutboxReportInputBean.getCategory() != null) {
                    stringBuffer.append(common.replaceEmptyorNullStringToALL(categoryRepository.getCategory(smsOutboxReportInputBean.getCategory()).getDescription()));
                } else {
                    stringBuffer.append("-ALL-");
                }
                stringBuffer.append('\n');

                stringBuffer.append("Delivery Status : ");
                if (!smsOutboxReportInputBean.getDelstatus().isEmpty() && smsOutboxReportInputBean.getDelstatus() != null) {
                    stringBuffer.append(common.replaceEmptyorNullStringToALL(commonRepository.getDeliveryStatus(smsOutboxReportInputBean.getDelstatus()).getDescription()));
                } else {
                    stringBuffer.append("-ALL-");
                }

                stringBuffer.append('\n');

                stringBuffer.append("Mobile Number : ");
                stringBuffer.append(common.replaceEmptyorNullStringToALL(smsOutboxReportInputBean.getMobileno()));

                stringBuffer.append('\n');

                stringBuffer.append("Transaction Type : ");
                if (!smsOutboxReportInputBean.getTxnType().isEmpty() && smsOutboxReportInputBean.getTxnType() != null) {
                    stringBuffer.append(common.replaceEmptyorNullStringToALL(commonRepository.getTxnType(smsOutboxReportInputBean.getTxnType()).getDescription()));
                } else {
                    stringBuffer.append("-ALL-");
                }

                stringBuffer.append('\n');

            }
        } catch (Exception e) {
            throw e;
        }
        return stringBuffer;
    }

    public Object generateExcelReport(HttpServletRequest request, SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
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
            count = smsOutboxRepository.getDataCount(smsOutboxReportInputBean);
            if (count > 0) {
                long maxRow = Long.parseLong(request.getServletContext().getInitParameter("numberofrowsperexcel"));
                SXSSFWorkbook workbook = this.createExcelTopSection(smsOutboxReportInputBean);
                Sheet sheet = workbook.getSheetAt(0);

                int fileCount = 0;
                int currRow = smsoutboxReportHeaderRowCount;
                currRow = this.createExcelTableHeaderSection(workbook, currRow);

                int selectRow = Integer.parseInt(request.getServletContext().getInitParameter("numberofselectrows"));
                long numberOfTimes = count / selectRow;
                if ((count % selectRow) > 0) {
                    numberOfTimes += 1;
                }

                int from = 0;
                int listrownumber = 1;
                for (int i = 0; i < numberOfTimes; i++) {
                    List<SmsOutbox> dataBeanList = smsOutboxRepository.getSmsOutBoxSearchResultListForReport(smsOutboxReportInputBean);
                    if (dataBeanList.size() > 0) {
                        for (SmsOutbox smsOutbox : dataBeanList) {
                            if (currRow + 1 > maxRow) {
                                fileCount++;
                                this.writeTemporaryFile(workbook, fileCount, directory);
                                workbook = this.createExcelTopSection(smsOutboxReportInputBean);
                                sheet = workbook.getSheetAt(0);
                                currRow = smsoutboxReportHeaderRowCount;
                                this.createExcelTableHeaderSection(workbook, currRow);
                            }
                            currRow = this.createExcelTableBodySection(workbook, smsOutbox, currRow, listrownumber);
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
//                sheet.autoSizeColumn(1);
//                sheet.autoSizeColumn(2);
//                sheet.autoSizeColumn(3);
//                sheet.autoSizeColumn(4);
//                sheet.autoSizeColumn(5);
//                sheet.autoSizeColumn(6);
//                sheet.autoSizeColumn(7);
//                sheet.autoSizeColumn(8);
//                //sheet.autoSizeColumn(9);
//                sheet.autoSizeColumn(10);

                returnObject = workbook;
            }
        } catch (Exception e) {
            throw e;
        }
        return returnObject;
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

    private int createExcelTableBodySection(SXSSFWorkbook workbook, SmsOutbox smsOutbox, int currrow, int rownumber) throws Exception {
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
            cell.setCellValue(smsOutbox.getReferenceNo());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(2);
            cell.setCellValue(smsOutbox.getMobileNumber());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(3);
            cell.setCellValue(smsOutbox.getMessage());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(4);
            cell.setCellValue(smsOutbox.getStatus());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(5);
            cell.setCellValue(smsOutbox.getDeleteStatus());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(6);
            cell.setCellValue(smsOutbox.getTelco());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(7);
            cell.setCellValue(smsOutbox.getChannel());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(8);
            cell.setCellValue(smsOutbox.getCategory());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(9);
            cell.setCellValue(smsOutbox.getTrnType());
            cell.setCellStyle(rowColumnCell);

            cell = row.createCell(10);
            Date date = smsOutbox.getCreatedTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = formatter.format(date);
            cell.setCellValue(strDate);
            cell.setCellStyle(rowColumnCell);
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
                file = new File(directory + File.separator + "SmsOutBoxReport_" + fileCount + ".xlsx");
            } else {
                file = new File(directory + File.separator + "SmsOutBoxReport.xlsx");
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
            cell.setCellValue("Reference No");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(2);
            cell.setCellValue("Mobile Number");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(3);
            cell.setCellValue("Message");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(4);
            cell.setCellValue("Status");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(5);
            cell.setCellValue("Delivery Status");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(6);
            cell.setCellValue("Telco Service Provider");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(7);
            cell.setCellValue("Channel");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(8);
            cell.setCellValue("SMS Category");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(9);
            cell.setCellValue("Transaction Type");
            cell.setCellStyle(columnHeaderCell);

            cell = row.createCell(10);
            cell.setCellValue("Created Date And Time");
            cell.setCellStyle(columnHeaderCell);
        } catch (Exception e) {
            throw e;
        }
        return currrow;
    }

    private SXSSFWorkbook createExcelTopSection(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        System.out.println("@@@@ 1");
        SXSSFWorkbook workbook = null;
        try {
            workbook = new SXSSFWorkbook(-1);
            Sheet sheet = workbook.createSheet("SMS_OUTBOX_REPORT");

            CellStyle fontBoldedUnderlinedCell = ExcelCommon.getFontBoldedUnderlinedCell(workbook);

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("RDB BANK SMS SOLUTION");
            cell.setCellStyle(fontBoldedUnderlinedCell);

            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("SMS OUTBOX REPORT");
            cell.setCellStyle(fontBoldedUnderlinedCell);

            row = sheet.createRow(4);
            cell = row.createCell(0);
            cell.setCellValue("From Date");
            cell = row.createCell(1);
            cell.setCellValue(common.replaceEmptyorNullStringToALL(smsOutboxReportInputBean.getFromDate()));
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(5);
            cell = row.createCell(0);
            cell.setCellValue("To Date");
            cell = row.createCell(1);
            cell.setCellValue(common.replaceEmptyorNullStringToALL(smsOutboxReportInputBean.getToDate()));
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(6);
            cell = row.createCell(0);
            cell.setCellValue("Telco");
            cell = row.createCell(1);
            cell.setCellValue(common.replaceEmptyorNullStringToALL(smsOutboxReportInputBean.getTelco()));
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(7);
            cell = row.createCell(0);
            cell.setCellValue("SMS Channel");
            cell = row.createCell(1);
            if (!smsOutboxReportInputBean.getChannel().isEmpty() && smsOutboxReportInputBean.getChannel() != null) {
                cell.setCellValue(common.replaceEmptyorNullStringToALL(channelRepository.getChannel(smsOutboxReportInputBean.getChannel()).getDescription()));
            } else {
                cell.setCellValue("-ALL-");
            }
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(8);
            cell = row.createCell(0);
            cell.setCellValue("SMS Category");
            cell = row.createCell(1);
            if (!smsOutboxReportInputBean.getCategory().isEmpty() && smsOutboxReportInputBean.getCategory() != null) {
                cell.setCellValue(common.replaceEmptyorNullStringToALL(categoryRepository.getCategory(smsOutboxReportInputBean.getCategory()).getDescription()));
            } else {
                cell.setCellValue("-ALL-");
            }
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(9);
            cell = row.createCell(0);
            cell.setCellValue("Delivery Status");
            cell = row.createCell(1);
            if (!smsOutboxReportInputBean.getDelstatus().isEmpty() && smsOutboxReportInputBean.getDelstatus() != null) {
                cell.setCellValue(common.replaceEmptyorNullStringToALL(commonRepository.getDeliveryStatus(smsOutboxReportInputBean.getDelstatus()).getDescription()));
            } else {
                cell.setCellValue("-ALL-");
            }
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(10);
            cell = row.createCell(0);
            cell.setCellValue("Mobile Number");
            cell = row.createCell(1);
            cell.setCellValue(common.replaceEmptyorNullStringToALL(smsOutboxReportInputBean.getMobileno()));
            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

            row = sheet.createRow(11);
            cell = row.createCell(0);
            cell.setCellValue("Transaction Type");
            cell = row.createCell(1);
            if (!smsOutboxReportInputBean.getTxnType().isEmpty() && smsOutboxReportInputBean.getTxnType() != null) {
                cell.setCellValue(common.replaceEmptyorNullStringToALL(commonRepository.getTxnType(smsOutboxReportInputBean.getTxnType()).getDescription()));
            } else {
                cell.setCellValue("-ALL-");
            }

            cell.setCellStyle(ExcelCommon.getAligneCell(workbook, null, XSSFCellStyle.ALIGN_RIGHT));

        } catch (Exception e) {
            throw e;
        }
        return workbook;
    }
}
