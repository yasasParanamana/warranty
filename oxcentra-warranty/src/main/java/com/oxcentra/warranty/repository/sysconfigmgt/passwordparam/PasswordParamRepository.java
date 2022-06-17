package com.oxcentra.warranty.repository.sysconfigmgt.passwordparam;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.sysconfigmgt.passwordparam.PasswordParamInputBean;
import com.oxcentra.warranty.mapping.sectionmgt.PasswordParam;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Scope("prototype")
public class PasswordParamRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from PASSWORDPARAM p where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC d where d.page=? and d.status=? and d.lastupdateduser <> ? and ";
    private final String SQL_UPDATE_PASSWORD_PARAM = "update PASSWORDPARAM set value=?,userroletype=?,lastupdateduser=?,lastupdatedtime=? where passwordparam=?";
    private final String SQL_FIND_PASSWORD_PARAM = "select p.passwordparam,p.userroletype,p.value,p.createdtime,p.lastupdatedtime,p.lastupdateduser from PASSWORDPARAM p where p.passwordparam = ? ";

    @Transactional(readOnly = true)
    public long getDataCount(PasswordParamInputBean passwordParamInputBean) throws Exception {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(passwordParamInputBean, dynamicClause);
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
    public List<PasswordParam> getPasswordParamSearchResults(PasswordParamInputBean passwordParamInputBean) throws Exception {
        List<PasswordParam> passwordParamList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(passwordParamInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (passwordParamInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by p.lastupdatedtime desc ";
            } else {
                sortingStr = " order by p.lastupdatedtime " + passwordParamInputBean.sortDirections.get(0);
            }

            String sql =
                    " select p.passwordparam, p.userroletype, p.value, p.createdtime from PASSWORDPARAM p " +
                            " where " + dynamicClause.toString() + sortingStr +
                            " limit " + passwordParamInputBean.displayLength + " offset " + passwordParamInputBean.displayStart;

            passwordParamList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                PasswordParam passwordParam = new PasswordParam();

                try {
                    passwordParam.setPasswordparam(rs.getString("passwordparam"));
                } catch (Exception e) {
                    passwordParam.setPasswordparam(null);
                }

                try {
                    passwordParam.setUserroletype(rs.getString("userroletype"));
                } catch (Exception e) {
                    passwordParam.setUserroletype(null);
                }

                try {
                    passwordParam.setValue(rs.getString("value"));
                } catch (Exception e) {
                    passwordParam.setValue(null);
                }

                try {
                    passwordParam.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    passwordParam.setCreatedTime(null);
                }
                return passwordParam;
            });
        } catch (Exception e) {
            throw e;
        }
        return passwordParamList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(PasswordParamInputBean passwordParamInputBean) throws Exception {
        long count = 0;
        StringBuilder dynamicClause = new StringBuilder();
        try {
            dynamicClause.append(SQL_GET_LIST_DUAL_DATA_COUNT);
            this.setDynamicClauseDual(passwordParamInputBean, dynamicClause);
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.PASSWORD_PARAMETER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (DataAccessException ex) {
            throw ex;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getPasswordParamSearchResultsDual(PasswordParamInputBean passwordParamInputBean) throws Exception {
        List<TempAuthRec> passwordParamDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(passwordParamInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (passwordParamInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + passwordParamInputBean.sortDirections.get(0);
            }

            String sql =
                    " select wta.id, wta.key1, wta.key2, wta.key3, t.description task, wta.createdtime, wta.lastupdatedtime, wta.lastupdateduser " +
                            " from WEB_TMPAUTHREC wta" +
                            " left outer join WEB_TASK t on t.taskcode = wta.task " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause.toString() + sortingStr +
                            " limit " + passwordParamInputBean.displayLength + " offset " + passwordParamInputBean.displayStart;

            passwordParamDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.PASSWORD_PARAMETER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRecBean = new TempAuthRec();

                try {
                    tempAuthRecBean.setId(rs.getString("id"));
                } catch (Exception e) {
                    tempAuthRecBean.setId(null);
                }

                try {
                    tempAuthRecBean.setTask(rs.getString("task"));
                } catch (Exception e) {
                    tempAuthRecBean.setTask(null);
                }

                try {
                    //password param code
                    tempAuthRecBean.setKey1(rs.getString("key1"));
                } catch (Exception e) {
                    tempAuthRecBean.setKey1(null);
                }

                try {
                    //user role type
                    tempAuthRecBean.setKey2(rs.getString("key2"));
                } catch (Exception e) {
                    tempAuthRecBean.setKey2(null);
                }

                try {
                    //value
                    tempAuthRecBean.setKey3(rs.getString("key3"));
                } catch (Exception e) {
                    tempAuthRecBean.setKey3(null);
                }

                try {
                    tempAuthRecBean.setLastUpdatedTime(rs.getString("LASTUPDATEDTIME"));
                } catch (Exception e) {
                    tempAuthRecBean.setLastUpdatedTime(null);
                }

                try {
                    tempAuthRecBean.setLastUpdatedUser(rs.getString("LASTUPDATEDUSER"));
                } catch (Exception e) {
                    tempAuthRecBean.setLastUpdatedUser(null);
                }

                try {
                    tempAuthRecBean.setCreatedTime(rs.getString("CREATEDTIME"));
                } catch (Exception e) {
                    tempAuthRecBean.setCreatedTime(null);
                }

                return tempAuthRecBean;
            });
        } catch (Exception e) {
            throw e;
        }
        return passwordParamDualList;
    }

    @Transactional(readOnly = true)
    public PasswordParam getPasswordParam(String passwordParamCode) throws Exception {
        PasswordParam pwdParam;
        try {
            pwdParam = jdbcTemplate.queryForObject(SQL_FIND_PASSWORD_PARAM, new Object[]{passwordParamCode}, (rs, rowNum) -> {
                PasswordParam passwordParam = new PasswordParam();

                try {
                    passwordParam.setPasswordparam(rs.getString("passwordparam"));
                } catch (Exception e) {
                    passwordParam.setPasswordparam(null);
                }

                try {
                    passwordParam.setUserroletype(rs.getString("userroletype"));
                } catch (Exception e) {
                    passwordParam.setUserroletype(null);
                }

                try {
                    passwordParam.setValue(rs.getString("value"));
                } catch (Exception e) {
                    passwordParam.setValue(null);
                }

                try {
                    passwordParam.setCreatedTime(new Date(rs.getDate("createdtime").getTime()));
                } catch (Exception e) {
                    passwordParam.setCreatedTime(null);
                }

                try {
                    passwordParam.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    passwordParam.setLastUpdatedUser(null);
                }

                try {
                    passwordParam.setLastUpdatedTime(new Date(rs.getDate("lastupdatedtime").getTime()));
                } catch (Exception e) {
                    passwordParam.setLastUpdatedTime(null);
                }
                return passwordParam;
            });
        } catch (DataAccessException ex) {
            throw ex;
        }
        return pwdParam;
    }

    @Transactional
    public String updatePasswordParam(PasswordParamInputBean passwordParamInputBean) throws Exception {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_PASSWORD_PARAM,
                    passwordParamInputBean.getValue(),
                    passwordParamInputBean.getUserroletype(),
                    passwordParamInputBean.getLastUpdatedUser(),
                    passwordParamInputBean.getLastUpdatedTime(),
                    passwordParamInputBean.getPasswordparam());
            if (value != 1) {
                message = "Error occurred while updating password param";
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    private StringBuilder setDynamicClause(PasswordParamInputBean paramBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (paramBean.getPasswordparam() != null && !paramBean.getPasswordparam().isEmpty()) {
                dynamicClause.append("and p.passwordparam = '").append(paramBean.getPasswordparam()).append("'");
            }

            if (paramBean.getUserroletype() != null && !paramBean.getUserroletype().isEmpty()) {
                dynamicClause.append("and p.userroletype = '").append(paramBean.getUserroletype()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(PasswordParamInputBean passwordParamInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (passwordParamInputBean.getPasswordparam() != null && !passwordParamInputBean.getPasswordparam().isEmpty()) {
                dynamicClause.append("and d.key1 = '").append(passwordParamInputBean.getPasswordparam()).append("'");
            }

            if (passwordParamInputBean.getUserroletype() != null && !passwordParamInputBean.getUserroletype().isEmpty()) {
                dynamicClause.append("and d.key2 = '").append(passwordParamInputBean.getUserroletype()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
