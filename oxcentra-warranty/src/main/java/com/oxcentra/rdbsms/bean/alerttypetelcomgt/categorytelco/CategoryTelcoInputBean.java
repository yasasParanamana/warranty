package com.oxcentra.rdbsms.bean.alerttypetelcomgt.categorytelco;

import com.oxcentra.rdbsms.bean.common.CommonCategoryBean;
import com.oxcentra.rdbsms.bean.common.CommonTelcoBean;
import com.oxcentra.rdbsms.bean.common.Status;
import com.oxcentra.rdbsms.util.common.DataTablesRequest;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CategoryTelcoInputBean extends DataTablesRequest {
    private String userTask;
    private String category;
    private String categoryDescription;
    private String telco;
    private String mtPort;
    private String status;
    private Date createdTime;
    private String createdUser;
    private Date lastUpdatedTime;
    private String lastUpdatedUser;
    private List<String> mtPortList;
    private List<CommonTelcoBean> telcoList;
    private List<CommonCategoryBean> categoryList;
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
