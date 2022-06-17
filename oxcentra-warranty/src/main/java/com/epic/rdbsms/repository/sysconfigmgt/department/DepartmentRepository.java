package com.epic.rdbsms.repository.sysconfigmgt.department;

import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.department.DepartmentInputBean;
import com.epic.rdbsms.mapping.department.Department;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.StatusVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;

@Repository
@Scope("prototype")
public class DepartmentRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from DEPARTMENT d left outer join STATUS s on s.statuscode=d.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";
    private final String SQL_INSERT_DEPARTMENT = "insert into DEPARTMENT(code,description,status,createdtime,createduser,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?) ";
    private final String SQL_FIND_DEPARTMENT = "select code,description,status,createdtime,createduser,lastupdatedtime,lastupdateduser from DEPARTMENT d where d.code = ?";
    private final String SQL_UPDATE_DEPARTMENT = "update DEPARTMENT d set description = ? , status = ? , lastupdatedtime = ? , lastupdateduser = ?  where d.code = ?";
    private final String SQL_DELETE_DEPARTMENT = "delete from DEPARTMENT where code = ?";

    @Transactional(readOnly = true)
    public long getDataCount(DepartmentInputBean departmentInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(departmentInputBean, dynamicClause);
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
    public List<Department> getDepartmentSearchList(DepartmentInputBean departmentInputBean) {
        List<Department> departmentList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(departmentInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (departmentInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by d.lastupdatedtime desc ";
            } else {
                sortingStr = " order by d.lastupdatedtime " + departmentInputBean.sortDirections.get(0);
            }

            String sql =
                    " select d.code as code,d.description as description, d.status as status,s.description as statusdescription," +
                            " d.createdtime as createdtime,d.createduser as createduser,d.lastupdatedtime as lastupdatedtime ,d.lastupdateduser as lastupdateduser from DEPARTMENT d " +
                            " left outer join STATUS s on s.statuscode=d.status " +
                            " where " + dynamicClause.toString() + sortingStr +
                            " limit " + departmentInputBean.displayLength + " offset " + departmentInputBean.displayStart;

            departmentList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Department department = new Department();
                try {
                    department.setCode(rs.getString("code"));
                } catch (Exception e) {
                    department.setCode(null);
                }

                try {
                    department.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    department.setDescription(null);
                }

                try {
                    department.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    department.setStatus(null);
                }

                try {
                    department.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    department.setCreatedTime(null);
                }

                try {
                    department.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    department.setCreatedUser(null);
                }

                try {
                    department.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    department.setLastUpdatedTime(null);
                }

                try {
                    department.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    department.setLastUpdatedUser(null);
                }
                return department;
            });
        } catch (EmptyResultDataAccessException ex) {
            return departmentList;
        } catch (Exception e) {
            throw e;
        }
        return departmentList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(DepartmentInputBean departmentInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(departmentInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.DEPARTMENT_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getDepartmentSearchResultsDual(DepartmentInputBean departmentInputBean) {
        List<TempAuthRec> departmentDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(departmentInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (departmentInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + departmentInputBean.sortDirections.get(0);
            }
            String sql =
                    " select wta.id,wta.key1,wta.key2,wta.key3,s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                            " from WEB_TMPAUTHREC wta" +
                            " left outer join STATUS s on s.statuscode = wta.key3 " +
                            " left outer join WEB_TASK t on t.taskcode = wta.task " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause.toString() + sortingStr +
                            " limit " + departmentInputBean.displayLength + " offset " + departmentInputBean.displayStart;

            departmentDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.DEPARTMENT_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
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
        return departmentDualList;
    }

    @Transactional
    public String insertDepartment(DepartmentInputBean departmentInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;

            System.out.println("time & user > ******************* " + departmentInputBean.getLastUpdatedTime() + " | " + departmentInputBean.getLastUpdatedUser());
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_DEPARTMENT, departmentInputBean.getCode(),
                    departmentInputBean.getDescription(),
                    departmentInputBean.getStatus(),
                    departmentInputBean.getCreatedTime(),
                    departmentInputBean.getCreatedUser(),
                    departmentInputBean.getLastUpdatedTime(),
                    departmentInputBean.getLastUpdatedUser());
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
    public Department getDepartment(String code) {
        Department department = null;
        try {
            department = jdbcTemplate.queryForObject(SQL_FIND_DEPARTMENT, new Object[]{code}, (rs, rowNum) -> {
                Department dpt = new Department();

                try {
                    dpt.setCode(rs.getString("code"));
                } catch (Exception e) {
                    dpt.setCode(null);
                }

                try {
                    dpt.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    dpt.setDescription(null);
                }

                try {
                    dpt.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    dpt.setStatus(null);
                }

                try {
                    dpt.setCreatedTime(new Date(rs.getDate("createdtime").getTime()));
                } catch (Exception e) {
                    dpt.setCreatedTime(null);
                }

                try {
                    dpt.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    dpt.setCreatedTime(null);
                }

                try {
                    dpt.setLastUpdatedTime(new Date(rs.getDate("lastupdatedtime").getTime()));
                } catch (Exception e) {
                    dpt.setLastUpdatedTime(null);
                }

                try {
                    dpt.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    dpt.setLastUpdatedUser(null);
                }

                return dpt;
            });
        } catch (EmptyResultDataAccessException erse) {
            department = null;
        } catch (Exception e) {
            throw e;
        }
        return department;
    }


    @Transactional
    public String updateDepartment(DepartmentInputBean departmentInputBean) {

        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_DEPARTMENT,
                    departmentInputBean.getDescription(),
                    departmentInputBean.getStatus(),
                    departmentInputBean.getLastUpdatedTime(),
                    departmentInputBean.getLastUpdatedUser(),
                    departmentInputBean.getCode());
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteDepartment(String code) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_DEPARTMENT, code);
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

    private StringBuilder setDynamicClause(DepartmentInputBean departmentInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (departmentInputBean.getCode() != null && !departmentInputBean.getCode().isEmpty()) {
                dynamicClause.append("and lower(d.code) like lower('%").append(departmentInputBean.getCode()).append("%')");
            }

            if (departmentInputBean.getDescription() != null && !departmentInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(d.description) like lower('%").append(departmentInputBean.getDescription()).append("%')");
            }

            if (departmentInputBean.getStatus() != null && !departmentInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and d.status = '").append(departmentInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(DepartmentInputBean departmentInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (departmentInputBean.getCode() != null && !departmentInputBean.getCode().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(departmentInputBean.getCode()).append("%')");
            }

            if (departmentInputBean.getDescription() != null && !departmentInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(departmentInputBean.getDescription()).append("%')");
            }

            if (departmentInputBean.getStatus() != null && !departmentInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key3 = '").append(departmentInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

}
