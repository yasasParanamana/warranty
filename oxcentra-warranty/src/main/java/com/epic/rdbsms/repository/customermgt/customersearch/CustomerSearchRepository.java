package com.epic.rdbsms.repository.customermgt.customersearch;

import com.epic.rdbsms.bean.customermgt.customersearch.CustomerSearchInputBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.mapping.customermgt.CustomerSearch;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.StatusVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Scope("prototype")
public class CustomerSearchRepository {

    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";
    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from CUSTOMERINFO ws left outer join CUSTOMERACCOUNT ca on ws.customerid=ca.customerid where ";
    private final String SQL_UPDATE_CUSTOMER = "update CUSTOMERINFO ws set ws.status=?, ws.waveoff=?, ws.remark=?, ws.lastupdateduser=?, ws.lastupdatedtime=? where ws.customerid = ?";
    private final String SQL_FIND_CUSTOMER = "select ws.customerid,ws.identification,ws.fullname,ws.dob,ws.waveoff,ws.mobile,s.description as status,ca.accountno,cc.description as customercategory," +
            " b.description as branch,ws.lastupdateduser,ws.status as statuscode,t.description as accounttype,ws.createdtime as createdtime,ws.lastupdatedtime as lastupdatedtime from CUSTOMERINFO ws " +
            " left outer join STATUS s on s.statuscode=ws.status " +
            " left outer join CUSTOMERACCOUNT ca on ws.customerid=ca.customerid " +
            " left outer join CUSTOMERCATEGORY cc on ws.customercategory=cc.code " +
            " left outer join BRANCH b on ws.branchcode=b.branchcode " +
            " left outer join ACCOUNTTYPE t on t.code=ca.accounttype " +
            " where ca.ISPRIMARY = 'YES' and ws.customerid = ? ";
    @Autowired
    SessionBean sessionBean;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    CommonRepository commonRepository;

