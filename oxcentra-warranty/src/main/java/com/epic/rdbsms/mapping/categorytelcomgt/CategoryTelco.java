package com.epic.rdbsms.mapping.categorytelcomgt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTelco {
    private String category;
    private String categoryDescription;
    private String telco;
    private String mtPort;
    private String status;
    private Date createdTime;
    private String createdUser;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
}
