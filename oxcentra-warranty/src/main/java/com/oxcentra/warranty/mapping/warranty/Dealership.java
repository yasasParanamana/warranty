package com.oxcentra.warranty.mapping.warranty;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Scope("prototype")
public class Dealership {
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


}
