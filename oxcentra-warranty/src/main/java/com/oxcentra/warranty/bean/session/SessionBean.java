package com.oxcentra.warranty.bean.session;

import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.mapping.passwordpolicy.PasswordPolicy;
import com.oxcentra.warranty.mapping.usermgt.Page;
import com.oxcentra.warranty.mapping.usermgt.PageTask;
import com.oxcentra.warranty.mapping.usermgt.Section;
import com.oxcentra.warranty.mapping.usermgt.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SessionBean {
    private String sessionid;
    private String username;
    private User user;
    private boolean changePwdMode;
    private int daysToExpire;
    private Audittrace audittrace;
    private List<Section> sectionList = new ArrayList<>();
    private Map<String, List<Page>> pageMap = new HashMap<String, List<Page>>();
    private Map<String, PageTask> pageTaskMap = new HashMap<String, PageTask>();
    private PasswordPolicy passwordPolicy;

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isChangePwdMode() {
        return changePwdMode;
    }

    public void setChangePwdMode(boolean changePwdMode) {
        this.changePwdMode = changePwdMode;
    }

    public int getDaysToExpire() {
        return daysToExpire;
    }

    public void setDaysToExpire(int daysToExpire) {
        this.daysToExpire = daysToExpire;
    }

    public Audittrace getAudittrace() {
        return audittrace;
    }

    public void setAudittrace(Audittrace audittrace) {
        this.audittrace = audittrace;
    }

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public Map<String, List<Page>> getPageMap() {
        return pageMap;
    }

    public void setPageMap(Map<String, List<Page>> pageMap) {
        this.pageMap = pageMap;
    }

    public Map<String, PageTask> getPageTaskMap() {
        return pageTaskMap;
    }

    public void setPageTaskMap(Map<String, PageTask> pageTaskMap) {
        this.pageTaskMap = pageTaskMap;
    }

    public PasswordPolicy getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(PasswordPolicy passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }
}
