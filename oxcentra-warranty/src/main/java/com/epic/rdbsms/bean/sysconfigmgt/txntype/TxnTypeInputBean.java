package com.epic.rdbsms.bean.sysconfigmgt.txntype;

import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.util.common.DataTablesRequest;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * @author Namila Withanage on 11/15/2021
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TxnTypeInputBean extends DataTablesRequest {
    private String txntype;
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
}
