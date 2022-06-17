package com.oxcentra.rdbsms.bean.usermgt.page;

import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.util.common.DataTablesRequest;
import lombok.*;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PageInputBean extends DataTablesRequest {
    private String pageCode;
    private String description;
    private String url;
    private int sortKey;
    private boolean actualFalg;
    private boolean currentFlag;
    private String status;
    private Date createdTime;
    private String createdUser;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
    /*-------for access control-----------*/
    private boolean vupdate;
    private boolean vdualauth;
    private boolean vconfirm;
    private boolean vreject;
    /*-------for access control-----------*/
    private List<Status> statusList;
}
