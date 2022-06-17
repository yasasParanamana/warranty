package com.oxcentra.warranty.bean.sysconfigmgt.passwordpolicy;

import com.oxcentra.warranty.util.common.DataTablesRequest;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PasswordPolicyInputBean extends DataTablesRequest {
    private String passwordPolicyId;
    private String minimumLength;
    private String maximumLength;
    private String minimumSpecialCharacters;
    private String minimumUpperCaseCharacters;
    private String minimumNumericalCharacters;
    private String minimumLowerCaseCharacters;
    private String noOfInvalidLoginAttempt;
    private String repeatCharactersAllow;
    private String initialPasswordExpiryStatus;
    private String passwordExpiryPeriod;
    private String noOfHistoryPassword;
    private String minimumPasswordChangePeriod;
    private String idleAccountExpiryPeriod;
    private String description;
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
}
