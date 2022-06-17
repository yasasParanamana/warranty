package com.oxcentra.rdbsms.repository.usermgt.userrole;

import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.usermgt.userrole.UserRoleInputBean;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.usermgt.Page;
import com.oxcentra.rdbsms.mapping.usermgt.Section;
import com.oxcentra.rdbsms.mapping.usermgt.Task;
import com.oxcentra.rdbsms.mapping.usermgt.UserRole;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.StatusVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@Scope("prototype")
public class UserRoleRepository {

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from USERROLE u left outer join STATUS s on s.statuscode=u.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC d where d.page=? and d.status=? and d.lastupdateduser <> ? and ";
    private final String SQL_INSERT_USERROLE = "insert into USERROLE (userrolecode, description, userroletype, status, createduser, createdtime, lastupdatedtime, lastupdateduser) values (?,?,?,?,?,?,?,?)";
    private final String SQL_FIND_USERROLE = "select u.userrolecode, u.description, u.userroletype, u.status, u.createduser, u.createdtime, u.lastupdatedtime, u.lastupdateduser from USERROLE u where u.userrolecode = ? ";
    private final String SQL_UPDATE_USERROLE = "update USERROLE set description=?, userroletype=?, status=?, lastupdateduser=?, lastupdatedtime=? where userrolecode=?";
    private final String SQL_DELETE_USERROLE = "delete from USERROLE where userrolecode=?";
    private final String SQL_GET_ALL_SECTION_LIST = "select sectioncode, description from WEB_SECTION ";
    private final String SQL_GET_ASSIGNED_PAGE_LIST = "select p.pagecode pagecode, p.description description from WEB_SECTIONPAGE sp inner join WEB_PAGE p on p.pagecode=sp.page where sp.USERROLE=? and sp.section=?";
    private final String SQL_GET_ALL_PAGE_LIST = "select pagecode, description from WEB_PAGE ";
    private final String SQL_GET_ASSIGNED_PAGE_LIST_FOR_USERROLE = "select p.pagecode pagecode, p.description description from WEB_SECTIONPAGE sp inner join WEB_PAGE p on p.pagecode=sp.page where sp.USERROLE=?";
    private final String SQL_UNASSIGNED_PAGE_TASK = "delete from WEB_PAGETASK  where USERROLE=:USERROLE and page in (:pages)";
    private final String SQL_UNASSIGNED_PAGE = "delete from WEB_SECTIONPAGE where USERROLE=:USERROLE and page in (:pages)";
    private final String SQL_ASSIGNED_PAGE = "insert into WEB_SECTIONPAGE (USERROLE, section, page, lastupdateduser) values (?,?,?,?)";
    private final String SQL_GET_ASSIGNED_SECTION_LIST = "select distinct s.sectioncode sectioncode, s.description description from WEB_SECTION s inner join WEB_SECTIONPAGE sp on sp.section=s.sectioncode where sp.USERROLE=?";
    private final String SQL_GET_ASSIGNED_TASK_LIST = "select t.taskcode taskcode, t.description description from WEB_TASK t inner join WEB_PAGETASK pt on pt.task=t.taskcode where pt.USERROLE=? and pt.page=? ";
    private final String SQL_GET_ALL_PAGE_TASK_LIST = "select t.taskcode taskcode, t.description description from WEB_TASK t inner join WEB_PAGETASK_TEMPLATE ptt on ptt.task=t.taskcode where ptt.page=? ";
    private final String SQL_UNASSIGNED_TASK = "delete from WEB_PAGETASK where USERROLE = :USERROLE and page=:page and task in (:tasks)";
    private final String SQL_ASSIGNED_TASK = "insert into WEB_PAGETASK (USERROLE, page, task, lastupdateduser) values (?,?,?,?)";
    private final String SQL_GET_PAGE_DESC = "select description from WEB_PAGE s where s.PAGECODE = ?";
    private final String SQL_GET_TASK_DESC = "select description from WEB_TASK s where s.TASKCODE = ?";

