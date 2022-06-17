package com.epic.rdbsms.mapping.comparisonfield;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class Comparisonfield {
    private String comparisonfieldcode;
    private String status;
    private String description;
    private String lastupdateduser;
    private Date lastupdatedtime;
    private Date createtime;

    public String getComparisonfieldcode() {
        return comparisonfieldcode;
    }

    public void setComparisonfieldcode(String comparisonfieldcode) {
        this.comparisonfieldcode = comparisonfieldcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastupdateduser() {
        return lastupdateduser;
    }

    public void setLastupdateduser(String lastupdateduser) {
        this.lastupdateduser = lastupdateduser;
    }

    public Date getLastupdatedtime() {
        return lastupdatedtime;
    }

    public void setLastupdatedtime(Date lastupdatedtime) {
        this.lastupdatedtime = lastupdatedtime;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}
