package com.oxcentra.warranty.repository.usermgt.systemuser;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.usermgt.systemuser.SystemUserInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.usermgt.SystemUser;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.service.common.CommonService;
import com.oxcentra.warranty.util.varlist.CommonVarList;
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
public class SystemUserRepository {

    private final String SQL_FIND_SYSTEMUSER_BY_NIC = "select username from web_systemuser where username != ? and nic = ?";
    private final String SQL_FIND_SYSTEMUSER_BY_SERVICEID = "select username from web_systemuser where username != ? and serviceid = ?";
    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from web_systemuser wu left outer join status s on s.statuscode=wu.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from web_tmpauthrec wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";
    private final String SQL_INSERT_SYSTEMUSER = "insert into web_systemuser(username,password,userrole,expirydate,fullname,email,mobile,nic,dealership,initialloginstatus,ad,status,lastupdateduser,lastupdatedtime,createtime,createduser,landline) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
    private final String SQL_FIND_SYSTEMUSER = "select username,password,userrole,expirydate,fullname,email,nic,dealership,mobile,noofinvlidattempt,loggeddate,initialloginstatus,ad,status,lastupdateduser,lastupdatedtime,createtime,landline from web_systemuser wsu where wsu.username = ?";
    private final String SQL_UPDATE_SYSTEMUSER = "update web_systemuser wsu set userrole = ?, fullname = ?, email = ?, mobile = ?, status = ?, nic = ?, dealership = ?, lastupdateduser = ?, lastupdatedtime = ?, landline = ? where wsu.username = ?";
    private final String SQL_DELETE_SYSTEMUSER = "delete from web_systemuser where username = ?";
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
    public long getDataCount(SystemUserInputBean systemUserInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(systemUserInputBean, dynamicClause);
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
    public List<SystemUser> getSystemUserSearchList(SystemUserInputBean systemUserInputBean) {
        List<SystemUser> systemUserList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(systemUserInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (systemUserInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wu.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wu.lastupdatedtime " + systemUserInputBean.sortDirections.get(0);
            }
            String sql =
                    " select wu.username as username,wu.fullname as fullname, u.description as userrole," +
                            " wu.email as email,wu.mobile as mobile,wu.nic,d.dealership_name as dealership ,wu.expirydate as expirydate,wu.loggeddate as loggeddate," +
                            " s.description as statusdescription,wu.createtime as createdtime,wu.createduser as createduser,wu.lastupdatedtime,wu.lastupdateduser, wu.landline from web_systemuser wu " +
                            " left outer join status s on s.statuscode=wu.status " +
                            " left outer join userrole u on u.userrolecode=wu.userrole " +
                            " left outer join reg_dealership d on d.dealership_code=wu.dealership " +
                            " where " + dynamicClause.toString() + sortingStr +
                            " limit " + systemUserInputBean.displayLength + " offset " + systemUserInputBean.displayStart;

            systemUserList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                SystemUser systemUser = new SystemUser();

                try {
                    systemUser.setUserName(rs.getString("username"));
                } catch (Exception e) {
                    systemUser.setUserName(null);
                }

                try {
                    systemUser.setFullName(rs.getString("fullname"));
                } catch (Exception e) {
                    systemUser.setFullName(null);
                }

                try {
                    systemUser.setUserRoleCode(rs.getString("userrole"));
                } catch (Exception e) {
                    systemUser.setUserRoleCode(null);
                }

                try {
                    systemUser.setNic(rs.getString("nic"));
                } catch (Exception e) {
                    systemUser.setNic(null);
                }

                try {
                    systemUser.setServiceid(rs.getString("dealership"));
                } catch (Exception e) {
                    systemUser.setServiceid(null);
                }

                try {
                    systemUser.setEmail(rs.getString("email"));
                } catch (SQLException e) {
                    systemUser.setEmail(null);
                }

                try {
                    systemUser.setMobileNumber(rs.getString("mobile"));
                } catch (Exception e) {
                    systemUser.setMobileNumber(null);
                }

                try {
                    systemUser.setNoOfInvalidAttempt(rs.getInt("noofinvlidattempt"));
                } catch (Exception e) {
                    systemUser.setNoOfInvalidAttempt(0);
                }

                try {
                    systemUser.setExpiryDate(rs.getDate("expirydate"));
                } catch (Exception e) {
                    systemUser.setExpiryDate(null);
                }

                try {
                    systemUser.setLastLoggedDate(rs.getDate("loggeddate"));
                } catch (Exception e) {
                    systemUser.setLastLoggedDate(null);
                }

                try {
                    systemUser.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    systemUser.setStatus(null);
                }

                try {
                    systemUser.setAd(rs.getInt("ad"));
                } catch (Exception e) {
                    systemUser.setAd(0);
                }

                try {
                    systemUser.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    systemUser.setCreatedTime(null);
                }

                try {
                    systemUser.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    systemUser.setCreatedUser(null);
                }

                try {
                    systemUser.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    systemUser.setLastUpdatedTime(null);
                }

                try {
                    systemUser.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    systemUser.setLastUpdatedUser(null);
                }

                try {
                    systemUser.setLandLine(rs.getString("landline"));
                } catch (Exception e) {
                    systemUser.setLandLine(null);
                }
                return systemUser;
            });
        } catch (EmptyResultDataAccessException ex) {
            return systemUserList;
        } catch (Exception e) {
            throw e;
        }
        return systemUserList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(SystemUserInputBean systemUserInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(systemUserInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.USER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getSystemUserSearchResultsDual(SystemUserInputBean systemUserInputBean) {
        List<TempAuthRec> systemUserDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(systemUserInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (systemUserInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + systemUserInputBean.sortDirections.get(0);
            }

            String sql = "" +
                    " select wta.id,wta.key1,wta.key2,wta.key3,wta.key4,wta.key5,wta.key6,wta.key8,wta.key9,s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                    " from web_tmpauthrec wta" +
                    " left outer join status s on s.statuscode=wta.key5 " +
                    " left outer join web_task t on t.taskcode = wta.task " +
                    " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                    " limit " + systemUserInputBean.displayLength + " offset " + systemUserInputBean.displayStart;

            systemUserDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.USER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRec = new TempAuthRec();

                try {
                    tempAuthRec.setId(rs.getString("id"));
                } catch (Exception e) {
                    tempAuthRec.setId(null);
                }

                try {
                    //set user name
                    tempAuthRec.setKey1(rs.getString("key1"));
                } catch (Exception e) {
                    tempAuthRec.setKey1(null);
                }

                try {
                    //set full name
                    tempAuthRec.setKey2(rs.getString("key2"));
                } catch (Exception e) {
                    tempAuthRec.setKey2(null);
                }

                try {
                    //email
                    tempAuthRec.setKey3(rs.getString("key3"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
                }

                try {
                    //user role code
                    tempAuthRec.setKey4(rs.getString("key4"));
                } catch (Exception e) {
                    tempAuthRec.setKey4(null);
                }

                try {
                    //status
                    tempAuthRec.setKey5(rs.getString("key5"));
                } catch (Exception e) {
                    tempAuthRec.setKey5(null);
                }

                try {
                    //mobile number
                    tempAuthRec.setKey6(rs.getString("key6"));
                } catch (Exception e) {
                    tempAuthRec.setKey6(null);
                }

                try {
                    //mobile number
                    tempAuthRec.setKey8(rs.getString("key8"));
                } catch (Exception e) {
                    tempAuthRec.setKey6(null);
                }

                try {
                    //mobile number
                    tempAuthRec.setKey9(rs.getString("key9"));
                } catch (Exception e) {
                    tempAuthRec.setKey6(null);
                }

                try {
                    tempAuthRec.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
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

                try {
                    tempAuthRec.setCreatedTime(rs.getString("createdtime"));
                } catch (Exception e) {
                    tempAuthRec.setCreatedTime(null);
                }

                return tempAuthRec;
            });
        } catch (Exception e) {
            throw e;
        }
        return systemUserDualList;
    }

    @Transactional
    public String insertSystemUser(SystemUserInputBean systemUserInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            Date userPasswordExpiryDate = commonService.getPasswordExpiryDate();
            //set default values to system user input bean
            systemUserInputBean.setExpiryDate(userPasswordExpiryDate);
            systemUserInputBean.setInitialLoginStatus(0);
            systemUserInputBean.setAd(0);
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_SYSTEMUSER, systemUserInputBean.getUserName(),
                    systemUserInputBean.getPassword(),
                    systemUserInputBean.getUserRoleCode(),
                    systemUserInputBean.getExpiryDate(),
                    systemUserInputBean.getFullName().toLowerCase(),
                    systemUserInputBean.getEmail(),
                    systemUserInputBean.getMobileNumber(),
                    systemUserInputBean.getNic(),
                    systemUserInputBean.getServiceId(),
                    systemUserInputBean.getInitialLoginStatus(),
                    systemUserInputBean.getAd(),
                    commonVarList.STATUS_NEW,
                    systemUserInputBean.getLastUpdatedUser(),
                    systemUserInputBean.getLastUpdatedTime(),
                    systemUserInputBean.getCreatedTime(),
                    systemUserInputBean.getCreatedUser(),
                    systemUserInputBean.getLandLine()
            );

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
    public SystemUser getSystemUser(String userName) throws SQLException {
        SystemUser systemUser = null;
        try {
            systemUser = jdbcTemplate.queryForObject(SQL_FIND_SYSTEMUSER, new Object[]{userName}, new RowMapper<SystemUser>() {
                @Override
                public SystemUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SystemUser systemUser = new SystemUser();
                    try {
                        systemUser.setUserName(rs.getString("username"));
                    } catch (Exception e) {
                        systemUser.setUserName(null);
                    }

                    try {
                        systemUser.setFullName(rs.getString("fullname"));
                    } catch (Exception e) {
                        systemUser.setFullName(null);
                    }

                    try {
                        systemUser.setUserRoleCode(rs.getString("userrole"));
                    } catch (Exception e) {
                        systemUser.setUserRoleCode(null);
                    }

                    try {
                        systemUser.setEmail(rs.getString("email"));
                    } catch (Exception e) {
                        systemUser.setEmail(null);
                    }

                    try {
                        systemUser.setMobileNumber(rs.getString("mobile"));
                    } catch (Exception e) {
                        systemUser.setMobileNumber(null);
                    }

                    try {
                        systemUser.setNic(rs.getString("nic"));
                    } catch (Exception e) {
                        systemUser.setNic(null);
                    }

                    try {
                        systemUser.setServiceid(rs.getString("dealership"));
                    } catch (Exception e) {
                        systemUser.setServiceid(null);
                    }

                    try {
                        systemUser.setNoOfInvalidAttempt(rs.getInt("noofinvlidattempt"));
                    } catch (Exception e) {
                        systemUser.setNoOfInvalidAttempt(0);
                    }

                    try {
                        systemUser.setExpiryDate(new Date(rs.getDate("expirydate").getTime()));
                    } catch (Exception e) {
                        systemUser.setExpiryDate(null);
                    }

                    try {
                        systemUser.setLastLoggedDate(new Date(rs.getDate("loggeddate").getTime()));
                    } catch (Exception e) {
                        systemUser.setLastLoggedDate(null);
                    }

                    try {
                        systemUser.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        systemUser.setStatus(null);
                    }

                    try {
                        systemUser.setAd(rs.getInt("ad"));
                    } catch (Exception e) {
                        systemUser.setAd(0);
                    }

                    try {
                        systemUser.setCreatedTime(new Date(rs.getDate("createtime").getTime()));
                    } catch (Exception e) {
                        systemUser.setCreatedTime(null);
                    }

                    try {
                        systemUser.setLastUpdatedTime(new Date(rs.getDate("lastupdatedtime").getTime()));
                    } catch (SQLException e) {
                        systemUser.setLastUpdatedTime(null);
                    }

                    try {
                        systemUser.setLastUpdatedUser(rs.getString("lastupdateduser"));
                    } catch (SQLException e) {
                        systemUser.setLastUpdatedUser(null);
                    }

                    try {
                        systemUser.setLandLine(rs.getString("landline"));
                    } catch (SQLException e) {
                        systemUser.setLandLine(null);
                    }
                    return systemUser;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            systemUser = null;
        } catch (Exception e) {
            throw e;
        }
        return systemUser;
    }

    @Transactional
    public String updateSystemUser(SystemUserInputBean systemUserInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_SYSTEMUSER,
                    systemUserInputBean.getUserRoleCode(),
                    systemUserInputBean.getFullName(),
                    systemUserInputBean.getEmail(),
                    systemUserInputBean.getMobileNumber(),
                    systemUserInputBean.getStatus(),
                    systemUserInputBean.getNic(),
                    systemUserInputBean.getServiceId(),
                    systemUserInputBean.getLastUpdatedUser(),
                    systemUserInputBean.getLastUpdatedTime(),
                    systemUserInputBean.getLandLine(),
                    systemUserInputBean.getUserName()
            );

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteSystemUser(String userName) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_SYSTEMUSER, userName);
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

    private StringBuilder setDynamicClause(SystemUserInputBean systemUserInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 and wu.username != '").append(sessionBean.getUsername()).append("'");

        try {
            if (systemUserInputBean.getUserName() != null && !systemUserInputBean.getUserName().isEmpty()) {
                dynamicClause.append("and lower(wu.username) like lower('%").append(systemUserInputBean.getUserName()).append("%')");
            }

            if (systemUserInputBean.getFullName() != null && !systemUserInputBean.getFullName().isEmpty()) {
                dynamicClause.append("and lower(wu.fullname) like lower('%").append(systemUserInputBean.getFullName()).append("%')");
            }

            if (systemUserInputBean.getEmail() != null && !systemUserInputBean.getEmail().isEmpty()) {
                dynamicClause.append("and lower(wu.email) like lower('%").append(systemUserInputBean.getEmail()).append("%')");
            }

            if (systemUserInputBean.getUserRoleCode() != null && !systemUserInputBean.getUserRoleCode().isEmpty()) {
                dynamicClause.append("and wu.userrole = '").append(systemUserInputBean.getUserRoleCode()).append("'");
            }

            if (systemUserInputBean.getMobileNumber() != null && !systemUserInputBean.getMobileNumber().isEmpty()) {
                dynamicClause.append("and wu.mobile = '").append(systemUserInputBean.getMobileNumber()).append("'");
            }

            if (systemUserInputBean.getStatus() != null && !systemUserInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wu.status = '").append(systemUserInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(SystemUserInputBean systemUserInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 and wta.key1 != '").append(sessionBean.getUsername()).append("'");

        if (systemUserInputBean.getUserName() != null && !systemUserInputBean.getUserName().isEmpty()) {
            dynamicClause.append("and lower(wta.key1) like lower('%").append(systemUserInputBean.getUserName()).append("%')");
        }

        if (systemUserInputBean.getFullName() != null && !systemUserInputBean.getFullName().isEmpty()) {
            dynamicClause.append("and lower(wta.key2) like lower('%").append(systemUserInputBean.getFullName()).append("%')");
        }

        if (systemUserInputBean.getEmail() != null && !systemUserInputBean.getEmail().isEmpty()) {
            dynamicClause.append("and lower(wta.key3) like lower('%").append(systemUserInputBean.getStatus()).append("%')");
        }

        if (systemUserInputBean.getUserRoleCode() != null && !systemUserInputBean.getUserRoleCode().isEmpty()) {
            dynamicClause.append("and wta.key4 = '").append(systemUserInputBean.getUserRoleCode()).append("'");
        }

        if (systemUserInputBean.getStatus() != null && !systemUserInputBean.getStatus().isEmpty()) {
            dynamicClause.append("and wta.key5 = '").append(systemUserInputBean.getStatus()).append("'");
        }

        if (systemUserInputBean.getMobileNumber() != null && !systemUserInputBean.getMobileNumber().isEmpty()) {
            dynamicClause.append("and wta.key6 = '").append(systemUserInputBean.getMobileNumber()).append("%'");
        }
        return dynamicClause;
    }

    @Transactional(readOnly = true)
    public SystemUser getSystemUserbyNIC(String userName, String nic) throws SQLException {
        SystemUser systemUser = null;
        try {
            systemUser = jdbcTemplate.queryForObject(SQL_FIND_SYSTEMUSER_BY_NIC, new Object[]{userName, nic}, new RowMapper<SystemUser>() {
                @Override
                public SystemUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SystemUser systemUser = new SystemUser();
                    try {
                        systemUser.setUserName(rs.getString("username"));
                    } catch (Exception e) {
                        systemUser.setUserName(null);
                    }
                    return systemUser;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            systemUser = null;
        } catch (Exception e) {
            throw e;
        }
        return systemUser;
    }

    @Transactional(readOnly = true)
    public SystemUser getSystemUserbyServiceID(String userName, String serviceId) throws SQLException {
        SystemUser systemUser = null;
        try {
            systemUser = jdbcTemplate.queryForObject(SQL_FIND_SYSTEMUSER_BY_SERVICEID, new Object[]{userName, serviceId}, new RowMapper<SystemUser>() {
                @Override
                public SystemUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SystemUser systemUser = new SystemUser();
                    try {
                        systemUser.setUserName(rs.getString("username"));
                    } catch (Exception e) {
                        systemUser.setUserName(null);
                    }
                    return systemUser;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            systemUser = null;
        } catch (Exception e) {
            throw e;
        }
        return systemUser;
    }

}
