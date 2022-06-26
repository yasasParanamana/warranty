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
public class Supplier {
    private String supplierCode;
    private String supplierName;
    private String supplierPhone;
    private String supplierEmail;
    private String supplierAddress;
    private String status;
    private String lastUpdatedUser;
    private String createdUser;
    private Date lastUpdatedTime;
    private Date createdTime;


}
