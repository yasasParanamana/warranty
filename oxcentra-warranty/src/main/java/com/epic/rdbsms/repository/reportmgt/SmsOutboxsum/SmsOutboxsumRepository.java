package com.epic.rdbsms.repository.reportmgt.SmsOutboxsum;

import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.smsoutbox.SmsOutboxReportInputBean;
import com.epic.rdbsms.mapping.smsoutboxsum.SmsOutboxsum;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.common.CommonService;
import com.epic.rdbsms.util.varlist.CommonVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
@Scope("prototype")
public class SmsOutboxsumRepository {

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from SMSOUTBOX so where ";
    @Autowired
    SessionBean sessionBean;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    CommonService commonService;
    @Autowired
    CommonRepository commonRepository;
    @Autowired
    CommonVarList commonVarList;

    @Transactional(readOnly = true)
    public long getDataCount(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(smsOutboxReportInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<SmsOutboxsum> getSmsOutBoxSumSearchResultListForReport(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        List<SmsOutboxsum> smsOutboxList = new ArrayList<>();
        SmsOutboxsum smsOutboxsum = new SmsOutboxsum();
        try {
            StringBuilder dynamicClauseTelcoCount = this.setDynamicClauseTelcoCount(smsOutboxReportInputBean, new StringBuilder());

            String sqlTelcoCount = "" +
                    " select " +
                    " count(so.id) as telcocount from SMSOUTBOX so " +
                    " where " + dynamicClauseTelcoCount.toString();

            Long telcoCount = jdbcTemplate.queryForObject(sqlTelcoCount, Long.class);
            smsOutboxsum.setTelcoCount(telcoCount);

            StringBuilder dynamicClauseDeliveredCount = this.setDynamicClauseDeliveredCount(smsOutboxReportInputBean, new StringBuilder());

            String sqlDeliveredCount = "" +
                    " select " +
                    " count(so.id) as delcount from SMSOUTBOX so " +
                    " where " + dynamicClauseDeliveredCount.toString();

            Long deliveredCount = jdbcTemplate.queryForObject(sqlDeliveredCount, Long.class);
            smsOutboxsum.setDelCount(deliveredCount);


            /*StringBuilder dynamicClauseErrorCount = this.setDynamicClauseErrorCount(smsOutboxReportInputBean, new StringBuilder());

            String sqlErrorCount = "" +
                    " select " +
                    " count(so.id) as errorcount from SMSOUTBOX so " +
                    " where " + dynamicClauseErrorCount.toString();

            Long errorCount = jdbcTemplate.queryForObject(sqlErrorCount, Long.class);
            smsOutboxsum.setErrCount(errorCount);*/

            StringBuilder dynamicClauseExpiredCount = this.setDynamicClauseExpiredCount(smsOutboxReportInputBean, new StringBuilder());

            String sqlExpiredCount = "" +
                    " select " +
                    " count(so.id) as expcount from SMSOUTBOX so " +
                    " where " + dynamicClauseExpiredCount.toString();

            Long expiredCount = jdbcTemplate.queryForObject(sqlExpiredCount, Long.class);
            smsOutboxsum.setExpireCount(expiredCount);


            /*StringBuilder dynamicClauseProcessedCount = this.setDynamicClause(smsOutboxReportInputBean, new StringBuilder());

            String sqlProcessedCount = "" +
                    " select " +
                    " count(so.id) as expcount from SMSOUTBOX so " +
                    " where " + dynamicClauseProcessedCount.toString();

            Long processedCount = jdbcTemplate.queryForObject(sqlProcessedCount, Long.class);
            smsOutboxsum.setProcCount(processedCount);*/
            StringBuilder dynamicClauseRejectCount = this.setDynamicClauseRejectCount(smsOutboxReportInputBean, new StringBuilder());

            String sqlRejectCount = "" +
                    " select " +
                    " count(so.id) as expcount from SMSOUTBOX so " +
                    " where " + dynamicClauseRejectCount.toString();

            Long rejectCount = jdbcTemplate.queryForObject(sqlRejectCount, Long.class);
            smsOutboxsum.setRejCount(rejectCount);


            StringBuilder dynamicClauseTotalCount = this.setDynamicClause(smsOutboxReportInputBean, new StringBuilder());

            String sqlTotalCount = "" +
                    " select " +
                    " sum(so.partcount) as expcount from SMSOUTBOX so " +
                    " where " + dynamicClauseTotalCount.toString();

            Long totalCount = jdbcTemplate.queryForObject(sqlTotalCount, Long.class);
            smsOutboxsum.setTotalSMS(totalCount);

            smsOutboxList.add(smsOutboxsum);
        } catch (EmptyResultDataAccessException ex) {
            return smsOutboxList;
        } catch (Exception e) {
            throw e;
        }
        return smsOutboxList;
    }

    private StringBuilder setDynamicClause(SmsOutboxReportInputBean smsOutboxReportInputBean, StringBuilder dynamicClause) throws Exception {
        dynamicClause.append(" 1=1 ");
        try {
            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat formatter = new SimpleDateFormat("YYYY/MM/dd 00:00:00");

            if (smsOutboxReportInputBean.getFromDate() != null && !smsOutboxReportInputBean.getFromDate().isEmpty()) {
                String fromDate = smsOutboxReportInputBean.getFromDate();
                Date fDate = parser.parse(fromDate);
                //format the from date
                String formattedFromDate = formatter.format(fDate);
                //add to dynamic clause
                dynamicClause.append(" and so.createdtime >='").append(formattedFromDate).append("'");
            }

            if (smsOutboxReportInputBean.getToDate() != null && !smsOutboxReportInputBean.getToDate().isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(smsOutboxReportInputBean.getToDate()));
                //add one to calender instance
                calendar.add(Calendar.DATE, 1);
                //format the from date
                String toDate = simpleDateFormat.format(calendar.getTime());
                Date tDate = parser.parse(toDate);
                String formattedToDate = formatter.format(tDate);
                //add to dynamic clause
                dynamicClause.append(" and so.createdtime <'").append(formattedToDate).append("'");
            }

            if (smsOutboxReportInputBean.getTelco() != null && !smsOutboxReportInputBean.getTelco().isEmpty()) {
                dynamicClause.append("and so.telco = '").append(smsOutboxReportInputBean.getTelco()).append("'");
            }

            if (smsOutboxReportInputBean.getChannel() != null && !smsOutboxReportInputBean.getChannel().isEmpty()) {
                dynamicClause.append("and so.channel = '").append(smsOutboxReportInputBean.getChannel()).append("'");
            }

            if (smsOutboxReportInputBean.getCategory() != null && !smsOutboxReportInputBean.getCategory().isEmpty()) {
                dynamicClause.append("and so.category = '").append(smsOutboxReportInputBean.getCategory()).append("'");
            }

            if (smsOutboxReportInputBean.getMobileno() != null && !smsOutboxReportInputBean.getMobileno().isEmpty()) {
                dynamicClause.append("and lower(so.mobile) like lower('%").append(smsOutboxReportInputBean.getMobileno()).append("%')");
            }

            if (smsOutboxReportInputBean.getTxnType() != null && !smsOutboxReportInputBean.getTxnType().isEmpty()) {
                dynamicClause.append("and so.TXN_TYPE = '").append(smsOutboxReportInputBean.getTxnType()).append("'");
            }

            if (smsOutboxReportInputBean.getStatus() != null && !smsOutboxReportInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and so.status = '").append(smsOutboxReportInputBean.getStatus()).append("'");
            }

        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    @Transactional(readOnly = true)
    public List<SmsOutboxsum> getSmsOutBoxSumSearchResultList(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        List<SmsOutboxsum> smsOutboxList = new ArrayList<>();
        SmsOutboxsum smsOutboxsum = new SmsOutboxsum();
        try {
            StringBuilder dynamicClauseTelcoCount = this.setDynamicClauseTelcoCount(smsOutboxReportInputBean, new StringBuilder());

            String sqlTelcoCount = "" +
                    " select " +
                    " count(so.id) as telcocount from SMSOUTBOX so " +
                    " where " + dynamicClauseTelcoCount.toString();

            Long telcoCount = jdbcTemplate.queryForObject(sqlTelcoCount, Long.class);
            smsOutboxsum.setTelcoCount(telcoCount);

            StringBuilder dynamicClauseDeliveredCount = this.setDynamicClauseDeliveredCount(smsOutboxReportInputBean, new StringBuilder());

            String sqlDeliveredCount = "" +
                    " select " +
                    " count(so.id) as delcount from SMSOUTBOX so " +
                    " where " + dynamicClauseDeliveredCount.toString();

            Long deliveredCount = jdbcTemplate.queryForObject(sqlDeliveredCount, Long.class);
            smsOutboxsum.setDelCount(deliveredCount);


            /*StringBuilder dynamicClauseErrorCount = this.setDynamicClauseErrorCount(smsOutboxReportInputBean, new StringBuilder());

            String sqlErrorCount = "" +
                    " select " +
                    " count(so.id) as errorcount from SMSOUTBOX so " +
                    " where " + dynamicClauseErrorCount.toString();

            Long errorCount = jdbcTemplate.queryForObject(sqlErrorCount, Long.class);
            smsOutboxsum.setErrCount(errorCount);*/

            StringBuilder dynamicClauseExpiredCount = this.setDynamicClauseExpiredCount(smsOutboxReportInputBean, new StringBuilder());

            String sqlExpiredCount = "" +
                    " select " +
                    " count(so.id) as expcount from SMSOUTBOX so " +
                    " where " + dynamicClauseExpiredCount.toString();

            Long expiredCount = jdbcTemplate.queryForObject(sqlExpiredCount, Long.class);
            smsOutboxsum.setExpireCount(expiredCount);


            /*StringBuilder dynamicClauseProcessedCount = this.setDynamicClause(smsOutboxReportInputBean, new StringBuilder());

            String sqlProcessedCount = "" +
                    " select " +
                    " count(so.id) as expcount from SMSOUTBOX so " +
                    " where " + dynamicClauseProcessedCount.toString();

            Long processedCount = jdbcTemplate.queryForObject(sqlProcessedCount, Long.class);
            smsOutboxsum.setProcCount(processedCount);*/


            StringBuilder dynamicClauseRejectCount = this.setDynamicClauseRejectCount(smsOutboxReportInputBean, new StringBuilder());

            String sqlRejectCount = "" +
                    " select " +
                    " count(so.id) as expcount from SMSOUTBOX so " +
                    " where " + dynamicClauseRejectCount.toString();

            Long rejectCount = jdbcTemplate.queryForObject(sqlRejectCount, Long.class);
            smsOutboxsum.setRejCount(rejectCount);


            StringBuilder dynamicClauseTotalCount = this.setDynamicClause(smsOutboxReportInputBean, new StringBuilder());

            String sqlTotalCount = "" +
                    " select " +
                    " sum(so.partcount) as expcount from SMSOUTBOX so " +
                    " where " + dynamicClauseTotalCount.toString();

            Long totalCount = jdbcTemplate.queryForObject(sqlTotalCount, Long.class);
            smsOutboxsum.setTotalSMS(totalCount);

            smsOutboxList.add(smsOutboxsum);
        } catch (EmptyResultDataAccessException ex) {
            return smsOutboxList;
        } catch (Exception e) {
            throw e;
        }
        return smsOutboxList;
    }

    private StringBuilder setDynamicClauseRejectCount(SmsOutboxReportInputBean smsOutboxReportInputBean, StringBuilder dynamicClause) throws Exception {
        dynamicClause.append(" 1=1 ");
        try {
            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat formatter = new SimpleDateFormat("YYYY/MM/dd 00:00:00");

            if (smsOutboxReportInputBean.getFromDate() != null && !smsOutboxReportInputBean.getFromDate().isEmpty()) {
                String fromDate = smsOutboxReportInputBean.getFromDate();
                Date fDate = parser.parse(fromDate);
                //format the from date
                String formattedFromDate = formatter.format(fDate);
                //add to dynamic clause
                dynamicClause.append(" and so.createdtime >='").append(formattedFromDate).append("'");
            }

            if (smsOutboxReportInputBean.getToDate() != null && !smsOutboxReportInputBean.getToDate().isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(smsOutboxReportInputBean.getToDate()));
                //add one to calender instance
                calendar.add(Calendar.DATE, 1);
                //format the from date
                String toDate = simpleDateFormat.format(calendar.getTime());
                Date tDate = parser.parse(toDate);
                String formattedToDate = formatter.format(tDate);
                //add to dynamic clause
                dynamicClause.append(" and so.createdtime <'").append(formattedToDate).append("'");
            }

            if (smsOutboxReportInputBean.getTelco() != null && !smsOutboxReportInputBean.getTelco().isEmpty()) {
                dynamicClause.append("and so.telco = '").append(smsOutboxReportInputBean.getTelco()).append("'");
            }

            if (smsOutboxReportInputBean.getChannel() != null && !smsOutboxReportInputBean.getChannel().isEmpty()) {
                dynamicClause.append("and so.channel = '").append(smsOutboxReportInputBean.getChannel()).append("'");
            }

            if (smsOutboxReportInputBean.getCategory() != null && !smsOutboxReportInputBean.getCategory().isEmpty()) {
                dynamicClause.append("and so.category = '").append(smsOutboxReportInputBean.getCategory()).append("'");
            }

            if (smsOutboxReportInputBean.getMobileno() != null && !smsOutboxReportInputBean.getMobileno().isEmpty()) {
                dynamicClause.append("and lower(so.mobile) like lower('%").append(smsOutboxReportInputBean.getMobileno()).append("%')");
            }

            if (smsOutboxReportInputBean.getTxnType() != null && !smsOutboxReportInputBean.getTxnType().isEmpty()) {
                dynamicClause.append("and so.TXN_TYPE = '").append(smsOutboxReportInputBean.getTxnType()).append("'");
            }

            if (smsOutboxReportInputBean.getStatus() != null && !smsOutboxReportInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and so.status = '").append(smsOutboxReportInputBean.getStatus()).append("'");
            }

            dynamicClause.append("and so.delstatus = 'REJECTD'");

        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseExpiredCount(SmsOutboxReportInputBean smsOutboxReportInputBean, StringBuilder dynamicClause) throws Exception {
        dynamicClause.append(" 1=1 ");
        try {
            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat formatter = new SimpleDateFormat("YYYY/MM/dd 00:00:00");

            if (smsOutboxReportInputBean.getFromDate() != null && !smsOutboxReportInputBean.getFromDate().isEmpty()) {
                String fromDate = smsOutboxReportInputBean.getFromDate();
                Date fDate = parser.parse(fromDate);
                //format the from date
                String formattedFromDate = formatter.format(fDate);
                //add to dynamic clause
                dynamicClause.append(" and so.createdtime >='").append(formattedFromDate).append("'");
            }

            if (smsOutboxReportInputBean.getToDate() != null && !smsOutboxReportInputBean.getToDate().isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(smsOutboxReportInputBean.getToDate()));
                //add one to calender instance
                calendar.add(Calendar.DATE, 1);
                //format the from date
                String toDate = simpleDateFormat.format(calendar.getTime());
                Date tDate = parser.parse(toDate);
                String formattedToDate = formatter.format(tDate);
                //add to dynamic clause
                dynamicClause.append(" and so.createdtime <'").append(formattedToDate).append("'");
            }

            if (smsOutboxReportInputBean.getTelco() != null && !smsOutboxReportInputBean.getTelco().isEmpty()) {
                dynamicClause.append("and so.telco = '").append(smsOutboxReportInputBean.getTelco()).append("'");
            }

            if (smsOutboxReportInputBean.getChannel() != null && !smsOutboxReportInputBean.getChannel().isEmpty()) {
                dynamicClause.append("and so.channel = '").append(smsOutboxReportInputBean.getChannel()).append("'");
            }

            if (smsOutboxReportInputBean.getCategory() != null && !smsOutboxReportInputBean.getCategory().isEmpty()) {
                dynamicClause.append("and so.category = '").append(smsOutboxReportInputBean.getCategory()).append("'");
            }

            if (smsOutboxReportInputBean.getMobileno() != null && !smsOutboxReportInputBean.getMobileno().isEmpty()) {
                dynamicClause.append("and lower(so.mobile) like lower('%").append(smsOutboxReportInputBean.getMobileno()).append("%')");
            }

            if (smsOutboxReportInputBean.getTxnType() != null && !smsOutboxReportInputBean.getTxnType().isEmpty()) {
                dynamicClause.append("and so.TXN_TYPE = '").append(smsOutboxReportInputBean.getTxnType()).append("'");
            }

            if (smsOutboxReportInputBean.getStatus() != null && !smsOutboxReportInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and so.status = '").append(smsOutboxReportInputBean.getStatus()).append("'");
            }

            dynamicClause.append("and so.delstatus = 'EXPIRED'");

        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDeliveredCount(SmsOutboxReportInputBean smsOutboxReportInputBean, StringBuilder dynamicClause) throws Exception {
        dynamicClause.append(" 1=1 ");
        try {
            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat formatter = new SimpleDateFormat("YYYY/MM/dd 00:00:00");

            if (smsOutboxReportInputBean.getFromDate() != null && !smsOutboxReportInputBean.getFromDate().isEmpty()) {
                String fromDate = smsOutboxReportInputBean.getFromDate();
                Date fDate = parser.parse(fromDate);
                //format the from date
                String formattedFromDate = formatter.format(fDate);
                //add to dynamic clause
                dynamicClause.append(" and so.createdtime >='").append(formattedFromDate).append("'");
            }

            if (smsOutboxReportInputBean.getToDate() != null && !smsOutboxReportInputBean.getToDate().isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(smsOutboxReportInputBean.getToDate()));
                //add one to calender instance
                calendar.add(Calendar.DATE, 1);
                //format the from date
                String toDate = simpleDateFormat.format(calendar.getTime());
                Date tDate = parser.parse(toDate);
                String formattedToDate = formatter.format(tDate);
                //add to dynamic clause
                dynamicClause.append(" and so.createdtime <'").append(formattedToDate).append("'");
            }

            if (smsOutboxReportInputBean.getTelco() != null && !smsOutboxReportInputBean.getTelco().isEmpty()) {
                dynamicClause.append("and so.telco = '").append(smsOutboxReportInputBean.getTelco()).append("'");
            }

            if (smsOutboxReportInputBean.getChannel() != null && !smsOutboxReportInputBean.getChannel().isEmpty()) {
                dynamicClause.append("and so.channel = '").append(smsOutboxReportInputBean.getChannel()).append("'");
            }

            if (smsOutboxReportInputBean.getCategory() != null && !smsOutboxReportInputBean.getCategory().isEmpty()) {
                dynamicClause.append("and so.category = '").append(smsOutboxReportInputBean.getCategory()).append("'");
            }

            if (smsOutboxReportInputBean.getMobileno() != null && !smsOutboxReportInputBean.getMobileno().isEmpty()) {
                dynamicClause.append("and lower(so.mobile) like lower('%").append(smsOutboxReportInputBean.getMobileno()).append("%')");
            }

            if (smsOutboxReportInputBean.getTxnType() != null && !smsOutboxReportInputBean.getTxnType().isEmpty()) {
                dynamicClause.append("and so.TXN_TYPE = '").append(smsOutboxReportInputBean.getTxnType()).append("'");
            }

            if (smsOutboxReportInputBean.getStatus() != null && !smsOutboxReportInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and so.status = '").append(smsOutboxReportInputBean.getStatus()).append("'");
            }

            dynamicClause.append("and so.delstatus = 'DELIVRD'");
            dynamicClause.append("and so.responsecode = 'ES00'");


        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseTelcoCount(SmsOutboxReportInputBean smsOutboxReportInputBean, StringBuilder dynamicClause) throws Exception {
        dynamicClause.append(" 1=1 ");
        try {
            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat formatter = new SimpleDateFormat("YYYY/MM/dd 00:00:00");

            if (smsOutboxReportInputBean.getFromDate() != null && !smsOutboxReportInputBean.getFromDate().isEmpty()) {
                String fromDate = smsOutboxReportInputBean.getFromDate();
                Date fDate = parser.parse(fromDate);
                //format the from date
                String formattedFromDate = formatter.format(fDate);
                //add to dynamic clause
                dynamicClause.append(" and so.createdtime >='").append(formattedFromDate).append("'");
            }

            if (smsOutboxReportInputBean.getToDate() != null && !smsOutboxReportInputBean.getToDate().isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(smsOutboxReportInputBean.getToDate()));
                //add one to calender instance
                calendar.add(Calendar.DATE, 1);
                //format the from date
                String toDate = simpleDateFormat.format(calendar.getTime());
                Date tDate = parser.parse(toDate);
                String formattedToDate = formatter.format(tDate);
                //add to dynamic clause
                dynamicClause.append(" and so.createdtime <'").append(formattedToDate).append("'");
            }

            if (smsOutboxReportInputBean.getTelco() != null && !smsOutboxReportInputBean.getTelco().isEmpty()) {
                dynamicClause.append("and so.telco = '").append(smsOutboxReportInputBean.getTelco()).append("'");
            }

            if (smsOutboxReportInputBean.getChannel() != null && !smsOutboxReportInputBean.getChannel().isEmpty()) {
                dynamicClause.append("and so.channel = '").append(smsOutboxReportInputBean.getChannel()).append("'");
            }

            if (smsOutboxReportInputBean.getCategory() != null && !smsOutboxReportInputBean.getCategory().isEmpty()) {
                dynamicClause.append("and so.category = '").append(smsOutboxReportInputBean.getCategory()).append("'");
            }

            if (smsOutboxReportInputBean.getMobileno() != null && !smsOutboxReportInputBean.getMobileno().isEmpty()) {
                dynamicClause.append("and lower(so.mobile) like lower('%").append(smsOutboxReportInputBean.getMobileno()).append("%')");
            }

            if (smsOutboxReportInputBean.getTxnType() != null && !smsOutboxReportInputBean.getTxnType().isEmpty()) {
                dynamicClause.append("and so.TXN_TYPE = '").append(smsOutboxReportInputBean.getTxnType()).append("'");
            }

            if (smsOutboxReportInputBean.getStatus() != null && !smsOutboxReportInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and so.status = '").append(smsOutboxReportInputBean.getStatus()).append("'");
            }

            dynamicClause.append("and so.delstatus in ('DELIVRD','EXPIRED','UNDELIVERABLE','REJECTD','UNKNOWN')");

        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

}
