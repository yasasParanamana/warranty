package com.oxcentra.warranty.bean.sysconfigmgt.passwordparam;

import com.oxcentra.warranty.mapping.common.CommonPasswordParam;
import com.oxcentra.warranty.mapping.usermgt.UserRoleType;
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
public class PasswordParamInputBean extends DataTablesRequest {
    private String passwordparam;
    private String userroletype;
    private String value;
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
    private List<UserRoleType> userRoleTypeBeanList;
    private List<CommonPasswordParam> passwordParamBeanList;
}
