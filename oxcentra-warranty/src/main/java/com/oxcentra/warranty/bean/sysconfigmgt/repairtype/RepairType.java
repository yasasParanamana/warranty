package com.oxcentra.warranty.bean.sysconfigmgt.repairtype;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RepairType {

    private String code;
    private String description;
    private String status;
    private String lastUpdatedUser;
    private String lastUpdatedTime;
    private String createdTime;
    private String createdUser;

}
