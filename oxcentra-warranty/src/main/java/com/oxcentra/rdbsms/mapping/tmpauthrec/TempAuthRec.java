package com.oxcentra.rdbsms.mapping.tmpauthrec;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TempAuthRec {
    private String id;

    private String page;
    private String status;
    private String task;

    private String key1;
    private String key2;
    private String key3;
    private String key4;
    private String key5;
    private String key6;
    private String key7;
    private String key8;
    private String key9;
    private String key10;
    private String key11;
    private String key12;
    private String key13;
    private String key14;
    private String key15;
    private String key16;
    private String key17;
    private String key18;
    private String key19;
    private String key20;

    private String tmpRecord;

    private String createdTime;
    private String lastUpdatedTime;
    private String lastUpdatedUser;

    public TempAuthRec() {
    }

    public TempAuthRec(String id) {
        this.id = id;
    }

    public TempAuthRec(String id, String page, String status, String task) {
        this.id = id;
        this.page = page;
        this.status = status;
        this.task = task;
    }

    public TempAuthRec(String page, String status, String task) {
        this.page = page;
        this.status = status;
        this.task = task;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public String getKey3() {
        return key3;
    }

    public void setKey3(String key3) {
        this.key3 = key3;
    }

    public String getKey4() {
        return key4;
    }

    public void setKey4(String key4) {
        this.key4 = key4;
    }

    public String getKey5() {
        return key5;
    }

    public void setKey5(String key5) {
        this.key5 = key5;
    }

    public String getKey6() {
        return key6;
    }

    public void setKey6(String key6) {
        this.key6 = key6;
    }

    public String getKey7() {
        return key7;
    }

    public void setKey7(String key7) {
        this.key7 = key7;
    }

    public String getKey8() {
        return key8;
    }

    public void setKey8(String key8) {
        this.key8 = key8;
    }

    public String getKey9() {
        return key9;
    }

    public void setKey9(String key9) {
        this.key9 = key9;
    }

    public String getKey10() {
        return key10;
    }

    public void setKey10(String key10) {
        this.key10 = key10;
    }

    public String getKey11() {
        return key11;
    }

    public void setKey11(String key11) {
        this.key11 = key11;
    }

    public String getKey12() {
        return key12;
    }

    public void setKey12(String key12) {
        this.key12 = key12;
    }

    public String getKey13() {
        return key13;
    }

    public void setKey13(String key13) {
        this.key13 = key13;
    }

    public String getKey14() {
        return key14;
    }

    public void setKey14(String key14) {
        this.key14 = key14;
    }

    public String getKey15() {
        return key15;
    }

    public void setKey15(String key15) {
        this.key15 = key15;
    }

    public String getKey16() {
        return key16;
    }

    public void setKey16(String key16) {
        this.key16 = key16;
    }

    public String getKey17() {
        return key17;
    }

    public void setKey17(String key17) {
        this.key17 = key17;
    }

    public String getKey18() {
        return key18;
    }

    public void setKey18(String key18) {
        this.key18 = key18;
    }

    public String getKey19() {
        return key19;
    }

    public void setKey19(String key19) {
        this.key19 = key19;
    }

    public String getKey20() {
        return key20;
    }

    public void setKey20(String key20) {
        this.key20 = key20;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getLastUpdatedUser() {
        return lastUpdatedUser;
    }

    public void setLastUpdatedUser(String lastUpdatedUser) {
        this.lastUpdatedUser = lastUpdatedUser;
    }

    public String getTmpRecord() {
        return tmpRecord;
    }

    public void setTmpRecord(String tmpRecord) {
        this.tmpRecord = tmpRecord;
    }
}
