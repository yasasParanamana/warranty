package com.epic.rdbsms.bean.sysconfigmgt.smsmtportmanagement;

import com.epic.rdbsms.bean.common.Status;
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
public class SmsMtPortInputBean extends DataTablesRequest {

    private String userTask;
    private String mtPort;
    private String status;
    private String createdUser;
    private Date createdTime;
    private String lastUpdatedUser;
    private Date lastUpdatedTime;
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

}
