package com.oxcentra.warranty.bean.usermgt.section;

import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.util.common.DataTablesRequest;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SectionInputBean extends DataTablesRequest {
    private String sectionCode;
    private String description;
    private String sortKey;
    private String status;
    private String lastUpdatedUser;
    private Date lastUpdatedTime;
    private Date createdTime;
    private String createdUser;

    /*------------for access control-----------*/
    private boolean vadd;
    private boolean vupdate;
    private boolean vdelete;
    private boolean vconfirm;
    private boolean vreject;
    private boolean vdualauth;
    /*-------------for access control----------*/

    private List<Status> statusList;
    private List<Status> statusActList;

}
