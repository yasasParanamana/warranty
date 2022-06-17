package com.oxcentra.rdbsms.repository.alerttypetelcomgt.categorytelcomgt;

import com.oxcentra.rdbsms.bean.alerttypetelcomgt.categorytelco.CategoryTelcoInputBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.mapping.categorytelcomgt.CategoryTelco;
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
public class CategoryTelcoRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from CATEGORYTELCO c left outer join STATUS s on s.statuscode=c.status" +
            " left outer join TELCO t on t.code=c.telco where";
    private final String SQL_FIND_CATEGORY = "select ct.category, c.description as categorydescription, ct.telco, ct.mtPort, ct.status, ct.createdtime, ct.createduser, ct.lastupdatedtime, ct.lastupdateduser from CATEGORYTELCO ct " +
            " left outer join CATEGORY c on c.category=ct.category where ct.category = ?";
    private final String SQL_INSERT_CATEGORY_TELCO = "insert into CATEGORYTELCO( category, telco, mtport, status, createdtime, createduser, lastupdatedtime, lastupdateduser ) VALUES(?,?,?,?,?,?,?,?)";
    private final String SQL_UPDATE_CATEGORY_TELCO = "update CATEGORYTELCO ct set ct.status = ?, ct.telco = ?, ct.mtport = ?, ct.lastupdateduser = ?, ct.lastupdatedtime = ?  where ct.category = ?";
    private final String SQL_DELETE_CATEGORY_TELCO = "delete from CATEGORYTELCO where category = ?";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";

    @Transactional(readOnly = true)
    public long getDataCount(CategoryTelcoInputBean categoryTelcoInputBean) {
        long count = 0;
        try {
            System.out.println("getDataCount method in Repo class");
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            System.out.println("||||||||| dynamicClause > " + dynamicClause);
            //create the where clause
            dynamicClause = this.setDynamicClauseForgetDataCount(categoryTelcoInputBean, dynamicClause);
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
    public String updateCategoryTelco(CategoryTelcoInputBean categoryTelcoInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_CATEGORY_TELCO,
                    categoryTelcoInputBean.getStatus(), categoryTelcoInputBean.getTelco(),
                    categoryTelcoInputBean.getMtPort(), categoryTelcoInputBean.getLastUpdatedUser(),
                    categoryTelcoInputBean.getLastUpdatedTime(), categoryTelcoInputBean.getCategory());
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(CategoryTelcoInputBean categoryTelcoInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(categoryTelcoInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.CATEGORY_TELCO_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional
    public String deleteCategoryTelco(String category) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_CATEGORY_TELCO, category);
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
    public List<TempAuthRec> getCategoryTelcoSearchResultsDual(CategoryTelcoInputBean categoryTelcoInputBean) {
        List<TempAuthRec> categoryTelcoDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(categoryTelcoInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            String col = "";

            switch (categoryTelcoInputBean.sortedColumns.get(0)) {
                case 0:
                    col = "wta.createdtime";
                    break;
                case 1:
                    col = "c.description";
                    break;
                case 2:
                    col = "t.description";
                    break;
                case 3:
                    col = "wta.key3";
                    break;
                case 4:
                    col = "s.description";
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
                    col = "wta.createdtime";
            }
            sortingStr = " order by " + col + " " + categoryTelcoInputBean.sortDirections.get(0);

            String sql =
                    " select wta.id,c.description as key1,s.description as statusdescription ,wt.description as taskdescription ,wta.createdtime, " +
                            " wta.lastupdatedtime,wta.lastupdateduser, t.description as key2, wta.key3 from WEB_TMPAUTHREC wta" +
                            " left outer join STATUS s on s.statuscode = wta.key4 " +
                            " left outer join WEB_TASK wt on wt.taskcode = wta.task " +
                            " left outer join TELCO t on t.code=wta.key2 " +
                            " left outer join CATEGORY c on c.category=wta.key1 " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                            " limit " + categoryTelcoInputBean.displayLength + " offset " + categoryTelcoInputBean.displayStart;

            categoryTelcoDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.CATEGORY_TELCO_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
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
        return categoryTelcoDualList;
    }


    @Transactional(readOnly = true)
    public List<CategoryTelco> getCategoryTelcoSearchList(CategoryTelcoInputBean categoryTelcoInputBean) {
        List<CategoryTelco> categoryTelcoList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(categoryTelcoInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            String col = "";


            if (categoryTelcoInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by ct.lastupdatedtime desc ";
            } else {
                switch (categoryTelcoInputBean.sortedColumns.get(0)) {

                    case 0:
                        col = "c.description";
                        break;
                    case 1:
                        col = "t.description";
                        break;
                    case 2:
                        col = "ct.mtport";
                        break;
                    case 3:
                        col = "s.description";
                        break;
                    case 4:
                        col = "ct.createdtime";
                        break;
                    case 5:
                        col = "ct.createduser";
                        break;
                    case 6:
                        col = "ct.lastupdatedtime";
                        break;
                    case 7:
                        col = "ct.lastupdateduser";
                        break;
                    default:
                        col = "ct.createdtime";
                }
                sortingStr = " order by " + col + " " + categoryTelcoInputBean.sortDirections.get(0);
                //sortingStr = " order by c.lastupdatedtime " + categoryTelcoInputBean.sortDirections.get(0);
            }

            String sql =
                    " select ct.category, c.description as categorydescription, t.description as telcodescription, ct.mtport as mtport, s.description as statusdescription," +
                            " ct.createdtime as createdtime,ct.createduser as createduser,ct.lastupdatedtime as lastupdatedtime ,ct.lastupdateduser as lastupdateduser from CATEGORYTELCO ct " +
                            " left outer join STATUS s on s.statuscode=ct.status " +
                            " left outer join TELCO t on t.code=ct.telco " +
                            " left outer join CATEGORY c on c.category=ct.category " +
                            " where " + dynamicClause + sortingStr +
                            " limit " + categoryTelcoInputBean.displayLength + " offset " + categoryTelcoInputBean.displayStart;


            categoryTelcoList = jdbcTemplate.query(sql, (rs, rowNum) -> {

                CategoryTelco categoryTelco = new CategoryTelco();
                try {
                    categoryTelco.setCategory(rs.getString("category"));
                } catch (Exception e) {
                    categoryTelco.setCategory(null);
                }

                try {
                    categoryTelco.setCategoryDescription(rs.getString("categorydescription"));
                } catch (Exception e) {
                    categoryTelco.setCategoryDescription(null);
                }

                try {
                    String telcodescription = rs.getString("telcodescription");
                    categoryTelco.setTelco(telcodescription);
                } catch (Exception e) {
                    categoryTelco.setTelco(null);
                }

                try {
                    String mp = rs.getString("mtport");
                    categoryTelco.setMtPort(mp);
                } catch (Exception e) {
                    categoryTelco.setMtPort(null);
                }

                try {
                    categoryTelco.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    categoryTelco.setStatus(null);
                }

                try {
                    categoryTelco.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    categoryTelco.setCreatedTime(null);
                }

                try {
                    categoryTelco.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    categoryTelco.setCreatedUser(null);
                }

                try {
                    categoryTelco.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    categoryTelco.setLastUpdatedTime(null);
                }

                try {
                    categoryTelco.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    categoryTelco.setLastUpdatedUser(null);
                }
                return categoryTelco;
            });
        } catch (EmptyResultDataAccessException ex) {
            return categoryTelcoList;
        } catch (Exception e) {
            throw e;
        }
        return categoryTelcoList;
    }

    @Transactional(readOnly = true)
    public CategoryTelco getCategoryTelco(String category) {
        CategoryTelco categoryTelco = null;
        try {
            categoryTelco = jdbcTemplate.queryForObject(SQL_FIND_CATEGORY, new Object[]{category}, new RowMapper<CategoryTelco>() {
                @Override
                public CategoryTelco mapRow(ResultSet rs, int rowNum) throws SQLException {
                    CategoryTelco categoryTelco = new CategoryTelco();

                    try {
                        categoryTelco.setCategory(rs.getString("category"));
                    } catch (Exception e) {
                        categoryTelco.setCategory(null);
                    }

                    try {
                        categoryTelco.setCategoryDescription(rs.getString("categorydescription"));
                    } catch (Exception e) {
                        categoryTelco.setCategoryDescription(null);
                    }

                    try {
                        categoryTelco.setTelco(rs.getString("telco"));
                    } catch (Exception e) {
                        categoryTelco.setTelco(null);
                    }

                    try {
                        categoryTelco.setMtPort(rs.getString("mtport"));
                    } catch (Exception e) {
                        categoryTelco.setMtPort(null);
                    }

                    try {
                        categoryTelco.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        categoryTelco.setStatus(null);
                    }

                    try {
                        categoryTelco.setCreatedTime(new Date(rs.getDate("createdtime").getTime()));
                    } catch (Exception e) {
                        categoryTelco.setCreatedTime(null);
                    }

                    try {
                        categoryTelco.setCreatedUser(rs.getString("createduser"));
                    } catch (Exception e) {
                        categoryTelco.setCreatedTime(null);
                    }

                    try {
                        categoryTelco.setLastUpdatedTime(new Date(rs.getDate("lastupdatedtime").getTime()));
                    } catch (SQLException e) {
                        categoryTelco.setLastUpdatedTime(null);
                    }

                    try {
                        categoryTelco.setLastUpdatedUser(rs.getString("lastupdateduser"));
                    } catch (SQLException e) {
                        categoryTelco.setLastUpdatedUser(null);
                    }

                    return categoryTelco;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            categoryTelco = null;
        } catch (Exception e) {
            throw e;
        }
        return categoryTelco;
    }

    @Transactional
    public String insertCategoryTelco(CategoryTelcoInputBean categoryTelcoInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_CATEGORY_TELCO,
                    categoryTelcoInputBean.getCategory(),
                    categoryTelcoInputBean.getTelco(),
                    categoryTelcoInputBean.getMtPort(),
                    categoryTelcoInputBean.getStatus(),
                    categoryTelcoInputBean.getCreatedTime(),
                    categoryTelcoInputBean.getCreatedUser(),
                    categoryTelcoInputBean.getLastUpdatedTime(),
                    categoryTelcoInputBean.getLastUpdatedUser()
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

    private StringBuilder setDynamicClause(CategoryTelcoInputBean categoryTelcoInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (categoryTelcoInputBean.getCategory() != null && !categoryTelcoInputBean.getCategory().isEmpty()) {
                dynamicClause.append("and lower(c.category) like lower('%").append(categoryTelcoInputBean.getCategory()).append("%')");
            }

            if (categoryTelcoInputBean.getTelco() != null && !categoryTelcoInputBean.getTelco().isEmpty()) {
                dynamicClause.append("and lower(t.description) like lower('%").append(categoryTelcoInputBean.getTelco()).append("%')");
            }

            if (categoryTelcoInputBean.getMtPort() != null && !categoryTelcoInputBean.getMtPort().isEmpty()) {
                dynamicClause.append("and lower(ct.mtport) like ('%").append(categoryTelcoInputBean.getMtPort()).append("%')");//removed lower
            }

            if (categoryTelcoInputBean.getStatus() != null && !categoryTelcoInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and t.status = '").append(categoryTelcoInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseForgetDataCount(CategoryTelcoInputBean categoryTelcoInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (categoryTelcoInputBean.getCategory() != null && !categoryTelcoInputBean.getCategory().isEmpty()) {
                dynamicClause.append("and lower(c.category) like lower('%").append(categoryTelcoInputBean.getCategory()).append("%')");
            }

            if (categoryTelcoInputBean.getTelco() != null && !categoryTelcoInputBean.getTelco().isEmpty()) {
                dynamicClause.append("and lower(t.description) like lower('%").append(categoryTelcoInputBean.getTelco()).append("%')");
            }

            if (categoryTelcoInputBean.getMtPort() != null && !categoryTelcoInputBean.getMtPort().isEmpty()) {
                dynamicClause.append("and lower(c.mtport) like ('%").append(categoryTelcoInputBean.getMtPort()).append("%')");//removed lower
            }

            if (categoryTelcoInputBean.getStatus() != null && !categoryTelcoInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and t.status = '").append(categoryTelcoInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(CategoryTelcoInputBean categoryTelcoInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (categoryTelcoInputBean.getCategory() != null && !categoryTelcoInputBean.getCategory().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(categoryTelcoInputBean.getCategory()).append("%')");
            }

            if (categoryTelcoInputBean.getTelco() != null && !categoryTelcoInputBean.getTelco().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(categoryTelcoInputBean.getTelco()).append("%')");
            }

            if (categoryTelcoInputBean.getMtPort() != null && !categoryTelcoInputBean.getMtPort().isEmpty()) {
                dynamicClause.append("and lower(wta.key3) like lower('%").append(categoryTelcoInputBean.getMtPort()).append("%')");
            }

            if (categoryTelcoInputBean.getStatus() != null && !categoryTelcoInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key4 = '").append(categoryTelcoInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
