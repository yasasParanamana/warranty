package com.oxcentra.warranty.bean.sysconfigmgt.failuretype;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FailureType {

    private String code;
    private String description;
    private String status;
    private String lastUpdatedUser;
    private String lastUpdatedTime;
    private String createdTime;
    private String createdUser;

}
