package com.epic.rdbsms.bean.sysconfigmgt.smssmppconfiguration;

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
public class SmsSmppConfigurationInputBean extends DataTablesRequest {
    private String userTask;
    private String smppCode;
    private String description;
    private String status;
    private String maxTps;
    private String primaryIp;
    private String secondaryIp;
    private String systemId;
    private String password;
    private String bindPort;
    private String bindMode;
    private String mtPort;
    private String moPort;
    private String maxBulkTps;
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
}
