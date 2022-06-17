package com.oxcentra.rdbsms.repository.usermgt.section;

import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.usermgt.section.SectionInputBean;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.usermgt.Section;
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
public class SectionRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from WEB_SECTION ws left outer join STATUS s on s.statuscode=ws.status where ";
    private final String SQL_INSERT_SECTION = "insert into WEB_SECTION(sectioncode,description,status,sortkey,createduser,createdtime,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?,?) ";
    private final String SQL_UPDATE_SECTION = "update WEB_SECTION ws set ws.description = ? , ws.status = ?, ws.sortkey=?, ws.lastupdateduser=?, ws.lastupdatedtime=? where ws.sectioncode = ?";
    private final String SQL_FIND_SECTION = "select sectioncode,description,status,sortkey,createdtime,lastupdatedtime,lastupdateduser from WEB_SECTION ws where ws.sectioncode = ?";
    private final String SQL_DELETE_SECTION = "delete from WEB_SECTION where sectioncode = ?";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";


    @Transactional(readOnly = true)
    public long getDataCount(SectionInputBean sectionInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(sectionInputBean, dynamicClause);
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
    public List<Section> getSectionSearchList(SectionInputBean sectionInputBean) {
        List<Section> sectionList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(sectionInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (sectionInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by ws.lastupdatedtime desc ";
            } else {
                sortingStr = " order by ws.lastupdatedtime " + sectionInputBean.sortDirections.get(0);
            }

            String sql =
                    " select ws.sectioncode as sectioncode,ws.description as description, ws.status as status,ws.sortkey as sortkey,s.description as statusdescription," +
                            " ws.createdtime as createdtime, ws.createduser as createduser, ws.lastupdatedtime as lastupdatedtime ,ws.lastupdateduser as lastupdateduser from WEB_SECTION ws " +
                            " left outer join STATUS s on s.statuscode=ws.status " +
                            " where " + dynamicClause + sortingStr +
                            " limit " + sectionInputBean.displayLength + " offset " + sectionInputBean.displayStart;

            sectionList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Section section = new Section();
                try {
                    section.setSectionCode(rs.getString("sectionCode"));
                } catch (Exception e) {
                    section.setSectionCode(null);
                }

                try {
                    section.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    section.setDescription(null);
                }

                try {
                    section.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    section.setStatus(null);
                }


                try {
                    section.setSortKey(rs.getInt("sortKey"));
                } catch (Exception e) {
                    section.setSortKey(0);
                }

                try {
                    section.setCreatedTime(rs.getDate("createdTime"));
                } catch (Exception e) {
                    section.setCreatedTime(null);
                }

                try {
                    section.setCreatedUser(rs.getString("createdUser"));
                } catch (Exception e) {
                    section.setCreatedUser(null);
                }


                try {
                    section.setLastUpdatedTime(rs.getDate("lastUpdatedTime"));
                } catch (Exception e) {
                    section.setLastUpdatedTime(null);
                }

                try {
                    section.setLastUpdatedUser(rs.getString("lastUpdatedUser"));
                } catch (Exception e) {
                    section.setLastUpdatedUser(null);
                }

                return section;
            });
        } catch (EmptyResultDataAccessException ex) {
            return sectionList;
        } catch (Exception e) {
            throw e;
        }
        return sectionList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(SectionInputBean sectionInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(sectionInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.SECTION_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getSectionSearchResultsDual(SectionInputBean sectionInputBean) {
        List<TempAuthRec> sectionDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(sectionInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (sectionInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + sectionInputBean.sortDirections.get(0);
            }
            String sql = "" +
                    " select wta.id,wta.key1,wta.key2,wta.key3,wta.key4, s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                    " from WEB_TMPAUTHREC wta" +
                    " left outer join STATUS s on s.statuscode = wta.key3 " +
                    " left outer join WEB_TASK t on t.taskcode = wta.task " +
                    " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                    " limit " + sectionInputBean.displayLength + " offset " + sectionInputBean.displayStart;

            sectionDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.SECTION_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
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
                    tempAuthRec.setKey3(rs.getString("statusdescription"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
                }

                try {
                    tempAuthRec.setKey4(rs.getString("key4"));
                } catch (Exception e) {
                    tempAuthRec.setKey4(null);
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
    public String insertSection(SectionInputBean sectionInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_SECTION, sectionInputBean.getSectionCode(),
                    sectionInputBean.getDescription(),
                    sectionInputBean.getStatus(),
                    Integer.parseInt(sectionInputBean.getSortKey()),
                    sectionInputBean.getCreatedUser(),
                    sectionInputBean.getCreatedTime(),
                    sectionInputBean.getLastUpdatedTime(),
                    sectionInputBean.getLastUpdatedUser());
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
    public Section getSection(String sectionCode) {
        Section section = null;
        try {
            section = jdbcTemplate.queryForObject(SQL_FIND_SECTION, new Object[]{sectionCode}, new RowMapper<Section>() {
                @Override
                public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Section section = new Section();

                    try {
                        section.setSectionCode(rs.getString("sectionCode"));
                    } catch (Exception e) {
                        section.setSectionCode(null);
                    }

                    try {
                        section.setDescription(rs.getString("description"));
                    } catch (Exception e) {
                        section.setDescription(null);
                    }

                    try {
                        section.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        section.setStatus(null);
                    }

                    try {
                        section.setSortKey(rs.getInt("sortKey"));
                    } catch (Exception e) {
                        section.setSortKey(0);
                    }

                    try {
                        section.setCreatedTime(new Date(rs.getDate("createdTime").getTime()));
                    } catch (Exception e) {
                        section.setCreatedTime(null);
                    }


                    try {
                        section.setLastUpdatedTime(new Date(rs.getDate("lastUpdatedTime").getTime()));
                    } catch (SQLException e) {
                        section.setLastUpdatedTime(null);
                    }

                    try {
                        section.setLastUpdatedUser(rs.getString("lastUpdatedUser"));
                    } catch (SQLException e) {
                        section.setLastUpdatedUser(null);
                    }

                    return section;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            section = null;
        } catch (Exception e) {
            throw e;
        }
        return section;
    }

    @Transactional
    public String updateSection(SectionInputBean sectionInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_SECTION,
                    sectionInputBean.getDescription(),
                    sectionInputBean.getStatus(),
                    Integer.parseInt(sectionInputBean.getSortKey()),
                    sectionInputBean.getLastUpdatedUser(),
                    sectionInputBean.getLastUpdatedTime(),
                    sectionInputBean.getSectionCode());

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteSection(String sectionCode) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_SECTION, sectionCode);
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

    private StringBuilder setDynamicClause(SectionInputBean sectionInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (sectionInputBean.getSectionCode() != null && !sectionInputBean.getSectionCode().isEmpty()) {
                dynamicClause.append("and lower(ws.sectionCode) like lower('%").append(sectionInputBean.getSectionCode()).append("%')");
            }

            if (sectionInputBean.getDescription() != null && !sectionInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(ws.description) like lower('%").append(sectionInputBean.getDescription()).append("%')");
            }

            if (sectionInputBean.getStatus() != null && !sectionInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and ws.status = '").append(sectionInputBean.getStatus()).append("'");
            }

            if (sectionInputBean.getSortKey() != null && !sectionInputBean.getSortKey().isEmpty()) {
                dynamicClause.append("and ws.sortKey = '").append(sectionInputBean.getSortKey()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(SectionInputBean sectionInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (sectionInputBean.getSectionCode() != null && !sectionInputBean.getSectionCode().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(sectionInputBean.getSectionCode()).append("%')");
            }

            if (sectionInputBean.getDescription() != null && !sectionInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(sectionInputBean.getDescription()).append("%')");
            }

            if (sectionInputBean.getStatus() != null && !sectionInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key3 = '").append(sectionInputBean.getStatus()).append("'");
            }

            if (sectionInputBean.getSortKey() != null && !sectionInputBean.getSortKey().isEmpty()) {
                dynamicClause.append("and ws.key4 = '").append(sectionInputBean.getSortKey()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

}
