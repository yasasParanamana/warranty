package com.epic.rdbsms.mapping.userparam;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class UserParam {

    private String paramCode;
    private String description;
    private String status;
    private String category;
    private Date createdTime;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
    private String createdUser;

    public UserParam() {

    }

    public UserParam(String paramCode, String description, String status, String category, Date createdTime, Date lastUpdatedTime, String lastUpdatedUser) {
        this.paramCode = paramCode;
        this.description = description;
        this.status = status;
        this.category = category;
        this.createdTime = createdTime;
        this.lastUpdatedTime = lastUpdatedTime;
        this.lastUpdatedUser = lastUpdatedUser;
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
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

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }
}
