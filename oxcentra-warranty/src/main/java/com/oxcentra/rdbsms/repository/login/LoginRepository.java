package com.oxcentra.rdbsms.repository.login;

import com.oxcentra.rdbsms.bean.login.LoginBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.usermgt.*;
import com.oxcentra.rdbsms.mapping.usermgt.*;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Scope("prototype")
public class LoginRepository {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    CommonVarList commonVarList;

    private final String SQL_GET_USER_LOGIN = "select username,password,USERROLE,expirydate,fullname,email,mobile,noofinvlidattempt,loggeddate,initialloginstatus,ad,status,lastupdateduser,lastupdatedtime,createtime from WEB_SYSTEMUSER where lower(username)=?";
    private final String SQL_UPDATE_VALID_USER_LOGIN = "update WEB_SYSTEMUSER set noofinvlidattempt=? , loggeddate = ? , status = ? , lastupdatedtime = ? where lower(username) =?";
    private final String SQL_UPDATE_INVALID_USER_LOGIN = "update WEB_SYSTEMUSER set noofinvlidattempt=? , status = ? , lastupdatedtime = ? where lower(username) =?";

    private final String SQL_GET_USER_SECTIONLIST = "select distinct s.description as description, sp.section as section, sp.USERROLE as USERROLE, s.sortkey as sortkey, s.status as status,s.lastupdateduser as lastupdateduser, s.lastupdatedtime as lastupdatedtime, s.createdtime as createdtime from WEB_SECTIONPAGE sp inner join WEB_SECTION s on sp.section = s.sectioncode where s.status=? and sp.USERROLE=? order by s.sortkey";
    private final String SQL_GET_USER_PAGELIST = "select p.description as description,p.pagecode as pagecode ,p.url as url,p.sortkey as sortkey,p.status as status ,p.aflag as aflag ,p.cflag as cflag,sp.section as section ,sp.USERROLE as USERROLE,p.lastupdateduser as lastupdateduser , p.createdtime as createtime ,p.lastupdatedtime as lastupdatedtime from WEB_SECTIONPAGE sp inner join WEB_PAGE p on sp.page = p.pagecode where p.status = ? and sp.USERROLE = ? and p.pagecode != 'PWCM' order by p.sortkey";
    private final String SQL_GET_USER_PAGETASKLIST = "select pt.USERROLE as USERROLE, wsp.section as section,pt.page as page ,p.description as pagedescription,pt.task as task ,t.description as taskdescription,t.status as taskstatus,t.lastupdateduser as tasklastupdateduser,t.createdtime as taskcreatetime , t.lastupdatedtime as tasklastupdatedtime,pt.lastupdateduser as lastupdateduser,pt.createtime as createtime , pt.lastupdatedtime as lastupdatedtime from WEB_PAGETASK pt inner join WEB_SECTIONPAGE wsp on pt.page = wsp.page inner join WEB_PAGE p on pt.page = p.pagecode inner join WEB_TASK t on pt.task = t.taskcode where pt.USERROLE=? and p.status =? and t.status = ?";

    @Transactional(readOnly = true)
    public User getUser(LoginBean loginBean) {
        User user = null;
        try {
            List<User> map = jdbcTemplate.query(SQL_GET_USER_LOGIN, new Object[]{loginBean.getUsername()}, (rs, rowNum) -> {
                User u = new User();
                try {
                    u.setUserName(rs.getString("username"));
                } catch (Exception e) {
                    u.setUserName(null);
                }

                try {
                    u.setPassword(rs.getString("password"));
                } catch (Exception e) {
                    u.setPassword(null);
                }

                try {
                    u.setUserrole(rs.getString("USERROLE"));
                } catch (Exception e) {
                    u.setUserrole(null);
                }

                try {
                    u.setExpirydate(rs.getDate("expirydate"));
                } catch (Exception e) {
                    u.setExpirydate(null);
                }

                try {
                    u.setFullname(rs.getString("fullname"));
                } catch (Exception e) {
                    u.setFullname(null);
                }

                try {
                    u.setEmail(rs.getString("email"));
                } catch (Exception e) {
                    u.setEmail(null);
                }

                try {
                    u.setNoofinvlidattempt(rs.getByte("noofinvlidattempt"));
                } catch (Exception e) {
                    u.setNoofinvlidattempt(null);
                }

                try {
                    u.setLoggeddate(rs.getDate("loggeddate"));
                } catch (Exception e) {
                    u.setLoggeddate(null);
                }

                try {
                    u.setInitialloginstatus(rs.getString("initialloginstatus"));
                } catch (Exception e) {
                    u.setInitialloginstatus(null);
                }

                try {
                    u.setAd(rs.getByte("ad"));
                } catch (Exception e) {
                    u.setAd(null);
                }

                try {
                    u.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    u.setStatus(null);
                }

                try {
                    u.setLastupdateduser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    u.setLastupdateduser(null);
                }

                try {
                    u.setLastupdatedtime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    u.setLastupdatedtime(null);
                }

                try {
                    u.setCreatetime(rs.getDate("createtime"));
                } catch (Exception e) {
                    u.setCreatetime(null);
                }

                try {
                    u.setMobile(rs.getString("mobile"));
                } catch (Exception e) {
                    u.setMobile(null);
                }

                return u;
            });

            if (map.size() > 0) {
                user = map.get(0);
            }
        } catch (EmptyResultDataAccessException ex) {
            return user;
        } catch (Exception e) {
            throw e;
        }
        return user;
    }

