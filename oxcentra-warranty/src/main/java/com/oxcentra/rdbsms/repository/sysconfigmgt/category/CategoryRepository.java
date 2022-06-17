package com.oxcentra.rdbsms.repository.sysconfigmgt.category;

import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.category.CategoryInputBean;
import com.oxcentra.rdbsms.mapping.category.Category;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
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
public class CategoryRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    CommonVarList commonVarList;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from CATEGORY c left outer join STATUS s on s.statuscode=c.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";
    private final String SQL_INSERT_CATEGORY = "insert into CATEGORY(category,description,isbulk,status,priority,ackwait,unsubscribe,ttlqueue,createdtime,createduser,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?,?,?,?,?,?) ";
    private final String SQL_FIND_CATEGORY = "select category,description,isbulk,status,priority,ttlqueue,ackwait,unsubscribe,createduser,lastupdatedtime,lastupdateduser from CATEGORY c where c.category = ?";
    private final String SQL_UPDATE_CATEGORY = "update CATEGORY c set description = ? , isbulk = ? , status = ? , priority = ? ,ackwait = ?,unsubscribe = ?, ttlqueue = ?,lastupdateduser = ? , lastupdatedtime = ? where c.category = ?";
    private final String SQL_DELETE_CATEGORY = "delete from CATEGORY where category = ?";

    @Transactional(readOnly = true)
    public long getDataCount(CategoryInputBean categoryInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(categoryInputBean, dynamicClause);
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
    public List<Category> getCategorySearchList(CategoryInputBean categoryInputBean) {
        List<Category> categoryList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(categoryInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (categoryInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by c.lastupdatedtime desc ";
            } else {
                sortingStr = " order by c.lastupdatedtime " + categoryInputBean.sortDirections.get(0);
            }

            String sql =
                    " select c.category as category,c.description as description, c.status as status,c.ttlqueue,c.ackwait,c.unsubscribe,c.isbulk as isbulk,s.description as statusdescription, p.description as priority , " +
                            " c.createdtime as createdtime,c.createduser as createduser,c.lastupdatedtime as lastupdatedtime , c.lastupdateduser as lastupdateduser from CATEGORY c " +
                            " left outer join STATUS s on s.statuscode=c.status " +
                            " left outer join PRIORITY p on p.prioritylevel= c.priority " +
                            " where " + dynamicClause + sortingStr +
                            " limit " + categoryInputBean.displayLength + " offset " + categoryInputBean.displayStart;

            categoryList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Category category = new Category();

                try {
                    category.setCategory(rs.getString("category"));
                } catch (Exception e) {
                    category.setCategory(null);
                }

                try {
                    category.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    category.setDescription(null);
                }

                try {
                    category.setIsBulk(rs.getString("isbulk"));
                } catch (Exception e) {
                    category.setIsBulk(null);
                }

                try {
                    category.setUnsubscribe(rs.getString("unsubscribe"));
                } catch (Exception e) {
                    category.setUnsubscribe(null);
                }

                try {
                    category.setAckwait(rs.getString("ackwait"));
                } catch (Exception e) {
                    category.setAckwait(null);
                }

                try {
                    category.setTtlqueue(rs.getInt("ttlqueue"));
                } catch (Exception e) {
                    category.setTtlqueue(null);
                }

                try {
                    category.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    category.setStatus(null);
                }

                try {
                    category.setPriority(rs.getString("priority"));
                } catch (Exception e) {
                    category.setPriority(null);
                }

                try {
                    category.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    category.setCreatedTime(null);
                }

                try {
                    category.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    category.setCreatedUser(null);
                }

                try {
                    category.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    category.setLastUpdatedTime(null);
                }

                try {
                    category.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    category.setLastUpdatedUser(null);
                }
                return category;
            });

        } catch (EmptyResultDataAccessException ex) {
            return categoryList;
        } catch (Exception e) {
            throw e;
        }
        return categoryList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(CategoryInputBean categoryInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(categoryInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.CATEGORY_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getCategorySearchResultsDual(CategoryInputBean categoryInputBean) {
        List<TempAuthRec> categoryDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(categoryInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (categoryInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + categoryInputBean.sortDirections.get(0);
            }

            String sql =
                    " select wta.id,wta.key1,wta.key2,wta.key3,wta.key4,wta.key5,wta.key6,wta.key7,wta.key8,s.description as statusdescription ,t.description as taskdescription ,p.description as prioritydescription,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                            " from WEB_TMPAUTHREC wta" +
                            " left outer join STATUS s on s.statuscode = wta.key4 " +
                            " left outer join WEB_TASK t on t.taskcode = wta.task " +
                            " left outer join PRIORITY p on p.prioritylevel = wta.key5 " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                            " limit " + categoryInputBean.displayLength + " offset " + categoryInputBean.displayStart;

            categoryDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.CATEGORY_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRec = new TempAuthRec();
                try {
                    tempAuthRec.setId(rs.getString("id"));
                } catch (Exception e) {
                    tempAuthRec.setId(null);
                }

                try {
                    //set category code
                    tempAuthRec.setKey1(rs.getString("key1"));
                } catch (Exception e) {
                    tempAuthRec.setKey1(null);
                }

                try {
                    //set description
                    tempAuthRec.setKey2(rs.getString("key2"));
                } catch (Exception e) {
                    tempAuthRec.setKey2(null);
                }

                try {
                    //set bulk enable
                    tempAuthRec.setKey3(rs.getString("key3"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
                }

                try {
                    //set status
                    tempAuthRec.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    //set priority
                    tempAuthRec.setKey5(rs.getString("prioritydescription"));
                } catch (Exception e) {
                    tempAuthRec.setKey5(null);
                }

                try {
                    //set bulk enable
                    tempAuthRec.setKey6(rs.getString("key6"));
                } catch (Exception e) {
                    tempAuthRec.setKey6(null);
                }

                try {
                    //set bulk enable
                    tempAuthRec.setKey7(rs.getString("key7"));
                } catch (Exception e) {
                    tempAuthRec.setKey7(null);
                }

                try {
                    //set bulk enable
                    tempAuthRec.setKey8(rs.getString("key8"));
                } catch (Exception e) {
                    tempAuthRec.setKey8(null);
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
        return categoryDualList;
    }

    @Transactional
    public String insertCategory(CategoryInputBean categoryInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_CATEGORY, categoryInputBean.getCategoryCode(),
                    categoryInputBean.getDescription(),
                    categoryInputBean.getBulkEnable(),
                    categoryInputBean.getStatus(),
                    categoryInputBean.getPriority(),
                    categoryInputBean.getAckwait(),
                    categoryInputBean.getUnsubscribe(),
                    categoryInputBean.getTtlqueue(),
                    categoryInputBean.getCreatedTime(),
                    categoryInputBean.getCreatedUser(),
                    categoryInputBean.getLastUpdatedTime(),
                    categoryInputBean.getLastUpdatedUser());

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
    public Category getCategory(String code) {
        Category category = null;
        try {
            category = jdbcTemplate.queryForObject(SQL_FIND_CATEGORY, new Object[]{code}, new RowMapper<Category>() {
                @Override
                public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Category category = new Category();

                    try {
                        category.setCategory(rs.getString("category"));
                    } catch (Exception e) {
                        category.setCategory(null);
                    }

                    try {
                        category.setDescription(rs.getString("description"));
                    } catch (Exception e) {
                        category.setDescription(null);
                    }

                    try {
                        category.setIsBulk(rs.getString("isbulk"));
                    } catch (Exception e) {
                        category.setIsBulk(null);
                    }

                    try {
                        category.setUnsubscribe(rs.getString("unsubscribe"));
                    } catch (Exception e) {
                        category.setUnsubscribe(null);
                    }

                    try {
                        category.setAckwait(rs.getString("ackwait"));
                    } catch (Exception e) {
                        category.setAckwait(null);
                    }

                    try {
                        category.setTtlqueue(rs.getInt("ttlqueue"));
                    } catch (Exception e) {
                        category.setTtlqueue(null);
                    }

                    try {
                        category.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        category.setStatus(null);
                    }

                    try {
                        category.setPriority(rs.getString("priority"));
                    } catch (Exception e) {
                        category.setPriority(null);
                    }

                    try {
                        category.setCreatedTime(new Date(rs.getDate("createdtime").getTime()));
                    } catch (Exception e) {
                        category.setCreatedTime(null);
                    }

                    try {
                        category.setCreatedUser(rs.getString("createduser"));
                    } catch (Exception e) {
                        category.setCreatedTime(null);
                    }

                    try {
                        category.setLastUpdatedTime(new Date(rs.getDate("lastupdatedtime").getTime()));
                    } catch (SQLException e) {
                        category.setLastUpdatedTime(null);
                    }

                    try {
                        category.setLastUpdatedUser(rs.getString("lastupdateduser"));
                    } catch (SQLException e) {
                        category.setLastUpdatedUser(null);
                    }
                    return category;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            category = null;
        } catch (Exception e) {
            throw e;
        }
        return category;
    }

    @Transactional
    public String updateCategory(CategoryInputBean categoryInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_CATEGORY,
                    categoryInputBean.getDescription(),
                    categoryInputBean.getBulkEnable(),
                    categoryInputBean.getStatus(),
                    categoryInputBean.getPriority(),
                    categoryInputBean.getAckwait(),
                    categoryInputBean.getUnsubscribe(),
                    categoryInputBean.getTtlqueue(),
                    categoryInputBean.getLastUpdatedUser(),
                    categoryInputBean.getLastUpdatedTime(),
                    categoryInputBean.getCategoryCode());
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteCategory(String code) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_CATEGORY, code);
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

    private StringBuilder setDynamicClause(CategoryInputBean categoryInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (categoryInputBean.getCategoryCode() != null && !categoryInputBean.getCategoryCode().isEmpty()) {
                dynamicClause.append("and lower(c.category) like lower('%").append(categoryInputBean.getCategoryCode()).append("%')");
            }

            if (categoryInputBean.getDescription() != null && !categoryInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(c.description) like lower('%").append(categoryInputBean.getDescription()).append("%')");
            }

            if (categoryInputBean.getStatus() != null && !categoryInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and c.status = '").append(categoryInputBean.getStatus()).append("'");
            }

            if (categoryInputBean.getPriority() != null && !categoryInputBean.getPriority().isEmpty()) {
                dynamicClause.append("and c.priority = '").append(categoryInputBean.getPriority()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(CategoryInputBean categoryInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (categoryInputBean.getCategoryCode() != null && !categoryInputBean.getCategoryCode().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(categoryInputBean.getCategoryCode()).append("%')");
            }

            if (categoryInputBean.getDescription() != null && !categoryInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(categoryInputBean.getDescription()).append("%')");
            }

            if (categoryInputBean.getStatus() != null && !categoryInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key4 = '").append(categoryInputBean.getStatus()).append("'");
            }

            if (categoryInputBean.getPriority() != null && !categoryInputBean.getPriority().isEmpty()) {
                dynamicClause.append("and wta.key5 = '").append(categoryInputBean.getPriority()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
