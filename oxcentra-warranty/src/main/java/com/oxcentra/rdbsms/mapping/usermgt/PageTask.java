package com.oxcentra.rdbsms.mapping.usermgt;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Scope("prototype")
public class PageTask {
    private String userRoleCode;
    private String pageCode;
    private String sectionCode;
    private String pageDescription;
    private List<Task> taskList = new ArrayList<>();
    private String lastUpdatedUser;
    private Date lastUpdatedTime;
    private Date createdTime;

    public PageTask() {
    }

    public PageTask(String userRoleCode, String pageCode, String sectionCode, String pageDescription, List<Task> taskList, String lastUpdatedUser, Date lastUpdatedTime, Date createdTime) {
        this.userRoleCode = userRoleCode;
        this.pageCode = pageCode;
        this.sectionCode = sectionCode;
        this.pageDescription = pageDescription;
        this.taskList = taskList;
        this.lastUpdatedUser = lastUpdatedUser;
        this.lastUpdatedTime = lastUpdatedTime;
        this.createdTime = createdTime;
    }

    public String getUserRoleCode() {
        return userRoleCode;
    }

    public void setUserRoleCode(String userRoleCode) {
        this.userRoleCode = userRoleCode;
    }

    public String getPageCode() {
        return pageCode;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getPageDescription() {
        return pageDescription;
    }

    public void setPageDescription(String pageDescription) {
        this.pageDescription = pageDescription;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public String getLastUpdatedUser() {
        return lastUpdatedUser;
    }

    public void setLastUpdatedUser(String lastUpdatedUser) {
        this.lastUpdatedUser = lastUpdatedUser;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
