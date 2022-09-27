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

public class SummaryInputBean {

    private List<SummaryBean> statusCountList = new ArrayList<SummaryBean>();
}
