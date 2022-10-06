package com.oxcentra.warranty.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class request {

    private String claimOnSupplier;
    private String model;
    private String failureArea;
    private String repairType;
    private String repairDescription;
    private String costDescription;

}

