package com.epic.rdbsms.repository.reportmgt.smsoutbox;

import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.smsoutbox.SmsOutboxReportInputBean;
import com.epic.rdbsms.mapping.smsoutbox.SmsOutbox;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
@Scope("prototype")
public class SmsOutboxRepository {

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

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from SMSOUTBOX so where ";

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
    public List<SmsOutbox> getSmsOutBoxSearchResultList(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        List<SmsOutbox> smsOutboxList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(smsOutboxReportInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (smsOutboxReportInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by so.lastupdatedtime desc ";
            } else {
                sortingStr = " order by so.lastupdatedtime " + smsOutboxReportInputBean.sortDirections.get(0);
            }

            String sql = "" +
                    " select " +
                    " so.id,so.refno,so.mobile,so.message,s.description as status,ds.description as delstatus,so.delrefno,so.bulkid,so.bulktype,so.partcount, " +
                    " t.description as telcodescription,d.description as departmentdescription,sc.description as channeldescription ,c.description as category,so.responsecode, " +
                    " so.createdtime as createdtime, tx.description as txntype, r.description as failedReason, so.isidd, so.lastupdatedtime as lastupdatedtime , so.lastupdateduser as lastupdateduser from SMSOUTBOX so " +
                    " left outer join TELCO t on t.code=so.telco " +
                    " left outer join STATUS s on s.statuscode=so.status " +
                    " left outer join DELIVERYSTATUS ds on ds.statuscode=so.delstatus " +
                    " left outer join RESPONCECODES r on r.responcecode=so.responsecode " +
                    " left outer join CATEGORY c on c.category=so.category " +
                    " left outer join DEPARTMENT d on d.code=so.department " +
                    " left outer join SMSCHANNEL sc on sc.channelcode = so.channel " +
                    " left outer join TXN_TYPE tx on tx.TXNTYPE = so.TXN_TYPE " +
                    " where " + dynamicClause.toString() + sortingStr + "limit " + smsOutboxReportInputBean.displayLength +
                    " offset " + smsOutboxReportInputBean.displayStart;

            smsOutboxList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                SmsOutbox smsOutbox = new SmsOutbox();

                try {
                    smsOutbox.setId(rs.getInt("id"));
                } catch (Exception e) {
                    smsOutbox.setId(0);
                }

                try {
                    smsOutbox.setReferenceNo(rs.getString("refno"));
                } catch (Exception e) {
                    smsOutbox.setReferenceNo(null);
                }

                try {
                    smsOutbox.setIsIDD(rs.getString("isidd"));
                } catch (Exception e) {
                    smsOutbox.setIsIDD(null);
                }

                try {
                    smsOutbox.setTrnType(rs.getString("txntype"));
                } catch (Exception e) {
                    smsOutbox.setTrnType(null);
                }

                try {
                    smsOutbox.setMobileNumber(rs.getString("mobile"));
                } catch (Exception e) {
                    smsOutbox.setMobileNumber(null);
                }

                try {
                    smsOutbox.setMessage(rs.getString("message"));
                } catch (Exception e) {
                    smsOutbox.setMessage(null);
                }

                try {
                    smsOutbox.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    smsOutbox.setStatus(null);
                }

                try {
                    smsOutbox.setDeleteStatus(rs.getString("delstatus"));
                } catch (Exception e) {
                    smsOutbox.setDeleteStatus(null);
                }

                try {
                    smsOutbox.setDeleteReferenceNo(rs.getString("delrefno"));
                } catch (Exception e) {
                    smsOutbox.setDeleteReferenceNo(null);
                }

                try {
                    smsOutbox.setBulkId(rs.getString("bulkid"));
                } catch (Exception e) {
                    smsOutbox.setBulkId(null);
                }

                try {
                    smsOutbox.setBulkType(rs.getString("bulktype"));
                } catch (Exception e) {
                    smsOutbox.setBulkType(null);
                }

                try {
                    smsOutbox.setPartCount(rs.getInt("partcount"));
                } catch (Exception e) {
                    smsOutbox.setPartCount(0);
                }

                try {
                    smsOutbox.setTelco(rs.getString("telcodescription"));
                } catch (Exception e) {
                    smsOutbox.setTelco(null);
                }

                try {
                    smsOutbox.setDepartment(rs.getString("departmentdescription"));
                } catch (Exception e) {
                    smsOutbox.setDepartment(null);
                }

                try {
                    smsOutbox.setChannel(rs.getString("channeldescription"));
                } catch (Exception e) {
                    smsOutbox.setChannel(null);
                }

                try {
                    smsOutbox.setCategory(rs.getString("category"));
                } catch (Exception e) {
                    smsOutbox.setCategory(null);
                }

                try {
                    smsOutbox.setResponseCode(rs.getString("responsecode"));
                } catch (Exception e) {
                    smsOutbox.setResponseCode(null);
                }

                try {
                    smsOutbox.setFailedReason(rs.getString("failedReason"));
                } catch (Exception e) {
                    smsOutbox.setFailedReason(null);
                }

                try {
                    smsOutbox.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    smsOutbox.setCreatedTime(null);
                }

                try {
                    smsOutbox.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    smsOutbox.setLastUpdatedTime(null);
                }

                try {
                    smsOutbox.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    smsOutbox.setLastUpdatedUser(null);
                }
                return smsOutbox;
            });

        } catch (EmptyResultDataAccessException ex) {
            return smsOutboxList;
        } catch (Exception e) {
            throw e;
        }
        return smsOutboxList;
    }

    @Transactional(readOnly = true)
    public List<SmsOutbox> getSmsOutBoxSearchResultListForReport(SmsOutboxReportInputBean smsOutboxReportInputBean) throws Exception {
        List<SmsOutbox> smsOutboxList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(smsOutboxReportInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = " order by so.lastupdatedtime desc ";

            String sql = "" +
                    " select " +
                    " so.id,so.refno,so.mobile,so.message,s.description as status,ds.description as delstatus,so.delrefno,so.bulkid,so.bulktype,so.partcount, " +
                    " t.description as telcodescription,d.description as departmentdescription,sc.description as channeldescription ,c.description as category,so.responsecode, " +
                    " so.createdtime as createdtime, tx.description as txntype, r.description as failedReason, so.isidd, so.lastupdatedtime as lastupdatedtime , so.lastupdateduser as lastupdateduser from SMSOUTBOX so " +
                    " left outer join TELCO t on t.code=so.telco " +
                    " left outer join STATUS s on s.statuscode=so.status " +
                    " left outer join DELIVERYSTATUS ds on ds.statuscode=so.delstatus " +
                    " left outer join RESPONCECODES r on r.responcecode=so.responsecode " +
                    " left outer join CATEGORY c on c.category=so.category " +
                    " left outer join DEPARTMENT d on d.code=so.department " +
                    " left outer join SMSCHANNEL sc on sc.channelcode = so.channel " +
                    " left outer join TXN_TYPE tx on tx.TXNTYPE = so.TXN_TYPE " +
                    " where " + dynamicClause.toString() + sortingStr;

            smsOutboxList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                SmsOutbox smsOutbox = new SmsOutbox();

                try {
                    smsOutbox.setId(rs.getInt("id"));
                } catch (Exception e) {
                    smsOutbox.setId(0);
                }

                try {
                    smsOutbox.setReferenceNo(rs.getString("refno"));
                } catch (Exception e) {
                    smsOutbox.setReferenceNo(null);
                }

                try {
                    smsOutbox.setIsIDD(rs.getString("isidd"));
                } catch (Exception e) {
                    smsOutbox.setIsIDD(null);
                }

                try {
                    smsOutbox.setTrnType(rs.getString("txntype"));
                } catch (Exception e) {
                    smsOutbox.setTrnType(null);
                }

                try {
                    smsOutbox.setMobileNumber(rs.getString("mobile"));
                } catch (Exception e) {
                    smsOutbox.setMobileNumber(null);
                }

                try {
                    smsOutbox.setMessage(rs.getString("message"));
                } catch (Exception e) {
                    smsOutbox.setMessage(null);
                }

                try {
                    smsOutbox.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    smsOutbox.setStatus(null);
                }

                try {
                    smsOutbox.setDeleteStatus(rs.getString("delstatus"));
                } catch (Exception e) {
                    smsOutbox.setDeleteStatus(null);
                }

                try {
                    smsOutbox.setDeleteReferenceNo(rs.getString("delrefno"));
                } catch (Exception e) {
                    smsOutbox.setDeleteReferenceNo(null);
                }

                try {
                    smsOutbox.setBulkId(rs.getString("bulkid"));
                } catch (Exception e) {
                    smsOutbox.setBulkId(null);
                }

                try {
                    smsOutbox.setBulkType(rs.getString("bulktype"));
                } catch (Exception e) {
                    smsOutbox.setBulkType(null);
                }

                try {
                    smsOutbox.setPartCount(rs.getInt("partcount"));
                } catch (Exception e) {
                    smsOutbox.setPartCount(0);
                }

                try {
                    smsOutbox.setTelco(rs.getString("telcodescription"));
                } catch (Exception e) {
                    smsOutbox.setTelco(null);
                }

                try {
                    smsOutbox.setDepartment(rs.getString("departmentdescription"));
                } catch (Exception e) {
                    smsOutbox.setDepartment(null);
                }

                try {
                    smsOutbox.setChannel(rs.getString("channeldescription"));
                } catch (Exception e) {
                    smsOutbox.setChannel(null);
                }

                try {
                    smsOutbox.setCategory(rs.getString("category"));
                } catch (Exception e) {
                    smsOutbox.setCategory(null);
                }

                try {
                    smsOutbox.setResponseCode(rs.getString("responsecode"));
                } catch (Exception e) {
                    smsOutbox.setResponseCode(null);
                }

                try {
                    smsOutbox.setFailedReason(rs.getString("failedReason"));
                } catch (Exception e) {
                    smsOutbox.setFailedReason(null);
                }

                try {
                    smsOutbox.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    smsOutbox.setCreatedTime(null);
                }

                try {
                    smsOutbox.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    smsOutbox.setLastUpdatedTime(null);
                }

                try {
                    smsOutbox.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    smsOutbox.setLastUpdatedUser(null);
                }
                return smsOutbox;
            });
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

            if (smsOutboxReportInputBean.getDepartment() != null && !smsOutboxReportInputBean.getDepartment().isEmpty()) {
                dynamicClause.append("and so.department = '").append(smsOutboxReportInputBean.getDepartment()).append("'");
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

            if (smsOutboxReportInputBean.getDelstatus() != null && !smsOutboxReportInputBean.getDelstatus().isEmpty()) {
                dynamicClause.append("and so.delstatus = '").append(smsOutboxReportInputBean.getDelstatus()).append("'");
            }

        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
