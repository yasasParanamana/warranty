package com.oxcentra.rdbsms.bean.usermgt.userrole;

public class UserRoleBean {
    private String userrolecode;
    private String description;
    private String status;
    private String userroletype;

    public String getUserrolecode() {
        return userrolecode;
    }

    public void setUserrolecode(String userrolecode) {
        this.userrolecode = userrolecode;
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

    public String getUserroletype() {
        return userroletype;
    }

    public void setUserroletype(String userroletype) {
        this.userroletype = userroletype;
    }
}
