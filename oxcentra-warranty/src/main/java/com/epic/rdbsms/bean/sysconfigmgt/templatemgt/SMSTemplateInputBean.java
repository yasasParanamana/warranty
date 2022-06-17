package com.epic.rdbsms.bean.sysconfigmgt.templatemgt;


import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.mapping.comparisonfield.Comparisonfield;
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
public class SMSTemplateInputBean extends DataTablesRequest {

    private String code;
    private String description;
    private String messageformat;
    private String status;
    private String des1;
    private String des2;
    private String des3;
    private String des4;
    private String des5;
    private String des6;
    private String des7;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;
    private String field6;
    private String field7;
    private Date createdtime;
    private Date lastupdatedtime;
    private String createduser;
    private String lastupdateduser;

    private List<Status> statusList;
    private List<Status> statusActList;
    private List<Comparisonfield> comparisonfieldList;

    /*-------for access control-----------*/
    private boolean vadd;
    private boolean vupdate;
    private boolean vdelete;
    private boolean vconfirm;
    private boolean vreject;
    private boolean vdualauth;
    /*-------for access control-----------*/
}
