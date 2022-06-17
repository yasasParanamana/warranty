package com.epic.rdbsms.repository.sysconfigmgt.txntype;

import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.txntype.TxnTypeInputBean;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.txntype.TxnType;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.StatusVarList;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;

/**
 * @author Namila Withanage on 11/15/2021
 */
@Repository
@Scope("prototype")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TxnTypeRepository {

    SessionBean sessionBean;
    JdbcTemplate jdbcTemplate;
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from TXN_TYPE d left outer join STATUS s on s.statuscode=d.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";
    private final String SQL_INSERT_TXNTYPE = "insert into TXN_TYPE(txntype,description,status,createdtime,createduser,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?) ";
    private final String SQL_FIND_TXNTYPE = "select txntype,description,status,createdtime,createduser,lastupdatedtime,lastupdateduser from TXN_TYPE d where d.txntype = ?";
    private final String SQL_UPDATE_TXNTYPE = "update TXN_TYPE d set description = ? , status = ? , lastupdatedtime = ? , lastupdateduser = ?  where d.txntype = ?";
    private final String SQL_DELETE_TXNTYPE = "delete from TXN_TYPE where txntype = ?";


    @Transactional(readOnly = true)
    public long getDataCount(TxnTypeInputBean txnTypeInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(txnTypeInputBean, dynamicClause);
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
    public List<TxnType> getTxnTypeSearchList(TxnTypeInputBean txnTypeInputBean) {
        List<TxnType> txnTypeList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(txnTypeInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (txnTypeInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by d.lastupdatedtime desc ";
            } else {
                sortingStr = " order by d.lastupdatedtime " + txnTypeInputBean.sortDirections.get(0);
            }

            String sql =
                    " select d.txntype as code,d.description as description, d.status as status,s.description as statusdescription," +
                            " d.createdtime as createdtime,d.createduser as createduser,d.lastupdatedtime as lastupdatedtime ,d.lastupdateduser as lastupdateduser from TXN_TYPE d " +
                            " left outer join STATUS s on s.statuscode=d.status " +
                            " where " + dynamicClause.toString() + sortingStr +
                            " limit " + txnTypeInputBean.displayLength + " offset " + txnTypeInputBean.displayStart;

            txnTypeList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                TxnType txnType = new TxnType();
                try {
                    txnType.setTxntype(rs.getString("code"));
                } catch (Exception e) {
                    txnType.setTxntype(null);
                }

                try {
                    txnType.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    txnType.setDescription(null);
                }

                try {
                    txnType.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    txnType.setStatus(null);
                }

                try {
                    txnType.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    txnType.setCreatedTime(null);
                }

                try {
                    txnType.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    txnType.setCreatedUser(null);
                }

                try {
                    txnType.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    txnType.setLastUpdatedTime(null);
                }

                try {
                    txnType.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    txnType.setLastUpdatedUser(null);
                }
                return txnType;
            });
        } catch (EmptyResultDataAccessException ex) {
            return txnTypeList;
        } catch (Exception e) {
            throw e;
        }
        return txnTypeList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(TxnTypeInputBean txnTypeInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(txnTypeInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.TXN_TYPE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getTxnTypeSearchResultsDual(TxnTypeInputBean txnTypeInputBean) {
        List<TempAuthRec> txnTypeDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(txnTypeInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (txnTypeInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + txnTypeInputBean.sortDirections.get(0);
            }
            String sql =
                    " select wta.id,wta.key1,wta.key2,wta.key3,s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                            " from WEB_TMPAUTHREC wta" +
                            " left outer join STATUS s on s.statuscode = wta.key3 " +
                            " left outer join WEB_TASK t on t.taskcode = wta.task " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause.toString() + sortingStr +
                            " limit " + txnTypeInputBean.displayLength + " offset " + txnTypeInputBean.displayStart;

            txnTypeDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.TXN_TYPE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
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
                    tempAuthRec.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    tempAuthRec.setCreatedTime(rs.getString("createdtime"));
                } catch (Exception e) {
                    tempAuthRec.setCreatedTime(null);
                }

                try {
                    tempAuthRec.setTask(rs.getString("taskdescription"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    tempAuthRec.setLastUpdatedTime(rs.getString("lastupdatedtime"));
                } catch (Exception e) {
                    tempAuthRec.setLastUpdatedTime(null);
                }

                try {
                    tempAuthRec.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    tempAuthRec.setLastUpdatedUser(null);
                }
                return tempAuthRec;
            });
        } catch (Exception e) {
            throw e;
        }
        return txnTypeDualList;
    }

    @Transactional
    public String insertTxnType(TxnTypeInputBean txnTypeInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;

            System.out.println("time & user > ******************* " + txnTypeInputBean.getLastUpdatedTime() + " | " + txnTypeInputBean.getLastUpdatedUser());
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_TXNTYPE, txnTypeInputBean.getTxntype(),
                    txnTypeInputBean.getDescription(),
                    txnTypeInputBean.getStatus(),
                    txnTypeInputBean.getCreatedTime(),
                    txnTypeInputBean.getCreatedUser(),
                    txnTypeInputBean.getLastUpdatedTime(),
                    txnTypeInputBean.getLastUpdatedUser());
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (DuplicateKeyException ex) {
            throw ex;
        } catch (Exception e) {
            throw e;
        }
        return message;
    }

    @Transactional(readOnly = true)
    public TxnType getTxnType(String code) {
        TxnType txnType = null;
        try {
            txnType = jdbcTemplate.queryForObject(SQL_FIND_TXNTYPE, new Object[]{code}, (rs, rowNum) -> {
                TxnType dpt = new TxnType();

                try {
                    dpt.setTxntype(rs.getString("txntype"));
                } catch (Exception e) {
                    dpt.setTxntype(null);
                }

                try {
                    dpt.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    dpt.setDescription(null);
                }

                try {
                    dpt.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    dpt.setStatus(null);
                }

                try {
                    dpt.setCreatedTime(new Date(rs.getDate("createdtime").getTime()));
                } catch (Exception e) {
                    dpt.setCreatedTime(null);
                }

                try {
                    dpt.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    dpt.setCreatedTime(null);
                }

                try {
                    dpt.setLastUpdatedTime(new Date(rs.getDate("lastupdatedtime").getTime()));
                } catch (Exception e) {
                    dpt.setLastUpdatedTime(null);
                }

                try {
                    dpt.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    dpt.setLastUpdatedUser(null);
                }

                return dpt;
            });
        } catch (EmptyResultDataAccessException erse) {
            txnType = null;
        } catch (Exception e) {
            throw e;
        }
        return txnType;
    }


    @Transactional
    public String updateTxnType(TxnTypeInputBean txnTypeInputBean) {

        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_TXNTYPE,
                    txnTypeInputBean.getDescription(),
                    txnTypeInputBean.getStatus(),
                    txnTypeInputBean.getLastUpdatedTime(),
                    txnTypeInputBean.getLastUpdatedUser(),
                    txnTypeInputBean.getTxntype());
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteTxnType(String code) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_TXNTYPE, code);
            if (count < 0) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
            throw cve;
        } catch (Exception e) {
            throw e;
        }
        return message;
    }

    private StringBuilder setDynamicClause(TxnTypeInputBean txnTypeInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (txnTypeInputBean.getTxntype() != null && !txnTypeInputBean.getTxntype().isEmpty()) {
                dynamicClause.append("and lower(d.txntype) like lower('%").append(txnTypeInputBean.getTxntype()).append("%')");
            }

            if (txnTypeInputBean.getDescription() != null && !txnTypeInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(d.description) like lower('%").append(txnTypeInputBean.getDescription()).append("%')");
            }

            if (txnTypeInputBean.getStatus() != null && !txnTypeInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and d.status = '").append(txnTypeInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(TxnTypeInputBean txnTypeInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (txnTypeInputBean.getTxntype() != null && !txnTypeInputBean.getTxntype().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(txnTypeInputBean.getTxntype()).append("%')");
            }

            if (txnTypeInputBean.getDescription() != null && !txnTypeInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(txnTypeInputBean.getDescription()).append("%')");
            }

            if (txnTypeInputBean.getStatus() != null && !txnTypeInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key3 = '").append(txnTypeInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }


}
