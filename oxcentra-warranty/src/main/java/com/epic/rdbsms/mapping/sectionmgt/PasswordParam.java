package com.epic.rdbsms.mapping.sectionmgt;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class PasswordParam {
    private String passwordparam;
    private String userroletype;
    private String value;
    private Date createdTime;
    private String createdUser;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;

    public PasswordParam() {
    }

    public PasswordParam(String passwordparam, String userroletype, String value, Date createdTime, String createdUser, Date lastUpdatedTime, String lastUpdatedUser) {
        this.passwordparam = passwordparam;
        this.userroletype = userroletype;
        this.value = value;
        this.createdTime = createdTime;
        this.createdUser = createdUser;
        this.lastUpdatedTime = lastUpdatedTime;
        this.lastUpdatedUser = lastUpdatedUser;
    }

    public String getPasswordparam() {
        return passwordparam;
    }

    public void setPasswordparam(String passwordparam) {
        this.passwordparam = passwordparam;
    }

    public String getUserroletype() {
        return userroletype;
    }

    public void setUserroletype(String userroletype) {
        this.userroletype = userroletype;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getLastUpdatedUser() {
        return lastUpdatedUser;
    }

    public void setLastUpdatedUser(String lastUpdatedUser) {
        this.lastUpdatedUser = lastUpdatedUser;
    }
}
