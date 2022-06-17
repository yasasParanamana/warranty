package com.oxcentra.rdbsms.mapping.txntype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Namila Withanage on 11/15/2021
 */
@Component
@Scope("prototype")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TxnType {

    private String txntype;
    private String description;
    private String status;
    private Date createdTime;
    private String createdUser;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;

}
