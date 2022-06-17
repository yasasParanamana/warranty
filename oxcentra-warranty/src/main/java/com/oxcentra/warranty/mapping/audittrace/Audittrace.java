package com.oxcentra.warranty.mapping.audittrace;

import com.oxcentra.warranty.bean.audit.AuditValueBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
@Scope("prototype")
public class Audittrace {
    private BigDecimal auditid;
    private String username;
    private String userrole;
    private String description;
    private String section;
    private String page;
    private String task;
    private String ip;
    private String remarks;
    private String field;
    private String oldvalue;
    private String newvalue;
    private String fields;
    private String lastupdateduser;
    private Date lastupdatedtime;
    private Date createdtime;
    private boolean isSkip;
    private List<AuditValueBean> valueBeanList;

    public Audittrace() {
    }

    public Audittrace(String section, String page, String task) {
        this.section = section;
        this.page = page;
        this.task = task;
    }

    public Audittrace(BigDecimal auditid, String userrole, String description, String section, String page, String task, String ip, String remarks, String field, String oldvalue, String newvalue, String fields, String lastupdateduser, Date lastupdatedtime, Date createdtime, boolean isSkip, List<AuditValueBean> valueBeanList) {
        this.auditid = auditid;
        this.userrole = userrole;
        this.description = description;
        this.section = section;
        this.page = page;
        this.task = task;
        this.ip = ip;
        this.remarks = remarks;
        this.field = field;
        this.oldvalue = oldvalue;
        this.newvalue = newvalue;
        this.fields = fields;
        this.lastupdateduser = lastupdateduser;
        this.lastupdatedtime = lastupdatedtime;
        this.createdtime = createdtime;
        this.isSkip = isSkip;
        this.valueBeanList = valueBeanList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getAuditid() {
        return auditid;
    }

    public void setAuditid(BigDecimal auditid) {
        this.auditid = auditid;
    }

    public String getUserrole() {
        return userrole;
    }

    public void setUserrole(String userrole) {
        this.userrole = userrole;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOldvalue() {
        return oldvalue;
    }

    public void setOldvalue(String oldvalue) {
        this.oldvalue = oldvalue;
    }

    public String getNewvalue() {
        return newvalue;
    }

    public void setNewvalue(String newvalue) {
        this.newvalue = newvalue;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
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

    public Date getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(Date createdtime) {
        this.createdtime = createdtime;
    }

    public boolean isSkip() {
        return isSkip;
    }

    public void setSkip(boolean skip) {
        isSkip = skip;
    }

    public List<AuditValueBean> getValueBeanList() {
        return valueBeanList;
    }

    public void setValueBeanList(List<AuditValueBean> valueBeanList) {
        this.valueBeanList = valueBeanList;
    }
}
