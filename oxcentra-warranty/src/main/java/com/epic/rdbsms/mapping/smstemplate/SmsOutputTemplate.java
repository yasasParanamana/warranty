package com.epic.rdbsms.mapping.smstemplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Namila Withanage on 11/20/2021
 */
@Component
@Scope("prototype")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsOutputTemplate {

    private String templatecode;
    private String description;
    private String messageformat;
    private String status;
    private Date createdTime;
    //    private String createdUser;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;

}
