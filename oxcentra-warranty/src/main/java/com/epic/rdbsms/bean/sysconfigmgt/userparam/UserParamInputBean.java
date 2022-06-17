package com.epic.rdbsms.bean.sysconfigmgt.userparam;


import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.bean.common.UserParamCategory;
import com.epic.rdbsms.util.common.DataTablesRequest;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserParamInputBean extends DataTablesRequest {
    private String userTask;
    private String paramCode;
    private String description;
    private String category;
    private String status;
    private Date createdTime;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
    private String createdUser;

    /*-------for access control-----------*/
    private boolean vadd;
    private boolean vupdate;
    private boolean vdelete;
    private boolean vconfirm;
    private boolean vreject;
    private boolean vdualauth;

    /*-------for access control-----------*/
    private List<Status> statusList;
    private List<Status> statusActList;

    private List<UserParamCategory> categoryList;


}
