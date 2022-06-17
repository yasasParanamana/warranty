package com.oxcentra.rdbsms.bean.usermgt.task;

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
public class TaskInputBean extends DataTablesRequest {
    private String taskCode;
    private String description;
    private String status;
    private Date createdTime;
    private String createdUser;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
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


    /* add getters setters */

}
