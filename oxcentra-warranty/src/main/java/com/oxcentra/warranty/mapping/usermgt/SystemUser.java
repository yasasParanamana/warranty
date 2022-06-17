package com.oxcentra.warranty.mapping.usermgt;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class SystemUser {
    private String userName;
    private String fullName;
    private String userRoleCode;
    private String email;
    private String nic;
    private String serviceid;
    private String mobileNumber;
    private int noOfInvalidAttempt;
    private Date expiryDate;
    private Date lastLoggedDate;
    private String status;
    private String password;
    private String confirmPassword;
    private int ad;
    private Date createdTime;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
    private String createdUser;

    public SystemUser() {
    }

    public SystemUser(String userName, String fullName, String userRoleCode, String email, String mobileNumber, int noOfInvalidAttempt, Date expiryDate, Date lastLoggedDate, String status, int ad, Date createdTime, Date lastUpdatedTime, String lastUpdatedUser) {
        this.userName = userName;
        this.fullName = fullName;
        this.userRoleCode = userRoleCode;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.noOfInvalidAttempt = noOfInvalidAttempt;
        this.expiryDate = expiryDate;
        this.lastLoggedDate = lastLoggedDate;
        this.status = status;
        this.ad = ad;
        this.createdTime = createdTime;
        this.lastUpdatedTime = lastUpdatedTime;
        this.lastUpdatedUser = lastUpdatedUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserRoleCode() {
        return userRoleCode;
    }

    public void setUserRoleCode(String userRoleCode) {
        this.userRoleCode = userRoleCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getNoOfInvalidAttempt() {
        return noOfInvalidAttempt;
    }

    public void setNoOfInvalidAttempt(int noOfInvalidAttempt) {
        this.noOfInvalidAttempt = noOfInvalidAttempt;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getLastLoggedDate() {
        return lastLoggedDate;
    }

    public void setLastLoggedDate(Date lastLoggedDate) {
        this.lastLoggedDate = lastLoggedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public int getAd() {
        return ad;
    }

    public void setAd(int ad) {
        this.ad = ad;
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

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }
}
