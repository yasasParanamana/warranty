package com.epic.rdbsms.repository.sysconfigmgt.passwordpolicy;

import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.passwordpolicy.PasswordPolicyInputBean;
import com.epic.rdbsms.mapping.passwordhistory.PasswordHistory;
import com.epic.rdbsms.mapping.passwordpolicy.PasswordPolicy;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.varlist.CommonVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.StatusVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Scope("prototype")
public class PasswordPolicyRepository {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Common common;

    @Autowired
    SessionBean sessionBean;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from WEB_PASSWORDPOLICY p where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC d where d.page=? and d.status=? and d.lastupdateduser <> ? and ";
    private final String SQL_FIND_WEB_PASSWORDPOLICY = "select passwordpolicyid , minimumlength , maximumlength , minimumspecialcharacters , minimumuppercasecharacters , minimumnumericalcharacters , minimumlowercasecharacters , noofinvalidloginattempt , repeatcharactersallow , initialpasswordexpirystatus , passwordexpiryperiod , noofhistorypassword , minimumpasswordchangeperiod , idleaccountexpiryperiod , description , lastupdateduser , lastupdatedtime , createtime from WEB_PASSWORDPOLICY where passwordpolicyid=?";
    private final String SQL_UPDATE_PASSWORDPOLICY = "update WEB_PASSWORDPOLICY set minimumlength=?,maximumlength=?,minimumspecialcharacters=?,minimumuppercasecharacters=?,minimumnumericalcharacters=?,minimumlowercasecharacters=?,noofinvalidloginattempt=?,repeatcharactersallow=?,initialpasswordexpirystatus=?,passwordexpiryperiod=?,noofhistorypassword=?,minimumpasswordchangeperiod=?,idleaccountexpiryperiod=?,description=?,lastupdateduser=?,lastupdatedtime=? where passwordpolicyid=?";
    private final String SQL_GET_WEB_PASSWORDPOLICY = "select passwordpolicyid , minimumlength , maximumlength , minimumspecialcharacters , minimumuppercasecharacters , minimumnumericalcharacters , minimumlowercasecharacters , noofinvalidloginattempt , repeatcharactersallow , initialpasswordexpirystatus , passwordexpiryperiod , noofhistorypassword , minimumpasswordchangeperiod , idleaccountexpiryperiod , description , lastupdateduser , lastupdatedtime , createtime from WEB_PASSWORDPOLICY where passwordpolicyid = ?";
    private final String SQL_GET_WEB_PASSWORDHISTORYLIST = "select id,username,password,lastupdateduser,lastupdatedtime,createdtime from WEB_PASSWORDHISTORY where username = ? order by id desc limit ?";

