package com.oxcentra.warranty.bean.warranty.claim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailRequestBean {

    private String claimOnSupplier;
    private String model;
    private String failureArea;
    private String repairType;
    private String repairDescription;
    private String costDescription;
    private String token;

}

