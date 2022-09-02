package com.oxcentra.warranty.repository.common;

import com.oxcentra.warranty.bean.common.*;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.sysconfigmgt.failurearea.FailureArea;
import com.oxcentra.warranty.bean.sysconfigmgt.failuretype.FailureType;
import com.oxcentra.warranty.bean.sysconfigmgt.model.Model;
import com.oxcentra.warranty.bean.sysconfigmgt.repairtype.RepairType;
import com.oxcentra.warranty.bean.sysconfigmgt.state.State;
import com.oxcentra.warranty.bean.usermgt.userrole.UserRoleBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.mapping.common.CommonPasswordParam;
import com.oxcentra.warranty.mapping.usermgt.UserRole;
import com.oxcentra.warranty.mapping.usermgt.UserRoleType;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.util.varlist.CommonVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Scope("prototype")
public class CommonRepository {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    CommonVarList commonVarList;

    //    private final String SQL_SYSTEM_TIME = "select to_char(sysdate, 'mm-dd-yyyy hh24:mi:ss') now from dual ";
    private final String SQL_SYSTEM_TIME = "select current_timestamp";
    private final String SQL_USERPARAM_BY_PARAMCODE_AND_USERROLETYPE = "select value from passwordparam where passwordparam = ? and userroletype = ?";
    private final String SQL_USERROLE_STATUS_BY_USERROLECODE = "select status from userrole where userrolecode=?";
    private final String SQL_INSERT_AUDITTRACE = "insert into web_systemaudit(userrole , section , page , task , ip , remarks , field , oldvalue , newvalue, lastupdateduser, description) values(? , ? , ? , ? , ? , ? , ? , ? , ?, ?, ?)";
    private final String SQL_GET_STATUS_LIST_BY_CATEGORY = "select statuscode,description from status where statuscategory = ?";
    private final String SQL_GET_ACTIVE_DEACTIVE_STATUS_LIST = "select statuscode,description from status where statuscode in (?,?)";
    private final String SQL_INSERT_TEMPAUTH_RECORD = "insert into web_tmpauthrec (page,task,status,key1,key2,key3,key4,key5,key6,key7,key8,key9,key10,key11,key12,key13,key14,key15,key16,key17,key18,key19,key20,tmprecord,createdtime,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String SQL_CHECK_PAGE_IS_DUALAUTHENTICATE = "select count(pagecode) from web_page where cflag=1 and pagecode=?";
    private final String SQL_GET_DUALAUTH_RECORD = "select page,task,status,key1,key2,key3,key4,key5,key6,key7,key8,key9,key10,key11,key12,key13,key14,key15,key16,key17,key18,key19,key20,tmprecord,createdtime,lastupdatedtime,lastupdateduser from web_tmpauthrec where id = ?";
    private final String SQL_UPDATE_DUALAUTH_RECORD = "update web_tmpauthrec set status=?,lastupdateduser=?,lastupdatedtime=? where id=?";
    private final String SQL_COUNT_DUALAUTH = "select count(*) from web_tmpauthrec where page=? and status=? and key1=?";
    private final String SQL_GET_SECTION_LIST = "select sectioncode,description from web_section order by description asc";
    private final String SQL_GET_PAGE_LIST = "select pagecode,description from web_page order by description asc";
    private final String SQL_GET_TASK_LIST = "select taskcode,description from web_task order by description asc";
    private final String SQL_GET_USERROLE_TYPE_LIST = "select userroletypecode, description from userroletype";
    private final String SQL_GET_ACTIVE_USERROLE_TYPE_LIST = "select userroletypecode, description from userroletype where status=?";
    private final String SQL_GET_COMMON_PASSWORD_PARAM_LIST = "select paramcode, description, unit from common_passwordparam";
    private final String SQL_GET_USERROLE_LIST = "select userrolecode, description, userroletype, status from userrole where status='act' order by userrolecode";
    private final String SQL_GET_USERROLETYPE_LIST = "select userroletypecode, description from userroletype order by userroletypecode";
    private final String SQL_GET_USERROLE_LIST_BY_USERROLECODE = "select userrolecode , description , status , lastupdateduser , lastupdatedtime , createdtime , userroletype from userrole where userroletype = ?";
    private final String SQL_GET_PRIORITY_LIST = "select prioritylevel,description,status,createdtime,lastupdatedtime,lastupdateduser from priority where status = ?";
    private final String SQL_GET_TELCO_LIST = "select code, description from telco where status = ?";
    private final String SQL_GET_CATEGORY_LIST = "select category, description from category where status = ?";
    private final String SQL_GET_MT_PORT_LIST = "select mtport from smsmtport where status = ?";
    private final String SQL_GET_DEPARTMENT_LIST = "select code,description,status,createdtime,createduser,lastupdatedtime,lastupdateduser from department where status = ?";
    private final String SQL_GET_SMSCHANNEL_LIST = "select channelcode,description,password,status,createdtime,createduser,lastupdatedtime,lastupdateduser from SMSCHANNEL where status = ?";
    private final String SQL_GET_TXNTYPE_LIST = "select txntype,description,status,createdtime,createduser,lastupdatedtime,lastupdateduser from txn_type where status = ?";
    private final String SQL_GET_SMSTEMPLATE_LIST = "select templatecode,messageformat,description,status,createdtime,lastupdatedtime,lastupdateduser from smsoutputtemplate where status = ?";

