package com.oxcentra.warranty.bean.warranty.dealership;

import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.util.common.DataTablesRequest;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DealershipInputBean extends DataTablesRequest {
    private String dealershipCode;
    private String dealershipName;
    private String dealershipPhone;
    private String dealershipEmail;
    private String dealershipAddress;
    private String status;
    private String lastUpdatedUser;
    private String createdUser;
    private Date lastUpdatedTime;
    private Date createdTime;

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
