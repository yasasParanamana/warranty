package com.oxcentra.warranty.bean.home;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class HomeBean {
    //-----------------------------------------form input values------------------------------------------------------//
    private String username;
    private String password;
    //-----------------------------------------form input values------------------------------------------------------//
    //-----------------------------------------user update values-----------------------------------------------------//
    private int attempts;
    private String statusCode;
    //-----------------------------------------user update values-----------------------------------------------------//
}