    @Transactional
    public int updateUser(LoginBean loginBean, Audittrace audittrace, boolean flag) throws Exception {
        int update;
        try {
            Date currentDate = commonRepository.getCurrentDate();
            if (flag) {
                update = jdbcTemplate.update(SQL_UPDATE_VALID_USER_LOGIN, loginBean.getAttempts(), currentDate, loginBean.getStatusCode(), currentDate, loginBean.getUsername());
            } else {
                update = jdbcTemplate.update(SQL_UPDATE_INVALID_USER_LOGIN, loginBean.getAttempts(), loginBean.getStatusCode(), currentDate, loginBean.getUsername());
            }

            //set the remaining values to audit trace
            if (audittrace != null && update == 1) {
                audittrace.setCreatedtime(currentDate);
                audittrace.setLastupdatedtime(currentDate);
                //set the audit trace to session bean
                sessionBean.setAudittrace(audittrace);
            }
        } catch (Exception e) {
            throw e;
        }
        return update;
    }

    @Transactional(readOnly = true)
    public List<Section> getUserSectionListByUserRoleCode(String userRole) {
        List<Section> sectionList = new ArrayList<>();
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(SQL_GET_USER_SECTIONLIST, commonVarList.STATUS_ACTIVE, userRole);
            if (list.size() != 0) {
                for (int i = 0; i < list.size(); i++) {
                    String sectionCode = list.get(i).get("section") + "";
                    String description = list.get(i).get("description") + "";
                    String userRoleCode = list.get(i).get("USERROLE") + "";
                    int sortKey = Integer.parseInt(list.get(i).get("sortkey") + "");
                    String status = list.get(i).get("status") + "";
                    Date createdTime = (Date) list.get(i).get("createdtime");
                    String lastUpdatedUser = list.get(i).get("lastupdateduser") + "";
                    Date lastUpdatedTime = (Date) list.get(i).get("lastupdatedtime");
                    //set values in section list
                    Section section = new Section();
                    section.setSectionCode(sectionCode);
                    section.setDescription(description);
                    section.setSortKey(sortKey);
                    section.setStatus(status);
                    section.setCreatedTime(createdTime);
                    section.setLastUpdatedUser(lastUpdatedUser);
                    section.setLastUpdatedTime(lastUpdatedTime);
                    sectionList.add(section);
                }
            }
        } catch (EmptyResultDataAccessException ere) {
            return sectionList;
        } catch (Exception e) {
            throw e;
        }
        return sectionList;
    }

    @Transactional(readOnly = true)
    public Map<String, List<Page>> getUserPageListByUserRoleCode(String userRole) {
        Map<String, List<Page>> pageListMap = new HashMap<String, List<Page>>();
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(SQL_GET_USER_PAGELIST, commonVarList.STATUS_ACTIVE, userRole);
            if (list.size() != 0) {
                for (int i = 0; i < list.size(); i++) {
                    String description = list.get(i).get("description") + "";
                    String pageCode = list.get(i).get("pagecode") + "";
                    String url = list.get(i).get("url") + "";
                    int sortKey = Integer.parseInt(list.get(i).get("sortkey") + "");
                    String statusCode = list.get(i).get("status") + "";
                    //byte aFlag = Byte.parseByte(list.get(i).get("aflag") + "");
                    //byte cFlag = Byte.parseByte(list.get(i).get("cflag") + "");
                    String sectionCode = list.get(i).get("section") + "";
                    String userRoleCode = list.get(i).get("USERROLE") + "";
                    //String lastUpdatedUser = list.get(i).get("lastupdateduser") + "";
                    //Date lastUpdatedTime = (Date) list.get(i).get("lastupdatedtime");
                    //Date createdTime = (Date) list.get(i).get("createdtime");
                    //set values in page list
                    Page page = new Page();
                    page.setDescription(description);
                    page.setPageCode(pageCode);
                    page.setUrl(url);
                    page.setSortKey(sortKey);
                    page.setStatusCode(statusCode);
                    //page.setaFlag(aFlag);
                    //page.setcFlag(cFlag);
                    page.setSectionCode(sectionCode);
                    page.setUserRoleCode(userRoleCode);
                    //page.setLastUpdatedUser(lastUpdatedUser);
                    //page.setLastUpdatedTime(lastUpdatedTime);
                    //page.setCreatedTime(createdTime);
                    //add the values map
                    if (pageListMap.containsKey(page.getSectionCode())) {
                        List<Page> pageList = pageListMap.get(page.getSectionCode());
                        pageList.add(page);
                        pageListMap.put(page.getSectionCode(), pageList);
                    } else {
                        List<Page> pageList = new ArrayList<Page>();
                        pageList.add(page);
                        pageListMap.put(page.getSectionCode(), pageList);
                    }
                }
            }
        } catch (EmptyResultDataAccessException ere) {
            return pageListMap;
        } catch (Exception e) {
            throw e;
        }
        return pageListMap;
    }

    @Transactional(readOnly = true)
    public Map<String, PageTask> getUserPageTaskListByByUserRoleCode(String userRoleCode) {
        Map<String, PageTask> pageTaskListMap = new HashMap<String, PageTask>();
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(SQL_GET_USER_PAGETASKLIST, userRoleCode, commonVarList.STATUS_ACTIVE, commonVarList.STATUS_ACTIVE);
            if (list.size() != 0) {
                for (int i = 0; i < list.size(); i++) {
                    String userRole = list.get(i).get("USERROLE") + "";
                    String sectionCode = list.get(i).get("section") + "";
                    String page = list.get(i).get("page") + "";
                    String pageDescription = list.get(i).get("pagedescription") + "";
                    String task = list.get(i).get("task") + "";
                    String taskDescription = list.get(i).get("taskdescription") + "";
                    String taskStatus = list.get(i).get("taskstatus") + "";
                    String taskLastupdatedUser = list.get(i).get("tasklastupdateduser") + "";
                    Date taskCreatedTime = (Date) list.get(i).get("taskcreatetime");
                    Date taskLastupdatedTime = (Date) list.get(i).get("tasklastupdatedtime");
                    String lastUpdatedUser = list.get(i).get("lastupdateduser") + "";
                    Date createdTime = (Date) list.get(i).get("createtime");
                    Date lastUpdatedTime = (Date) list.get(i).get("lastupdatedtime");
                    //add values to page task map
                    PageTask pt = new PageTask();
                    pt.setUserRoleCode(userRole);
                    pt.setSectionCode(sectionCode);
                    pt.setPageCode(page);
                    pt.setPageDescription(pageDescription);
                    pt.setLastUpdatedUser(lastUpdatedUser);
                    pt.setCreatedTime(createdTime);
                    pt.setLastUpdatedTime(lastUpdatedTime);
                    //create the task object
                    Task t = new Task(task, taskDescription, taskStatus, taskCreatedTime, taskLastupdatedUser, taskLastupdatedTime, lastUpdatedUser);
                    //add the values map
                    if (pageTaskListMap.containsKey(page)) {
                        PageTask pageTask = pageTaskListMap.get(page);
                        //get the existing task list
                        List<Task> taskList = pageTask.getTaskList();
                        taskList.add(t);
                        //set the new task list
                        pageTask.setTaskList(taskList);
                        pageTaskListMap.put(page, pageTask);
                    } else {
                        List<Task> taskList = new ArrayList<Task>() {{
                            add(t);
                        }};
                        pt.setTaskList(taskList);
                        pageTaskListMap.put(page, pt);
                    }
                }
            }
        } catch (EmptyResultDataAccessException ere) {
            return pageTaskListMap;
        } catch (Exception e) {
            throw e;
        }
        return pageTaskListMap;
    }
}
