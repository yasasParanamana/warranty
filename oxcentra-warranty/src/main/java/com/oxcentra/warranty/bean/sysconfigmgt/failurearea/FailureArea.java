package com.oxcentra.warranty.bean.sysconfigmgt.failurearea;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FailureArea {

    private String code;
    private String description;
    private String status;
    private String lastUpdatedUser;
    private String lastUpdatedTime;
    private String createdTime;
    private String createdUser;

}