    private final String SQL_GET_USERPARAM_CATEGORY_LIST = "select * from userparamcategory";
    private final String SQL_FIND_COMPARISON_FIELD = "select * from comparisonfield";
    private final String SQL_GET_DELIVERY_STATUS_LIST = "select * from deliverystatus";

    private final String SQL_FIND_DELIVERY_STATUS = "select statuscode,description from deliverystatus d where d.statuscode = ?";
    private final String SQL_FIND_STATUS = "select statuscode,description from STATUS s where s.statuscode = ?";
    private final String SQL_FIND_TXN_TYPE = "select txntype,description from txn_type tx where tx.txntype = ?";
    private final String SQL_FIND_TELCO = "select code,description from TELCO d where d.code = ?";
    private final String SQL_GET_MODEL_LIST_BY_STATUS = "select id,model from reg_model where status = ?";
    private final String SQL_GET_STATE_LIST_BY_STATUS = "select state_id,state_name from state where status = ?";
    private final String SQL_GET_FAILURE_TYPE_LIST_BY_STATUS = "select CODE,DESCRIPTION from reg_failure_type where status = ?";
    private final String SQL_GET_FAILURE_AREA_LIST_BY_STATUS = "select CODE,DESCRIPTION from reg_failure_area where status = ?";
    private final String SQL_GET_REPAIR_TYPE_LIST_BY_STATUS = "select CODE,DESCRIPTION from reg_failure_type_repair where status = ?";
    private final String SQL_GET_SUPPLIER_LIST_BY_STATUS = "select supplier_code,supplier_name from reg_supplier where status = ?";


