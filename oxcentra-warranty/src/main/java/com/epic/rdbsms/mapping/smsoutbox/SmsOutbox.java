package com.epic.rdbsms.mapping.smsoutbox;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class SmsOutbox {
    private int id;
    private String referenceNo;
    private String mobileNumber;
    private String message;
    private String status;
    private String deleteStatus;
    private String deleteReferenceNo;
    private String bulkId;
    private String bulkType;
    private String isIDD;
    private int partCount;
    private String telco;
    private String department;
    private String channel;
    private String category;
    private String responseCode;
    private String failedReason;
    private String createdUser;
    private Date createdTime;
    private String lastUpdatedUser;
    private Date lastUpdatedTime;
    private String trnType;

    public SmsOutbox() {
    }

    public SmsOutbox(int id, String referenceNo, String mobileNumber, String message, String status, String deleteStatus, String deleteReferenceNo, String bulkId, String bulkType, int partCount, String telco, String department, String channel, String category, String responseCode, String createdUser, Date createdTime, String lastUpdatedUser, Date lastUpdatedTime) {
        this.id = id;
        this.referenceNo = referenceNo;
        this.mobileNumber = mobileNumber;
        this.message = message;
        this.status = status;
        this.deleteStatus = deleteStatus;
        this.deleteReferenceNo = deleteReferenceNo;
        this.bulkId = bulkId;
        this.bulkType = bulkType;
        this.partCount = partCount;
        this.telco = telco;
        this.department = department;
        this.channel = channel;
        this.category = category;
        this.responseCode = responseCode;
        this.createdUser = createdUser;
        this.createdTime = createdTime;
        this.lastUpdatedUser = lastUpdatedUser;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getDeleteReferenceNo() {
        return deleteReferenceNo;
    }

    public void setDeleteReferenceNo(String deleteReferenceNo) {
        this.deleteReferenceNo = deleteReferenceNo;
    }

    public String getBulkId() {
        return bulkId;
    }

    public void setBulkId(String bulkId) {
        this.bulkId = bulkId;
    }

    public String getBulkType() {
        return bulkType;
    }

    public void setBulkType(String bulkType) {
        this.bulkType = bulkType;
    }

    public int getPartCount() {
        return partCount;
    }

    public void setPartCount(int partCount) {
        this.partCount = partCount;
    }

    public String getTelco() {
        return telco;
    }

    public void setTelco(String telco) {
        this.telco = telco;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
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

    public String getIsIDD() {
        return isIDD;
    }

    public void setIsIDD(String isIDD) {
        this.isIDD = isIDD;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public String getTrnType() {
        return trnType;
    }

    public void setTrnType(String trnType) {
        this.trnType = trnType;
    }
}
