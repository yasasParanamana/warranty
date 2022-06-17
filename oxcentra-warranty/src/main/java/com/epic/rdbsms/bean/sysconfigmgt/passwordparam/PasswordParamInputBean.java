package com.epic.rdbsms.bean.sysconfigmgt.passwordparam;

import com.epic.rdbsms.mapping.common.CommonPasswordParam;
import com.epic.rdbsms.mapping.usermgt.UserRoleType;
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
