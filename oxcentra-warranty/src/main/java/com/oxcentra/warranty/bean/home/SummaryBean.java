package com.oxcentra.warranty.bean.home;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString

public class SummaryBean {

    private String status;
    private String statusCount;

    private String failingArea;
    private String failingAreaCount;

    private List<SummaryBean> statusCountList = new ArrayList<SummaryBean>();

    String[] labelName={};
    String[] labelCount={};

}
