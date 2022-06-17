package com.oxcentra.rdbsms.repository.sysconfigmgt.channeltxntype;

import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.channeltxntype.ChannelTxnTypeInputBean;
import com.oxcentra.rdbsms.mapping.channeltxntype.ChannelTxnType;
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

/**
 * @author Namila Withanage on 11/19/2021
 */
@Repository
@Scope("prototype")
public class ChannelTxnTypeRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from CHANNEL_TXNTYPE ct left outer join STATUS s on s.statuscode=ct.status where";
    private final String SQL_FIND_CATEGORY = "select ct.txntype, tt.description as txntypeDescription, ct.channelcode as channel, sc.description as channelDescription, ct.templatecode as template, st.description as templateDescription, " +
            "ct.status, ct.createdtime, ct.lastupdatedtime, ct.lastupdateduser from CHANNEL_TXNTYPE ct left outer join TXN_TYPE tt on tt.txntype=ct.txntype left outer join SMSCHANNEL sc on sc.channelcode=ct.channelcode " +
            "left outer join SMSOUTPUTTEMPLATE st on st.templatecode=ct.templatecode where ct.txntype = ? and ct.channelcode = ?";
    private final String SQL_INSERT_CHANNEL_TXN_TYPE = "insert into CHANNEL_TXNTYPE( txntype, channelcode, templatecode, status, createduser, createdtime, lastupdatedtime, lastupdateduser ) VALUES(?,?,?,?,?,?,?,?)";
    private final String SQL_UPDATE_CHANNEL_TXN_TYPE = "update CHANNEL_TXNTYPE ct set ct.status = ?, ct.templatecode = ?, ct.lastupdateduser = ?, ct.lastupdatedtime = ? where ct.txntype = ? and ct.channelcode = ?";
    private final String SQL_DELETE_CHANNEL_TXN_TYPE = "delete from CHANNEL_TXNTYPE where txntype = ? and channelcode = ?";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";

    @Transactional(readOnly = true)
    public long getDataCount(ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        long count = 0;
        try {
            System.out.println("getDataCount method in Repo class");
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            System.out.println("||||||||| dynamicClause > " + dynamicClause);
            //create the where clause
            dynamicClause = this.setDynamicClauseForgetDataCount(channelTxnTypeInputBean, dynamicClause);
            System.out.println("|||||||||*** dynamicClause > " + dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional
    public String updateChannelTxnType(ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_CHANNEL_TXN_TYPE, channelTxnTypeInputBean.getStatus(), channelTxnTypeInputBean.getTemplate(), channelTxnTypeInputBean.getLastUpdatedUser(), channelTxnTypeInputBean.getLastUpdatedTime(), channelTxnTypeInputBean.getTxntype(), channelTxnTypeInputBean.getChannel());
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(channelTxnTypeInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional
    public String deleteChannelTxnType(String txntype, String channel) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_CHANNEL_TXN_TYPE, txntype, channel);
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

    @Transactional(readOnly = true)
    public List<TempAuthRec> getChannelTxnTypeSearchResultsDual(ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        List<TempAuthRec> channelTxnTypeDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(channelTxnTypeInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            String col = "";

            switch (channelTxnTypeInputBean.sortedColumns.get(0)) {
                case 0:
                    col = "wta.createdtime";
                    break;
                case 1:
                    col = "tt.description";
                    break;
                case 2:
                    col = "sc.description";
                    break;
                case 3:
                    col = "wta.key3";
                    break;
                case 4:
                    col = "st.description";
                    break;
                case 5:
                    col = "wt.description";
                    break;
                case 6:
                    col = "wta.createdtime";
                    break;
                case 7:
                    col = "wta.lastupdatedtime";
                    break;
                case 8:
                    col = "wta.lastupdateduser";
                    break;
                default:
                    col = "wta.lastupdatedtime";
            }
            sortingStr = " order by " + col + " " + channelTxnTypeInputBean.sortDirections.get(0);

            String sql =
                    " select wta.id, tt.description as key1, sc.description as key2, st.description as key3, s.description as statusdescription, " +
                            " wt.description as taskdescription, wta.createdtime, wta.lastupdatedtime,wta.lastupdateduser from WEB_TMPAUTHREC wta" +
                            " left outer join STATUS s on s.statuscode = wta.key4 " +
                            " left outer join WEB_TASK wt on wt.taskcode = wta.task " +
                            " left outer join TXN_TYPE tt on tt.txntype = wta.key1 " +
                            " left outer join SMSCHANNEL sc on sc.channelcode = wta.key2 " +
                            " left outer join SMSOUTPUTTEMPLATE st on st.templatecode = wta.key3 " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                            " limit " + channelTxnTypeInputBean.displayLength + " offset " + channelTxnTypeInputBean.displayStart;

            channelTxnTypeDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.CHANNEL_TXN_TYPE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
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
        return channelTxnTypeDualList;
    }


    @Transactional(readOnly = true)
    public List<ChannelTxnType> getChannelTxnTypeSearchList(ChannelTxnTypeInputBean channelTxnTypeInputBean) {
        List<ChannelTxnType> channelTxnTypeList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(channelTxnTypeInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            String col = "";


            if (channelTxnTypeInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by ct.lastupdatedtime desc ";
            } else {
                switch (channelTxnTypeInputBean.sortedColumns.get(0)) {

                    case 0:
                        col = "tt.description";
                        break;
                    case 1:
                        col = "sc.description";
                        break;
                    case 2:
                        col = "ct.description";
                        break;
                    case 3:
                        col = "s.description";
                        break;
                    case 4:
                        col = "ct.createdtime";
                        break;
                    case 5:
                        col = "ct.lastupdatedtime";
                        break;
                    case 6:
                        col = "ct.lastupdateduser";
                        break;
                    default:
                        col = "ct.lastupdatedtime";
                }
                sortingStr = " order by " + col + " " + channelTxnTypeInputBean.sortDirections.get(0);
                //sortingStr = " order by c.lastupdatedtime " + categoryTelcoInputBean.sortDirections.get(0);
            }

            String sql =
                    " select ct.txntype, tt.description as txntypeDescription, ct.channelcode as channel, sc.description as channelDescription, ct.templatecode as template, st.description as templateDescription, " +
                            " s.description as status, ct.createdtime as createdtime, ct.createduser as createduser, ct.lastupdatedtime as lastupdatedtime, ct.lastupdateduser as lastupdateduser from CHANNEL_TXNTYPE ct " +
                            " left outer join STATUS s on s.statuscode=ct.status " +
                            " left outer join TXN_TYPE tt on tt.txntype=ct.txntype " +
                            " left outer join SMSCHANNEL sc on sc.channelcode=ct.channelcode " +
                            " left outer join SMSOUTPUTTEMPLATE st on st.templatecode=ct.templatecode " +
                            " where " + dynamicClause + sortingStr +
                            " limit " + channelTxnTypeInputBean.displayLength + " offset " + channelTxnTypeInputBean.displayStart;


            channelTxnTypeList = jdbcTemplate.query(sql, (rs, rowNum) -> {

                ChannelTxnType channelTxnType = new ChannelTxnType();
                try {
                    channelTxnType.setTxntype(rs.getString("txntype"));
                } catch (Exception e) {
                    channelTxnType.setTxntype(null);
                }

                try {
                    channelTxnType.setTxntypeDescription(rs.getString("txntypeDescription"));
                } catch (Exception e) {
                    channelTxnType.setTxntypeDescription(null);
                }

                try {
                    channelTxnType.setChannelcode(rs.getString("channel"));
                } catch (Exception e) {
                    channelTxnType.setChannelcode(null);
                }

                try {
                    channelTxnType.setChannelDescription(rs.getString("channelDescription"));
                } catch (Exception e) {
                    channelTxnType.setChannelDescription(null);
                }

                try {
                    channelTxnType.setTemplatecode(rs.getString("template"));
                } catch (Exception e) {
                    channelTxnType.setTemplatecode(null);
                }

                try {
                    channelTxnType.setTemplateDescription(rs.getString("templateDescription"));
                } catch (Exception e) {
                    channelTxnType.setTemplateDescription(null);
                }

                try {
                    channelTxnType.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    channelTxnType.setStatus(null);
                }

                try {
                    channelTxnType.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    channelTxnType.setCreatedTime(null);
                }

                try {
                    channelTxnType.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    channelTxnType.setLastUpdatedTime(null);
                }

                try {
                    channelTxnType.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    channelTxnType.setLastUpdatedUser(null);
                }

                try {
                    channelTxnType.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    channelTxnType.setCreatedUser(null);
                }
                return channelTxnType;
            });
        } catch (EmptyResultDataAccessException ex) {
            return channelTxnTypeList;
        } catch (Exception e) {
            throw e;
        }
        return channelTxnTypeList;
    }

    @Transactional(readOnly = true)
    public ChannelTxnType getChannelTxnType(String txntype, String channel) {
        ChannelTxnType newChannelTxnType = null;
        try {
            newChannelTxnType = jdbcTemplate.queryForObject(SQL_FIND_CATEGORY, new Object[]{txntype, channel}, new RowMapper<ChannelTxnType>() {
                @Override
                public ChannelTxnType mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ChannelTxnType channelTxnType = new ChannelTxnType();

                    try {
                        channelTxnType.setTxntype(rs.getString("txntype"));
                    } catch (Exception e) {
                        channelTxnType.setTxntype(null);
                    }

                    try {
                        channelTxnType.setTxntypeDescription(rs.getString("txntypeDescription"));
                    } catch (Exception e) {
                        channelTxnType.setTxntypeDescription(null);
                    }

                    try {
                        channelTxnType.setChannelcode(rs.getString("channel"));
                    } catch (Exception e) {
                        channelTxnType.setChannelcode(null);
                    }

                    try {
                        channelTxnType.setChannelDescription(rs.getString("channelDescription"));
                    } catch (Exception e) {
                        channelTxnType.setChannelDescription(null);
                    }

                    try {
                        channelTxnType.setTemplatecode(rs.getString("template"));
                    } catch (Exception e) {
                        channelTxnType.setTemplatecode(null);
                    }

                    try {
                        channelTxnType.setTemplateDescription(rs.getString("templateDescription"));
                    } catch (Exception e) {
                        channelTxnType.setTemplateDescription(null);
                    }

                    try {
                        channelTxnType.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        channelTxnType.setStatus(null);
                    }

                    try {
                        channelTxnType.setCreatedTime(new Date(rs.getDate("createdtime").getTime()));
                    } catch (Exception e) {
                        channelTxnType.setCreatedTime(null);
                    }

                    try {
                        channelTxnType.setLastUpdatedTime(new Date(rs.getDate("lastupdatedtime").getTime()));
                    } catch (SQLException e) {
                        channelTxnType.setLastUpdatedTime(null);
                    }

                    try {
                        channelTxnType.setLastUpdatedUser(rs.getString("lastupdateduser"));
                    } catch (SQLException e) {
                        channelTxnType.setLastUpdatedUser(null);
                    }

                    return channelTxnType;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            newChannelTxnType = null;
        } catch (Exception e) {
            throw e;
        }
        return newChannelTxnType;
    }

    @Transactional
    public String insertChannelTxnType(ChannelTxnTypeInputBean channelTxnTypeInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_CHANNEL_TXN_TYPE,
                    channelTxnTypeInputBean.getTxntype(),
                    channelTxnTypeInputBean.getChannel(),
                    channelTxnTypeInputBean.getTemplate(),
                    channelTxnTypeInputBean.getStatus(),
                    channelTxnTypeInputBean.getCreatedUser(),
                    channelTxnTypeInputBean.getCreatedTime(),
                    channelTxnTypeInputBean.getLastUpdatedTime(),
                    channelTxnTypeInputBean.getLastUpdatedUser()
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

    private StringBuilder setDynamicClause(ChannelTxnTypeInputBean channelTxnTypeInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (channelTxnTypeInputBean.getTxntype() != null && !channelTxnTypeInputBean.getTxntype().isEmpty()) {
                dynamicClause.append("and lower(ct.txntype) like lower('%").append(channelTxnTypeInputBean.getTxntype()).append("%')");
            }

            if (channelTxnTypeInputBean.getChannel() != null && !channelTxnTypeInputBean.getChannel().isEmpty()) {
                dynamicClause.append("and lower(ct.channelcode) like lower('%").append(channelTxnTypeInputBean.getChannel()).append("%')");
            }

            if (channelTxnTypeInputBean.getTemplate() != null && !channelTxnTypeInputBean.getTemplate().isEmpty()) {
                dynamicClause.append("and lower(ct.templatecode) like ('%").append(channelTxnTypeInputBean.getTemplate()).append("%')");//removed lower
            }

            if (channelTxnTypeInputBean.getStatus() != null && !channelTxnTypeInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and t.status = '").append(channelTxnTypeInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseForgetDataCount(ChannelTxnTypeInputBean channelTxnTypeInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (channelTxnTypeInputBean.getTxntype() != null && !channelTxnTypeInputBean.getTxntype().isEmpty()) {
                dynamicClause.append("and lower(ct.txntype) like lower('%").append(channelTxnTypeInputBean.getTxntype()).append("%')");
            }

            if (channelTxnTypeInputBean.getChannel() != null && !channelTxnTypeInputBean.getChannel().isEmpty()) {
                dynamicClause.append("and lower(ct.channelcode) like lower('%").append(channelTxnTypeInputBean.getChannel()).append("%')");
            }

            if (channelTxnTypeInputBean.getTemplate() != null && !channelTxnTypeInputBean.getTemplate().isEmpty()) {
                dynamicClause.append("and lower(ct.templatecode) like ('%").append(channelTxnTypeInputBean.getTemplate()).append("%')");//removed lower
            }

            if (channelTxnTypeInputBean.getStatus() != null && !channelTxnTypeInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and t.status = '").append(channelTxnTypeInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(ChannelTxnTypeInputBean channelTxnTypeInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (channelTxnTypeInputBean.getTxntype() != null && !channelTxnTypeInputBean.getTxntype().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(channelTxnTypeInputBean.getTxntype()).append("%')");
            }

            if (channelTxnTypeInputBean.getChannel() != null && !channelTxnTypeInputBean.getChannel().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(channelTxnTypeInputBean.getChannel()).append("%')");
            }

            if (channelTxnTypeInputBean.getTemplate() != null && !channelTxnTypeInputBean.getTemplate().isEmpty()) {
                dynamicClause.append("and lower(wta.key3) like lower('%").append(channelTxnTypeInputBean.getTemplate()).append("%')");
            }

            if (channelTxnTypeInputBean.getStatus() != null && !channelTxnTypeInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key4 = '").append(channelTxnTypeInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

}
