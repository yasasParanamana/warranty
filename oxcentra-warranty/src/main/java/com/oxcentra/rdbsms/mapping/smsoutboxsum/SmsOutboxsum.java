package com.oxcentra.rdbsms.mapping.smsoutboxsum;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Scope("prototype")
public class SmsOutboxsum {
    private Long telcoCount;
    private BigDecimal telcoPerc;
    private Long delCount;
    private BigDecimal delPerc;
    private Long errCount;
    private BigDecimal errPerc;
    private Long expireCount;
    private BigDecimal expirePerc;
    private Long procCount;
    private BigDecimal procPerc;
    private Long rejCount;
    private BigDecimal rejPerc;
    private Long totalSMS;

    public SmsOutboxsum() {
    }

    public Long getTelcoCount() {
        return telcoCount;
    }

    public void setTelcoCount(Long telcoCount) {
        this.telcoCount = telcoCount;
    }

    public BigDecimal getTelcoPerc() {
        return telcoPerc;
    }

    public void setTelcoPerc(BigDecimal telcoPerc) {
        this.telcoPerc = telcoPerc;
    }

    public Long getDelCount() {
        return delCount;
    }

    public void setDelCount(Long delCount) {
        this.delCount = delCount;
    }

    public BigDecimal getDelPerc() {
        return delPerc;
    }

    public void setDelPerc(BigDecimal delPerc) {
        this.delPerc = delPerc;
    }

    public Long getErrCount() {
        return errCount;
    }

    public void setErrCount(Long errCount) {
        this.errCount = errCount;
    }

    public BigDecimal getErrPerc() {
        return errPerc;
    }

    public void setErrPerc(BigDecimal errPerc) {
        this.errPerc = errPerc;
    }

    public Long getExpireCount() {
        return expireCount;
    }

    public void setExpireCount(Long expireCount) {
        this.expireCount = expireCount;
    }

    public BigDecimal getExpirePerc() {
        return expirePerc;
    }

    public void setExpirePerc(BigDecimal expirePerc) {
        this.expirePerc = expirePerc;
    }

    public Long getProcCount() {
        return procCount;
    }

    public void setProcCount(Long procCount) {
        this.procCount = procCount;
    }

    public BigDecimal getProcPerc() {
        return procPerc;
    }

    public void setProcPerc(BigDecimal procPerc) {
        this.procPerc = procPerc;
    }

    public Long getRejCount() {
        return rejCount;
    }

    public void setRejCount(Long rejCount) {
        this.rejCount = rejCount;
    }

    public BigDecimal getRejPerc() {
        return rejPerc;
    }

    public void setRejPerc(BigDecimal rejPerc) {
        this.rejPerc = rejPerc;
    }

    public Long getTotalSMS() {
        return totalSMS;
    }

    public void setTotalSMS(Long totalSMS) {
        this.totalSMS = totalSMS;
    }
}
