package com.oxcentra.rdbsms.service.reportmgt.smsoutboxsum;

import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.smsoutbox.SmsOutboxReportInputBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.smsoutboxsum.SmsOutboxsum;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.reportmgt.SmsOutboxsum.SmsOutboxsumRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.category.CategoryRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.channel.ChannelRepository;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.SectionVarList;
import com.oxcentra.rdbsms.util.varlist.TaskVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class SmsOutboxsumService {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    Common common;

    @Autowired
    SmsOutboxsumRepository smsOutboxsumRepository;

    @Autowired
    Audittrace audittrace;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CategoryRepository categoryRepository;

    public long getCount(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        long count = 0;
        try {
            count = smsOutboxsumRepository.getDataCount(smsOutboxReportInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<SmsOutboxsum> getSmsOutBoxSumSearchResultListForReport(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        List<SmsOutboxsum> smsOutBoxList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SMSOUTBOX_SUM_REPORT_PAGE);
            audittrace.setTask(TaskVarList.DOWNLOAD_TASK);
            audittrace.setDescription("Get sms outbox search list.");
            //get sms outbox search list for report
            smsOutBoxList = smsOutboxsumRepository.getSmsOutBoxSumSearchResultListForReport(smsOutboxReportInputBean);
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
            List<SmsOutboxsum> smsOutboxList = this.getSmsOutBoxSumSearchResultListForReport(smsOutboxReportInputBean);
            if (!smsOutboxList.isEmpty()) {
                stringBuffer.append("Sent to Telco Count");
                stringBuffer.append(',');

                stringBuffer.append("Delivered Count");
                stringBuffer.append(',');

                stringBuffer.append("Expired Count");
                stringBuffer.append(',');

                stringBuffer.append("Rejected Count");
                stringBuffer.append(',');

                stringBuffer.append("Total No. of SMSs");
                stringBuffer.append('\n');

                for (SmsOutboxsum smsOutbox : smsOutboxList) {
                    try {
                        if (smsOutbox.getTelcoCount() != null) {
                            stringBuffer.append(smsOutbox.getTelcoCount());
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
                        if (smsOutbox.getDelCount() != null) {
                            stringBuffer.append(smsOutbox.getDelCount());
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
                        if (smsOutbox.getExpireCount() != null) {
                            stringBuffer.append(smsOutbox.getExpireCount());
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
                        if (smsOutbox.getRejCount() != null) {
                            stringBuffer.append(smsOutbox.getRejCount());
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
                        if (smsOutbox.getTotalSMS() != null) {
                            stringBuffer.append(smsOutbox.getTotalSMS());
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
                    stringBuffer.append(common.replaceEmptyorNullStringToALL(smsOutboxReportInputBean.getTelco()));
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

                stringBuffer.append("Status : ");
                if (!smsOutboxReportInputBean.getStatus().isEmpty() && smsOutboxReportInputBean.getStatus() != null) {
                    stringBuffer.append(common.replaceEmptyorNullStringToALL(commonRepository.getStatus(smsOutboxReportInputBean.getStatus()).getDescription()));
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

    public List<SmsOutboxsum> getSmsOutBoxSumSearchResultList(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        List<SmsOutboxsum> smsOutboxsumList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.SMSOUTBOX_SUM_REPORT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get sms outbox sum search list.");
            //get sms outbox search list
            smsOutboxsumList = smsOutboxsumRepository.getSmsOutBoxSumSearchResultList(smsOutboxReportInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return smsOutboxsumList;
    }
}
