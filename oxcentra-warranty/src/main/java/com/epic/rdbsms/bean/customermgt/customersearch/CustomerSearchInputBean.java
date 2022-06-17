package com.epic.rdbsms.bean.customermgt.customersearch;

import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.util.common.DataTablesRequest;
import lombok.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomerSearchInputBean extends DataTablesRequest {

    private String identification;
    private String customerid;
    private String accountno;
    private String mobileno;
    private String lastUpdatedUser;
    private String status;
    private Date lastUpdatedTime;
    private String waiveoffstatus;
    private Date createdTime;
    private List<Status> statusList;
    private String customerCategory;
    private String customerName;
    private String statuscode;
    private String branch;
    private String accountType;
    private Date dob;
    private String dobSt;
    private String remark;

    /*------------for access control-----------*/
    private boolean vadd;
    private boolean vupdate;
    private boolean vdelete;
    private boolean vconfirm;
    private boolean vreject;
    private boolean vdualauth;
    private boolean vdownload;



    /*-------------for access control----------*/

}
