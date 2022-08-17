package com.oxcentra.warranty.bean.sysconfigmgt.state;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class State {

    private String state_id;
    private String state_name;
    private String status;
}
