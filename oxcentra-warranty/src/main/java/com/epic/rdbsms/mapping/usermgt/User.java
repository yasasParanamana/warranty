package com.epic.rdbsms.mapping.usermgt;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class User {
    private String userName;
    private String password;
    private String userrole;
    private Date expirydate;
    private String fullname;
    private String email;
    private String mobile;
    private Byte noofinvlidattempt;
    private Date loggeddate;
    private String initialloginstatus;
    private Byte ad;
    private String status;
    private String lastupdateduser;
    private Date lastupdatedtime;
    private Date createtime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserrole() {
        return userrole;
    }

    public void setUserrole(String userrole) {
        this.userrole = userrole;
    }

    public Date getExpirydate() {
        return expirydate;
    }

    public void setExpirydate(Date expirydate) {
        this.expirydate = expirydate;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Byte getNoofinvlidattempt() {
        return noofinvlidattempt;
    }

    public void setNoofinvlidattempt(Byte noofinvlidattempt) {
        this.noofinvlidattempt = noofinvlidattempt;
    }

    public Date getLoggeddate() {
        return loggeddate;
    }

    public void setLoggeddate(Date loggeddate) {
        this.loggeddate = loggeddate;
    }

    public String getInitialloginstatus() {
        return initialloginstatus;
    }

    public void setInitialloginstatus(String initialloginstatus) {
        this.initialloginstatus = initialloginstatus;
    }

    public Byte getAd() {
        return ad;
    }

    public void setAd(Byte ad) {
        this.ad = ad;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
