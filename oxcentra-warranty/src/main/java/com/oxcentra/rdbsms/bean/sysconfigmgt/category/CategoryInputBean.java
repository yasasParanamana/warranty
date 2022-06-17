package com.oxcentra.rdbsms.bean.sysconfigmgt.category;

import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.mapping.department.Priority;
import com.oxcentra.rdbsms.util.common.DataTablesRequest;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CategoryInputBean extends DataTablesRequest {
    private String userTask;
    private String categoryCode;
    private String description;
    private String bulkEnable;
    private String unsubscribe;
    private String ackwait;
    private Integer ttlqueue;
    private String status;
    private String priority;
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
    private List<Priority> priorityList;
}