    @Transactional(readOnly = true)
    public long getDataCount(PasswordPolicyInputBean passwordPolicy) throws Exception {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(passwordPolicy, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<PasswordPolicy> getPasswordPolicySearchResults(PasswordPolicyInputBean passwordPolicyInputBean) throws Exception {
        List<PasswordPolicy> passwordPolicyList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(passwordPolicyInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (passwordPolicyInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by p.lastupdatedtime desc ";
            } else {
                sortingStr = " order by p.lastupdatedtime " + passwordPolicyInputBean.sortDirections.get(0);
            }

            String sql =
                    " select p.passwordpolicyid, p.minimumlength, p.maximumlength, p.minimumspecialcharacters, p.minimumuppercasecharacters," +
                            " p.minimumnumericalcharacters, p.minimumlowercasecharacters, p.lastupdateduser, p.lastupdatedtime, p.createtime," +
                            " p.noofinvalidloginattempt, p.repeatcharactersallow, p.initialpasswordexpirystatus, p.passwordexpiryperiod," +
                            " p.noofhistorypassword, p.minimumpasswordchangeperiod, p.idleaccountexpiryperiod, p.description from WEB_PASSWORDPOLICY p " +
                            " limit " + passwordPolicyInputBean.displayLength + " offset " + passwordPolicyInputBean.displayStart;

            passwordPolicyList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                PasswordPolicy passwordPolicy = new PasswordPolicy();

                try {
                    passwordPolicy.setPasswordPolicyId(rs.getInt("passwordpolicyid"));
                } catch (Exception e) {
                    passwordPolicy.setPasswordPolicyId(0);
                }

                try {
                    passwordPolicy.setMinimumLength(rs.getInt("minimumlength"));
                } catch (Exception e) {
                    passwordPolicy.setMinimumLength(0);
                }

                try {
                    passwordPolicy.setMaximumLength(rs.getInt("maximumlength"));
                } catch (Exception e) {
                    passwordPolicy.setMaximumLength(0);
                }

                try {
                    passwordPolicy.setMinimumSpecialCharacters(rs.getInt("minimumspecialcharacters"));
                } catch (Exception e) {
                    passwordPolicy.setMinimumSpecialCharacters(0);
                }

                try {
                    passwordPolicy.setMinimumUpperCaseCharacters(rs.getInt("minimumuppercasecharacters"));
                } catch (Exception e) {
                    passwordPolicy.setMinimumUpperCaseCharacters(0);
                }

                try {
                    passwordPolicy.setMinimumNumericalCharacters(rs.getInt("minimumnumericalcharacters"));
                } catch (Exception e) {
                    passwordPolicy.setMinimumNumericalCharacters(0);
                }

                try {
                    passwordPolicy.setMinimumLowerCaseCharacters(rs.getInt("minimumlowercasecharacters"));
                } catch (Exception e) {
                    passwordPolicy.setMinimumLowerCaseCharacters(0);
                }

                try {
                    passwordPolicy.setNoOfInvalidLoginAttempt(rs.getInt("noofinvalidloginattempt"));
                } catch (Exception e) {
                    passwordPolicy.setNoOfInvalidLoginAttempt(0);
                }

                try {
                    passwordPolicy.setRepeatCharactersAllow(rs.getInt("repeatcharactersallow"));
                } catch (Exception e) {
                    passwordPolicy.setRepeatCharactersAllow(0);
                }

                try {
                    passwordPolicy.setInitialPasswordExpiryStatus(rs.getInt("initialpasswordexpirystatus"));
                } catch (Exception e) {
                    passwordPolicy.setInitialPasswordExpiryStatus(0);
                }

                try {
                    passwordPolicy.setPasswordExpiryPeriod(rs.getInt("passwordexpiryperiod"));
                } catch (Exception e) {
                    passwordPolicy.setPasswordExpiryPeriod(0);
                }

                try {
                    passwordPolicy.setNoOfHistoryPassword(rs.getInt("noofhistorypassword"));
                } catch (Exception e) {
                    passwordPolicy.setNoOfHistoryPassword(0);
                }

                try {
                    passwordPolicy.setMinimumPasswordChangePeriod(rs.getInt("minimumpasswordchangeperiod"));
                } catch (Exception e) {
                    passwordPolicy.setMinimumPasswordChangePeriod(0);
                }

                try {
                    passwordPolicy.setIdleAccountExpiryPeriod(rs.getInt("idleaccountexpiryperiod"));
                } catch (Exception e) {
                    passwordPolicy.setIdleAccountExpiryPeriod(0);
                }

                try {
                    passwordPolicy.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    passwordPolicy.setDescription(null);
                }

                try {
                    passwordPolicy.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    passwordPolicy.setLastUpdatedUser(null);
                }
                return passwordPolicy;
            });
        } catch (EmptyResultDataAccessException ex) {
            return passwordPolicyList;
        } catch (Exception e) {
            throw e;
        }
        return passwordPolicyList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(PasswordPolicyInputBean passwordPolicyInputBean) throws Exception {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(passwordPolicyInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.PASSWORDPOLICY_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getPasswordPolicySearchResultsDual(PasswordPolicyInputBean passwordPolicyInputBean) throws Exception {
        List<TempAuthRec> passwordPolicyDualList;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(passwordPolicyInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (passwordPolicyInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + passwordPolicyInputBean.sortDirections.get(0);
            }
            String sql =
                    " select wta.id,wta.key1,wta.key2,wta.key3,wta.key4,wta.key5,wta.key6,wta.key7,wta.key8,wta.key9,wta.key10,wta.key11,wta.key12,wta.key13,wta.key14,wta.key15,t.description task,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                            " from WEB_TMPAUTHREC wta" +
                            " left outer join WEB_TASK t on t.taskcode = wta.task " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause.toString() + sortingStr +
                            " limit " + passwordPolicyInputBean.displayLength + " offset " + passwordPolicyInputBean.displayStart;

            passwordPolicyDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.PASSWORDPOLICY_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRec = new TempAuthRec();

                try {
                    tempAuthRec.setId(rs.getString("id"));
                } catch (Exception e) {
                    tempAuthRec.setId(null);
                }

                try {
                    tempAuthRec.setTask(rs.getString("task"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    //password policy id
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
                    tempAuthRec.setKey5(rs.getString("key5"));
                } catch (Exception e) {
                    tempAuthRec.setKey5(null);
                }

                try {
                    tempAuthRec.setKey6(rs.getString("key6"));
                } catch (Exception e) {
                    tempAuthRec.setKey6(null);
                }

                try {
                    tempAuthRec.setKey7(rs.getString("key7"));
                } catch (Exception e) {
                    tempAuthRec.setKey7(null);
                }

                try {
                    tempAuthRec.setKey8(rs.getString("key8"));
                } catch (Exception e) {
                    tempAuthRec.setKey8(null);
                }

                try {
                    tempAuthRec.setKey9(rs.getString("key9"));
                } catch (Exception e) {
                    tempAuthRec.setKey9(null);
                }

                try {
                    tempAuthRec.setKey10(rs.getString("key10"));
                } catch (Exception e) {
                    tempAuthRec.setKey10(null);
                }

                try {
                    tempAuthRec.setKey11(rs.getString("key11"));
                } catch (Exception e) {
                    tempAuthRec.setKey11(null);
                }

                try {
                    tempAuthRec.setKey12(rs.getString("key12"));
                } catch (Exception e) {
                    tempAuthRec.setKey12(null);
                }

                try {
                    tempAuthRec.setKey13(rs.getString("key13"));
                } catch (Exception e) {
                    tempAuthRec.setKey13(null);
                }

                try {
                    tempAuthRec.setKey14(rs.getString("key14"));
                } catch (Exception e) {
                    tempAuthRec.setKey14(null);
                }

                try {
                    tempAuthRec.setKey15(rs.getString("key15"));
                } catch (Exception e) {
                    tempAuthRec.setKey15(null);
                }

                try {
                    tempAuthRec.setCreatedTime(rs.getString("createdtime"));
                } catch (Exception e) {
                    tempAuthRec.setCreatedTime(null);
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
        return passwordPolicyDualList;
    }

    @Transactional(readOnly = true)
    public PasswordPolicy getWebPasswordPolicy(int passwordPolicyCode) {
        PasswordPolicy passwordPolicy = null;
        try {
            passwordPolicy = jdbcTemplate.queryForObject(SQL_FIND_WEB_PASSWORDPOLICY, new Object[]{passwordPolicyCode}, (rs, rowNum) -> {
                PasswordPolicy pwdPolicy = new PasswordPolicy();

                try {
                    pwdPolicy.setPasswordPolicyId(rs.getLong("passwordpolicyid"));
                } catch (Exception e) {
                    pwdPolicy.setPasswordPolicyId(0);
                }

                try {
                    pwdPolicy.setMinimumLength(rs.getLong("minimumlength"));
                } catch (Exception e) {
                    pwdPolicy.setMinimumLength(0);
                }

                try {
                    pwdPolicy.setMaximumLength(rs.getLong("maximumlength"));
                } catch (Exception e) {
                    pwdPolicy.setMaximumLength(0);
                }

                try {
                    pwdPolicy.setMinimumSpecialCharacters(rs.getLong("minimumspecialcharacters"));
                } catch (Exception e) {
                    pwdPolicy.setMinimumSpecialCharacters(0);
                }

                try {
                    pwdPolicy.setMinimumUpperCaseCharacters(rs.getLong("minimumuppercasecharacters"));
                } catch (Exception e) {
                    pwdPolicy.setMinimumUpperCaseCharacters(0);
                }

                try {
                    pwdPolicy.setMinimumNumericalCharacters(rs.getLong("minimumnumericalcharacters"));
                } catch (Exception e) {
                    pwdPolicy.setMinimumNumericalCharacters(0);
                }

                try {
                    pwdPolicy.setMinimumLowerCaseCharacters(rs.getLong("minimumlowercasecharacters"));
                } catch (Exception e) {
                    pwdPolicy.setMinimumLowerCaseCharacters(0);
                }

                try {
                    pwdPolicy.setNoOfInvalidLoginAttempt(rs.getLong("noofinvalidloginattempt"));
                } catch (Exception e) {
                    pwdPolicy.setNoOfInvalidLoginAttempt(0);
                }

                try {
                    pwdPolicy.setRepeatCharactersAllow(rs.getLong("repeatcharactersallow"));
                } catch (Exception e) {
                    pwdPolicy.setRepeatCharactersAllow(0);
                }

                try {
                    pwdPolicy.setInitialPasswordExpiryStatus(rs.getLong("initialpasswordexpirystatus"));
                } catch (Exception e) {
                    pwdPolicy.setInitialPasswordExpiryStatus(0);
                }

                try {
                    pwdPolicy.setPasswordExpiryPeriod(rs.getLong("passwordexpiryperiod"));
                } catch (Exception e) {
                    pwdPolicy.setPasswordExpiryPeriod(0);
                }

                try {
                    pwdPolicy.setNoOfHistoryPassword(rs.getLong("noofhistorypassword"));
                } catch (Exception e) {
                    pwdPolicy.setNoOfHistoryPassword(0);
                }

                try {
                    pwdPolicy.setMinimumPasswordChangePeriod(rs.getLong("minimumpasswordchangeperiod"));
                } catch (Exception e) {
                    pwdPolicy.setMinimumPasswordChangePeriod(0);
                }

                try {
                    pwdPolicy.setIdleAccountExpiryPeriod(rs.getLong("idleaccountexpiryperiod"));
                } catch (Exception e) {
                    pwdPolicy.setIdleAccountExpiryPeriod(0);
                }

                try {
                    pwdPolicy.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    pwdPolicy.setDescription(null);
                }

                try {
                    pwdPolicy.setCreatedTime(rs.getDate("createtime"));
                } catch (Exception e) {
                    pwdPolicy.setCreatedTime(null);
                }

                try {
                    pwdPolicy.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    pwdPolicy.setLastUpdatedUser(null);
                }

                try {
                    pwdPolicy.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    pwdPolicy.setLastUpdatedTime(null);
                }
                return pwdPolicy;
            });
        } catch (EmptyResultDataAccessException ex) {
            return passwordPolicy;
        } catch (Exception e) {
            throw e;
        }
        return passwordPolicy;
    }

    @Transactional(readOnly = true)
    public List<PasswordHistory> getPasswordHistoryList(String userName, int noOfHistoryPassword) {
        List<PasswordHistory> passwordHistoryList;
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(SQL_GET_WEB_PASSWORDHISTORYLIST, userName, noOfHistoryPassword);
            passwordHistoryList = list.stream().map((record) -> {
                PasswordHistory passwordHistory = new PasswordHistory();
                passwordHistory.setId(new BigInteger(record.get("id").toString()).longValue());
                passwordHistory.setUserName(record.get("username").toString());
                passwordHistory.setPassword(record.get("password").toString());
                passwordHistory.setLastUpdatedUser(record.get("lastupdateduser").toString());
                passwordHistory.setCreatedTime((Date) record.get("createdtime"));
                passwordHistory.setLastUpdatedTime((Date) record.get("lastupdatedtime"));
                return passwordHistory;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ex) {
            passwordHistoryList = null;
        } catch (Exception e) {
            throw e;
        }
        return passwordHistoryList;
    }

    @Transactional(readOnly = true)
    public PasswordPolicy getPasswordPolicy(String passwordPolicy) throws Exception {
        PasswordPolicy pwdPolicy;
        try {
            pwdPolicy = jdbcTemplate.queryForObject(SQL_FIND_WEB_PASSWORDPOLICY, new Object[]{passwordPolicy}, (rs, rowNum) -> {
                PasswordPolicy policy = new PasswordPolicy();

                try {
                    policy.setPasswordPolicyId(rs.getLong("passwordpolicyid"));
                } catch (Exception e) {
                    policy.setPasswordPolicyId(0);
                }

                try {
                    policy.setMinimumLength(rs.getLong("minimumlength"));
                } catch (Exception e) {
                    policy.setMinimumLength(0);
                }

                try {
                    policy.setMaximumLength(rs.getLong("maximumlength"));
                } catch (Exception e) {
                    policy.setMaximumLength(0);
                }

                try {
                    policy.setMinimumSpecialCharacters(rs.getLong("minimumspecialcharacters"));
                } catch (Exception e) {
                    policy.setMinimumSpecialCharacters(0);
                }

                try {
                    policy.setMinimumUpperCaseCharacters(rs.getLong("minimumuppercasecharacters"));
                } catch (Exception e) {
                    policy.setMinimumUpperCaseCharacters(0);
                }

                try {
                    policy.setMinimumNumericalCharacters(rs.getLong("minimumnumericalcharacters"));
                } catch (Exception e) {
                    policy.setMinimumNumericalCharacters(0);
                }

                try {
                    policy.setMinimumLowerCaseCharacters(rs.getLong("minimumlowercasecharacters"));
                } catch (Exception e) {
                    policy.setMinimumLowerCaseCharacters(0);
                }

                try {
                    policy.setNoOfInvalidLoginAttempt(Long.parseLong(rs.getString("noofinvalidloginattempt")));
                } catch (Exception e) {
                    policy.setNoOfInvalidLoginAttempt(0);
                }

                try {
                    policy.setRepeatCharactersAllow(Long.parseLong(rs.getString("repeatcharactersallow")));
                } catch (Exception e) {
                    policy.setRepeatCharactersAllow(0);
                }

                try {
                    policy.setInitialPasswordExpiryStatus(Long.parseLong(rs.getString("initialpasswordexpirystatus")));
                } catch (Exception e) {
                    policy.setInitialPasswordExpiryStatus(0);
                }

                try {
                    policy.setPasswordExpiryPeriod(Long.parseLong(rs.getString("passwordexpiryperiod")));
                } catch (Exception e) {
                    policy.setPasswordExpiryPeriod(0);
                }

                try {
                    policy.setNoOfHistoryPassword(Long.parseLong(rs.getString("noofhistorypassword")));
                } catch (Exception e) {
                    policy.setNoOfHistoryPassword(0);
                }

                try {
                    policy.setMinimumPasswordChangePeriod(Long.parseLong(rs.getString("minimumpasswordchangeperiod")));
                } catch (Exception e) {
                    policy.setMinimumPasswordChangePeriod(0);
                }

                try {
                    policy.setIdleAccountExpiryPeriod(Long.parseLong(rs.getString("idleaccountexpiryperiod")));
                } catch (Exception e) {
                    policy.setIdleAccountExpiryPeriod(0);
                }

                try {
                    policy.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    policy.setDescription(null);
                }

                try {
                    policy.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    policy.setLastUpdatedUser(null);
                }
                return policy;
            });
        } catch (DataAccessException ex) {
            throw ex;
        }
        return pwdPolicy;
    }

    @Transactional
    public String updatePasswordPolicy(PasswordPolicyInputBean passwordPolicyInputBean) throws Exception {
        String message = "";
        int value = 0;
        try {
            value = jdbcTemplate.update(SQL_UPDATE_PASSWORDPOLICY,
                    passwordPolicyInputBean.getMinimumLength(),
                    passwordPolicyInputBean.getMaximumLength(),
                    passwordPolicyInputBean.getMinimumSpecialCharacters(),
                    passwordPolicyInputBean.getMinimumUpperCaseCharacters(),
                    passwordPolicyInputBean.getMinimumNumericalCharacters(),
                    passwordPolicyInputBean.getMinimumLowerCaseCharacters(),
                    passwordPolicyInputBean.getNoOfInvalidLoginAttempt(),
                    passwordPolicyInputBean.getRepeatCharactersAllow(),
                    passwordPolicyInputBean.getInitialPasswordExpiryStatus(),
                    passwordPolicyInputBean.getPasswordExpiryPeriod(),
                    passwordPolicyInputBean.getNoOfHistoryPassword(),
                    passwordPolicyInputBean.getMinimumPasswordChangePeriod(),
                    passwordPolicyInputBean.getIdleAccountExpiryPeriod(),
                    passwordPolicyInputBean.getDescription(),
                    passwordPolicyInputBean.getLastUpdatedUser(),
                    passwordPolicyInputBean.getLastUpdatedTime(),
                    passwordPolicyInputBean.getPasswordPolicyId());
            if (value != 1) {
                message = "Error occurred while updating password policy";
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    private StringBuilder setDynamicClause(PasswordPolicyInputBean sc, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            dynamicClause.toString();
            if (sc.getDescription() != null && !sc.getDescription().isEmpty()) {
                dynamicClause.append("and p.description like '%").append(sc.getDescription()).append("%'");
            }

            if (sc.getPasswordPolicyId() != null && !sc.getPasswordPolicyId().isEmpty()) {
                dynamicClause.append("and p.passwordpolicyid = '").append(sc.getPasswordPolicyId()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(PasswordPolicyInputBean passwordPolicyInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (passwordPolicyInputBean.getDescription() != null && !passwordPolicyInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and d.key1 like '%").append(passwordPolicyInputBean.getDescription()).append("%'");
            }

            if (passwordPolicyInputBean.getPasswordPolicyId() != null && !passwordPolicyInputBean.getPasswordPolicyId().isEmpty()) {
                dynamicClause.append("and d.key2 = '").append(passwordPolicyInputBean.getPasswordPolicyId()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
