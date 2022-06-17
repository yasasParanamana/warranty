package com.oxcentra.warranty.bean.audit;

import com.oxcentra.warranty.bean.common.CommonBean;
import com.oxcentra.warranty.util.common.DataTablesRequest;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AuditTraceInputBean extends DataTablesRequest {
    private String id;
    private String fromDate;
    private String toDate;
    private String userName;
    private String section;
    private String page;
    private String task;
    private String userRole;
    private String ip;
    private String remarks;
    private String description;
    private String newValue;
    private String oldValue;
    private String fields;
    private String createdTime;
    private String lastUpdatedTime;
    private String lastUpdatedUser;
    private List<AuditValueBean> valueBeanList;
    private List<CommonBean> sectionList;
    private List<CommonBean> pageList;
    private List<CommonBean> taskList;
    /*-------for access control-----------*/
    private boolean vdownload;
    /*-------for access control-----------*/
}
