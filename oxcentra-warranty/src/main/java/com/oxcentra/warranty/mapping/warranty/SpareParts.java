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
public class SpareParts {
    private String id;
    private String warrantyId;
    private String supplierId;
    private String token;
    private String sparePartType;
    private String sparePartName;
    private String customer;
    private String qty;
    private String trackingNoSupplier;
    private String trackingNoInHouse;
    private String lastUpdatedUser;
    private String createdUser;
    private Date lastUpdatedTime;
    private Date createdTime;
}
