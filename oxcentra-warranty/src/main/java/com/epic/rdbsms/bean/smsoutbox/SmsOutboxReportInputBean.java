package com.epic.rdbsms.bean.smsoutbox;

import com.epic.rdbsms.bean.common.CommonCategoryBean;
import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.mapping.deliverystatus.DeliveryStatus;
import com.epic.rdbsms.mapping.department.Department;
import com.epic.rdbsms.mapping.smschannel.SmsChannel;
import com.epic.rdbsms.mapping.telco.Telco;
import com.epic.rdbsms.mapping.txntype.TxnType;
import com.epic.rdbsms.util.common.DataTablesRequest;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SmsOutboxReportInputBean extends DataTablesRequest {
    private String fromDate;
    private String toDate;
    private String telco;
    private String department;
    private String channel;
    private String category;
    private String status;
    private String delstatus;
    private String mobileno;
    private String txnType;
    /*-------for access control-----------*/
    private boolean vdownload;
    /*-------for access control-----------*/
    private List<Telco> telcoList;
    private List<Department> departmentList;
    private List<SmsChannel> smsChannelList;
    private List<TxnType> txnTypeList;
    private List<CommonCategoryBean> categoryList;
    private List<Status> statusList;
    private List<DeliveryStatus> delStatusList;

}
