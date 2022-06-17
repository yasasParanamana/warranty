package com.oxcentra.rdbsms.repository.common;

import com.oxcentra.rdbsms.bean.common.*;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.usermgt.userrole.UserRoleBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.common.CommonPasswordParam;
import com.oxcentra.rdbsms.mapping.comparisonfield.Comparisonfield;
import com.oxcentra.rdbsms.mapping.deliverystatus.DeliveryStatus;
import com.oxcentra.rdbsms.mapping.department.Department;
import com.oxcentra.rdbsms.mapping.department.Priority;
import com.oxcentra.rdbsms.mapping.smschannel.SmsChannel;
import com.oxcentra.rdbsms.mapping.smstemplate.SmsOutputTemplate;
import com.oxcentra.rdbsms.mapping.telco.Telco;
import com.oxcentra.rdbsms.mapping.txntype.TxnType;
import com.oxcentra.rdbsms.mapping.usermgt.UserRole;
import com.oxcentra.rdbsms.mapping.usermgt.UserRoleType;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import com.oxcentra.rdbsms.util.varlist.StatusVarList;
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
    private final String SQL_USERPARAM_BY_PARAMCODE_AND_USERROLETYPE = "select value from PASSWORDPARAM where passwordparam = ? and userroletype = ?";
    private final String SQL_USERROLE_STATUS_BY_USERROLECODE = "select status from USERROLE where userrolecode=?";
    private final String SQL_INSERT_AUDITTRACE = "insert into WEB_SYSTEMAUDIT(USERROLE , section , page , task , ip , remarks , field , oldvalue , newvalue, lastupdateduser, DESCRIPTION) values(? , ? , ? , ? , ? , ? , ? , ? , ?, ?, ?)";
    private final String SQL_GET_STATUS_LIST_BY_CATEGORY = "SELECT STATUSCODE,DESCRIPTION FROM STATUS WHERE STATUSCATEGORY = ?";
    private final String SQL_GET_ACTIVE_DEACTIVE_STATUS_LIST = "SELECT STATUSCODE,DESCRIPTION FROM STATUS WHERE STATUSCODE IN (?,?)";
    private final String SQL_INSERT_TEMPAUTH_RECORD = "INSERT INTO WEB_TMPAUTHREC (PAGE,TASK,STATUS,KEY1,KEY2,KEY3,KEY4,KEY5,KEY6,KEY7,KEY8,KEY9,KEY10,KEY11,KEY12,KEY13,KEY14,KEY15,KEY16,KEY17,KEY18,KEY19,KEY20,TMPRECORD,CREATEDTIME,LASTUPDATEDTIME,LASTUPDATEDUSER) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String SQL_CHECK_PAGE_IS_DUALAUTHENTICATE = "SELECT COUNT(PAGECODE) FROM WEB_PAGE WHERE CFLAG=1 AND PAGECODE=?";
    private final String SQL_GET_DUALAUTH_RECORD = "SELECT PAGE,TASK,STATUS,KEY1,KEY2,KEY3,KEY4,KEY5,KEY6,KEY7,KEY8,KEY9,KEY10,KEY11,KEY12,KEY13,KEY14,KEY15,KEY16,KEY17,KEY18,KEY19,KEY20,TMPRECORD,CREATEDTIME,LASTUPDATEDTIME,LASTUPDATEDUSER FROM WEB_TMPAUTHREC WHERE ID = ?";
    private final String SQL_UPDATE_DUALAUTH_RECORD = "UPDATE WEB_TMPAUTHREC SET STATUS=?,LASTUPDATEDUSER=?,LASTUPDATEDTIME=? WHERE ID=?";
    private final String SQL_COUNT_DUALAUTH = "SELECT COUNT(*) FROM WEB_TMPAUTHREC WHERE PAGE=? AND STATUS=? AND KEY1=?";
    private final String SQL_GET_SECTION_LIST = "SELECT SECTIONCODE,DESCRIPTION FROM WEB_SECTION ORDER BY DESCRIPTION ASC";
    private final String SQL_GET_PAGE_LIST = "SELECT PAGECODE,DESCRIPTION FROM WEB_PAGE ORDER BY DESCRIPTION ASC";
    private final String SQL_GET_TASK_LIST = "SELECT TASKCODE,DESCRIPTION FROM WEB_TASK ORDER BY DESCRIPTION ASC";
    private final String SQL_GET_USERROLE_TYPE_LIST = "SELECT USERROLETYPECODE, DESCRIPTION FROM USERROLETYPE";
    private final String SQL_GET_ACTIVE_USERROLE_TYPE_LIST = "SELECT USERROLETYPECODE, DESCRIPTION FROM USERROLETYPE WHERE STATUS=?";
    private final String SQL_GET_COMMON_PASSWORD_PARAM_LIST = "SELECT PARAMCODE, DESCRIPTION, UNIT FROM COMMON_PASSWORDPARAM";
    private final String SQL_GET_USERROLE_LIST = "SELECT USERROLECODE, DESCRIPTION, USERROLETYPE, STATUS FROM USERROLE where STATUS='ACT' ORDER BY USERROLECODE";
    private final String SQL_GET_USERROLETYPE_LIST = "SELECT USERROLETYPECODE, DESCRIPTION FROM USERROLETYPE ORDER BY USERROLETYPECODE";
    private final String SQL_GET_USERROLE_LIST_BY_USERROLECODE = "select userrolecode , description , status , lastupdateduser , lastupdatedtime , createdtime , USERROLETYPE from USERROLE where USERROLETYPE = ?";
    private final String SQL_GET_PRIORITY_LIST = "select prioritylevel,description,status,createdtime,lastupdatedtime,lastupdateduser from PRIORITY where status = ?";
    private final String SQL_GET_TELCO_LIST = "select code, description from TELCO where status = ?";
    private final String SQL_GET_CATEGORY_LIST = "select category, description from CATEGORY where status = ?";
    private final String SQL_GET_MT_PORT_LIST = "SELECT MTPORT FROM SMSMTPORT where status = ?";
    private final String SQL_GET_DEPARTMENT_LIST = "select code,description,status,createdtime,createduser,lastupdatedtime,lastupdateduser from DEPARTMENT where status = ?";
    private final String SQL_GET_SMSCHANNEL_LIST = "select channelcode,description,password,status,createdtime,createduser,lastupdatedtime,lastupdateduser from SMSCHANNEL where status = ?";
    private final String SQL_GET_TXNTYPE_LIST = "select txntype,description,status,createdtime,createduser,lastupdatedtime,lastupdateduser from TXN_TYPE where status = ?";
    private final String SQL_GET_SMSTEMPLATE_LIST = "select templatecode,messageformat,description,status,createdtime,lastupdatedtime,lastupdateduser from SMSOUTPUTTEMPLATE where status = ?";

    private final String SQL_GET_USERPARAM_CATEGORY_LIST = "select * from USERPARAMCATEGORY";
    private final String SQL_FIND_COMPARISON_FIELD = "select * from COMPARISONFIELD";
    private final String SQL_GET_DELIVERY_STATUS_LIST = "select * from DELIVERYSTATUS";

    private final String SQL_FIND_DELIVERY_STATUS = "select statuscode,description from DELIVERYSTATUS d where d.statuscode = ?";
    private final String SQL_FIND_STATUS = "select statuscode,description from STATUS s where s.statuscode = ?";
    private final String SQL_FIND_TXN_TYPE = "select TXNTYPE,DESCRIPTION from TXN_TYPE tx where tx.TXNTYPE = ?";
    private final String SQL_FIND_TELCO = "select code,description from TELCO d where d.code = ?";

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

    @Transactional(readOnly = true)
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
    }

    @Transactional(readOnly = true)
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
    }

    @Transactional(readOnly = true)
    public List<CommonTelcoBean> getTelcoList() throws Exception {
        List<CommonTelcoBean> telcoBeanList;
        try {
            List<Map<String, Object>> telcoList = jdbcTemplate.queryForList(SQL_GET_TELCO_LIST, commonVarList.STATUS_ACTIVE);
            telcoBeanList = telcoList.stream().map((record) -> {
                CommonTelcoBean commonTelcoBean = new CommonTelcoBean();
                commonTelcoBean.setCode(record.get("CODE").toString());
                commonTelcoBean.setDescription(record.get("DESCRIPTION").toString());
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
    public List<Department> getDepartmentList(String statusCode) {
        List<Department> departmentList;
        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(SQL_GET_DEPARTMENT_LIST, statusCode);
            departmentList = resultList.stream().map((record) -> {
                Department department = new Department();
                department.setCode(record.get("code").toString());
                department.setDescription(record.get("description").toString());
                department.setStatus(record.get("status").toString());
                department.setCreatedTime(record.get("createdtime") != null ? (Date) record.get("createdtime") : null);
                department.setCreatedUser(record.get("createduser") != null ? record.get("createduser").toString() : null);
                department.setLastUpdatedTime(record.get("lastupdatedtime") != null ? (Date) record.get("lastupdatedtime") : null);
                department.setLastUpdatedUser(record.get("lastupdateduser") != null ? record.get("lastupdateduser").toString() : null);
                return department;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            departmentList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return departmentList;
    }

    @Transactional(readOnly = true)
    public List<SmsChannel> getSmsChannelList(String statusCode) {
        List<SmsChannel> smsChannelList;
        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(SQL_GET_SMSCHANNEL_LIST, statusCode);
            smsChannelList = resultList.stream().map((record) -> {
                SmsChannel smsChannel = new SmsChannel();
                smsChannel.setChannelCode(record.get("channelcode").toString());
                smsChannel.setDescription(record.get("description").toString());
                smsChannel.setPassword(record.get("password") != null ? record.get("password").toString() : "");
                smsChannel.setStatus(record.get("status").toString());
                smsChannel.setCreatedTime(record.get("createdtime") != null ? (Date) record.get("createdtime") : null);
                smsChannel.setCreatedUser(record.get("createduser") != null ? record.get("createduser").toString() : null);
                smsChannel.setLastUpdatedTime(record.get("lastupdatedtime") != null ? (Date) record.get("lastupdatedtime") : null);
                smsChannel.setLastUpdatedUser(record.get("lastupdateduser") != null ? record.get("lastupdateduser").toString() : null);
                return smsChannel;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            smsChannelList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return smsChannelList;
    }

    @Transactional(readOnly = true)
    public List<CommonCategoryBean> getCategoryList(String statusCode) throws Exception {
        List<CommonCategoryBean> categoryBeanList;
        try {
            List<Map<String, Object>> categoryList = jdbcTemplate.queryForList(SQL_GET_CATEGORY_LIST, statusCode);
            categoryBeanList = categoryList.stream().map((record) -> {
                CommonCategoryBean commonCategoryBean = new CommonCategoryBean();
                commonCategoryBean.setCategory(record.get("CATEGORY").toString());
                commonCategoryBean.setDescription(record.get("DESCRIPTION").toString());
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
                    return resultSet.getString("MTPORT");
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
                    category.setParamCategoryCode(resultSet.getString("CODE"));
                    category.setDescription(resultSet.getString("DESCRIPTION"));
                    return category;
                }
            });
        } catch (Exception e) {
            throw e;
        }
        return categoryList;
    }

    public List<DeliveryStatus> getDeliveryStatusList() throws Exception {
        List<DeliveryStatus> deliveryStatusList;
        try {
            deliveryStatusList = jdbcTemplate.query(SQL_GET_DELIVERY_STATUS_LIST, new RowMapper<DeliveryStatus>() {
                @Override
                public DeliveryStatus mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                    DeliveryStatus deliveryStatus = new DeliveryStatus();
                    deliveryStatus.setCode(resultSet.getString("STATUSCODE"));
                    deliveryStatus.setDescription(resultSet.getString("DESCRIPTION"));
                    return deliveryStatus;
                }
            });
        } catch (Exception e) {
            throw e;
        }
        return deliveryStatusList;
    }

    @Transactional(readOnly = true)
    public DeliveryStatus getDeliveryStatus(String code) {
        DeliveryStatus deliveryStatus = null;
        try {
            deliveryStatus = jdbcTemplate.queryForObject(SQL_FIND_DELIVERY_STATUS, new Object[]{code}, new RowMapper<DeliveryStatus>() {
                @Override
                public DeliveryStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DeliveryStatus deliveryStatus = new DeliveryStatus();

                    try {
                        deliveryStatus.setCode(rs.getString("STATUSCODE"));
                    } catch (Exception e) {
                        deliveryStatus.setCode(null);
                    }

                    try {
                        deliveryStatus.setDescription(rs.getString("DESCRIPTION"));
                    } catch (Exception e) {
                        deliveryStatus.setDescription(null);
                    }

                    return deliveryStatus;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            deliveryStatus = null;
        } catch (Exception e) {
            throw e;
        }
        return deliveryStatus;
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
                        status.setStatusCode(rs.getString("STATUSCODE"));
                    } catch (Exception e) {
                        status.setStatusCode(null);
                    }

                    try {
                        status.setDescription(rs.getString("DESCRIPTION"));
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

    @Transactional(readOnly = true)
    public List<TxnType> getTxnTypeList(String statusCode) {
        List<TxnType> txnTypeList;
        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(SQL_GET_TXNTYPE_LIST, statusCode);
            txnTypeList = resultList.stream().map((record) -> {
                TxnType txnType = new TxnType();
                txnType.setTxntype(record.get("txntype").toString());
                txnType.setDescription(record.get("description").toString());
                txnType.setStatus(record.get("status").toString());
                txnType.setCreatedTime(record.get("createdtime") != null ? (Date) record.get("createdtime") : null);
                txnType.setCreatedUser(record.get("createduser") != null ? record.get("createduser").toString() : null);
                txnType.setLastUpdatedTime(record.get("lastupdatedtime") != null ? (Date) record.get("lastupdatedtime") : null);
                txnType.setLastUpdatedUser(record.get("lastupdateduser") != null ? record.get("lastupdateduser").toString() : null);
                return txnType;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            txnTypeList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return txnTypeList;
    }

    @Transactional(readOnly = true)
    public List<SmsOutputTemplate> getTemplateList(String statusCode) {
        List<SmsOutputTemplate> outputTemplateList;
        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(SQL_GET_SMSTEMPLATE_LIST, statusCode);
            outputTemplateList = resultList.stream().map((record) -> {
                SmsOutputTemplate smsOutputTemplate = new SmsOutputTemplate();
                smsOutputTemplate.setTemplatecode(record.get("templatecode").toString());
                smsOutputTemplate.setDescription(record.get("description").toString());
                smsOutputTemplate.setMessageformat(record.get("messageformat").toString());
                smsOutputTemplate.setStatus(record.get("status").toString());
                smsOutputTemplate.setCreatedTime(record.get("createdtime") != null ? (Date) record.get("createdtime") : null);
                smsOutputTemplate.setLastUpdatedTime(record.get("lastupdatedtime") != null ? (Date) record.get("lastupdatedtime") : null);
                smsOutputTemplate.setLastUpdatedUser(record.get("lastupdateduser") != null ? record.get("lastupdateduser").toString() : null);
                return smsOutputTemplate;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            outputTemplateList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return outputTemplateList;
    }


    @Transactional(readOnly = true)
    public List<Comparisonfield> getComparisonfieldList() {
        List<Comparisonfield> comparisonfieldList;
        try {
            comparisonfieldList = jdbcTemplate.query(SQL_FIND_COMPARISON_FIELD, new RowMapper<Comparisonfield>() {
                @Override
                public Comparisonfield mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                    Comparisonfield comparisonfield = new Comparisonfield();
                    comparisonfield.setComparisonfieldcode(resultSet.getString("COMPARISONFIELDCODE"));
                    comparisonfield.setDescription(resultSet.getString("DESCRIPTION"));
                    return comparisonfield;
                }
            });
        } catch (Exception e) {
            throw e;
        }
        return comparisonfieldList;
    }

    @Transactional(readOnly = true)
    public TxnType getTxnType(String code) throws Exception {
        TxnType txnType = null;
        try {
            txnType = jdbcTemplate.queryForObject(SQL_FIND_TXN_TYPE, new Object[]{code}, new RowMapper<TxnType>() {
                @Override
                public TxnType mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TxnType txnType = new TxnType();

                    try {
                        txnType.setTxntype(rs.getString("TXNTYPE"));
                    } catch (Exception e) {
                        txnType.setTxntype(null);
                    }

                    try {
                        txnType.setDescription(rs.getString("DESCRIPTION"));
                    } catch (Exception e) {
                        txnType.setDescription(null);
                    }

                    return txnType;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            txnType = null;
        } catch (Exception e) {
            throw e;
        }
        return txnType;
    }

    @Transactional(readOnly = true)
    public Telco getTelco(String code) {
        Telco telco = null;
        try {
            telco = jdbcTemplate.queryForObject(SQL_FIND_TELCO, new Object[]{code}, new RowMapper<Telco>() {
                @Override
                public Telco mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Telco telco = new Telco();

                    try {
                        telco.setCode(rs.getString("CODE"));
                    } catch (Exception e) {
                        telco.setCode(null);
                    }

                    try {
                        telco.setDescription(rs.getString("DESCRIPTION"));
                    } catch (Exception e) {
                        telco.setDescription(null);
                    }

                    return telco;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            telco = null;
        } catch (Exception e) {
            throw e;
        }
        return telco;
    }

}
