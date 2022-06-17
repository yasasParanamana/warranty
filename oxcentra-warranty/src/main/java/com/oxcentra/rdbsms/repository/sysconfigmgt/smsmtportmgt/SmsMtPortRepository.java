package com.oxcentra.rdbsms.repository.sysconfigmgt.smsmtportmgt;

import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.smsmtportmanagement.SmsMtPortInputBean;
import com.oxcentra.rdbsms.mapping.smsmtportmgt.SmsMtPort;
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
public class SmsMtPortRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from SMSMTPORT m left outer join STATUS s on s.statuscode=m.status where";
    private final String SQL_FIND_SMSMTPORT = "select mtport,status,createdtime,createduser,lastupdatedtime,lastupdateduser from SMSMTPORT m where m.mtport = ?";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";
    private final String SQL_INSERT_SMSMTPORT = "insert into SMSMTPORT( mtport,status,createdtime,createduser,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?) ";
    private final String SQL_DELETE_SMSMTPORT = "delete from SMSMTPORT where mtport = ?";
    private final String SQL_UPDATE_SMSMTPORT = "update SMSMTPORT m set m.status = ?,m.lastupdatedtime=?,m.lastupdateduser=? where m.mtport = ?";

    @Transactional(readOnly = true)
    public long getDataCount(SmsMtPortInputBean smsMtPortInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(smsMtPortInputBean, dynamicClause);
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
    public long getDataCountDual(SmsMtPortInputBean smsMtPortInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(smsMtPortInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.SMSMTPORT_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getSmsMtPortSearchResultsDual(SmsMtPortInputBean smsMtPortInputBean) {
        List<TempAuthRec> smsMtPortDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(smsMtPortInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            String col = "";

            switch (smsMtPortInputBean.sortedColumns.get(0)) {
                case 0:
                    col = "wta.createdtime";
                    break;
                case 1:
                    col = "wta.key1";
                    break;
                case 2:
                    col = "s.description";
                    break;
                case 3:
                    col = "t.description";
                    break;
                case 4:
                    col = "wta.createdtime";
                    break;
                case 5:
                    col = "wta.lastupdatedtime";
                    break;
                case 6:
                    col = "wta.lastupdateduser";
                    break;
                default:
                    col = "wta.lastupdatedtime";
            }
            sortingStr = " order by " + col + " " + smsMtPortInputBean.sortDirections.get(0);

            String sql = "" +
                    " select wta.id,wta.key1,s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                    " from WEB_TMPAUTHREC wta" +
                    " left outer join STATUS s on s.statuscode = wta.key3 " +
                    " left outer join WEB_TASK t on t.taskcode = wta.task " +
                    " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                    " limit " + smsMtPortInputBean.displayLength + " offset " + smsMtPortInputBean.displayStart;

            smsMtPortDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.SMSMTPORT_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
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
                    tempAuthRec.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    tempAuthRec.setStatus(null);
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
        return smsMtPortDualList;
    }

    @Transactional(readOnly = true)
    public List<SmsMtPort> getSmsMtPortSearchList(SmsMtPortInputBean smsMtPortInputBean) {
        List<SmsMtPort> smsmtportList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(smsMtPortInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            String col = "";
            int index = smsMtPortInputBean.sortedColumns.get(0);

            switch (index) {
                case 0:
                    col = "m.mtport";
                    break;
                case 1:
                    col = "m.status";
                    break;
                case 2:
                    col = "m.createdtime";
                    break;
                case 3:
                    col = "m.createduser";
                    break;
                case 4:
                    col = "m.lastupdatedtime";
                    break;
                case 5:
                    col = "m.lastupdateduser";
                    break;
                default:
                    col = "m.lastupdatedtime";
            }

            if (smsMtPortInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by m.lastupdatedtime desc ";
            } else {
                sortingStr = " order by m.lastupdatedtime " + smsMtPortInputBean.sortDirections.get(0);
            }

            String sql =
                    " select m.mtport as mtport, m.status as status,s.description as statusdescription, m.createdtime as createdtime," +
                            " m.createduser as createduser,m.lastupdatedtime as lastupdatedtime ,m.lastupdateduser as lastupdateduser from SMSMTPORT m " +
                            " left outer join STATUS s on s.statuscode=m.status " +
                            " where " + dynamicClause + sortingStr +
                            " limit " + smsMtPortInputBean.displayLength + " offset " + smsMtPortInputBean.displayStart;

            smsmtportList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                SmsMtPort smsMtPort = new SmsMtPort();
                try {
                    smsMtPort.setMtPort(rs.getString("mtport"));
                } catch (Exception e) {
                    smsMtPort.setMtPort(null);
                }

                try {
                    smsMtPort.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    smsMtPort.setStatus(null);
                }

                try {
                    smsMtPort.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    smsMtPort.setCreatedTime(null);
                }

                try {
                    smsMtPort.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    smsMtPort.setCreatedUser(null);
                }

                try {
                    smsMtPort.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    smsMtPort.setLastUpdatedTime(null);
                }

                try {
                    smsMtPort.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    smsMtPort.setLastUpdatedUser(null);
                }

                return smsMtPort;
            });
        } catch (EmptyResultDataAccessException ex) {
            return smsmtportList;
        } catch (Exception e) {
            throw e;
        }
        return smsmtportList;
    }

    @Transactional(readOnly = true)
    public SmsMtPort getSmsMtPort(String mtport) {
        SmsMtPort smsMtPort = null;
        try {
            smsMtPort = jdbcTemplate.queryForObject(SQL_FIND_SMSMTPORT, new Object[]{mtport}, new RowMapper<SmsMtPort>() {
                @Override
                public SmsMtPort mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SmsMtPort smsMtPort = new SmsMtPort();

                    try {
                        smsMtPort.setMtPort(rs.getString("mtport"));
                    } catch (Exception e) {
                        smsMtPort.setMtPort(null);
                    }

                    try {
                        smsMtPort.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        smsMtPort.setStatus(null);
                    }

                    try {
                        smsMtPort.setCreatedTime(new Date(rs.getDate("createdtime").getTime()));
                    } catch (Exception e) {
                        smsMtPort.setCreatedTime(null);
                    }

                    try {
                        smsMtPort.setCreatedUser(rs.getString("createduser"));
                    } catch (Exception e) {
                        smsMtPort.setCreatedTime(null);
                    }

                    try {
                        smsMtPort.setLastUpdatedTime(new Date(rs.getDate("lastupdatedtime").getTime()));
                    } catch (SQLException e) {
                        smsMtPort.setLastUpdatedTime(null);
                    }

                    try {
                        smsMtPort.setLastUpdatedUser(rs.getString("lastupdateduser"));
                    } catch (SQLException e) {
                        smsMtPort.setLastUpdatedUser(null);
                    }

                    return smsMtPort;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            smsMtPort = null;
        } catch (Exception e) {
            throw e;
        }
        return smsMtPort;
    }

    @Transactional
    public String deleteSmsMtPort(String mtPort) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_SMSMTPORT, mtPort);
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

    @Transactional
    public String insertSmsMtPort(SmsMtPortInputBean smsMtPortInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_SMSMTPORT,
                    smsMtPortInputBean.getMtPort(),
                    smsMtPortInputBean.getStatus(),
                    smsMtPortInputBean.getCreatedTime(),
                    smsMtPortInputBean.getCreatedUser(),
                    smsMtPortInputBean.getLastUpdatedTime(),
                    smsMtPortInputBean.getLastUpdatedUser()
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

    @Transactional
    public String updateSmsMtPort(SmsMtPortInputBean smsMtPortInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_SMSMTPORT, smsMtPortInputBean.getStatus(), smsMtPortInputBean.getLastUpdatedTime(), smsMtPortInputBean.getLastUpdatedUser(), smsMtPortInputBean.getMtPort());
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    private StringBuilder setDynamicClause(SmsMtPortInputBean smsMtPortInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (smsMtPortInputBean.getMtPort() != null && !smsMtPortInputBean.getMtPort().isEmpty()) {
                dynamicClause.append("and lower(m.mtport) like lower('%").append(smsMtPortInputBean.getMtPort()).append("%')");
            }

            if (smsMtPortInputBean.getStatus() != null && !smsMtPortInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and m.status = '").append(smsMtPortInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(SmsMtPortInputBean smsMtPortInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (smsMtPortInputBean.getMtPort() != null && !smsMtPortInputBean.getMtPort().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(smsMtPortInputBean.getMtPort()).append("%')");
            }

            if (smsMtPortInputBean.getStatus() != null && !smsMtPortInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key3 = '").append(smsMtPortInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
