package com.epic.rdbsms.bean.sysconfigmgt.channeltxntype;

import com.epic.rdbsms.bean.common.Status;
import com.epic.rdbsms.mapping.smschannel.SmsChannel;
import com.epic.rdbsms.mapping.smstemplate.SmsOutputTemplate;
import com.epic.rdbsms.mapping.txntype.TxnType;
import com.epic.rdbsms.util.common.DataTablesRequest;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * @author Namila Withanage on 11/19/2021
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ChannelTxnTypeInputBean extends DataTablesRequest {

    private String userTask;
    private String txntype;
    private String txntypeDescription;
    private String channel;
    private String channelDescription;
    private String template;
    private String templateDescription;
    private String status;
    private Date createdTime;
    private String createdUser;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
    private List<TxnType> txnTypeList;
    private List<SmsChannel> channelList;
    private List<SmsOutputTemplate> templateList;
    /*-------for access control-----------*/
    private boolean vadd;
    private boolean vupdate;
    private boolean vdelete;
    private boolean vconfirm;
    private boolean vreject;
    private boolean vdualauth;
    /*-------for access control-----------*/
    private List<Status> statusList;
    private List<Status> statusActList;

}
