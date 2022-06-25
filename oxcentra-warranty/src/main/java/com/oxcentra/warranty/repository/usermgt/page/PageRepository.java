package com.oxcentra.warranty.repository.usermgt.page;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.usermgt.page.PageInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.usermgt.Page;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Repository
@Scope("prototype")
public class PageRepository {

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from web_page p left outer join status s on s.statuscode=p.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from web_tmpauthrec d where d.page=? and d.status=? and d.lastupdateduser <> ? and ";
    private final String SQL_FIND_PAGE = "select p.pagecode, p.description, p.url, p.sortkey, p.aflag, p.cflag, p.status, p.createdtime, p.lastupdatedtime, p.lastupdateduser from web_page p where p.pagecode = ? ";
    private final String SQL_UPDATE_PAGE = "update web_page set description=?, sortkey=?, cflag=?, status=?, lastupdateduser=?, lastupdatedtime=? where pagecode=?";
    private final String SQL_CHECK_SORTKEY_EXIST = "select count(pagecode) from web_page where sortkey = ? and pagecode != ?";
    private final String SQL_CHECK_TEMP_SORTKEY_EXIST = "select count(id) from web_tmpauthrec where page= ? and key4 = ? and status = ? ";
    @Autowired
    SessionBean sessionBean;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    CommonRepository commonRepository;

