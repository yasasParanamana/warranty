package com.oxcentra.warranty.mapping.usermgt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Getter
@Setter
@Component
@Scope("prototype")
public class Page {
    private String pageCode;
    private String description;
    private String url;
    private int sortKey;
    private boolean actualFalg;
    private boolean currentFlag;
    private String statusCode;
    private String sectionCode;
    private String userRoleCode;
    private String lastUpdatedUser;
    private Date lastUpdatedTime;
    private Date createdTime;
    private String createdUser;
}
