package com.epic.rdbsms.bean.usermgt.userrole;

import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.mapping.usermgt.Page;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.mapping.usermgt.UserRoleType;
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
public class UserRoleInputBean extends DataTablesRequest {
    private String userroleCode;
    private String description;
    private String userroleType;
    private String status;
    private String statusCode;
    private String section;
    private String page;
    private List<String> unAssignList;
    private List<String> assignList;
    private Date createdTime;
    private String lastUpdatedUser;
    private String createdUser;
    private Date lastUpdatedTime;
    /*-------for access control-----------*/
    private boolean vadd;
    private boolean vupdate;
    private boolean vdelete;
    private boolean vassignpage;
    private boolean vassigntask;
    private boolean vdualauth;
    private boolean vconfirm;
    private boolean vreject;
    /*-------for access control-----------*/
    private List<Status> statusList;
    private List<Status> statusActList;

    private List<UserRoleType> userroleTypeList;
    private List<UserRoleType> userroleTypeActList;

    private List<Page> assignedList;
    private List<Page> notAssignedList;

    private List<String> assignedPages;
    private List<String> notAssignedPages;

    private List<Task> assignedTaskList;
    private List<Task> notAssignedTaskList;

    private List<String> assignedTasks;
    private List<String> notAssignedTasks;
}
