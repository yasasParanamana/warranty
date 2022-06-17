package com.oxcentra.rdbsms.mapping.usermgt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope("prototype")
public class UserRoleType {

    private String userRoleTypeCode;
    private String description;
    private String status;
}
