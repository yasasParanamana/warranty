package com.oxcentra.rdbsms.repository.sysconfigmgt.smssmppconfiguration;

import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.smssmppconfiguration.SmsSmppConfigurationInputBean;
import com.oxcentra.rdbsms.mapping.smppconfig.SmppConfiguration;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.StatusVarList;
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
public class SmppConfigurationRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from SMSSMPPCONFIGURATION sc left outer join STATUS s on s.statuscode=sc.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";
    private final String SQL_INSERT_SMPPCONFIG = "insert into SMSSMPPCONFIGURATION(smppcode,description,status,maxtps,primaryip,secondaryip,systemid,password,bindport,bindmode,mtport,moport,maxbulktps,createdtime,createduser,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
    private final String SQL_FIND_SMPPCONFIG = "select smppcode,description,status,maxtps,primaryip,secondaryip,systemid,password,bindport,bindmode,mtport,moport,maxbulktps,createdtime,createduser,lastupdatedtime,lastupdateduser from SMSSMPPCONFIGURATION sc where sc.smppcode = ?";
    private final String SQL_UPDATE_SMPPCONFIG = "update SMSSMPPCONFIGURATION sc set sc.description = ? , sc.status = ? , sc.maxtps = ? , sc.primaryip = ? , sc.secondaryip = ? , sc.systemid = ?  , sc.password = ? , sc.bindport = ?  , sc.bindmode = ?  , sc.mtport = ? , sc.moport = ?  , sc.maxbulktps = ?, sc.lastupdateduser = ?, sc.lastupdatedtime = ? where sc.smppcode = ?";
    private final String SQL_DELETE_SMPPCONFIG = "delete from SMSSMPPCONFIGURATION where smppcode = ?";

    @Transactional(readOnly = true)
    public long getDataCount(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(smsSmppConfigurationInputBean, dynamicClause);
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
    public List<SmppConfiguration> getSmppConfigurationSearchList(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        List<SmppConfiguration> smppConfigurationList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(smsSmppConfigurationInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (smsSmppConfigurationInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by sc.lastupdatedtime desc ";
            } else {
                sortingStr = " order by sc.lastupdatedtime " + smsSmppConfigurationInputBean.sortDirections.get(0);
            }

            String sql =
                    " select sc.smppcode as smppcode,sc.description as description, sc.status as status,s.description as statusdescription," +
                            " sc.maxtps as maxtps,sc.primaryip as primaryip,sc.secondaryip as secondaryip , sc.systemid as systemid,sc.password as password," +
                            " sc.bindport as bindport , sc.bindmode as bindmode , sc.mtport as mtport , sc.moport as moport , sc.maxbulktps as maxbulktps," +
                            " sc.createdtime as createdtime,sc.createduser as createduser,sc.lastupdatedtime as lastupdatedtime ,sc.lastupdateduser as lastupdateduser from SMSSMPPCONFIGURATION sc " +
                            " left outer join STATUS s on s.statuscode = sc.status " +
                            " where " + dynamicClause + sortingStr +
                            " limit " + smsSmppConfigurationInputBean.displayLength + " offset " + smsSmppConfigurationInputBean.displayStart;

            smppConfigurationList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                SmppConfiguration smppConfiguration = new SmppConfiguration();

                try {
                    smppConfiguration.setSmppCode(rs.getString("smppcode"));
                } catch (Exception e) {
                    smppConfiguration.setSmppCode(null);
                }

                try {
                    smppConfiguration.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    smppConfiguration.setDescription(null);
                }

                try {
                    smppConfiguration.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    smppConfiguration.setStatus(null);
                }

                try {
                    smppConfiguration.setMaxTps(rs.getInt("maxtps"));
                } catch (Exception e) {
                    smppConfiguration.setMaxTps(0);
                }

                try {
                    smppConfiguration.setPrimaryIp(rs.getString("primaryip"));
                } catch (Exception e) {
                    smppConfiguration.setPrimaryIp(null);
                }

                try {
                    smppConfiguration.setSecondaryIp(rs.getString("secondaryip"));
                } catch (Exception e) {
                    smppConfiguration.setSecondaryIp(null);
                }

                try {
                    smppConfiguration.setSystemId(rs.getString("systemid"));
                } catch (Exception e) {
                    smppConfiguration.setSystemId(null);
                }

                try {
                    smppConfiguration.setPassword(rs.getString("password"));
                } catch (Exception e) {
                    smppConfiguration.setPassword(null);
                }

                try {
                    smppConfiguration.setBindPort(rs.getInt("bindport"));
                } catch (Exception e) {
                    smppConfiguration.setBindPort(0);
                }

                try {
                    smppConfiguration.setBindMode(rs.getString("bindmode"));
                } catch (Exception e) {
                    smppConfiguration.setBindMode(null);
                }

                try {
                    smppConfiguration.setMtPort(rs.getString("mtport"));
                } catch (Exception e) {
                    smppConfiguration.setMtPort(null);
                }

                try {
                    smppConfiguration.setMoPort(rs.getString("moport"));
                } catch (Exception e) {
                    smppConfiguration.setMoPort(null);
                }

                try {
                    smppConfiguration.setMaxBulkTps(rs.getInt("maxbulktps"));
                } catch (Exception e) {
                    smppConfiguration.setMaxBulkTps(0);
                }

                try {
                    smppConfiguration.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    smppConfiguration.setCreatedTime(null);
                }

                try {
                    smppConfiguration.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    smppConfiguration.setCreatedUser(null);
                }

                try {
                    smppConfiguration.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    smppConfiguration.setLastUpdatedTime(null);
                }

                try {
                    smppConfiguration.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    smppConfiguration.setLastUpdatedUser(null);
                }
                return smppConfiguration;
            });
        } catch (EmptyResultDataAccessException ex) {
            return smppConfigurationList;
        } catch (Exception e) {
            throw e;
        }
        return smppConfigurationList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(smsSmppConfigurationInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.SMPPCONFIG_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getSmppConfigurationSearchResultsDual(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        List<TempAuthRec> smppConfigurationDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(smsSmppConfigurationInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (smsSmppConfigurationInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + smsSmppConfigurationInputBean.sortDirections.get(0);
            }

            String sql = "" +
                    " select wta.id,wta.key1,wta.key2,wta.key3,wta.key4,wta.key5,wta.key6,wta.key7,wta.key8,wta.key8,wta.key9,wta.key10,wta.key11,wta.key12,wta.key13," +
                    " s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser from WEB_TMPAUTHREC wta" +
                    " left outer join STATUS s on s.statuscode = wta.key3 " +
                    " left outer join WEB_TASK t on t.taskcode = wta.task " +
                    " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                    " limit " + smsSmppConfigurationInputBean.displayLength + " offset " + smsSmppConfigurationInputBean.displayStart;

            smppConfigurationDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.SMPPCONFIG_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRec = new TempAuthRec();

                try {
                    tempAuthRec.setId(rs.getString("id"));
                } catch (Exception e) {
                    tempAuthRec.setId(null);
                }

                try {
                    //sms config smpp code
                    tempAuthRec.setKey1(rs.getString("key1"));
                } catch (Exception e) {
                    tempAuthRec.setKey1(null);
                }

                try {
                    //smpp config description
                    tempAuthRec.setKey2(rs.getString("key2"));
                } catch (Exception e) {
                    tempAuthRec.setKey2(null);
                }

                try {
                    //status code
                    tempAuthRec.setKey3(rs.getString("key3"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
                }

                try {
                    //max tps
                    tempAuthRec.setKey4(rs.getString("key4"));
                } catch (Exception e) {
                    tempAuthRec.setKey4(null);
                }

                try {
                    //primary ip
                    tempAuthRec.setKey5(rs.getString("key5"));
                } catch (Exception e) {
                    tempAuthRec.setKey5(null);
                }

                try {
                    //secondary ip
                    tempAuthRec.setKey6(rs.getString("key6"));
                } catch (Exception e) {
                    tempAuthRec.setKey6(null);
                }

                try {
                    //system id
                    tempAuthRec.setKey7(rs.getString("key7"));
                } catch (Exception e) {
                    tempAuthRec.setKey7(null);
                }

                try {
                    //password
                    tempAuthRec.setKey8(rs.getString("key8"));
                } catch (Exception e) {
                    tempAuthRec.setKey8(null);
                }

                try {
                    //bind port
                    tempAuthRec.setKey9(rs.getString("key9"));
                } catch (Exception e) {
                    tempAuthRec.setKey9(null);
                }

                try {
                    //bind mode
                    tempAuthRec.setKey10(rs.getString("key10"));
                } catch (Exception e) {
                    tempAuthRec.setKey10(null);
                }

                try {
                    //mt port
                    tempAuthRec.setKey11(rs.getString("key11"));
                } catch (Exception e) {
                    tempAuthRec.setKey11(null);
                }

                try {
                    //mo port
                    tempAuthRec.setKey12(rs.getString("key12"));
                } catch (Exception e) {
                    tempAuthRec.setKey12(null);
                }

                try {
                    //max bulk tps
                    tempAuthRec.setKey13(rs.getString("key13"));
                } catch (Exception e) {
                    tempAuthRec.setKey13(null);
                }

                try {
                    //status description
                    tempAuthRec.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    //created time
                    tempAuthRec.setCreatedTime(rs.getString("createdtime"));
                } catch (Exception e) {
                    tempAuthRec.setCreatedTime(null);
                }

                try {
                    //task description
                    tempAuthRec.setTask(rs.getString("taskdescription"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    //last updated time
                    tempAuthRec.setLastUpdatedTime(rs.getString("lastupdatedtime"));
                } catch (Exception e) {
                    tempAuthRec.setLastUpdatedTime(null);
                }

                try {
                    //last updated user
                    tempAuthRec.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    tempAuthRec.setLastUpdatedUser(null);
                }
                return tempAuthRec;
            });
        } catch (Exception e) {
            throw e;
        }
        return smppConfigurationDualList;
    }

    @Transactional
    public String insertSmppConfiguration(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_SMPPCONFIG, smsSmppConfigurationInputBean.getSmppCode(),
                    smsSmppConfigurationInputBean.getDescription(),
                    smsSmppConfigurationInputBean.getStatus(),
                    smsSmppConfigurationInputBean.getMaxTps(),
                    smsSmppConfigurationInputBean.getPrimaryIp(),
                    smsSmppConfigurationInputBean.getSecondaryIp(),
                    smsSmppConfigurationInputBean.getSystemId(),
                    smsSmppConfigurationInputBean.getPassword(),
                    smsSmppConfigurationInputBean.getBindPort(),
                    smsSmppConfigurationInputBean.getBindMode(),
                    smsSmppConfigurationInputBean.getMtPort(),
                    smsSmppConfigurationInputBean.getMoPort(),
                    smsSmppConfigurationInputBean.getMaxBulkTps(),
                    smsSmppConfigurationInputBean.getCreatedTime(),
                    smsSmppConfigurationInputBean.getCreatedUser(),
                    smsSmppConfigurationInputBean.getLastUpdatedTime(),
                    smsSmppConfigurationInputBean.getLastUpdatedUser());
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
    public SmppConfiguration getSmppConfiguration(String smppCode) {
        SmppConfiguration smppConfiguration = null;
        try {
            smppConfiguration = jdbcTemplate.queryForObject(SQL_FIND_SMPPCONFIG, new Object[]{smppCode}, new RowMapper<SmppConfiguration>() {
                @Override
                public SmppConfiguration mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                    SmppConfiguration smppConfiguration = new SmppConfiguration();

                    try {
                        smppConfiguration.setSmppCode(resultSet.getString("smppcode"));
                    } catch (Exception e) {
                        smppConfiguration.setSmppCode(null);
                    }


                    try {
                        smppConfiguration.setDescription(resultSet.getString("description"));
                    } catch (Exception e) {
                        smppConfiguration.setDescription(null);
                    }

                    try {
                        smppConfiguration.setStatus(resultSet.getString("status"));
                    } catch (Exception e) {
                        smppConfiguration.setStatus(null);
                    }

                    try {
                        smppConfiguration.setMaxTps(resultSet.getInt("maxtps"));
                    } catch (Exception e) {
                        smppConfiguration.setMaxTps(0);
                    }

                    try {
                        smppConfiguration.setPrimaryIp(resultSet.getString("primaryip"));
                    } catch (Exception e) {
                        smppConfiguration.setPrimaryIp(null);
                    }

                    try {
                        smppConfiguration.setSecondaryIp(resultSet.getString("secondaryip"));
                    } catch (Exception e) {
                        smppConfiguration.setSecondaryIp(null);
                    }

                    try {
                        smppConfiguration.setSystemId(resultSet.getString("systemid"));
                    } catch (Exception e) {
                        smppConfiguration.setSystemId(null);
                    }

                    try {
                        smppConfiguration.setPassword(resultSet.getString("password"));
                    } catch (Exception e) {
                        smppConfiguration.setPassword(null);
                    }

                    try {
                        smppConfiguration.setBindPort(resultSet.getInt("bindport"));
                    } catch (Exception e) {
                        smppConfiguration.setBindPort(0);
                    }

                    try {
                        smppConfiguration.setBindMode(resultSet.getString("bindmode"));
                    } catch (Exception e) {
                        smppConfiguration.setBindMode(null);
                    }

                    try {
                        smppConfiguration.setMtPort(resultSet.getString("mtport"));
                    } catch (Exception e) {
                        smppConfiguration.setMtPort(null);
                    }

                    try {
                        smppConfiguration.setMoPort(resultSet.getString("moport"));
                    } catch (Exception e) {
                        smppConfiguration.setMoPort(null);
                    }

                    try {
                        smppConfiguration.setMaxBulkTps(resultSet.getInt("maxbulktps"));
                    } catch (Exception e) {
                        smppConfiguration.setMaxBulkTps(0);
                    }

                    try {
                        smppConfiguration.setCreatedTime(new Date(resultSet.getDate("createdtime").getTime()));
                    } catch (Exception e) {
                        smppConfiguration.setCreatedTime(null);
                    }

                    try {
                        smppConfiguration.setCreatedUser(resultSet.getString("createduser"));
                    } catch (Exception e) {
                        smppConfiguration.setCreatedTime(null);
                    }

                    try {
                        smppConfiguration.setLastUpdatedTime(new Date(resultSet.getDate("lastupdatedtime").getTime()));
                    } catch (SQLException e) {
                        smppConfiguration.setLastUpdatedTime(null);
                    }

                    try {
                        smppConfiguration.setLastUpdatedUser(resultSet.getString("lastupdateduser"));
                    } catch (SQLException e) {
                        smppConfiguration.setLastUpdatedUser(null);
                    }
                    return smppConfiguration;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            smppConfiguration = null;
        } catch (Exception e) {
            throw e;
        }
        return smppConfiguration;
    }


    @Transactional
    public String updateSmppConfiguration(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_SMPPCONFIG,
                    smsSmppConfigurationInputBean.getDescription(),
                    smsSmppConfigurationInputBean.getStatus(),
                    smsSmppConfigurationInputBean.getMaxTps(),
                    smsSmppConfigurationInputBean.getPrimaryIp(),
                    smsSmppConfigurationInputBean.getSecondaryIp(),
                    smsSmppConfigurationInputBean.getSystemId(),
                    smsSmppConfigurationInputBean.getPassword(),
                    smsSmppConfigurationInputBean.getBindPort(),
                    smsSmppConfigurationInputBean.getBindMode(),
                    smsSmppConfigurationInputBean.getMtPort(),
                    smsSmppConfigurationInputBean.getMoPort(),
                    smsSmppConfigurationInputBean.getMaxBulkTps(),
                    smsSmppConfigurationInputBean.getLastUpdatedUser(),
                    smsSmppConfigurationInputBean.getLastUpdatedTime(),
                    smsSmppConfigurationInputBean.getSmppCode());
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteSmppConfiguration(String smppCode) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_SMPPCONFIG, smppCode);
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

    private StringBuilder setDynamicClause(SmsSmppConfigurationInputBean smsSmppConfigurationInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (smsSmppConfigurationInputBean.getSmppCode() != null && !smsSmppConfigurationInputBean.getSmppCode().isEmpty()) {
                dynamicClause.append("and lower(sc.smppcode) like lower('%").append(smsSmppConfigurationInputBean.getSmppCode()).append("%')");
            }

            if (smsSmppConfigurationInputBean.getDescription() != null && !smsSmppConfigurationInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(sc.description) like lower('%").append(smsSmppConfigurationInputBean.getDescription()).append("%')");
            }

            if (smsSmppConfigurationInputBean.getStatus() != null && !smsSmppConfigurationInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and sc.status = '").append(smsSmppConfigurationInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(SmsSmppConfigurationInputBean
                                                       smsSmppConfigurationInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (smsSmppConfigurationInputBean.getSmppCode() != null && !smsSmppConfigurationInputBean.getSmppCode().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(smsSmppConfigurationInputBean.getSmppCode()).append("%')");
            }

            if (smsSmppConfigurationInputBean.getDescription() != null && !smsSmppConfigurationInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(smsSmppConfigurationInputBean.getDescription()).append("%')");
            }

            if (smsSmppConfigurationInputBean.getStatus() != null && !smsSmppConfigurationInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key3 = '").append(smsSmppConfigurationInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