    /**
     * @Author shalika_w
     * @CreatedTime 2021-01-13 12:23:41 PM
     * @Version V1.00
     * @MethodName getCurrentDate
     * @MethodParams []
     * @MethodDescription - Get current date from database
     */
    @Transactional(readOnly = true)
    public Date getCurrentDate() throws Exception {
//        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date formattedCurrentDate = null;
        try {
            Map<String, Object> currentDate = jdbcTemplate.queryForMap(SQL_SYSTEM_TIME);
            formattedCurrentDate = formatter.parse(currentDate.get("current_timestamp").toString());
            System.out.println("formattedCurrentDate ---- " + formattedCurrentDate);
        } catch (Exception e) {
            throw e;
        }
        return formattedCurrentDate;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-13 12:23:47 PM
     * @Version V1.00
     * @MethodName getPasswordParam
     * @MethodParams [paramcode, userroletype]
     * @MethodDescription - Get password param by param code and user role type
     */
    @Transactional(readOnly = true)
    public int getPasswordParam(String paramcode, String userroletype) throws Exception {
        int count = 0;
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(SQL_USERPARAM_BY_PARAMCODE_AND_USERROLETYPE, paramcode, userroletype);
            if (map.size() != 0) {
                count = map.get("value") != null ? Integer.parseInt(map.get("value").toString()) : 0;
            }
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-13 02:14:18 PM
     * @Version V1.00
     * @MethodName getUserRoleStatusCode
     * @MethodParams [USERROLE]
     * @MethodDescription - Get user role status code
     */
    @Transactional(readOnly = true)
    public String getUserRoleStatusCode(String USERROLE) {
        String statusCode = "";
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(SQL_USERROLE_STATUS_BY_USERROLECODE, USERROLE);
            if (map.size() != 0) {
                statusCode = (String) map.get("status");
            }
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            statusCode = "";
        } catch (Exception e) {
            throw e;
        }
        return statusCode;
    }

    @Transactional(readOnly = true)
    public List<Status> getActiveDeActiveStatusList() throws Exception {
        List<Status> statusBeanList;
        try {
            List<Map<String, Object>> statusList = jdbcTemplate.queryForList(SQL_GET_ACTIVE_DEACTIVE_STATUS_LIST, commonVarList.STATUS_ACTIVE, commonVarList.STATUS_DEACTIVE);
            statusBeanList = statusList.stream().map((record) -> {
                Status statusBean = new Status();
                statusBean.setStatusCode(record.get("statuscode").toString());
                statusBean.setDescription(record.get("description").toString());
                return statusBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            statusBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return statusBeanList;

    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-20 12:07:05 PM
     * @Version V1.00
     * @MethodName getStatusList
     * @MethodParams [statusCategory]
     * @MethodDescription - This method return the status list
     */
    @Transactional(readOnly = true)
    public List<Status> getStatusList(String statusCategory) throws Exception {
        List<Status> statusBeanList;
        try {
            List<Map<String, Object>> statusList = jdbcTemplate.queryForList(SQL_GET_STATUS_LIST_BY_CATEGORY, statusCategory);
            statusBeanList = statusList.stream().map((record) -> {
                Status statusBean = new Status();
                statusBean.setStatusCode(record.get("STATUSCODE").toString());
                statusBean.setDescription(record.get("DESCRIPTION").toString());
                return statusBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            statusBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return statusBeanList;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-20 12:07:33 PM
     * @Version V1.00
     * @MethodName insertDualAuthRecordSQL
     * @MethodParams [tempAuthRecord]
     * @MethodDescription - This method insert the dual auth record
     */
    public String insertDualAuthRecordSQL(TempAuthRecBean tempAuthRecord) throws Exception {
        String message = "";
        int value = 0;
        try {
            value = jdbcTemplate.update(SQL_INSERT_TEMPAUTH_RECORD,
                    tempAuthRecord.getPage(), tempAuthRecord.getTask(), StatusVarList.STATUS_AUTH_PEN,
                    tempAuthRecord.getKey1(), tempAuthRecord.getKey2(), tempAuthRecord.getKey3(), tempAuthRecord.getKey4(), tempAuthRecord.getKey5(), tempAuthRecord.getKey6(), tempAuthRecord.getKey7(), tempAuthRecord.getKey8(), tempAuthRecord.getKey9(), tempAuthRecord.getKey10(),
                    tempAuthRecord.getKey11(), tempAuthRecord.getKey12(), tempAuthRecord.getKey13(), tempAuthRecord.getKey14(), tempAuthRecord.getKey15(), tempAuthRecord.getKey16(), tempAuthRecord.getKey17(), tempAuthRecord.getKey18(), tempAuthRecord.getKey19(), tempAuthRecord.getKey20(), tempAuthRecord.getTmpRecord(),
                    this.getCurrentDate(), this.getCurrentDate(), sessionBean.getUsername());

            if (value == 1) {
                message = "";
            }
        } catch (DuplicateKeyException ex) {
            throw ex;
        } catch (NumberFormatException | DataAccessException ex) {
            throw ex;
        }
        return message;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-20 12:08:50 PM
     * @Version V1.00
     * @MethodName checkPageIsDualAuthenticate
     * @MethodParams [pageCode]
     * @MethodDescription - This method check the dual authentication in a page
     */
    @Transactional(readOnly = true)
    public boolean checkPageIsDualAuthenticate(String pageCode) {
        boolean check;
        long count = 0;
        try {
            count = jdbcTemplate.queryForObject(SQL_CHECK_PAGE_IS_DUALAUTHENTICATE, new Object[]{pageCode}, Long.class);
            check = count > 0;
        } catch (EmptyResultDataAccessException ere) {
            check = false;
        } catch (Exception ex) {
            throw ex;
        }
        return check;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-19 04:53:52 PM
     * @Version V1.00
     * @MethodName insertAudittrace
     * @MethodParams [audittrace]
     * @MethodDescription - This method insert to audit table
     */
    @Transactional
    public boolean insertAudittrace(Audittrace audittrace) {
        boolean isInsertOk = false;
        try {
            Object[] parameters = {
                    audittrace.getUserrole(), audittrace.getSection(), audittrace.getPage(),
                    audittrace.getTask(), audittrace.getIp(), audittrace.getRemarks(), audittrace.getField(),
                    audittrace.getOldvalue(), audittrace.getNewvalue(), audittrace.getLastupdateduser(), audittrace.getDescription()
            };

            int updateValue = jdbcTemplate.update(SQL_INSERT_AUDITTRACE, parameters);
            if (updateValue == 1) {
                isInsertOk = true;
            }
        } catch (Exception e) {
            throw e;
        }
        return isInsertOk;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-25 12:45:29 PM
     * @Version V1.00
     * @MethodName getTempAuthRecord
     * @MethodParams [id]
     * @MethodDescription - This method get the temp auth record from table
     */
    @Transactional(readOnly = true)
    public TempAuthRecBean getTempAuthRecord(String id) throws Exception {
        TempAuthRecBean tempAuthRecBean;
        try {
            tempAuthRecBean = jdbcTemplate.queryForObject(SQL_GET_DUALAUTH_RECORD, new Object[]{id}, (rs, rowNum) -> {
                TempAuthRecBean authRecBean = new TempAuthRecBean();

                try {
                    authRecBean.setId(id);
                } catch (Exception e) {
                    authRecBean.setId(null);
                }

                try {
                    authRecBean.setPage(rs.getString("PAGE"));
                } catch (Exception e) {
                    authRecBean.setPage(null);
                }

                try {
                    authRecBean.setTask(rs.getString("TASK"));
                } catch (Exception e) {
                    authRecBean.setTask(null);
                }

                try {
                    authRecBean.setStatus(rs.getString("STATUS"));
                } catch (Exception e) {
                    authRecBean.setStatus(null);
                }

                try {
                    authRecBean.setKey1(rs.getString("KEY1"));
                } catch (Exception e) {
                    authRecBean.setKey1(null);
                }

                try {
                    authRecBean.setKey2(rs.getString("KEY2"));
                } catch (Exception e) {
                    authRecBean.setKey2(null);
                }

                try {
                    authRecBean.setKey3(rs.getString("KEY3"));
                } catch (Exception e) {
                    authRecBean.setKey3(null);
                }

                try {
                    authRecBean.setKey4(rs.getString("KEY4"));
                } catch (Exception e) {
                    authRecBean.setKey4(null);
                }

                try {
                    authRecBean.setKey5(rs.getString("KEY5"));
                } catch (Exception e) {
                    authRecBean.setKey5(null);
                }

                try {
                    authRecBean.setKey6(rs.getString("KEY6"));
                } catch (Exception e) {
                    authRecBean.setKey6(null);
                }

                try {
                    authRecBean.setKey7(rs.getString("KEY7"));
                } catch (Exception e) {
                    authRecBean.setKey7(null);
                }

                try {
                    authRecBean.setKey8(rs.getString("KEY8"));
                } catch (Exception e) {
                    authRecBean.setKey8(null);
                }

                try {
                    authRecBean.setKey9(rs.getString("KEY9"));
                } catch (Exception e) {
                    authRecBean.setKey9(null);
                }

                try {
                    authRecBean.setKey10(rs.getString("KEY10"));
                } catch (Exception e) {
                    authRecBean.setKey10(null);
                }

                try {
                    authRecBean.setKey11(rs.getString("KEY11"));
                } catch (Exception e) {
                    authRecBean.setKey11(null);
                }

                try {
                    authRecBean.setKey12(rs.getString("KEY12"));
                } catch (Exception e) {
                    authRecBean.setKey2(null);
                }

                try {
                    authRecBean.setKey13(rs.getString("KEY13"));
                } catch (Exception e) {
                    authRecBean.setKey13(null);
                }

                try {
                    authRecBean.setKey14(rs.getString("KEY14"));
                } catch (Exception e) {
                    authRecBean.setKey14(null);
                }

                try {
                    authRecBean.setKey15(rs.getString("KEY15"));
                } catch (Exception e) {
                    authRecBean.setKey15(null);
                }

                try {
                    authRecBean.setKey16(rs.getString("KEY16"));
                } catch (Exception e) {
                    authRecBean.setKey16(null);
                }

                try {
                    authRecBean.setKey17(rs.getString("KEY17"));
                } catch (Exception e) {
                    authRecBean.setKey17(null);
                }

                try {
                    authRecBean.setKey18(rs.getString("KEY18"));
                } catch (Exception e) {
                    authRecBean.setKey18(null);
                }

                try {
                    authRecBean.setKey19(rs.getString("KEY19"));
                } catch (Exception e) {
                    authRecBean.setKey19(null);
                }

                try {
                    authRecBean.setKey20(rs.getString("KEY20"));
                } catch (Exception e) {
                    authRecBean.setKey20(null);
                }

                try {
                    authRecBean.setTmpRecord(rs.getString("TMPRECORD"));
                } catch (Exception e) {
                    authRecBean.setTmpRecord(null);
                }

                try {
                    authRecBean.setLastUpdatedTime(rs.getString("LASTUPDATEDTIME"));
                } catch (Exception e) {
                    authRecBean.setLastUpdatedTime(null);
                }

                try {
                    authRecBean.setLastUpdatedUser(rs.getString("LASTUPDATEDUSER"));
                } catch (Exception e) {
                    authRecBean.setLastUpdatedUser(null);
                }

                try {
                    authRecBean.setCreatedTime(rs.getString("CREATEDTIME"));
                } catch (Exception e) {
                    authRecBean.setCreatedTime(null);
                }

                return authRecBean;
            });
        } catch (EmptyResultDataAccessException ere) {
            tempAuthRecBean = null;
        } catch (Exception ex) {
            throw ex;
        }
        return tempAuthRecBean;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-25 12:49:15 PM
     * @Version V1.00
     * @MethodName updateTempAuthRecord
     * @MethodParams [id, status]
     * @MethodDescription - This method update the temp auth record
     */
    public String updateTempAuthRecord(String id, String status) throws Exception {
        String message = "";
        int value = 0;
        try {
            value = jdbcTemplate.update(SQL_UPDATE_DUALAUTH_RECORD,
                    status,
                    sessionBean.getUsername(),
                    this.getCurrentDate(),
                    id);
            if (value != 1) {
                message = "Error occurred while updating task";
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-25 12:49:37 PM
     * @Version V1.00
     * @MethodName getTempAuthRecordCount
     * @MethodParams [primaryKey, page, status]
     * @MethodDescription - This method get the temp auth record count
     */
    @Transactional(readOnly = true)
    public long getTempAuthRecordCount(String primaryKey, String page, String status) throws Exception {

        long count = 0;
        try {
            count = jdbcTemplate.queryForObject(SQL_COUNT_DUALAUTH, new Object[]{
                    page,
                    status,
                    primaryKey
            }, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception ex) {
            throw ex;
        }
        return count;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-25 12:49:51 PM
     * @Version V1.00
     * @MethodName getPageList
     * @MethodParams []
     * @MethodDescription - This method get the page list
     */
    @Transactional(readOnly = true)
    public List<CommonBean> getPageList() {
        List<CommonBean> pageBeanList;
        try {
            List<Map<String, Object>> pageList = jdbcTemplate.queryForList(SQL_GET_PAGE_LIST);
            pageBeanList = pageList.stream().map((record) -> {
                CommonBean pageBean = new CommonBean();
                pageBean.setKey(record.get("PAGECODE").toString());
                pageBean.setValue(record.get("DESCRIPTION").toString());
                return pageBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            pageBeanList = new ArrayList<>();
        } catch (DataAccessException e) {
            throw e;
        }
        return pageBeanList;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-25 12:52:25 PM
     * @Version V1.00
     * @MethodName getSectionList
     * @MethodParams []
     * @MethodDescription - This method get the section list
     */
    @Transactional(readOnly = true)
    public List<CommonBean> getSectionList() {
        List<CommonBean> sectionBeanList = null;
        try {
            List<Map<String, Object>> sectionList = jdbcTemplate.queryForList(SQL_GET_SECTION_LIST);
            sectionBeanList = sectionList.stream().map((record) -> {
                CommonBean pageBean = new CommonBean();
                pageBean.setKey(record.get("SECTIONCODE").toString());
                pageBean.setValue(record.get("DESCRIPTION").toString());
                return pageBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            sectionBeanList = new ArrayList<>();
        } catch (DataAccessException e) {
            throw e;
        }
        return sectionBeanList;
    }

    /**
     * @Author dilanka_w
     * @CreatedTime 2021-01-25 12:54:49 PM
     * @Version V1.00
     * @MethodName getTaskList
     * @MethodParams []
     * @MethodDescription - This method get the task list
     */
    @Transactional(readOnly = true)
    public List<CommonBean> getTaskList() {
        List<CommonBean> taskBeanList = null;
        try {
            List<Map<String, Object>> taskList = jdbcTemplate.queryForList(SQL_GET_TASK_LIST);
            taskBeanList = taskList.stream().map((record) -> {
                CommonBean pageBean = new CommonBean();
                pageBean.setKey(record.get("TASKCODE").toString());
                pageBean.setValue(record.get("DESCRIPTION").toString());
                return pageBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            taskBeanList = new ArrayList<>();
        } catch (DataAccessException e) {
            throw e;
        }
        return taskBeanList;
    }


    @Transactional(readOnly = true)
    public List<UserRoleType> getUserroleTypeList() throws Exception {
        List<UserRoleType> userRoleTypeList;
        try {
            List<Map<String, Object>> typeList = jdbcTemplate.queryForList(SQL_GET_USERROLE_TYPE_LIST);
            userRoleTypeList = typeList.stream().map((record) -> {
                UserRoleType dataBean = new UserRoleType();
                dataBean.setUserRoleTypeCode(record.get("USERROLETYPECODE").toString());
                dataBean.setDescription(record.get("DESCRIPTION").toString());
                return dataBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            userRoleTypeList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return userRoleTypeList;
    }

    @Transactional(readOnly = true)
    public List<UserRoleType> getActUserroleTypeList() throws Exception {
        List<UserRoleType> userRoleTypeList;
        try {
            List<Map<String, Object>> typeList = jdbcTemplate.queryForList(SQL_GET_ACTIVE_USERROLE_TYPE_LIST, StatusVarList.STATUS_DFLT_ACT);
            userRoleTypeList = typeList.stream().map((record) -> {
                UserRoleType dataBean = new UserRoleType();
                dataBean.setUserRoleTypeCode(record.get("USERROLETYPECODE").toString());
                dataBean.setDescription(record.get("DESCRIPTION").toString());
                return dataBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            userRoleTypeList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return userRoleTypeList;
    }

    /**
     * @Author suren_v
     * @CreatedTime 2021-03-26 01:50:59 PM
     * @Version V1.00
     * @MethodName getCommonPasswordParamList
     * @MethodParams []
     * @MethodDescription - This method get the common password param list
     */
    @Transactional(readOnly = true)
    public List<CommonPasswordParam> getCommonPasswordParamList() {
        List<CommonPasswordParam> passwordParamList = null;
        try {
            List<Map<String, Object>> commonPasswordParamList = jdbcTemplate.queryForList(SQL_GET_COMMON_PASSWORD_PARAM_LIST);
            passwordParamList = commonPasswordParamList.stream().map((record) -> {
                CommonPasswordParam commonPasswordParam = new CommonPasswordParam();
                commonPasswordParam.setParamcode(record.get("PARAMCODE").toString());
                commonPasswordParam.setDescription(record.get("DESCRIPTION").toString());
                commonPasswordParam.setUnit(record.get("UNIT").toString());
                return commonPasswordParam;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            passwordParamList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return passwordParamList;
    }

    /**
     * @Author suren_v
     * @CreatedTime 2021-01-25 12:54:49 PM
     * @Version V1.00
     * @MethodName getActiveUserRoleList
     * @MethodParams []
     * @MethodDescription - This method get the user role list
     */
    @Transactional
    public List<UserRoleBean> getActiveUserRoleList() {
        List<UserRoleBean> roleBeanList = null;
        try {
            List<Map<String, Object>> userRoleList = jdbcTemplate.queryForList(SQL_GET_USERROLE_LIST);
            roleBeanList = userRoleList.stream().map((record) -> {
                UserRoleBean userRoleBean = new UserRoleBean();
                userRoleBean.setUserrolecode(record.get("USERROLECODE").toString());
                userRoleBean.setUserroletype(record.get("userroletype").toString());
                userRoleBean.setDescription(record.get("DESCRIPTION").toString());
                userRoleBean.setStatus(record.get("STATUS").toString());
                return userRoleBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            roleBeanList = new ArrayList<>();
        } catch (DataAccessException e) {
            throw e;
        }
        return roleBeanList;
    }

    /**
     * @Author UserRoleType
     * @CreatedTime 2021-03-26 01:48:27 PM
     * @Version V1.00
     * @MethodName getActiveUserRoleTypeList
     * @MethodParams []
     * @MethodDescription - This method get the user role list
     */
    @Transactional(readOnly = true)
    public List<UserRoleType> getActiveUserRoleTypeList() {
        List<UserRoleType> roleTypeBeanList = null;
        try {
            List<Map<String, Object>> userRoleList = jdbcTemplate.queryForList(SQL_GET_USERROLETYPE_LIST);
            roleTypeBeanList = userRoleList.stream().map((record) -> {
                UserRoleType userRoleBean = new UserRoleType();
                userRoleBean.setUserRoleTypeCode(record.get("USERROLETYPECODE").toString());
                userRoleBean.setDescription(record.get("DESCRIPTION").toString());
                return userRoleBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            roleTypeBeanList = new ArrayList<>();
        } catch (DataAccessException e) {
            throw e;
        }
        return roleTypeBeanList;
    }

    @Transactional(readOnly = true)
    public List<UserRole> getUserRoleListByUserRoleTypeCode(String userRoleTypeCode) throws Exception {
        List<UserRole> userroleList;
        try {
            List<Map<String, Object>> userRoleList = jdbcTemplate.queryForList(SQL_GET_USERROLE_LIST_BY_USERROLECODE, userRoleTypeCode);
            userroleList = userRoleList.stream().map((record) -> {
                UserRole userRole = new UserRole();
                userRole.setUserroleCode(record.get("userrolecode").toString());
                userRole.setDescription(record.get("description").toString());
                userRole.setStatus(record.get("status").toString());
                userRole.setCreatedTime(record.get("createdtime") != null ? (Date) record.get("createdtime") : null);
                userRole.setLastUpdatedTime(record.get("lastupdatedtime") != null ? (Date) record.get("lastupdatedtime") : null);
                userRole.setLastUpdatedUser(record.get("lastupdateduser") != null ? record.get("lastupdateduser").toString() : null);
                userRole.setUserroleType(record.get("userroletype").toString());
                return userRole;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            userroleList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return userroleList;
    }

  /*  @Transactional(readOnly = true)
    public List<Priority> getActivePriorityList() {
        List<Priority> priorityList;
        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(SQL_GET_PRIORITY_LIST, commonVarList.STATUS_ACTIVE);
            priorityList = resultList.stream().map((record) -> {
                Priority priority = new Priority();
                priority.setPriorityLevel(record.get("prioritylevel").toString());
                priority.setDescription(record.get("description").toString());
                priority.setStatus(record.get("status").toString());
                priority.setCreatedTime(record.get("createdtime") != null ? (Date) record.get("createdtime") : null);
                priority.setLastUpdatedTime(record.get("lastupdatedtime") != null ? (Date) record.get("lastupdatedtime") : null);
                priority.setLastUpdatedUser(record.get("lastupdateduser") != null ? record.get("lastupdateduser").toString() : null);
                return priority;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            priorityList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return priorityList;
    }*/

 /*   @Transactional(readOnly = true)
    public List<Telco> getTelcoList(String statusCode) {
        List<Telco> telcoList;
        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(SQL_GET_TELCO_LIST, statusCode);
            telcoList = resultList.stream().map((record) -> {
                Telco telco = new Telco();
                telco.setCode(record.get("code") != null ? record.get("code").toString() : null);
                telco.setDescription(record.get("description") != null ? record.get("description").toString() : null);
                telco.setStatus(record.get("status") != null ? record.get("status").toString() : null);
                telco.setCreatedTime(record.get("createdtime") != null ? (Date) record.get("createdtime") : null);
                telco.setCreatedUser(record.get("createduser") != null ? record.get("createduser").toString() : null);
                telco.setLastUpdatedTime(record.get("lastupdatedtime") != null ? (Date) record.get("lastupdatedtime") : null);
                telco.setLastUpdatedUser(record.get("lastupdateduser") != null ? record.get("lastupdateduser").toString() : null);
                return telco;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            telcoList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return telcoList;
    }*/

    @Transactional(readOnly = true)
    public List<CommonTelcoBean> getTelcoList() throws Exception {
        List<CommonTelcoBean> telcoBeanList;
        try {
            List<Map<String, Object>> telcoList = jdbcTemplate.queryForList(SQL_GET_TELCO_LIST, commonVarList.STATUS_ACTIVE);
            telcoBeanList = telcoList.stream().map((record) -> {
                CommonTelcoBean commonTelcoBean = new CommonTelcoBean();
                commonTelcoBean.setCode(record.get("code").toString());
                commonTelcoBean.setDescription(record.get("description").toString());
                return commonTelcoBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            telcoBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return telcoBeanList;
    }

    @Transactional(readOnly = true)
    public List<CommonCategoryBean> getCategoryList(String statusCode) throws Exception {
        List<CommonCategoryBean> categoryBeanList;
        try {
            List<Map<String, Object>> categoryList = jdbcTemplate.queryForList(SQL_GET_CATEGORY_LIST, statusCode);
            categoryBeanList = categoryList.stream().map((record) -> {
                CommonCategoryBean commonCategoryBean = new CommonCategoryBean();
                commonCategoryBean.setCategory(record.get("category").toString());
                commonCategoryBean.setDescription(record.get("description").toString());
                return commonCategoryBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            categoryBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return categoryBeanList;
    }

    @Transactional(readOnly = true)
    public List<String> getMtPortList() throws Exception {
        List<String> mtPortList;
        try {
            mtPortList = jdbcTemplate.query(SQL_GET_MT_PORT_LIST, new Object[]{commonVarList.STATUS_ACTIVE}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getString("mtport");
                }
            });
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            mtPortList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return mtPortList;
    }

    public List<UserParamCategory> getUserParamCategoryList() throws Exception {
        List<UserParamCategory> categoryList;
        try {
            categoryList = jdbcTemplate.query(SQL_GET_USERPARAM_CATEGORY_LIST, new RowMapper<UserParamCategory>() {
                @Override
                public UserParamCategory mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                    UserParamCategory category = new UserParamCategory();
                    category.setParamCategoryCode(resultSet.getString("code"));
                    category.setDescription(resultSet.getString("description"));
                    return category;
                }
            });
        } catch (Exception e) {
            throw e;
        }
        return categoryList;
    }


    @Transactional(readOnly = true)
    public Status getStatus(String code) {
        Status status = null;
        try {
            status = jdbcTemplate.queryForObject(SQL_FIND_STATUS, new Object[]{code}, new RowMapper<Status>() {
                @Override
                public Status mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Status status = new Status();

                    try {
                        status.setStatusCode(rs.getString("statuscode"));
                    } catch (Exception e) {
                        status.setStatusCode(null);
                    }

                    try {
                        status.setDescription(rs.getString("description"));
                    } catch (Exception e) {
                        status.setDescription(null);
                    }

                    return status;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            status = null;
        } catch (Exception e) {
            throw e;
        }
        return status;
    }

    /**
     * @Author Yasas
     * @CreatedTime 2022-08-17 10:18:05 AM
     * @Version V1.00
     * @MethodName getActiveModelList
     * @MethodParams [status]
     * @MethodDescription - This method return the model list
     */

    @Transactional(readOnly = true)
    public List<Model> getActiveModelList(String status) throws Exception {
        List<Model> modelBeanList;
        try {
            List<Map<String, Object>> modelList = jdbcTemplate.queryForList(SQL_GET_MODEL_LIST_BY_STATUS, status);
            modelBeanList = modelList.stream().map((record) -> {
                Model modelBean = new Model();
                modelBean.setId(record.get("id").toString());
                modelBean.setModel(record.get("model").toString());
                return modelBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            modelBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return modelBeanList;
    }

    /**
     * @Author Yasas
     * @CreatedTime 2022-08-17 10:48:05 AM
     * @Version V1.00
     * @MethodName getActiveStateList
     * @MethodParams [status]
     * @MethodDescription - This method return the state list
     */

    @Transactional(readOnly = true)
    public List<State> getActiveStateList(String status) throws Exception {
        List<State> stateBeanList;
        try {
            List<Map<String, Object>> stateList = jdbcTemplate.queryForList(SQL_GET_STATE_LIST_BY_STATUS, status);
            stateBeanList = stateList.stream().map((record) -> {
                State stateBean = new State();
                stateBean.setState_id(record.get("state_id").toString());
                stateBean.setState_name(record.get("state_name").toString());
                return stateBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            stateBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return stateBeanList;
    }

    /**
     * @Author Yasas
     * @CreatedTime 2022-08-17 10:48:05 AM
     * @Version V1.00
     * @MethodName getActiveStateList
     * @MethodParams [status]
     * @MethodDescription - This method return the state list
     */

    @Transactional(readOnly = true)
    public List<FailureType> getActiveFailureTypeList(String status) throws Exception {
        List<FailureType> failureTypeBeanList;
        try {
            List<Map<String, Object>> failureTypeList = jdbcTemplate.queryForList(SQL_GET_FAILURE_TYPE_LIST_BY_STATUS, status);
            failureTypeBeanList = failureTypeList.stream().map((record) -> {
                FailureType failureTypeBean = new FailureType();
                failureTypeBean.setCode(record.get("CODE").toString());
                failureTypeBean.setDescription(record.get("DESCRIPTION").toString());
                return failureTypeBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            failureTypeBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return failureTypeBeanList;
    }

    /**
     * @Author Yasas
     * @CreatedTime 2022-08-17 10:48:05 AM
     * @Version V1.00
     * @MethodName getActiveStateList
     * @MethodParams [status]
     * @MethodDescription - This method return the state list
     */

    @Transactional(readOnly = true)
    public List<FailureArea> getActiveFailureAreaList(String status) throws Exception {
        List<FailureArea> failureAreaBeanList;
        try {
            List<Map<String, Object>> failureAreaList = jdbcTemplate.queryForList(SQL_GET_FAILURE_AREA_LIST_BY_STATUS, status);
            failureAreaBeanList = failureAreaList.stream().map((record) -> {
                FailureArea failureAreaBean = new FailureArea();
                failureAreaBean.setCode(record.get("CODE").toString());
                failureAreaBean.setDescription(record.get("DESCRIPTION").toString());
                return failureAreaBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            failureAreaBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return failureAreaBeanList;
    }

    /**
     * @Author Yasas
     * @CreatedTime 2022-08-17 10:48:05 AM
     * @Version V1.00
     * @MethodName getActiveStateList
     * @MethodParams [status]
     * @MethodDescription - This method return the state list
     */

    @Transactional(readOnly = true)
    public List<RepairType> getActiveRepairTypeList(String status) throws Exception {
        List<RepairType> repairTypeBeanList;
        try {
            List<Map<String, Object>> failureTypeList = jdbcTemplate.queryForList(SQL_GET_REPAIR_TYPE_LIST_BY_STATUS, status);
            repairTypeBeanList = failureTypeList.stream().map((record) -> {
                RepairType repairTypeBean = new RepairType();
                repairTypeBean.setCode(record.get("CODE").toString());
                repairTypeBean.setDescription(record.get("DESCRIPTION").toString());
                return repairTypeBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            repairTypeBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return repairTypeBeanList;
    }

    /**
     * @Author Yasas
     * @CreatedTime 2022-08-17 10:48:05 AM
     * @Version V1.00
     * @MethodName getActiveStateList
     * @MethodParams [status]
     * @MethodDescription - This method return the state list
     */

    @Transactional(readOnly = true)
    public List<Supplier> getActiveSupplierList(String status) throws Exception {
        List<Supplier> supplierBeanList;
        try {
            List<Map<String, Object>> supplierList = jdbcTemplate.queryForList(SQL_GET_SUPPLIER_LIST_BY_STATUS, status);
            supplierBeanList = supplierList.stream().map((record) -> {
                Supplier supplierBean = new Supplier();
                supplierBean.setSupplierCode(record.get("supplier_code").toString());
                supplierBean.setSupplierName(record.get("supplier_name").toString());
                return supplierBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            supplierBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return supplierBeanList;
    }



}
