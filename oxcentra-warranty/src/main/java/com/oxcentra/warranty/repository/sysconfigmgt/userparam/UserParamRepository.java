package com.oxcentra.warranty.repository.sysconfigmgt.userparam;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.sysconfigmgt.userparam.UserParamInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.userparam.UserParam;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
@Scope("prototype")
public class UserParamRepository {
    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from USERPARAM d left outer join STATUS s on s.statuscode=d.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";
    private final String SQL_INSERT_USERPARAM = "insert into USERPARAM(paramcode,description,category,status,lastupdateduser,lastupdatedtime,createduser,createdtime) values (?,?,?,?,?,?,?,?) ";
    private final String SQL_FIND_USERPARAM = "select paramcode,description,category,status,lastupdateduser,lastupdatedtime,createdtime from USERPARAM d where d.paramcode = ?";
    private final String SQL_UPDATE_USERPARAM = "update USERPARAM d set d.description = ? , d.status = ?, d.lastupdateduser = ?, d.lastupdatedtime = ? where d.paramcode = ?";
    private final String SQL_DELETE_USERPARAM = "delete from USERPARAM where paramcode = ?";

    @Transactional(readOnly = true)
    public long getDataCount(UserParamInputBean userParamInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(userParamInputBean, dynamicClause);
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
    public List<UserParam> getUserParamSearchList(UserParamInputBean userParamInputBean) {
        List<UserParam> userParamList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(userParamInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (userParamInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by d.lastupdatedtime desc ";
            } else {
                sortingStr = " order by d.lastupdatedtime " + userParamInputBean.sortDirections.get(0);
            }

            String sql =
                    " select d.paramcode as code,d.description as description,d.category as category,upc.description as userparamcategorydescription,d.status as status,s.description as statusdescription," +
                            " d.lastupdateduser as lastupdateduser,d.createduser as createduser,d.lastupdatedtime as lastupdatedtime, d.createdtime as createdtime  from USERPARAM d " +
                            " left outer join STATUS s on s.statuscode=d.status " +
                            " left outer join USERPARAMCATEGORY upc on upc.code=d.category " +
                            " where " + dynamicClause.toString() + sortingStr +
                            " limit " + userParamInputBean.displayLength + " offset " + userParamInputBean.displayStart;

            userParamList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                UserParam userParam = new UserParam();
                try {
                    userParam.setParamCode(rs.getString("code"));
                } catch (Exception e) {
                    userParam.setParamCode(null);
                }

                try {
                    userParam.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    userParam.setDescription(null);
                }

                try {
                    userParam.setCategory(rs.getString("userparamcategorydescription"));
                } catch (Exception e) {
                    userParam.setCategory(null);
                }

                try {
                    userParam.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    userParam.setStatus(null);
                }

                try {
                    userParam.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    userParam.setCreatedTime(null);
                }

                try {
                    userParam.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    userParam.setLastUpdatedTime(null);
                }

                try {
                    userParam.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    userParam.setLastUpdatedUser(null);
                }

                try {
                    userParam.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    userParam.setCreatedUser(null);
                }


                return userParam;
            });
        } catch (EmptyResultDataAccessException ex) {
            return userParamList;
        } catch (Exception e) {
            throw e;
        }
        return userParamList;
    }


    @Transactional(readOnly = true)
    public long getDataCountDual(UserParamInputBean userParamInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(userParamInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.USERPARAMETER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getUserParamSearchResultsDual(UserParamInputBean userParamInputBean) {
        List<TempAuthRec> userParamtDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(userParamInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (userParamInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + userParamInputBean.sortDirections.get(0);
            }
            String sql = "" +
                    " select wta.id,wta.key1,wta.key2,upc.description as userparamdescription,s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                    " from WEB_TMPAUTHREC wta" +
                    " left outer join USERPARAMCATEGORY upc on upc.code = wta.key3 " +
                    " left outer join STATUS s on s.statuscode = wta.key4 " +
                    " left outer join WEB_TASK t on t.taskcode = wta.task " +
                    " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause.toString() + sortingStr +
                    " limit " + userParamInputBean.displayLength + " offset " + userParamInputBean.displayStart;

            userParamtDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.USERPARAMETER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRec = new TempAuthRec();

                try {
                    tempAuthRec.setId(rs.getString("id"));
                } catch (Exception e) {
                    tempAuthRec.setId(null);
                }

                try {
                    //param code
                    tempAuthRec.setKey1(rs.getString("key1"));
                } catch (Exception e) {
                    tempAuthRec.setKey1(null);
                }

                try {
                    //description
                    tempAuthRec.setKey2(rs.getString("key2"));
                } catch (Exception e) {
                    tempAuthRec.setKey2(null);
                }


                try {
                    //category
                    tempAuthRec.setKey3(rs.getString("userparamdescription"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
                }


                try {
                    //status
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
        return userParamtDualList;
    }

    @Transactional
    public String insertUserParam(UserParamInputBean userParamInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_USERPARAM, userParamInputBean.getParamCode(),
                    userParamInputBean.getDescription(),
                    userParamInputBean.getCategory(),
                    userParamInputBean.getStatus(),
                    userParamInputBean.getLastUpdatedUser(),
                    userParamInputBean.getLastUpdatedTime(),
                    userParamInputBean.getCreatedUser(),
                    userParamInputBean.getCreatedTime());
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
    public UserParam getUserParam(String code) {
        UserParam userParam = null;
        try {
            userParam = jdbcTemplate.queryForObject(SQL_FIND_USERPARAM, new Object[]{code}, new RowMapper<UserParam>() {
                @Override
                public UserParam mapRow(ResultSet rs, int rowNum) throws SQLException {
                    UserParam userParam = new UserParam();

                    try {
                        userParam.setParamCode(rs.getString("paramcode"));
                    } catch (Exception e) {
                        userParam.setParamCode(null);
                    }

                    try {
                        userParam.setDescription(rs.getString("description"));
                    } catch (Exception e) {
                        userParam.setDescription(null);
                    }

                    try {
                        userParam.setCategory(rs.getString("category"));
                    } catch (Exception e) {
                        userParam.setCategory(null);
                    }

                    try {
                        userParam.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        userParam.setStatus(null);
                    }

                    try {
                        userParam.setCreatedTime(new Date(rs.getDate("createdtime").getTime()));
                    } catch (Exception e) {
                        userParam.setCreatedTime(null);
                    }

                    try {
                        userParam.setLastUpdatedTime(new Date(rs.getDate("lastupdatedtime").getTime()));
                    } catch (SQLException e) {
                        userParam.setLastUpdatedTime(null);
                    }

                    try {
                        userParam.setLastUpdatedUser(rs.getString("lastupdateduser"));
                    } catch (SQLException e) {
                        userParam.setLastUpdatedUser(null);
                    }

                    return userParam;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            userParam = null;
        } catch (Exception e) {
            throw e;
        }
        return userParam;
    }

    @Transactional
    public String updateUserParam(UserParamInputBean userParamInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_USERPARAM,
                    userParamInputBean.getDescription(),
                    userParamInputBean.getStatus(),
                    userParamInputBean.getLastUpdatedUser(),
                    userParamInputBean.getLastUpdatedTime(),
                    userParamInputBean.getParamCode());
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteUserParam(String code) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_USERPARAM, code);
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


    private StringBuilder setDynamicClause(UserParamInputBean userParamInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (userParamInputBean.getParamCode() != null && !userParamInputBean.getParamCode().isEmpty()) {
                dynamicClause.append("and lower(d.paramcode) like lower('%").append(userParamInputBean.getParamCode()).append("%')");
            }

            if (userParamInputBean.getDescription() != null && !userParamInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(d.description) like lower('%").append(userParamInputBean.getDescription()).append("%')");
            }

            if (userParamInputBean.getStatus() != null && !userParamInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and d.status = '").append(userParamInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }


    private StringBuilder setDynamicClauseDual(UserParamInputBean userParamInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (userParamInputBean.getParamCode() != null && !userParamInputBean.getParamCode().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(userParamInputBean.getParamCode()).append("%')");
            }

            if (userParamInputBean.getDescription() != null && !userParamInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(userParamInputBean.getDescription()).append("%')");
            }

            if (userParamInputBean.getStatus() != null && !userParamInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key3 = '").append(userParamInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }


}
