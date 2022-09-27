package com.oxcentra.warranty.bean.home;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    private String strArrayStatus;
    private String strCount;

    private List<SummaryBean> statusCountList = new ArrayList<SummaryBean>();
    private List<SummaryBean> failingAreaCountList = new ArrayList<SummaryBean>();
    private List<SummaryBean> failingAreaCostCountList = new ArrayList<SummaryBean>();

}
