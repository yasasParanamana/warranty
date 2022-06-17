package com.oxcentra.rdbsms.repository.sysconfigmgt.channel;

import com.oxcentra.rdbsms.bean.channel.ChannelInputBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.mapping.channel.Channel;
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
public class ChannelRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from SMSCHANNEL d left outer join STATUS s on s.statuscode=d.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";
    private final String SQL_INSERT_CHANNEL = "insert into SMSCHANNEL(channelcode,description,password,status,createdtime,createduser,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?,?) ";
    private final String SQL_FIND_CHANNEL = "select channelcode,description,status,createdtime,createduser,lastupdatedtime,lastupdateduser from SMSCHANNEL d where d.channelcode = ?";
    private final String SQL_UPDATE_CHANNEL = "update SMSCHANNEL d set description = ?, status = ?, lastupdateduser = ?,lastupdatedtime = ? where d.channelcode = ?";
    private final String SQL_DELETE_CHANNEL = "delete from SMSCHANNEL where channelcode = ?";

    @Transactional(readOnly = true)
    public long getDataCount(ChannelInputBean channelInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(channelInputBean, dynamicClause);
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
    public List<Channel> getChannelSearchList(ChannelInputBean channelInputBean) {
        List<Channel> channelList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(channelInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (channelInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by d.lastupdatedtime desc ";
            } else {
                sortingStr = " order by d.lastupdatedtime " + channelInputBean.sortDirections.get(0);
            }

            String sql =
                    " select d.channelcode as channelCode,d.description as description, d.status as status,s.description as statusdescription," +
                            " d.createdtime as createdtime,d.createduser as createdUser,d.lastupdatedtime as lastupdatedtime ,d.lastupdateduser as lastupdateduser from SMSCHANNEL d " +
                            " left outer join STATUS s on s.statuscode=d.status " +
                            " where " + dynamicClause + sortingStr +
                            " limit " + channelInputBean.displayLength + " offset " + channelInputBean.displayStart;

            channelList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Channel channel = new Channel();
                try {
                    channel.setChannelCode(rs.getString("channelCode"));
                } catch (Exception e) {
                    channel.setChannelCode(null);
                }

                try {
                    channel.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    channel.setDescription(null);
                }

                try {
                    channel.setPassword(rs.getString("password"));
                } catch (Exception e) {
                    channel.setPassword(null);
                }

                try {
                    channel.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    channel.setStatus(null);
                }

                try {
                    channel.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    channel.setCreatedTime(null);
                }

                try {
                    channel.setCreatedUser(rs.getString("createdUser"));
                } catch (Exception e) {
                    channel.setCreatedUser(null);
                }

                try {
                    channel.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    channel.setLastUpdatedTime(null);
                }

                try {
                    channel.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    channel.setLastUpdatedUser(null);
                }

                return channel;
            });
        } catch (EmptyResultDataAccessException ex) {
            return channelList;
        } catch (Exception e) {
            throw e;
        }
        return channelList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(ChannelInputBean channelInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(channelInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.CHANNEL_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getChannelSearchResultsDual(ChannelInputBean channelInputBean) {
        List<TempAuthRec> channelDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(channelInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (channelInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + channelInputBean.sortDirections.get(0);
            }
            String sql = "" +
                    " select wta.id,wta.key1,wta.key2,wta.key3,wta.key7,s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                    " from WEB_TMPAUTHREC wta" +
                    " left outer join STATUS s on s.statuscode = wta.key3 " +
                    " left outer join WEB_TASK t on t.taskcode = wta.task " +
                    " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                    " limit " + channelInputBean.displayLength + " offset " + channelInputBean.displayStart;

            channelDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.CHANNEL_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
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
                    tempAuthRec.setKey7(rs.getString("key7"));
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
        return channelDualList;
    }

    @Transactional
    public String insertChannel(ChannelInputBean channelInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_CHANNEL, channelInputBean.getChannelCode(),
                    channelInputBean.getDescription(),
                    channelInputBean.getPassword(),
                    channelInputBean.getStatus(),
                    channelInputBean.getCreatedTime(),
                    channelInputBean.getCreatedUser(),
                    channelInputBean.getLastUpdatedTime(),
                    channelInputBean.getLastUpdatedUser());
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
    public Channel getChannel(String channelCode) {
        Channel channel = null;
        try {
            channel = jdbcTemplate.queryForObject(SQL_FIND_CHANNEL, new Object[]{channelCode}, new RowMapper<Channel>() {
                @Override
                public Channel mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Channel channel = new Channel();

                    try {
                        channel.setChannelCode(rs.getString("channelcode"));
                    } catch (Exception e) {
                        channel.setChannelCode(null);
                    }

                    try {
                        channel.setDescription(rs.getString("description"));
                    } catch (Exception e) {
                        channel.setDescription(null);
                    }
                    try {
                        channel.setPassword(rs.getString("password"));
                    } catch (Exception e) {
                        channel.setPassword(null);
                    }

                    try {
                        channel.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        channel.setStatus(null);
                    }

                    try {
                        channel.setCreatedTime(new Date(rs.getDate("createdtime").getTime()));
                    } catch (Exception e) {
                        channel.setCreatedTime(null);
                    }

                    try {
                        channel.setCreatedUser(rs.getString("createduser"));
                    } catch (Exception e) {
                        channel.setCreatedTime(null);
                    }

                    try {
                        channel.setLastUpdatedTime(new Date(rs.getDate("lastupdatedtime").getTime()));
                    } catch (SQLException e) {
                        channel.setLastUpdatedTime(null);
                    }

                    try {
                        channel.setLastUpdatedUser(rs.getString("lastupdateduser"));
                    } catch (SQLException e) {
                        channel.setLastUpdatedUser(null);
                    }

                    return channel;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            channel = null;
        } catch (Exception e) {
            throw e;
        }
        return channel;
    }

    @Transactional
    public String updateChannel(ChannelInputBean channelInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_CHANNEL,
                    channelInputBean.getDescription(),
                    channelInputBean.getStatus(),
                    channelInputBean.getLastUpdatedUser(),
                    channelInputBean.getLastUpdatedTime(),
                    channelInputBean.getChannelCode());

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteChannel(String channelCode) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_CHANNEL, channelCode);
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

    private StringBuilder setDynamicClause(ChannelInputBean channelInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (channelInputBean.getChannelCode() != null && !channelInputBean.getChannelCode().isEmpty()) {
                dynamicClause.append("and lower(d.channelcode) like lower('%").append(channelInputBean.getChannelCode()).append("%')");
            }

            if (channelInputBean.getDescription() != null && !channelInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(d.description) like lower('%").append(channelInputBean.getDescription()).append("%')");
            }

            if (channelInputBean.getPassword() != null && !channelInputBean.getPassword().isEmpty()) {
                dynamicClause.append("and lower(d.password) like lower('%").append(channelInputBean.getPassword()).append("%')");
            }

            if (channelInputBean.getStatus() != null && !channelInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and d.status = '").append(channelInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(ChannelInputBean channelInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");

        if (channelInputBean.getChannelCode() != null && !channelInputBean.getChannelCode().isEmpty()) {
            dynamicClause.append("and lower(wta.key1) like lower('%").append(channelInputBean.getChannelCode()).append("%')");
        }

        if (channelInputBean.getDescription() != null && !channelInputBean.getDescription().isEmpty()) {
            dynamicClause.append("and lower(wta.key2) like lower('%").append(channelInputBean.getDescription()).append("%')");
        }

        if (channelInputBean.getPassword() != null && !channelInputBean.getPassword().isEmpty()) {
            dynamicClause.append("and lower(wta.key7) like lower('%").append(channelInputBean.getPassword()).append("%')");
        }

        if (channelInputBean.getStatus() != null && !channelInputBean.getStatus().isEmpty()) {
            dynamicClause.append("and wta.key3 = '").append(channelInputBean.getStatus()).append("'");
        }
        return dynamicClause;
    }
}
