package com.oxcentra.rdbsms.bean.common;

public class Status {
    private String statusCode;
    private String description;
    private String statusCategoryCode;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatusCategoryCode() {
        return statusCategoryCode;
    }

    public void setStatusCategoryCode(String statusCategoryCode) {
        this.statusCategoryCode = statusCategoryCode;
    }
}
