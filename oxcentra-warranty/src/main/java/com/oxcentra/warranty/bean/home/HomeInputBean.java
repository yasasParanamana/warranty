package com.oxcentra.warranty.bean.home;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class HomeInputBean {
    private String countPending;
    private String countInPurchase;
    private String countNoted;
}
