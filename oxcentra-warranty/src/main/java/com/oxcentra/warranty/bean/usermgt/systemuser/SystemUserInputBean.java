package com.oxcentra.warranty.bean.usermgt.systemuser;

import com.oxcentra.warranty.bean.common.Status;
import com.oxcentra.warranty.mapping.usermgt.UserRole;
import com.oxcentra.warranty.mapping.warranty.Dealership;
import com.oxcentra.warranty.util.common.DataTablesRequest;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SystemUserInputBean extends DataTablesRequest {
    private String userTask;
    private String userName;
    private String fullName;
    private String email;
    private String userRoleCode;
    private Date expiryDate;
    private String status;
    private String mobileNumber;
    private String nic;
    private String serviceId;
    private int initialLoginStatus;
    private int ad;
    private String password;
    private String confirmPassword;
    private Date createdTime;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
    private String createdUser;
    private String landLine;

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
    private List<UserRole> userRoleList;
    private List<Dealership> dealershipList;
}
