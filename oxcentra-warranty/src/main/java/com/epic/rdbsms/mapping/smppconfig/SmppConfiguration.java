package com.epic.rdbsms.mapping.smppconfig;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class SmppConfiguration {
    private String smppCode;
    private String description;
    private String status;
    private int maxTps;
    private String primaryIp;
    private String secondaryIp;
    private String systemId;
    private String password;
    private int bindPort;
    private String bindMode;
    private String mtPort;
    private String moPort;
    private int maxBulkTps;
    private String createdUser;
    private Date createdTime;
    private String lastUpdatedUser;
    private Date lastUpdatedTime;

    public SmppConfiguration() {
    }

    public SmppConfiguration(String smppCode) {
        this.smppCode = smppCode;
    }

    public SmppConfiguration(String smppCode, String description, String status, int maxTps, String primaryIp, String secondaryIp, String systemId, String password, int bindPort, String bindMode, String mtPort, String moPort, int maxBulkTps, String createdUser, Date createdTime, String lastUpdatedUser, Date lastUpdatedTime) {
        this.smppCode = smppCode;
        this.description = description;
        this.status = status;
        this.maxTps = maxTps;
        this.primaryIp = primaryIp;
        this.secondaryIp = secondaryIp;
        this.systemId = systemId;
        this.password = password;
        this.bindPort = bindPort;
        this.bindMode = bindMode;
        this.mtPort = mtPort;
        this.moPort = moPort;
        this.maxBulkTps = maxBulkTps;
        this.createdUser = createdUser;
        this.createdTime = createdTime;
        this.lastUpdatedUser = lastUpdatedUser;
        this.lastUpdatedTime = lastUpdatedTime;
    }


    public String getSmppCode() {
        return smppCode;
    }

    public void setSmppCode(String smppCode) {
        this.smppCode = smppCode;
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

    public int getMaxTps() {
        return maxTps;
    }

    public void setMaxTps(int maxTps) {
        this.maxTps = maxTps;
    }

    public String getPrimaryIp() {
        return primaryIp;
    }

    public void setPrimaryIp(String primaryIp) {
        this.primaryIp = primaryIp;
    }

    public String getSecondaryIp() {
        return secondaryIp;
    }

    public void setSecondaryIp(String secondaryIp) {
        this.secondaryIp = secondaryIp;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBindPort() {
        return bindPort;
    }

    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }

    public String getBindMode() {
        return bindMode;
    }

    public void setBindMode(String bindMode) {
        this.bindMode = bindMode;
    }

    public String getMtPort() {
        return mtPort;
    }

    public void setMtPort(String mtPort) {
        this.mtPort = mtPort;
    }

    public String getMoPort() {
        return moPort;
    }

    public void setMoPort(String moPort) {
        this.moPort = moPort;
    }

    public int getMaxBulkTps() {
        return maxBulkTps;
    }

    public void setMaxBulkTps(int maxBulkTps) {
        this.maxBulkTps = maxBulkTps;
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
}