    @Transactional(readOnly = true)
    public long getDataCount(CustomerSearchInputBean customerSearchInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(customerSearchInputBean, dynamicClause);
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
    public List<CustomerSearch> getCustomerSearchListForReport(CustomerSearchInputBean customerSearchInputBean) throws Exception {
        List<CustomerSearch> customerSearchList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(customerSearchInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = " order by ws.lastupdatedtime desc ";

            String sql =
                    " select ws.customerid,ws.identification,ws.fullname,ws.dob,ws.waveoff,ws.mobile,s.description as status,ca.accountno,cc.description as customercategory," +
                            " b.description as branch,t.description as accounttype,ws.createdtime as createdtime,ws.lastupdatedtime as lastupdatedtime from CUSTOMERINFO ws " +
                            " left outer join STATUS s on s.statuscode=ws.status " +
                            " left outer join CUSTOMERACCOUNT ca on ws.customerid=ca.customerid " +
                            " left outer join CUSTOMERCATEGORY cc on ws.customercategory=cc.code " +
                            " left outer join BRANCH b on ws.branchcode=b.branchcode " +
                            " left outer join ACCOUNTTYPE t on t.code=ca.accounttype " +
                            " where " + dynamicClause.toString() + sortingStr;

            customerSearchList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                CustomerSearch customerSearch = new CustomerSearch();
                try {
                    customerSearch.setCustomerId(rs.getString("customerid"));
                } catch (Exception e) {
                    customerSearch.setCustomerId(null);
                }

                try {
                    customerSearch.setIdentification(rs.getString("identification"));
                } catch (Exception e) {
                    customerSearch.setIdentification(null);
                }

                try {
                    customerSearch.setCustomerName(rs.getString("fullname"));
                } catch (Exception e) {
                    customerSearch.setCustomerName(null);
                }

                try {
                    customerSearch.setDob(rs.getDate("dob"));
                } catch (Exception e) {
                    customerSearch.setDob(null);
                }

                try {
                    customerSearch.setWaiveoffstatus(rs.getString("waveoff"));
                } catch (Exception e) {
                    customerSearch.setWaiveoffstatus(null);
                }

                try {
                    customerSearch.setMobileNo(rs.getString("mobile"));
                } catch (Exception e) {
                    customerSearch.setMobileNo(null);
                }

                try {
                    customerSearch.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    customerSearch.setStatus(null);
                }

                try {
                    customerSearch.setAccountNo(rs.getString("accountno"));
                } catch (Exception e) {
                    customerSearch.setAccountNo(null);
                }

                try {
                    customerSearch.setCustomerCategory(rs.getString("customercategory"));
                } catch (Exception e) {
                    customerSearch.setCustomerCategory(null);
                }

                try {
                    customerSearch.setBranch(rs.getString("branch"));
                } catch (Exception e) {
                    customerSearch.setBranch(null);
                }

                try {
                    customerSearch.setAccountType(rs.getString("accounttype"));
                } catch (Exception e) {
                    customerSearch.setAccountType(null);
                }

                try {
                    customerSearch.setCreatedTime(rs.getDate("createdTime"));
                } catch (Exception e) {
                    customerSearch.setCreatedTime(null);
                }

                try {
                    customerSearch.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    customerSearch.setLastUpdatedTime(null);
                }

                return customerSearch;
            });
        } catch (EmptyResultDataAccessException ex) {
            return customerSearchList;
        } catch (Exception e) {
            throw e;
        }
        return customerSearchList;
    }

    @Transactional(readOnly = true)
    public List<CustomerSearch> getCustomerSearchList(CustomerSearchInputBean customerSearchInputBean) {
        List<CustomerSearch> customerSearchList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(customerSearchInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (customerSearchInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by ws.lastupdatedtime desc ";
            } else {
                sortingStr = " order by ws.lastupdatedtime " + customerSearchInputBean.sortDirections.get(0);
            }

            String sql =
                    " select ws.customerid,ws.identification,ws.fullname,ws.dob,ws.waveoff,ws.mobile,s.description as status,ca.accountno,cc.description as customercategory," +
                            " b.description as branch,t.description as accounttype,ws.createdtime as createdtime,ws.lastupdatedtime as lastupdatedtime from CUSTOMERINFO ws " +
                            " left outer join STATUS s on s.statuscode=ws.status " +
                            " left outer join CUSTOMERACCOUNT ca on ws.customerid=ca.customerid " +
                            " left outer join CUSTOMERCATEGORY cc on ws.customercategory=cc.code " +
                            " left outer join BRANCH b on ws.branchcode=b.branchcode " +
                            " left outer join ACCOUNTTYPE t on t.code=ca.accounttype " +
                            " where " + dynamicClause.toString() + sortingStr +
                            " limit " + customerSearchInputBean.displayLength + " offset " + customerSearchInputBean.displayStart;

            customerSearchList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                CustomerSearch customerSearch = new CustomerSearch();
                try {
                    customerSearch.setCustomerId(rs.getString("customerid"));
                } catch (Exception e) {
                    customerSearch.setCustomerId(null);
                }

                try {
                    customerSearch.setIdentification(rs.getString("identification"));
                } catch (Exception e) {
                    customerSearch.setIdentification(null);
                }

                try {
                    customerSearch.setCustomerName(rs.getString("fullname"));
                } catch (Exception e) {
                    customerSearch.setCustomerName(null);
                }

                try {
                    customerSearch.setDob(rs.getDate("dob"));
                } catch (Exception e) {
                    customerSearch.setDob(null);
                }

                try {
                    customerSearch.setWaiveoffstatus(rs.getString("waveoff"));
                } catch (Exception e) {
                    customerSearch.setWaiveoffstatus(null);
                }

                try {
                    customerSearch.setMobileNo(rs.getString("mobile"));
                } catch (Exception e) {
                    customerSearch.setMobileNo(null);
                }

                try {
                    customerSearch.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    customerSearch.setStatus(null);
                }

                try {
                    customerSearch.setAccountNo(rs.getString("accountno"));
                } catch (Exception e) {
                    customerSearch.setAccountNo(null);
                }

                try {
                    customerSearch.setCustomerCategory(rs.getString("customercategory"));
                } catch (Exception e) {
                    customerSearch.setCustomerCategory(null);
                }

                try {
                    customerSearch.setBranch(rs.getString("branch"));
                } catch (Exception e) {
                    customerSearch.setBranch(null);
                }

                try {
                    customerSearch.setAccountType(rs.getString("accounttype"));
                } catch (Exception e) {
                    customerSearch.setAccountType(null);
                }

                try {
                    customerSearch.setCreatedTime(rs.getDate("createdTime"));
                } catch (Exception e) {
                    customerSearch.setCreatedTime(null);
                }

                try {
                    customerSearch.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    customerSearch.setLastUpdatedTime(null);
                }

                return customerSearch;
            });
        } catch (EmptyResultDataAccessException ex) {
            return customerSearchList;
        } catch (Exception e) {
            throw e;
        }
        return customerSearchList;
    }

    private StringBuilder setDynamicClause(CustomerSearchInputBean customerSearchInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (customerSearchInputBean.getIdentification() != null && !customerSearchInputBean.getIdentification().isEmpty()) {
                dynamicClause.append("and lower(ws.IDENTIFICATION) like lower('%").append(customerSearchInputBean.getIdentification()).append("%')");
            }

            if (customerSearchInputBean.getCustomerid() != null && !customerSearchInputBean.getCustomerid().isEmpty()) {
                dynamicClause.append("and lower(ws.CUSTOMERID) like lower('%").append(customerSearchInputBean.getCustomerid()).append("%')");
            }

            if (customerSearchInputBean.getAccountno() != null && !customerSearchInputBean.getAccountno().isEmpty()) {
                dynamicClause.append("and lower(ca.ACCOUNTNO) like lower('%").append(customerSearchInputBean.getAccountno()).append("%')");
            }

            if (customerSearchInputBean.getMobileno() != null && !customerSearchInputBean.getMobileno().isEmpty()) {
                dynamicClause.append("and lower(ws.MOBILE) like lower('%").append(customerSearchInputBean.getMobileno()).append("%')");
            }
            dynamicClause.append("and ca.ISPRIMARY = 'YES'");

        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    @Transactional(readOnly = true)
    public CustomerSearch getCustomerSearch(String customerId) {
        CustomerSearch customerSearch = null;
        try {
            customerSearch = jdbcTemplate.queryForObject(SQL_FIND_CUSTOMER, new Object[]{customerId}, new RowMapper<CustomerSearch>() {
                @Override
                public CustomerSearch mapRow(ResultSet rs, int rowNum) throws SQLException {
                    CustomerSearch customerSearch = new CustomerSearch();
                    try {
                        customerSearch.setCustomerId(rs.getString("customerid"));
                    } catch (Exception e) {
                        customerSearch.setCustomerId(null);
                    }

                    try {
                        customerSearch.setIdentification(rs.getString("identification"));
                    } catch (Exception e) {
                        customerSearch.setIdentification(null);
                    }

                    try {
                        customerSearch.setCustomerName(rs.getString("fullname"));
                    } catch (Exception e) {
                        customerSearch.setCustomerName(null);
                    }

                    try {
                        customerSearch.setDob(rs.getDate("dob"));
                    } catch (Exception e) {
                        customerSearch.setDob(null);
                    }

                    try {
                        customerSearch.setWaiveoffstatus(rs.getString("waveoff"));
                    } catch (Exception e) {
                        customerSearch.setWaiveoffstatus(null);
                    }

                    try {
                        customerSearch.setMobileNo(rs.getString("mobile"));
                    } catch (Exception e) {
                        customerSearch.setMobileNo(null);
                    }

                    try {
                        customerSearch.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        customerSearch.setStatus(null);
                    }

                    try {
                        customerSearch.setStatuscode(rs.getString("statuscode"));
                    } catch (Exception e) {
                        customerSearch.setStatuscode(null);
                    }


                    try {
                        customerSearch.setAccountNo(rs.getString("accountno"));
                    } catch (Exception e) {
                        customerSearch.setAccountNo(null);
                    }

                    try {
                        customerSearch.setCustomerCategory(rs.getString("customercategory"));
                    } catch (Exception e) {
                        customerSearch.setCustomerCategory(null);
                    }

                    try {
                        customerSearch.setBranch(rs.getString("branch"));
                    } catch (Exception e) {
                        customerSearch.setBranch(null);
                    }

                    try {
                        customerSearch.setAccountType(rs.getString("accounttype"));
                    } catch (Exception e) {
                        customerSearch.setAccountType(null);
                    }

                    try {
                        customerSearch.setCreatedTime(rs.getDate("createdTime"));
                    } catch (Exception e) {
                        customerSearch.setCreatedTime(null);
                    }

                    try {
                        customerSearch.setLastUpdatedUser(rs.getString("lastupdateduser"));
                    } catch (Exception e) {
                        customerSearch.setLastUpdatedUser(null);
                    }

                    try {
                        customerSearch.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                    } catch (Exception e) {
                        customerSearch.setLastUpdatedTime(null);
                    }
                    return customerSearch;
                }
            });
        } catch (EmptyResultDataAccessException ex) {
            throw ex;
        }
        return customerSearch;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(CustomerSearchInputBean customerSearchInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(customerSearchInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.CUSTOMER_SEARCH_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    private StringBuilder setDynamicClauseDual(CustomerSearchInputBean customerSearchInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (customerSearchInputBean.getIdentification() != null && !customerSearchInputBean.getIdentification().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(customerSearchInputBean.getIdentification()).append("%')");
            }

            if (customerSearchInputBean.getCustomerid() != null && !customerSearchInputBean.getCustomerid().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(customerSearchInputBean.getCustomerid()).append("%')");
            }

            if (customerSearchInputBean.getAccountno() != null && !customerSearchInputBean.getAccountno().isEmpty()) {
                dynamicClause.append("and lower(wta.key3) like lower('%").append(customerSearchInputBean.getAccountno()).append("%')");
            }

            if (customerSearchInputBean.getMobileno() != null && !customerSearchInputBean.getMobileno().isEmpty()) {
                dynamicClause.append("and lower(wta.key4) like lower('%").append(customerSearchInputBean.getMobileno()).append("%')");
            }

        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getCustomerSearchResultsDual(CustomerSearchInputBean customerSearchInputBean) {
        List<TempAuthRec> sectionDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(customerSearchInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (customerSearchInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + customerSearchInputBean.sortDirections.get(0);
            }
            String sql = "" +
                    " select wta.id,wta.key1,wta.key2,wta.key3,wta.key4, s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                    " from WEB_TMPAUTHREC wta" +
                    " left outer join STATUS s on s.statuscode = wta.key5 " +
                    " left outer join WEB_TASK t on t.taskcode = wta.task " +
                    " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                    " limit " + customerSearchInputBean.displayLength + " offset " + customerSearchInputBean.displayStart;

            sectionDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.CUSTOMER_SEARCH_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRec = new TempAuthRec();

                try {
                    tempAuthRec.setId(rs.getString("id"));
                } catch (Exception e) {
                    tempAuthRec.setId(null);
                }

                try {
                    tempAuthRec.setKey1(rs.getString("key1"));
                } catch (Exception e) {
                    tempAuthRec.setKey1(null);
                }

                try {
                    tempAuthRec.setKey2(rs.getString("key2"));
                } catch (Exception e) {
                    tempAuthRec.setKey2(null);
                }

                try {
                    tempAuthRec.setKey3(rs.getString("key3"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
                }

                try {
                    tempAuthRec.setKey4(rs.getString("key4"));
                } catch (Exception e) {
                    tempAuthRec.setKey4(null);
                }

                try {
                    tempAuthRec.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    tempAuthRec.setStatus(null);
                }

                try {
                    tempAuthRec.setTask(rs.getString("taskdescription"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    tempAuthRec.setCreatedTime(rs.getString("createdTime"));
                } catch (Exception e) {
                    tempAuthRec.setCreatedTime(null);
                }

                try {
                    tempAuthRec.setLastUpdatedTime(rs.getString("lastUpdatedTime"));
                } catch (Exception e) {
                    tempAuthRec.setLastUpdatedTime(null);
                }

                try {
                    tempAuthRec.setLastUpdatedUser(rs.getString("lastUpdatedUser"));
                } catch (Exception e) {
                    tempAuthRec.setLastUpdatedUser(null);
                }
                return tempAuthRec;
            });
        } catch (Exception e) {
            throw e;
        }
        return sectionDualList;
    }


    @Transactional
    public String updateCustomer(CustomerSearchInputBean customerSearchInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_CUSTOMER,
                    customerSearchInputBean.getStatus(),
                    customerSearchInputBean.getWaiveoffstatus(),
                    customerSearchInputBean.getRemark(),
                    customerSearchInputBean.getLastUpdatedUser(),
                    customerSearchInputBean.getLastUpdatedTime(),
                    customerSearchInputBean.getCustomerid());

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }


}
