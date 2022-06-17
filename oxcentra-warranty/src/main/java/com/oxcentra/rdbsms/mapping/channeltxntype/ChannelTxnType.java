package com.oxcentra.rdbsms.mapping.channeltxntype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Namila Withanage on 11/19/2021
 */
@Component
@Scope("prototype")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelTxnType {

    private String txntype;
    private String txntypeDescription;
    private String channelcode;
    private String channelDescription;
    private String templatecode;
    private String templateDescription;
    private String status;
    private Date createdTime;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
    private String createdUser;
}
