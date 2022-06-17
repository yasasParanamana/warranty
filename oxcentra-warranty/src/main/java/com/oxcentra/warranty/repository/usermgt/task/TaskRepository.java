package com.oxcentra.warranty.repository.usermgt.task;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.usermgt.task.TaskInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Repository
@Scope("prototype")
public class TaskRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from WEB_TASK t left outer join STATUS s on s.statuscode=t.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC d where d.page=? and d.status=? and d.lastupdateduser <> ? and ";
    private final String SQL_INSERT_TASK = "insert into WEB_TASK (taskcode,description,status,createdtime,createduser,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?)";
    private final String SQL_UPDATE_TASK = "update WEB_TASK set description=?,status=?,lastupdateduser=?,lastupdatedtime=? where taskcode=?";
    private final String SQL_FIND_TASK = "select t.taskcode,t.description,t.status,t.createdtime,t.createduser,t.lastupdatedtime,t.lastupdateduser from WEB_TASK t where t.taskcode = ? ";
    private final String SQL_DELETE_TASK = "delete from WEB_TASK where taskcode=?";

    @Transactional(readOnly = true)
    public long getDataCount(TaskInputBean taskInputBean) throws Exception {
        long count = 0;
        new StringBuilder();
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(taskInputBean, dynamicClause);
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
    public List<Task> getTaskSearchResults(TaskInputBean taskInputBean) throws Exception {
        List<Task> taskList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(taskInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (taskInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by t.lastupdatedtime desc ";
            } else {
                sortingStr = " order by t.lastupdatedtime " + taskInputBean.sortDirections.get(0);
            }

            String sql = "" +
                    " select " +
                    " t.taskcode, t.description, s.description as statusdes,t.createduser,t.createdtime,t.lastupdateduser,t.lastupdatedtime from WEB_TASK t " +
                    " left outer join STATUS s on s.statuscode=t.status " +
                    " where " + dynamicClause.toString() + sortingStr +
                    " limit " + taskInputBean.displayLength + " offset " + taskInputBean.displayStart;

            taskList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Task task = new Task();

                try {
                    task.setTaskCode(rs.getString("taskcode"));
                } catch (Exception e) {
                    task.setTaskCode(null);
                }

                try {
                    task.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    task.setDescription(null);
                }

                try {
                    task.setStatus(rs.getString("statusdes"));
                } catch (Exception e) {
                    task.setStatus(null);
                }

                try {
                    task.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    task.setCreatedTime(null);
                }

                try {
                    task.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    task.setCreatedUser(null);
                }

                try {
                    task.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    task.setCreatedTime(null);
                }

                try {
                    task.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    task.setCreatedUser(null);
                }


                return task;
            });
        } catch (EmptyResultDataAccessException ex) {
            return taskList;
        } catch (Exception e) {
            throw e;
        }
        return taskList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(TaskInputBean taskInputBean) throws Exception {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(taskInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.TASK_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (DataAccessException ex) {
            throw ex;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getTaskSearchResultsDual(TaskInputBean taskInputBean) throws Exception {
        List<TempAuthRec> taskDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(taskInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (taskInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + taskInputBean.sortDirections.get(0);
            }

            String sql =
                    " select wta.id, wta.key1, wta.key2, s.description key3, t.description task, wta.createdtime, wta.lastupdatedtime, wta.lastupdateduser " +
                            " from WEB_TMPAUTHREC wta" +
                            " left outer join STATUS s on s.statuscode = wta.key3 " +
                            " left outer join WEB_TASK t on t.taskcode = wta.task " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause.toString() + sortingStr +
                            " limit " + taskInputBean.displayLength + " offset " + taskInputBean.displayStart;

            taskDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.TASK_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
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
                    //task code
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
                    //status
                    tempAuthRec.setKey3(rs.getString("key3"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
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
        } catch (Exception ex) {
            throw ex;
        }
        return taskDualList;
    }


    @Transactional
    public String insertTask(TaskInputBean taskInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_TASK, taskInputBean.getTaskCode(),
                    taskInputBean.getDescription(),
                    taskInputBean.getStatus(),
                    taskInputBean.getCreatedTime(),
                    taskInputBean.getCreatedUser(),
                    taskInputBean.getLastUpdatedTime(),
                    taskInputBean.getLastUpdatedUser());

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (DuplicateKeyException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional(readOnly = true)
    public Task getTask(String taskCode) throws Exception {
        System.out.println("Hi");
        Task task;
        try {
            task = jdbcTemplate.queryForObject(SQL_FIND_TASK, new Object[]{taskCode}, (rs, rowNum) -> {
                Task t = new Task();

                try {
                    t.setTaskCode(rs.getString("taskcode"));
                } catch (Exception e) {
                    t.setTaskCode(null);
                }

                try {
                    t.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    t.setDescription(null);
                }

                try {
                    t.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    t.setStatus(null);
                }

                try {
                    t.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    t.setCreatedTime(null);
                }

                try {
                    t.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    t.setLastUpdatedTime(null);
                }

                try {
                    t.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    t.setLastUpdatedUser(null);
                }

                try {
                    t.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    t.setCreatedUser(null);
                }

                return t;
            });
        } catch (EmptyResultDataAccessException erse) {
            task = null;
        } catch (Exception e) {
            throw e;
        }
        return task;
    }

    @Transactional
    public String updateTask(TaskInputBean taskInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            value = jdbcTemplate.update(SQL_UPDATE_TASK, taskInputBean.getDescription(),
                    taskInputBean.getStatus(),
                    taskInputBean.getLastUpdatedUser(),
                    taskInputBean.getLastUpdatedTime(),
                    taskInputBean.getTaskCode());
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
    public String deleteTask(String taskcode) throws Exception {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_TASK, taskcode);
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

    private StringBuilder setDynamicClause(TaskInputBean taskInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (taskInputBean.getTaskCode() != null && !taskInputBean.getTaskCode().isEmpty()) {
                dynamicClause.append("and lower(t.taskcode) like lower('%").append(taskInputBean.getTaskCode()).append("%')");
            }

            if (taskInputBean.getDescription() != null && !taskInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(t.description) like lower('%").append(taskInputBean.getDescription()).append("%')");
            }

            if (taskInputBean.getStatus() != null && !taskInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and t.status = '").append(taskInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(TaskInputBean taskInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (taskInputBean.getTaskCode() != null && !taskInputBean.getTaskCode().isEmpty()) {
                dynamicClause.append("and lower(d.key1) like lower('%").append(taskInputBean.getTaskCode()).append("%')");
            }

            if (taskInputBean.getDescription() != null && !taskInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(d.key2) like lower('%").append(taskInputBean.getDescription()).append("%')");
            }

            if (taskInputBean.getStatus() != null && !taskInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and d.key3 = '").append(taskInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