    @Autowired
    SessionBean sessionBean;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    CommonRepository commonRepository;
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional(readOnly = true)
    public long getDataCount(UserRoleInputBean inputBean) throws Exception {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(inputBean, dynamicClause);
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
    public List<UserRole> getUserRoleSearchResults(UserRoleInputBean userRoleInputBean) throws Exception {
        List<UserRole> userRoleList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(userRoleInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (userRoleInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by u.createdtime DESC ";
            } else {
                sortingStr = " order by u.createdtime " + userRoleInputBean.sortDirections.get(0);
            }

            String sql =
                    " select u.userrolecode, u.description, ut.description as userroletypedesc, u.status, s.description as statusdes, u.createduser, u.createdtime, u.lastupdateduser, u.lastupdatedtime from USERROLE u " +
                            " left outer join USERROLETYPE ut on ut.userroletypecode=u.userroletype " +
                            " left outer join STATUS s on s.statuscode=u.status " +
                            " where " + dynamicClause + sortingStr +
                            " limit " + userRoleInputBean.displayLength + " offset " + userRoleInputBean.displayStart;

            userRoleList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                UserRole userRole = new UserRole();

                try {
                    userRole.setUserroleCode(rs.getString("userrolecode"));
                } catch (Exception e) {
                    userRole.setUserroleCode(null);
                }

                try {
                    userRole.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    userRole.setDescription(null);
                }

                try {
                    userRole.setUserroleType(rs.getString("userroletypedesc"));
                } catch (Exception e) {
                    userRole.setUserroleType(null);
                }

                try {
                    userRole.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    userRole.setStatus(null);
                }

                try {
                    userRole.setStatus(rs.getString("statusdes"));
                } catch (Exception e) {
                    userRole.setStatus(null);
                }

                try {
                    userRole.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    userRole.setCreatedTime(null);
                }

                try {
                    userRole.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    userRole.setCreatedUser(null);
                }

                try {
                    userRole.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    userRole.setCreatedTime(null);
                }

                try {
                    userRole.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    userRole.setCreatedUser(null);
                }

                return userRole;
            });
        } catch (EmptyResultDataAccessException ex) {
            return userRoleList;
        } catch (Exception e) {
            throw e;
        }
        return userRoleList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(UserRoleInputBean inputBean) throws Exception {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(inputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.USERROLE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getUserRoleSearchResultsDual(UserRoleInputBean userRoleInputBean) throws Exception {
        List<TempAuthRec> pageDualList;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(userRoleInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (userRoleInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + userRoleInputBean.sortDirections.get(0);
            }
            String sql =
                    " select wta.id, wta.key1, wta.key2, wta.key3, wta.key5, wta.key6, wta.tmprecord, s.description key4, t.description task, wta.createdtime, wta.lastupdatedtime, wta.lastupdateduser " +
                            " from WEB_TMPAUTHREC wta" +
                            " left outer join STATUS s on s.statuscode = wta.key4 " +
                            " left outer join WEB_TASK t on t.taskcode = wta.task " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                            " limit " + userRoleInputBean.displayLength + " offset " + userRoleInputBean.displayStart;

            pageDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.USERROLE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
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
                    //User role code
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
                    //User role type
                    tempAuthRec.setKey3(rs.getString("key3"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
                }

                try {
                    //status
                    tempAuthRec.setKey4(rs.getString("key4"));
                } catch (Exception e) {
                    tempAuthRec.setKey4(null);
                }

                try {
                    //status
                    tempAuthRec.setKey5(rs.getString("key5"));
                } catch (Exception e) {
                    tempAuthRec.setKey5(null);
                }

                try {
                    //status
                    tempAuthRec.setKey6(rs.getString("key6"));
                } catch (Exception e) {
                    tempAuthRec.setKey6(null);
                }

                try {
                    tempAuthRec.setLastUpdatedTime(rs.getString("lastupdatedtime"));
                } catch (SQLException e) {
                    tempAuthRec.setLastUpdatedTime(null);
                }

                try {
                    tempAuthRec.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (SQLException e) {
                    tempAuthRec.setLastUpdatedUser(null);
                }

                try {
                    tempAuthRec.setCreatedTime(rs.getString("createdtime"));
                } catch (SQLException e) {
                    tempAuthRec.setCreatedTime(null);
                }

                try {
                    tempAuthRec.setTmpRecord(rs.getString("tmprecord"));

                    List<String> assignedPages = new ArrayList<>();
                    if (tempAuthRec.getTmpRecord() != null && !tempAuthRec.getTmpRecord().isEmpty()) {
                        assignedPages = new ArrayList<>(Arrays.asList(tempAuthRec.getTmpRecord().replaceAll(" ", "").split(",")));
                    }

                    List<String> alreadyAssignedList = new ArrayList<>();

                    if(tempAuthRec.getKey6() != null){
                        try {
                            alreadyAssignedList = this.getAssignedTaskCodes(tempAuthRec.getKey1().trim(), tempAuthRec.getKey6().trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            alreadyAssignedList = this.getAssignedPageCodes(tempAuthRec.getKey1().trim(), tempAuthRec.getKey5().trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    List<String> unAssignedPages = new ArrayList<>();

                    if (alreadyAssignedList.size() != 0) {
                        for (String pageCode : alreadyAssignedList) {
                            if (assignedPages.contains(pageCode)) {
                                assignedPages.remove(pageCode);
                            } else {
                                unAssignedPages.add(pageCode);
                            }
                        }
                        List<String> unAssignedDes = new ArrayList<>();

                        if(tempAuthRec.getKey6() != null){
                            try {
                                for (String pageCode : assignedPages) {
                                    Task task = this.getTaskDescription(pageCode);
                                    unAssignedDes.add(task.getDescription());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                for (String pageCode : assignedPages) {
                                    Page page = this.getPageDescription(pageCode);
                                    unAssignedDes.add(page.getDescription());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        tempAuthRec.setTmpRecord(unAssignedDes.toString().replace("[", "").replace("]", ""));
                    } else {
                        List<String> assignedDes = new ArrayList<>();

                        if(tempAuthRec.getKey6() != null){
                            try {
                                for (String pageCode : assignedPages) {
                                    Task task = this.getTaskDescription(pageCode);
                                    assignedDes.add(task.getDescription());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                for (String pageCode : assignedPages) {
                                    Page page = this.getPageDescription(pageCode);
                                    assignedDes.add(page.getDescription());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        tempAuthRec.setTmpRecord(assignedDes.toString().replace("[", "").replace("]", ""));
                    }
                } catch (SQLException e) {
                    tempAuthRec.setTmpRecord(null);
                }

                return tempAuthRec;
            });
        } catch (Exception e) {
            throw e;
        }
        return pageDualList;
    }

    @Transactional
    public String insertUserRole(UserRoleInputBean userRoleInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_USERROLE,
                    userRoleInputBean.getUserroleCode(),
                    userRoleInputBean.getDescription(),
                    userRoleInputBean.getUserroleType(),
                    userRoleInputBean.getStatus(),
                    userRoleInputBean.getCreatedUser(),
                    userRoleInputBean.getCreatedTime(),
                    userRoleInputBean.getLastUpdatedTime(),
                    userRoleInputBean.getLastUpdatedUser());
            if (value != 1) {
                message = MessageVarList.USERROLE_MGT_ERROR_ADD;
            }
        } catch (DuplicateKeyException ex) {
            throw ex;
        } catch (Exception e) {
            throw e;
        }
        return message;
    }


    @Transactional(readOnly = true)
    public UserRole getUserRole(String userroleCode) throws Exception {
        UserRole userRole = null;
        try {
            userRole = jdbcTemplate.queryForObject(SQL_FIND_USERROLE, new Object[]{userroleCode}, (rs, rowNum) -> {
                UserRole u = new UserRole();

                try {
                    u.setUserroleCode(rs.getString("userrolecode"));
                } catch (Exception e) {
                    u.setUserroleCode(null);
                }

                try {
                    u.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    u.setDescription(null);
                }

                try {
                    u.setUserroleType(rs.getString("userroletype"));
                } catch (Exception e) {
                    u.setUserroleType(null);
                }

                try {
                    u.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    u.setStatus(null);
                }

                try {
                    u.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    u.setCreatedTime(null);
                }

                try {
                    u.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    u.setCreatedUser(null);
                }


                try {
                    u.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    u.setLastUpdatedTime(null);
                }

                try {
                    u.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    u.setLastUpdatedUser(null);
                }

                return u;
            });
        } catch (EmptyResultDataAccessException erse) {
            userRole = null;
        } catch (Exception e) {
            throw e;
        }
        return userRole;
    }

    @Transactional
    public String updateUserRole(UserRoleInputBean userRoleInputBean) throws Exception {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_USERROLE,
                    userRoleInputBean.getDescription(),
                    userRoleInputBean.getUserroleType(),
                    userRoleInputBean.getStatus(),
                    userRoleInputBean.getLastUpdatedUser(),
                    userRoleInputBean.getLastUpdatedTime(),
                    userRoleInputBean.getUserroleCode());
            if (value != 1) {
                message = MessageVarList.USERROLE_MGT_ERROR_UPDATE;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteUserRole(String userroleCode) throws Exception {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_USERROLE, userroleCode);
            if (count < 0) {
                message = MessageVarList.USERROLE_MGT_ERROR_DELETE;
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
    public List<Section> getAllSection() throws Exception {
        List<Section> sectionList;
        try {
            sectionList = jdbcTemplate.query(SQL_GET_ALL_SECTION_LIST, new RowMapper<Section>() {
                @Override
                public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Section section = new Section();
                    section.setSectionCode(rs.getString("sectioncode"));
                    section.setDescription(rs.getString("description"));
                    return section;
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            sectionList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return sectionList;
    }

    @Transactional(readOnly = true)
    public List<Page> getAssignedPages(String userroleCode, String sectionCode) throws Exception {
        List<Page> assignedPageList;
        try {
            assignedPageList = jdbcTemplate.query(SQL_GET_ASSIGNED_PAGE_LIST, new Object[]{userroleCode, sectionCode}, new RowMapper<Page>() {
                @Override
                public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Page page = new Page();
                    page.setPageCode(rs.getString("pagecode"));
                    page.setDescription(rs.getString("description"));
                    return page;
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            assignedPageList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return assignedPageList;
    }

    @Transactional(readOnly = true)
    public List<Page> getAllPages() throws Exception {
        List<Page> allPageList;
        try {
            allPageList = jdbcTemplate.query(SQL_GET_ALL_PAGE_LIST, new RowMapper<Page>() {
                @Override
                public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Page page = new Page();
                    page.setPageCode(rs.getString("pagecode"));
                    page.setDescription(rs.getString("description"));
                    return page;
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            allPageList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return allPageList;
    }

    @Transactional(readOnly = true)
    public List<String> getAssignedPagesForUserrole(String userroleCode) throws Exception {
        List<String> assignedUserrolePageList;
        try {
            assignedUserrolePageList = jdbcTemplate.query(SQL_GET_ASSIGNED_PAGE_LIST_FOR_USERROLE, new Object[]{userroleCode}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("pagecode");
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            assignedUserrolePageList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return assignedUserrolePageList;
    }

    @Transactional(readOnly = true)
    public List<String> getAssignedPageCodes(String userroleCode, String sectionCode) throws Exception {
        List<String> assignPageCodeList;
        try {
            assignPageCodeList = jdbcTemplate.query(SQL_GET_ASSIGNED_PAGE_LIST, new Object[]{userroleCode, sectionCode}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("pagecode");
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            assignPageCodeList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return assignPageCodeList;
    }

    @Transactional(readOnly = true)
    public Page getPageDescription(String pageCode) {
        Page page = null;
        try {
            page = jdbcTemplate.queryForObject(SQL_GET_PAGE_DESC, new Object[]{pageCode}, new RowMapper<Page>() {
                @Override
                public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Page page = new Page();

                    try {
                        page.setDescription(rs.getString("DESCRIPTION"));
                    } catch (Exception e) {
                        page.setDescription(null);
                    }

                    return page;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            page = null;
        } catch (Exception e) {
            throw e;
        }
        return page;
    }

    @Transactional(readOnly = true)
    public Task getTaskDescription(String pageCode) {
        Task task = null;
        try {
            task = jdbcTemplate.queryForObject(SQL_GET_TASK_DESC, new Object[]{pageCode}, new RowMapper<Task>() {
                @Override
                public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Task task = new Task();

                    try {
                        task.setDescription(rs.getString("DESCRIPTION"));
                    } catch (Exception e) {
                        task.setDescription(null);
                    }

                    return task;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            task = null;
        } catch (Exception e) {
            throw e;
        }
        return task;
    }

    @Transactional
    public String assignPages(UserRoleInputBean userRoleInputBean) throws SQLException {
        String message = "";
        try {
            int count = 0;
            if (userRoleInputBean.getUnAssignList() != null && userRoleInputBean.getUnAssignList().size() > 0) {
                List<String> pages = userRoleInputBean.getUnAssignList();

                MapSqlParameterSource parameters = new MapSqlParameterSource();
                parameters.addValue("pages", pages);
                parameters.addValue("USERROLE", userRoleInputBean.getUserroleCode());

                count = namedParameterJdbcTemplate.update(SQL_UNASSIGNED_PAGE_TASK, parameters);
                count = namedParameterJdbcTemplate.update(SQL_UNASSIGNED_PAGE, parameters);

                if (count < 0) {
                    message = MessageVarList.USERROLE_MGT_ERROR_UNASSIGNED_PAGE;
                }
            }

            if (message.isEmpty() && userRoleInputBean.getAssignedPages().size() > 0) {
                int[] numOfRows = jdbcTemplate.batchUpdate(SQL_ASSIGNED_PAGE, new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, userRoleInputBean.getUserroleCode());
                        ps.setString(2, userRoleInputBean.getSection());
                        ps.setString(3, userRoleInputBean.getAssignedPages().get(i));
                        ps.setString(4, sessionBean.getUsername());
                    }


                    public int getBatchSize() {
                        return userRoleInputBean.getAssignedPages().size();
                    }
                });

                if (numOfRows.length == 0) {
                    message = MessageVarList.USERROLE_MGT_ERROR_ASSIGNED_PAGE;
                }
            }
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return message;
    }


    @Transactional(readOnly = true)
    public List<Section> getAssignedSection(String userroleCode) throws Exception {
        List<Section> sectionList;
        try {
            sectionList = jdbcTemplate.query(SQL_GET_ASSIGNED_SECTION_LIST, new Object[]{userroleCode}, new RowMapper<Section>() {
                @Override
                public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Section section = new Section();
                    section.setSectionCode(rs.getString("sectioncode"));
                    section.setDescription(rs.getString("description"));
                    return section;
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            sectionList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return sectionList;
    }

    @Transactional(readOnly = true)
    public List<Task> getAssignedTasks(String userroleCode, String pageCode) throws Exception {
        List<Task> assignTaskList;
        try {
            assignTaskList = jdbcTemplate.query(SQL_GET_ASSIGNED_TASK_LIST, new Object[]{userroleCode, pageCode}, new RowMapper<Task>() {
                @Override
                public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Task task = new Task();
                    task.setTaskCode(rs.getString("taskcode"));
                    task.setDescription(rs.getString("description"));
                    return task;
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            assignTaskList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return assignTaskList;
    }

    @Transactional(readOnly = true)
    public List<Task> getAllPageTasks(String pageCode) throws Exception {
        List<Task> allPageTaskList;
        try {
            allPageTaskList = jdbcTemplate.query(SQL_GET_ALL_PAGE_TASK_LIST, new Object[]{pageCode}, new RowMapper<Task>() {
                @Override
                public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Task task = new Task();
                    task.setTaskCode(rs.getString("taskcode"));
                    task.setDescription(rs.getString("description"));
                    return task;
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            allPageTaskList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return allPageTaskList;
    }

    @Transactional(readOnly = true)
    public List<String> getAssignedTasksForUserrole(String userroleCode, String pageCode) throws Exception {
        List<String> assignedUserroleTaskList;
        try {
            assignedUserroleTaskList = jdbcTemplate.query(SQL_GET_ASSIGNED_TASK_LIST, new Object[]{userroleCode, pageCode}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("taskcode");
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            assignedUserroleTaskList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return assignedUserroleTaskList;
    }


    @Transactional(readOnly = true)
    public List<String> getAssignedTaskCodes(String userroleCode, String pageCode) throws Exception {
        List<String> assignTaskCodeList;
        try {
            assignTaskCodeList = jdbcTemplate.query(SQL_GET_ASSIGNED_TASK_LIST, new Object[]{userroleCode, pageCode}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("taskcode");
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            assignTaskCodeList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return assignTaskCodeList;
    }

    @Transactional
    public String assignTasks(UserRoleInputBean userRoleInputBean) {
        String message = "";
        try {
            int count = 0;
            if (userRoleInputBean.getUnAssignList() != null && userRoleInputBean.getUnAssignList().size() > 0) {
                List<String> tasks = userRoleInputBean.getUnAssignList();

                MapSqlParameterSource parameters = new MapSqlParameterSource();
                parameters.addValue("USERROLE", userRoleInputBean.getUserroleCode());
                parameters.addValue("page", userRoleInputBean.getPage());
                parameters.addValue("tasks", tasks);

                count = namedParameterJdbcTemplate.update(SQL_UNASSIGNED_TASK, parameters);
                if (count < 0) {
                    message = MessageVarList.USERROLE_MGT_ERROR_UNASSIGNED_TASK;
                }
            }

            if (message.isEmpty() && userRoleInputBean.getAssignedTasks().size() > 0) {
                int[] numOfRows = jdbcTemplate.batchUpdate(SQL_ASSIGNED_TASK, new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, userRoleInputBean.getUserroleCode());
                        ps.setString(2, userRoleInputBean.getPage());
                        ps.setString(3, userRoleInputBean.getAssignedTasks().get(i));
                        ps.setString(4, sessionBean.getUsername());
                    }

                    public int getBatchSize() {
                        return userRoleInputBean.getAssignedTasks().size();
                    }
                });

                if (numOfRows.length == 0) {
                    message = MessageVarList.USERROLE_MGT_ERROR_ASSIGNED_TASK;
                }
            }

        } catch (DataAccessException e) {
            throw e;
        }
        return message;
    }

    private StringBuilder setDynamicClause(UserRoleInputBean inputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (inputBean.getUserroleCode() != null && !inputBean.getUserroleCode().isEmpty()) {
                dynamicClause.append("and lower(u.userrolecode) like lower('%").append(inputBean.getUserroleCode()).append("%')");
            }

            if (inputBean.getDescription() != null && !inputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(u.description) like lower('%").append(inputBean.getDescription()).append("%')");
            }

            if (inputBean.getUserroleType() != null && !inputBean.getUserroleType().isEmpty()) {
                dynamicClause.append("and u.userroletype = '").append(inputBean.getUserroleType()).append("'");
            }

            if (inputBean.getStatus() != null && !inputBean.getStatus().isEmpty()) {
                dynamicClause.append("and u.status = '").append(inputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(UserRoleInputBean inputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (inputBean.getUserroleCode() != null && !inputBean.getUserroleCode().isEmpty()) {
                dynamicClause.append("and lower(d.key1) like lower('%").append(inputBean.getUserroleCode()).append("%')");
            }

            if (inputBean.getDescription() != null && !inputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(d.key2) like lower('%").append(inputBean.getDescription()).append("%')");
            }

            if (inputBean.getUserroleType() != null && !inputBean.getUserroleType().isEmpty()) {
                dynamicClause.append("and d.key3 = '").append(inputBean.getUserroleType()).append("'");
            }

            if (inputBean.getStatus() != null && !inputBean.getStatus().isEmpty()) {
                dynamicClause.append("and d.key4 = '").append(inputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
