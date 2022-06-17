package com.oxcentra.rdbsms.bean.smsoutbox;

import com.oxcentra.rdbsms.bean.common.CommonCategoryBean;
import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.mapping.deliverystatus.DeliveryStatus;
import com.oxcentra.rdbsms.mapping.department.Department;
import com.oxcentra.rdbsms.mapping.smschannel.SmsChannel;
import com.oxcentra.rdbsms.mapping.telco.Telco;
import com.oxcentra.rdbsms.mapping.txntype.TxnType;
import com.oxcentra.rdbsms.util.common.DataTablesRequest;
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
