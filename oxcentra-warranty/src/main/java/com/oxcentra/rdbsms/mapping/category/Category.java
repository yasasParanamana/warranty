package com.oxcentra.rdbsms.mapping.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope("prototype")
public class Category {
    private String category;
    private String description;
    private String isBulk;
    private String status;
    private String priority;
    private String unsubscribe;
    private String ackwait;
    private Integer ttlqueue;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;
    private String createdUser;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedTime;
    private String lastUpdatedUser;

}
