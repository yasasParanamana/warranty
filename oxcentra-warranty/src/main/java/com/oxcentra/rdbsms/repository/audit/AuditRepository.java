package com.oxcentra.rdbsms.repository.audit;

import com.oxcentra.rdbsms.bean.audit.AuditTraceInputBean;
import com.oxcentra.rdbsms.bean.audit.AuditValueBean;
import com.oxcentra.rdbsms.bean.common.CommonBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
@Scope("prototype")
public class AuditRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from WEB_SYSTEMAUDIT a left outer join WEB_SECTION s on s.sectioncode=a.section left outer join WEB_PAGE p on p.pagecode=a.page left outer join USERROLE u on u.userrolecode=a.userrole left outer join WEB_TASK t on t.taskcode=a.task where ";
    private final String SQL_FIND_AUDIT = "select a.systemauditid, a.description, s.description as sectiondes,p.description as pagedes, t.description as taskdes,r.description roledes,a.createtime,a.lastupdateduser,a.lastupdatedtime,a.remarks,a.ip,a.field,a.oldvalue,a.newvalue from WEB_SYSTEMAUDIT a left outer join WEB_SECTION s on s.sectioncode=a.section left outer join WEB_PAGE p on p.pagecode=a.page left outer join WEB_TASK t on t.taskcode=a.task left outer join USERROLE r on r.userrolecode=a.userrole where a.systemauditid=?";
    private final String SQL_GET_ASSIGNED_TASK_LIST = "select t.taskcode taskcode, t.description description from WEB_TASK t inner join WEB_PAGETASK pt on pt.task=t.taskcode where pt.page=? ";
    private final String SQL_GET_ASSIGNED_PAGE_LIST_FOR_SECTION = "select p.pagecode pagecode, p.description description from WEB_SECTIONPAGE sp inner join WEB_PAGE p on p.pagecode=sp.page where sp.section=?";

    @Transactional(readOnly = true)
    public long getDataCount(AuditTraceInputBean auditInputBean) throws Exception {
        System.out.println("***** -2");
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(auditInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception ex) {
            throw ex;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<Audittrace> getAuditSearchResultList(AuditTraceInputBean auditTraceInputBean) throws Exception {
        System.out.println("***** -1");
        List<Audittrace> auditTraceList;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(auditTraceInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (auditTraceInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by a.lastupdatedtime desc ";
            } else {
                sortingStr = " order by a.lastupdatedtime " + auditTraceInputBean.sortDirections.get(0);
            }

            String sql = "" +
                    " select " + "a.systemauditid, a.remarks, u.description as userroledes, a.ip, a.description, s.description as sectiondes, p.description as pagedes, t.description as taskdes, a.lastupdateduser, a.lastupdatedtime "
                    + "from WEB_SYSTEMAUDIT a "
                    + "left outer join WEB_SECTION s on s.sectioncode=a.section "
                    + "left outer join WEB_PAGE p on p.pagecode=a.page "
                    + "left outer join USERROLE u on u.userrolecode=a.userrole "
                    + "left outer join WEB_TASK t on t.taskcode=a.task "
                    + "where " + dynamicClause + sortingStr + "limit " + auditTraceInputBean.displayLength +
                    " offset " + auditTraceInputBean.displayStart;

            auditTraceList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Audittrace audittrace = new Audittrace();

                try {
                    audittrace.setAuditid(rs.getBigDecimal("systemauditid"));
                } catch (Exception e) {
                    audittrace.setAuditid(new BigDecimal(0));
                }

                try {
                    if (rs.getString("description") != null && !rs.getString("description").isEmpty()) {
                        audittrace.setDescription(rs.getString("description"));
                    } else {
                        audittrace.setDescription("--");
                    }
                } catch (Exception e) {
                    audittrace.setDescription("--");
                }

                try {
                    if (rs.getString("sectiondes") != null && !rs.getString("sectiondes").isEmpty()) {
                        audittrace.setSection(rs.getString("sectiondes"));
                    } else {
                        audittrace.setSection("--");
                    }
                } catch (Exception e) {
                    audittrace.setSection("--");
                }

                try {
                    if (rs.getString("pagedes") != null && !rs.getString("pagedes").isEmpty()) {
                        audittrace.setPage(rs.getString("pagedes"));
                    } else {
                        audittrace.setPage("--");
                    }
                } catch (Exception e) {
                    audittrace.setPage(null);
                }

                try {
                    if (rs.getString("taskdes") != null && !rs.getString("taskdes").isEmpty()) {
                        audittrace.setTask(rs.getString("taskdes"));
                    } else {
                        audittrace.setTask("--");
                    }
                } catch (Exception e) {
                    audittrace.setTask("--");
                }

                try {
                    if (rs.getString("ip") != null && !rs.getString("ip").isEmpty()) {
                        audittrace.setIp(rs.getString("ip"));
                    } else {
                        audittrace.setIp("--");
                    }
                } catch (Exception e) {
                    audittrace.setIp("--");
                }

                try {
                    if (rs.getString("userroledes") != null && !rs.getString("userroledes").isEmpty()) {
                        audittrace.setUserrole(rs.getString("userroledes"));
                    } else {
                        audittrace.setUserrole("--");
                    }
                } catch (Exception e) {
                    audittrace.setUserrole("--");
                }

                try {
                    if (rs.getString("lastupdateduser") != null && !rs.getString("lastupdateduser").isEmpty()) {
                        audittrace.setLastupdateduser(rs.getString("lastupdateduser"));
                    } else {
                        audittrace.setLastupdateduser("--");
                    }
                } catch (Exception e) {
                    audittrace.setLastupdateduser("--");
                }

                try {
                    if (rs.getString("lastupdatedtime") != null && !rs.getString("lastupdatedtime").isEmpty()) {
                        audittrace.setCreatedtime(rs.getDate("lastupdatedtime"));
                    } else {
                        audittrace.setCreatedtime(null);
                    }
                } catch (Exception e) {
                    audittrace.setCreatedtime(null);
                }

                return audittrace;
            });
        } catch (EmptyResultDataAccessException ere) {
            auditTraceList = null;
        } catch (Exception ex) {
            throw ex;
        }
        return auditTraceList;
    }

    @Transactional(readOnly = true)
    public List<Audittrace> getAuditTraceSearchResultListForReport(AuditTraceInputBean auditTraceInputBean) throws Exception {
        System.out.println("***** 1");
        List<Audittrace> auditTraceList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(auditTraceInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = " order by a.lastupdatedtime desc ";

            String sql = "" +
                    " select " +
                    " a.systemauditid, a.remarks, u.description as userroledes, a.ip, a.description, s.description as sectiondes, p.description as pagedes, t.description as taskdes, a.lastupdateduser, a.createtime, a.lastupdatedtime from WEB_SYSTEMAUDIT a "
                    + "left outer join WEB_SECTION s on s.sectioncode=a.section "
                    + "left outer join WEB_PAGE p on p.pagecode=a.page "
                    + "left outer join USERROLE u on u.userrolecode=a.userrole "
                    + "left outer join WEB_TASK t on t.taskcode=a.task "
                    + "where " + dynamicClause + sortingStr;

            auditTraceList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Audittrace audittrace = new Audittrace();

                try {
                    audittrace.setAuditid(rs.getBigDecimal("systemauditid"));
                } catch (Exception e) {
                    audittrace.setAuditid(new BigDecimal(0));
                }

                try {
                    if (rs.getString("description") != null && !rs.getString("description").isEmpty()) {
                        audittrace.setDescription(rs.getString("description"));
                    } else {
                        audittrace.setDescription("--");
                    }
                } catch (Exception e) {
                    audittrace.setDescription("--");
                }

                try {
                    if (rs.getString("remarks") != null && !rs.getString("remarks").isEmpty()) {
                        audittrace.setRemarks(rs.getString("remarks"));
                    } else {
                        audittrace.setRemarks("--");
                    }
                } catch (Exception e) {
                    audittrace.setRemarks("--");
                }

                try {
                    if (rs.getString("sectiondes") != null && !rs.getString("sectiondes").isEmpty()) {
                        audittrace.setSection(rs.getString("sectiondes"));
                    } else {
                        audittrace.setSection("--");
                    }
                } catch (Exception e) {
                    audittrace.setSection("--");
                }

                try {
                    if (rs.getString("pagedes") != null && !rs.getString("pagedes").isEmpty()) {
                        audittrace.setPage(rs.getString("pagedes"));
                    } else {
                        audittrace.setPage("--");
                    }
                } catch (Exception e) {
                    audittrace.setPage(null);
                }

                try {
                    if (rs.getString("taskdes") != null && !rs.getString("taskdes").isEmpty()) {
                        audittrace.setTask(rs.getString("taskdes"));
                    } else {
                        audittrace.setTask("--");
                    }
                } catch (Exception e) {
                    audittrace.setTask("--");
                }

                try {
                    if (rs.getString("ip") != null && !rs.getString("ip").isEmpty()) {
                        audittrace.setIp(rs.getString("ip"));
                    } else {
                        audittrace.setIp("--");
                    }
                } catch (Exception e) {
                    audittrace.setIp("--");
                }

                try {
                    if (rs.getString("userroledes") != null && !rs.getString("userroledes").isEmpty()) {
                        audittrace.setUserrole(rs.getString("userroledes"));
                    } else {
                        audittrace.setUserrole("--");
                    }
                } catch (Exception e) {
                    audittrace.setUserrole("--");
                }

                try {
                    if (rs.getString("lastupdateduser") != null && !rs.getString("lastupdateduser").isEmpty()) {
                        audittrace.setLastupdateduser(rs.getString("lastupdateduser"));
                    } else {
                        audittrace.setLastupdateduser("--");
                    }
                } catch (Exception e) {
                    audittrace.setLastupdateduser("--");
                }

                try {
                    if (rs.getString("lastupdatedtime") != null && !rs.getString("lastupdatedtime").isEmpty()) {
                        audittrace.setLastupdatedtime(rs.getDate("lastupdatedtime"));
                    } else {
                        audittrace.setLastupdatedtime(null);
                    }
                } catch (Exception e) {
                    audittrace.setCreatedtime(null);
                }

                try {
                    if (rs.getString("createtime") != null && !rs.getString("createtime").isEmpty()) {
                        audittrace.setCreatedtime(rs.getDate("createtime"));
                    } else {
                        audittrace.setCreatedtime(null);
                    }
                } catch (Exception e) {
                    audittrace.setCreatedtime(null);
                }

                return audittrace;
            });
        } catch (EmptyResultDataAccessException ex) {
            return auditTraceList;
        } catch (Exception e) {
            throw e;
        }
        return auditTraceList;
    }

    @Transactional(readOnly = true)
    public Audittrace getAuditTrace(String auditId) {
        System.out.println("***** 2");
        Audittrace auditTrace = null;
        try {
            auditTrace = jdbcTemplate.queryForObject(SQL_FIND_AUDIT, new Object[]{auditId}, ((rs, rowNum) -> {
                Audittrace audittrace = new Audittrace();

                try {
                    audittrace.setAuditid(rs.getBigDecimal("systemauditid"));
                } catch (Exception e) {
                    audittrace.setAuditid(new BigDecimal(0));
                }

                try {
                    if (rs.getString("username") != null && !rs.getString("username").isEmpty()) {
                        audittrace.setUsername(rs.getString("username"));
                    } else {
                        audittrace.setUsername("--");
                    }
                } catch (Exception e) {
                    audittrace.setUsername("--");
                }

                try {
                    if (rs.getString("description") != null && !rs.getString("description").isEmpty()) {
                        audittrace.setDescription(rs.getString("description"));
                    } else {
                        audittrace.setDescription("--");
                    }
                } catch (Exception e) {
                    audittrace.setDescription("--");
                }

                try {
                    if (rs.getString("sectiondes") != null && !rs.getString("sectiondes").isEmpty()) {
                        audittrace.setSection(rs.getString("sectiondes"));
                    } else {
                        audittrace.setSection("--");
                    }
                } catch (Exception e) {
                    audittrace.setSection("--");
                }

                try {
                    if (rs.getString("pagedes") != null && !rs.getString("pagedes").isEmpty()) {
                        audittrace.setPage(rs.getString("pagedes"));
                    } else {
                        audittrace.setPage("--");
                    }
                } catch (Exception e) {
                    audittrace.setPage(null);
                }

                try {
                    if (rs.getString("taskdes") != null && !rs.getString("taskdes").isEmpty()) {
                        audittrace.setTask(rs.getString("taskdes"));
                    } else {
                        audittrace.setTask("--");
                    }
                } catch (Exception e) {
                    audittrace.setTask("--");
                }

                try {
                    if (rs.getString("roledes") != null && !rs.getString("roledes").isEmpty()) {
                        audittrace.setUserrole(rs.getString("roledes"));
                    } else {
                        audittrace.setUserrole("--");
                    }
                } catch (Exception e) {
                    audittrace.setUserrole("--");
                }

                try {
                    if (rs.getString("remarks") != null && !rs.getString("remarks").isEmpty()) {
                        audittrace.setRemarks(rs.getString("remarks"));
                    } else {
                        audittrace.setRemarks("--");
                    }
                } catch (Exception e) {
                    audittrace.setRemarks("--");
                }

                try {
                    if (rs.getString("ip") != null && !rs.getString("ip").isEmpty()) {
                        audittrace.setIp(rs.getString("ip"));
                    } else {
                        audittrace.setIp("--");
                    }
                } catch (Exception e) {
                    audittrace.setIp("--");
                }

                try {
                    if (rs.getString("lastupdateduser") != null && !rs.getString("lastupdateduser").isEmpty()) {
                        audittrace.setLastupdateduser(rs.getString("lastupdateduser"));
                    } else {
                        audittrace.setLastupdateduser("--");
                    }
                } catch (Exception e) {
                    audittrace.setLastupdateduser("--");
                }

                try {
                    if (rs.getString("lastupdatedtime") != null && !rs.getString("lastupdatedtime").isEmpty()) {
                        audittrace.setLastupdatedtime(rs.getDate("lastupdatedtime"));
                    } else {
                        audittrace.setLastupdatedtime(null);
                    }
                } catch (Exception e) {
                    audittrace.setLastupdatedtime(null);
                }

                try {
                    if (rs.getString("createtime") != null && !rs.getString("createtime").isEmpty()) {
                        audittrace.setCreatedtime(rs.getDate("createtime"));
                    } else {
                        audittrace.setCreatedtime(null);
                    }
                } catch (Exception e) {
                    audittrace.setCreatedtime(null);
                }


                try {
                    //set old value, new value with field name
                    if (rs.getString("field") != null && !rs.getString("field").isEmpty()) {
                        String[] oldArray = null;
                        String[] newArray = null;
                        List<AuditValueBean> valueBeanList = new ArrayList<>();

                        String[] fieldArray = rs.getString("field").split("\\|");
                        if (rs.getString("oldvalue") != null && !rs.getString("oldvalue").isEmpty()) {
                            oldArray = rs.getString("oldvalue").split("\\|");
                        } else {
                            oldArray = null;
                        }

                        if (rs.getString("newvalue") != null && !rs.getString("newvalue").isEmpty()) {
                            newArray = rs.getString("newvalue").split("\\|");
                        } else {
                            newArray = null;
                        }

                        for (int i = 0; i < fieldArray.length; i++) {
                            AuditValueBean valueBean = new AuditValueBean();
                            valueBean.setField(fieldArray[i]);

                            if (oldArray != null && oldArray[i] != null && !oldArray[i].isEmpty()) {
                                valueBean.setOldValue(oldArray[i]);
                            } else {
                                valueBean.setOldValue("--");
                            }

                            if (newArray != null && newArray[i] != null && !newArray[i].isEmpty()) {
                                valueBean.setNewValue(newArray[i]);
                            } else {
                                valueBean.setNewValue("--");
                            }
                            valueBeanList.add(valueBean);
                        }

                        audittrace.setValueBeanList(valueBeanList);
                    } else {
                        audittrace.setFields(null);
                    }
                } catch (Exception e) {
                    audittrace.setFields(null);
                }
                return audittrace;
            }));
        } catch (EmptyResultDataAccessException ex) {
            throw ex;
        }
        return auditTrace;
    }

    @Transactional(readOnly = true)
    public List<Audittrace> getAuditExcelResults(AuditTraceInputBean auditInputBean) throws Exception {
        System.out.println("***** 3");
        List<Audittrace> auditTraceList;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(auditInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = " order by a.lastupdatedtime desc ";

            String sql
                    = "select a.systemauditid, a.remarks, a.description, s.description as sectiondes, p.description as pagedes, t.description as taskdes, a.lastupdatedtime, a.lastupdateduser,a.ip,u.description as userrole "
                    + "from WEB_SYSTEMAUDIT a "
                    + "left outer join WEB_SECTION s on s.sectioncode=a.section "
                    + "left outer join WEB_PAGE p on p.pagecode=a.page "
                    + "left outer join WEB_TASK t on t.taskcode=a.task "
                    + "left outer join USERROLE u on u.userrolecode=a.userrole "
                    + "where " + dynamicClause + sortingStr;

            auditTraceList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Audittrace audittrace = new Audittrace();

                try {
                    audittrace.setAuditid(rs.getBigDecimal("systemauditid"));
                } catch (Exception e) {
                    audittrace.setAuditid(new BigDecimal(0));
                }

                try {
                    if (rs.getString("description") != null && !rs.getString("description").isEmpty()) {
                        audittrace.setDescription(rs.getString("description"));
                    } else {
                        audittrace.setDescription("--");
                    }
                } catch (Exception e) {
                    audittrace.setDescription("--");
                }

                try {
                    if (rs.getString("sectiondes") != null && !rs.getString("sectiondes").isEmpty()) {
                        audittrace.setSection(rs.getString("sectiondes"));
                    } else {
                        audittrace.setSection("--");
                    }
                } catch (Exception e) {
                    audittrace.setSection("--");
                }

                try {
                    if (rs.getString("remarks") != null && !rs.getString("remarks").isEmpty()) {
                        audittrace.setRemarks(rs.getString("remarks"));
                    } else {
                        audittrace.setSection("--");
                    }
                } catch (Exception e) {
                    audittrace.setSection("--");
                }

                try {
                    if (rs.getString("pagedes") != null && !rs.getString("pagedes").isEmpty()) {
                        audittrace.setPage(rs.getString("pagedes"));
                    } else {
                        audittrace.setPage("--");
                    }
                } catch (Exception e) {
                    audittrace.setPage(null);
                }

                try {
                    if (rs.getString("taskdes") != null && !rs.getString("taskdes").isEmpty()) {
                        audittrace.setTask(rs.getString("taskdes"));
                    } else {
                        audittrace.setTask("--");
                    }
                } catch (Exception e) {
                    audittrace.setTask("--");
                }

                try {
                    if (rs.getString("ip") != null && !rs.getString("ip").isEmpty()) {
                        audittrace.setIp(rs.getString("ip"));
                    } else {
                        audittrace.setIp("--");
                    }
                } catch (Exception e) {
                    audittrace.setIp("--");
                }

                try {
                    if (rs.getString("userrole") != null && !rs.getString("userrole").isEmpty()) {
                        audittrace.setUserrole(rs.getString("userrole"));
                    } else {
                        audittrace.setUserrole("--");
                    }
                } catch (Exception e) {
                    audittrace.setUserrole("--");
                }

                try {
                    if (rs.getString("lastupdateduser") != null && !rs.getString("lastupdateduser").isEmpty()) {
                        audittrace.setLastupdateduser(rs.getString("lastupdateduser"));
                    } else {
                        audittrace.setLastupdateduser("--");
                    }
                } catch (Exception e) {
                    audittrace.setLastupdateduser("--");
                }

                try {
                    if (rs.getString("lastupdatedtime") != null && !rs.getString("lastupdatedtime").isEmpty()) {
                        audittrace.setLastupdatedtime(rs.getDate("lastupdatedtime"));
                    } else {
                        audittrace.setLastupdatedtime(null);
                    }
                } catch (Exception e) {
                    audittrace.setCreatedtime(null);
                }
                return audittrace;
            });
        } catch (EmptyResultDataAccessException ere) {
            auditTraceList = null;
        } catch (Exception ex) {
            throw ex;
        }
        return auditTraceList;
    }

    private StringBuilder setDynamicClause(AuditTraceInputBean auditTraceInputBean, StringBuilder dynamicClause) throws Exception {

        dynamicClause.append(" 1=1 ");
        try {
            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat formatter = new SimpleDateFormat("YYYY/MM/dd 00:00:00");

            /*
            if (auditTraceInputBean.getUserName() != null && !auditTraceInputBean.getUserName().isEmpty()) {
                dynamicClause.append("and lower(a.lastupdateduser) like lower('%").append(auditTraceInputBean.getUserName()).append("%')");
            }
*/
            if (auditTraceInputBean.getSection() != null && !auditTraceInputBean.getSection().isEmpty()) {
                dynamicClause.append("and a.section = '").append(auditTraceInputBean.getSection()).append("'");
            }

            if (auditTraceInputBean.getPage() != null && !auditTraceInputBean.getPage().isEmpty()) {
                dynamicClause.append("and a.page = '").append(auditTraceInputBean.getPage()).append("'");
            }

            if (auditTraceInputBean.getTask() != null && !auditTraceInputBean.getTask().isEmpty()) {
                dynamicClause.append("and a.task = '").append(auditTraceInputBean.getTask()).append("'");
            }

            if (auditTraceInputBean.getDescription() != null && !auditTraceInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(a.description) like lower('%").append(auditTraceInputBean.getDescription()).append("%')");
            }

            if (auditTraceInputBean.getUserName() != null && !auditTraceInputBean.getUserName().isEmpty()) {
                dynamicClause.append("and lower(a.lastupdateduser) like lower('%").append(auditTraceInputBean.getUserName()).append("%')");
            }


            if (auditTraceInputBean.getFromDate() != null && !auditTraceInputBean.getFromDate().isEmpty()) {
                String fromDate = auditTraceInputBean.getFromDate();
                Date fDate = parser.parse(fromDate);
                //format the from date
                String formattedFromDate = formatter.format(fDate);
                //add to dynamic clause
                dynamicClause.append(" and a.createtime >='").append(formattedFromDate).append("'");
            }

            if (auditTraceInputBean.getToDate() != null && !auditTraceInputBean.getToDate().isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(auditTraceInputBean.getToDate()));
                //add one to calender instance
                calendar.add(Calendar.DATE, 1);
                //format the from date
                String toDate = simpleDateFormat.format(calendar.getTime());
                Date tDate = parser.parse(toDate);
                String formattedToDate = formatter.format(tDate);
                //add to dynamic clause
                dynamicClause.append(" and a.createtime <'").append(formattedToDate).append("'");
            }
        } catch (Exception ex) {
            throw ex;
        }
        return dynamicClause;
    }

    @Transactional(readOnly = true)
    public List<CommonBean> getAssignedPages(String sectionCode) throws Exception {
        List<CommonBean> assignedPageList;
        try {
            assignedPageList = jdbcTemplate.query(SQL_GET_ASSIGNED_PAGE_LIST_FOR_SECTION, new Object[]{sectionCode}, new RowMapper<CommonBean>() {
                @Override
                public CommonBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    CommonBean commonBean = new CommonBean();
                    commonBean.setKey(rs.getString("pagecode"));
                    commonBean.setValue(rs.getString("description"));
                    return commonBean;
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            assignedPageList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return assignedPageList;
    }

    @Transactional(readOnly = true)
    public List<CommonBean> getAssignedTasks(String pageCode) throws SQLException {
        List<CommonBean> assignedPageList;
        try {
            assignedPageList = jdbcTemplate.query(SQL_GET_ASSIGNED_TASK_LIST, new Object[]{pageCode}, new RowMapper<CommonBean>() {
                @Override
                public CommonBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    CommonBean commonBean = new CommonBean();
                    commonBean.setKey(rs.getString("taskcode"));
                    commonBean.setValue(rs.getString("description"));
                    return commonBean;
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            assignedPageList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return assignedPageList;
    }

}