    @Transactional(readOnly = true)
    public long getDataCount(PageInputBean pageInputBean) throws Exception {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(pageInputBean, dynamicClause);
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
    public List<Page> getPageSearchResults(PageInputBean pageInputBean) throws Exception {
        List<Page> pageList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(pageInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (pageInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by p.lastupdatedtime desc ";
            } else {
                sortingStr = " order by p.lastupdatedtime " + pageInputBean.sortDirections.get(0);
            }

            String sql =
                    " select p.pagecode, p.description, p.url, p.sortkey, p.aflag, p.cflag, s.description as statusdes, p.lastupdateduser, p.lastupdatedtime, p.createduser, p.createdtime from web_page p " +
                            " left outer join status s on s.statuscode=p.status " +
                            " where " + dynamicClause.toString() + sortingStr +
                            " limit " + pageInputBean.displayLength + " offset " + pageInputBean.displayStart;

            pageList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Page page = new Page();
                try {
                    page.setPageCode(rs.getString("pagecode"));
                } catch (Exception e) {
                    page.setPageCode(null);
                }

                try {
                    page.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    page.setDescription(null);
                }

                try {
                    page.setUrl(rs.getString("url"));
                } catch (Exception e) {
                    page.setUrl(null);
                }

                try {
                    page.setSortKey(rs.getInt("sortkey"));
                } catch (Exception e) {
                    page.setSortKey(0);
                }

                try {
                    page.setActualFalg(rs.getBoolean("aflag"));
                } catch (Exception e) {
                    page.setActualFalg(false);
                }

                try {
                    page.setCurrentFlag(rs.getBoolean("cflag"));
                } catch (Exception e) {
                    page.setCurrentFlag(false);
                }

                try {
                    page.setStatusCode(rs.getString("statusdes"));
                } catch (Exception e) {
                    page.setStatusCode(null);
                }

                try {
                    page.setCreatedTime(rs.getDate("createdtime"));
                } catch (SQLException e) {
                    page.setCreatedTime(null);
                }

                try {
                    page.setCreatedUser(rs.getString("createduser"));
                } catch (SQLException e) {
                    page.setCreatedUser(null);
                }

                try {
                    page.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (SQLException e) {
                    page.setCreatedTime(null);
                }

                try {
                    page.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (SQLException e) {
                    page.setCreatedUser(null);
                }
                return page;
            });
        } catch (EmptyResultDataAccessException ex) {
            return pageList;
        } catch (Exception e) {
            throw e;
        }
        return pageList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(PageInputBean pageInputBean) throws Exception {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(pageInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.PAGE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getPageSearchResultsDual(PageInputBean pageInputBean) throws Exception {
        List<TempAuthRec> pageDualList;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(pageInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (pageInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + pageInputBean.sortDirections.get(0);
            }

            String sql =
                    " select wta.id, wta.key1, wta.key2, wta.key3, wta.key4, wta.key5, wta.key6, s.description key7, t.description task, wta.createdtime, wta.lastupdatedtime, wta.lastupdateduser " +
                            " from web_tmpauthrec wta" +
                            " left outer join status s on s.statuscode = wta.key3 " +
                            " left outer join web_task t on t.taskcode = wta.task " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause.toString() + sortingStr +
                            " limit " + pageInputBean.displayLength + " offset " + pageInputBean.displayStart;

            pageDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.PAGE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, ((rs, rowNum) -> {
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
                    //page code
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
                    //url
                    tempAuthRec.setKey3(rs.getString("key3"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
                }

                try {
                    //sort key
                    tempAuthRec.setKey4(rs.getString("key4"));
                } catch (Exception e) {
                    tempAuthRec.setKey4(null);
                }

                try {
                    //actual dual auth flag
                    tempAuthRec.setKey5(rs.getString("key5"));
                } catch (Exception e) {
                    tempAuthRec.setKey5(null);
                }

                try {
                    //current dual auth flag
                    tempAuthRec.setKey6(rs.getString("key6"));
                } catch (Exception e) {
                    tempAuthRec.setKey6(null);
                }

                try {
                    //status
                    tempAuthRec.setKey7(rs.getString("key7"));
                } catch (Exception e) {
                    tempAuthRec.setKey7(null);
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
            }));
        } catch (Exception e) {
            throw e;
        }
        return pageDualList;
    }

    @Transactional(readOnly = true)
    public Page getPage(String pageCode) throws Exception {
        Page page = null;
        try {
            page = jdbcTemplate.queryForObject(SQL_FIND_PAGE, new Object[]{pageCode}, ((rs, rowNum) -> {
                Page p = new Page();

                try {
                    p.setPageCode(rs.getString("pagecode"));
                } catch (Exception e) {
                    p.setPageCode(null);
                }

                try {
                    p.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    p.setDescription(null);
                }

                try {
                    p.setUrl(rs.getString("url"));
                } catch (Exception e) {
                    p.setUrl(null);
                }

                try {
                    p.setSortKey(rs.getInt("sortkey"));
                } catch (Exception e) {
                    p.setSortKey(0);
                }

                try {
                    p.setActualFalg(rs.getBoolean("aflag"));
                } catch (Exception e) {
                    p.setActualFalg(false);
                }

                try {
                    p.setCurrentFlag(rs.getBoolean("cflag"));
                } catch (Exception e) {
                    p.setCurrentFlag(false);
                }

                try {
                    p.setStatusCode(rs.getString("status"));
                } catch (Exception e) {
                    p.setStatusCode(null);
                }

                try {
                    p.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    p.setCreatedTime(null);
                }

                try {
                    p.setLastUpdatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    p.setLastUpdatedTime(null);
                }

                try {
                    p.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    p.setLastUpdatedUser(null);
                }
                return p;
            }));
        } catch (EmptyResultDataAccessException erse) {
            page = null;
        } catch (Exception e) {
            throw e;
        }
        return page;
    }

    @Transactional
    public String updatePage(PageInputBean pageInputBean) throws Exception {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_PAGE,
                    pageInputBean.getDescription(),
                    pageInputBean.getSortKey(),
                    pageInputBean.isCurrentFlag(),
                    pageInputBean.getStatus(),
                    pageInputBean.getLastUpdatedUser(),
                    pageInputBean.getLastUpdatedTime(),
                    pageInputBean.getPageCode());
            if (value != 1) {
                message = MessageVarList.PAGE_MGT_ERROR_UPDATE;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional(readOnly = true)
    public boolean checkSortKeyExist(String pageCode, int sortKey) {
        boolean check;
        try {
            long count = jdbcTemplate.queryForObject(SQL_CHECK_SORTKEY_EXIST, new Object[]{sortKey, pageCode}, Long.class);
            check = count > 0;
        } catch (EmptyResultDataAccessException ere) {
            check = false;
        } catch (Exception ex) {
            throw ex;
        }
        return check;
    }

    @Transactional(readOnly = true)
    public boolean checkTempSortKeyExist(String pagecode, int sortKey) {
        boolean check;
        try {
            long count = jdbcTemplate.queryForObject(SQL_CHECK_TEMP_SORTKEY_EXIST, new Object[]{pagecode, sortKey, StatusVarList.STATUS_AUTH_PEN}, Long.class);
            check = count > 0;
        } catch (EmptyResultDataAccessException ere) {
            check = false;
        } catch (Exception ex) {
            throw ex;
        }
        return check;
    }

    private StringBuilder setDynamicClause(PageInputBean inputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (inputBean.getPageCode() != null && !inputBean.getPageCode().isEmpty()) {
                dynamicClause.append("and lower(p.pagecode) like lower('%").append(inputBean.getPageCode()).append("%')");
            }

            if (inputBean.getDescription() != null && !inputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(p.description) like lower('%").append(inputBean.getDescription()).append("%')");
            }

            if (inputBean.getSortKey() != 0) {
                dynamicClause.append("and p.sortKey = '").append(inputBean.getSortKey()).append("'");
            }

            if (inputBean.getStatus() != null && !inputBean.getStatus().isEmpty()) {
                dynamicClause.append("and p.status = '").append(inputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(PageInputBean pg, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (pg.getPageCode() != null && !pg.getPageCode().isEmpty()) {
                dynamicClause.append("and lower(d.key1) like lower('%").append(pg.getPageCode()).append("%')");
            }

            if (pg.getDescription() != null && !pg.getDescription().isEmpty()) {
                dynamicClause.append("and lower(d.key2) like lower('%").append(pg.getDescription()).append("%')");
            }

            if (pg.getSortKey() != 0) {
                dynamicClause.append("and d.key4 = '").append(pg.getSortKey()).append("'");
            }

            if (pg.getStatus() != null && !pg.getStatus().isEmpty()) {
                dynamicClause.append("and d.key7 = '").append(pg.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
