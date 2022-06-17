package com.oxcentra.rdbsms.repository.sysconfigmgt.templatemgt;

import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.templatemgt.SMSTemplateInputBean;
import com.oxcentra.rdbsms.mapping.templatemgt.TemplateMgt;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.StatusVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Scope("prototype")
public class TemplateMgtRepository {
    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from SMSOUTPUTTEMPLATE u left outer join STATUS s on s.statuscode=u.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from WEB_TMPAUTHREC wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";
    private final String SQL_INSERT_SMS_TEMPLATE = "insert into SMSOUTPUTTEMPLATE(templatecode,description,status,messageformat,createdtime,createduser,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?,?) ";
    private final String SQL_FIND_TEMPLATE = "select * from SMSOUTPUTTEMPLATE u where u.templatecode = ?";
    private final String SQL_UPDATE_TEMPLATE = "update SMSOUTPUTTEMPLATE d set description= ? ,status= ? ,messageformat= ? ,lastupdateduser= ? ,lastupdatedtime= ?  where d.templatecode = ?";
    private final String SQL_DELETE_TEMPLATE = "delete from SMSOUTPUTTEMPLATE where templatecode = ?";

    @Transactional(readOnly = true)
    public long getDataCount(SMSTemplateInputBean smsTemplateInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(smsTemplateInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TemplateMgt> getTemplateSearchResultList(SMSTemplateInputBean smsTemplateInputBean) {
        List<TemplateMgt> templateMgtList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(smsTemplateInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (smsTemplateInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by u.lastupdatedtime desc ";
            } else {
                sortingStr = " order by u.lastupdatedtime " + smsTemplateInputBean.sortDirections.get(0);
            }

            String sql =
                    " select u.templatecode as code, u.description, u.messageformat, s.description as statusdescription," +
                            " u.lastupdatedtime, u.createdtime, u.lastupdateduser, u.createduser from SMSOUTPUTTEMPLATE u " +
                            " left outer join STATUS s on s.statuscode = u.status " +
                            " where " + dynamicClause + sortingStr +
                            " limit " + smsTemplateInputBean.displayLength + " offset " + smsTemplateInputBean.displayStart;

            templateMgtList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                TemplateMgt templateMgt = new TemplateMgt();
                try {
                    templateMgt.setCode(rs.getString("code"));
                } catch (Exception e) {
                    templateMgt.setCode(null);
                }

                try {
                    templateMgt.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    templateMgt.setDescription(null);
                }

                try {
                    templateMgt.setMessageformat(rs.getString("messageformat"));
                } catch (Exception e) {
                    templateMgt.setMessageformat(null);
                }

                try {
                    templateMgt.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    templateMgt.setStatus(null);
                }

                try {
                    templateMgt.setCreatedtime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    templateMgt.setCreatedtime(null);
                }

                try {
                    templateMgt.setLastupdatedtime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    templateMgt.setLastupdatedtime(null);
                }

                try {
                    templateMgt.setCreateduser(rs.getString("createduser"));
                } catch (Exception e) {
                    templateMgt.setCreatedtime(null);
                }

                try {
                    templateMgt.setLastupdateduser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    templateMgt.setLastupdatedtime(null);
                }

                return templateMgt;
            });
        } catch (EmptyResultDataAccessException ex) {
            return templateMgtList;
        } catch (Exception e) {
            throw e;
        }
        return templateMgtList;
    }

    private StringBuilder setDynamicClause(SMSTemplateInputBean smsTemplateInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (smsTemplateInputBean.getCode() != null && !smsTemplateInputBean.getCode().isEmpty()) {
                dynamicClause.append("and lower(u.templatecode) like lower('%").append(smsTemplateInputBean.getCode()).append("%')");
            }

            if (smsTemplateInputBean.getDescription() != null && !smsTemplateInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(u.description) like lower('%").append(smsTemplateInputBean.getDescription()).append("%')");
            }

            if (smsTemplateInputBean.getStatus() != null && !smsTemplateInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and u.status = '").append(smsTemplateInputBean.getStatus()).append("'");
            }

            if (smsTemplateInputBean.getMessageformat() != null && !smsTemplateInputBean.getMessageformat().isEmpty()) {
                dynamicClause.append("and lower(u.messageformat) like lower('%").append(smsTemplateInputBean.getMessageformat()).append("%')");
            }

        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    @Transactional(readOnly = true)
    public TemplateMgt getSMSTemplate(String code) {
        TemplateMgt templateMgt = null;
        try {
            templateMgt = jdbcTemplate.queryForObject(SQL_FIND_TEMPLATE, new Object[]{code}, new RowMapper<TemplateMgt>() {
                @Override
                public TemplateMgt mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TemplateMgt templateMgt = new TemplateMgt();

                    try {
                        templateMgt.setCode(rs.getString("templatecode"));
                    } catch (Exception e) {
                        templateMgt.setCode(null);
                    }

                    try {
                        templateMgt.setDescription(rs.getString("description"));
                    } catch (Exception e) {
                        templateMgt.setDescription(null);
                    }

                    try {
                        templateMgt.setMessageformat(rs.getString("messageformat"));
                    } catch (Exception e) {
                        templateMgt.setMessageformat(null);
                    }

                    try {
                        templateMgt.setCreatedtime(rs.getDate("createdtime"));
                    } catch (Exception e) {
                        templateMgt.setCreatedtime(null);
                    }

                    try {
                        templateMgt.setLastupdatedtime(rs.getDate("lastupdatedtime"));
                    } catch (Exception e) {
                        templateMgt.setLastupdatedtime(null);
                    }

                    try {
                        templateMgt.setCreateduser(rs.getString("createduser"));
                    } catch (Exception e) {
                        templateMgt.setCreateduser(null);
                    }

                    try {
                        templateMgt.setLastupdateduser(rs.getString("lastupdateduser"));
                    } catch (Exception e) {
                        templateMgt.setCreateduser(null);
                    }

                    try {
                        templateMgt.setLastupdateduser(rs.getString("lastupdateduser"));
                    } catch (Exception e) {
                        templateMgt.setCreateduser(null);
                    }

                    try {
                        templateMgt.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        templateMgt.setStatus(null);
                    }

                    return templateMgt;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            templateMgt = null;
        } catch (Exception e) {
            throw e;
        }
        return templateMgt;
    }

    @Transactional
    public String deleteSMSTemplate(String code) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_TEMPLATE, code);
            if (count < 0) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
            throw cve;
        } catch (Exception e) {
            throw e;
        }
        return message;
    }

    public long getDataCountDual(SMSTemplateInputBean smsTemplateInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(smsTemplateInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.SMS_TEMPLATE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    private StringBuilder setDynamicClauseDual(SMSTemplateInputBean smsTemplateInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (smsTemplateInputBean.getCode() != null && !smsTemplateInputBean.getCode().isEmpty()) {
                dynamicClause.append("and lower(wta.key1) like lower('%").append(smsTemplateInputBean.getCode()).append("%')");
            }

            if (smsTemplateInputBean.getDescription() != null && !smsTemplateInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(smsTemplateInputBean.getDescription()).append("%')");
            }

            if (smsTemplateInputBean.getMessageformat() != null && !smsTemplateInputBean.getMessageformat().isEmpty()) {
                dynamicClause.append("and lower(wta.tmprecord) like lower('%").append(smsTemplateInputBean.getMessageformat()).append("%')");
            }

            if (smsTemplateInputBean.getStatus() != null && !smsTemplateInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key3 = '").append(smsTemplateInputBean.getStatus()).append("'");
            }

        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getSmsTemplateSearchResultsDual(SMSTemplateInputBean smsTemplateInputBean) {
        List<TempAuthRec> tempAuthRecList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(smsTemplateInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (smsTemplateInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + smsTemplateInputBean.sortDirections.get(0);
            }

            String sql =
                    " select wta.id,wta.key1,wta.key2,wta.key3,wta.tmprecord,s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                            " from WEB_TMPAUTHREC wta" +
                            " left outer join STATUS s on s.statuscode = wta.key4 " +
                            " left outer join WEB_TASK t on t.taskcode = wta.task " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                            " limit " + smsTemplateInputBean.displayLength + " offset " + smsTemplateInputBean.displayStart;

            tempAuthRecList = jdbcTemplate.query(sql, new Object[]{PageVarList.SMS_TEMPLATE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRec = new TempAuthRec();
                try {
                    tempAuthRec.setId(rs.getString("id"));
                } catch (Exception e) {
                    tempAuthRec.setId(null);
                }

                try {
                    //set category code
                    tempAuthRec.setKey1(rs.getString("key1"));
                } catch (Exception e) {
                    tempAuthRec.setKey1(null);
                }

                try {
                    //set description
                    tempAuthRec.setKey2(rs.getString("key2"));
                } catch (Exception e) {
                    tempAuthRec.setKey2(null);
                }

                try {
                    //set bulk enable
                    tempAuthRec.setKey3(commonRepository.getStatus(rs.getString("key3")).getDescription());
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
                }

                try {
                    //set status
                    tempAuthRec.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    //set priority
                    tempAuthRec.setKey4(rs.getString("tmprecord"));
                } catch (Exception e) {
                    tempAuthRec.setKey5(null);
                }

                try {
                    tempAuthRec.setCreatedTime(rs.getString("createdtime"));
                } catch (Exception e) {
                    tempAuthRec.setCreatedTime(null);
                }

                try {
                    tempAuthRec.setTask(rs.getString("taskdescription"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    tempAuthRec.setLastUpdatedTime(rs.getString("lastupdatedtime"));
                } catch (Exception e) {
                    tempAuthRec.setLastUpdatedTime(null);
                }

                try {
                    tempAuthRec.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    tempAuthRec.setLastUpdatedUser(null);
                }
                return tempAuthRec;
            });
        } catch (Exception e) {
            throw e;
        }
        return tempAuthRecList;
    }

    @Transactional
    public String updateSMSTemplate(SMSTemplateInputBean smsTemplateInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_TEMPLATE,
                    smsTemplateInputBean.getDescription(),
                    smsTemplateInputBean.getStatus(),
                    smsTemplateInputBean.getMessageformat(),
                    smsTemplateInputBean.getLastupdateduser(),
                    smsTemplateInputBean.getLastupdatedtime(),
                    smsTemplateInputBean.getCode());
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    public String insertSMSTemplate(SMSTemplateInputBean smsTemplateInputBean) {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_SMS_TEMPLATE, smsTemplateInputBean.getCode(),
                    smsTemplateInputBean.getDescription(),
                    smsTemplateInputBean.getStatus(),
                    smsTemplateInputBean.getMessageformat(),
                    smsTemplateInputBean.getCreatedtime(),
                    smsTemplateInputBean.getCreateduser(),
                    smsTemplateInputBean.getLastupdatedtime(),
                    smsTemplateInputBean.getLastupdateduser());

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (DuplicateKeyException ex) {
            throw ex;
        } catch (Exception e) {
            throw e;
        }
        return message;
    }
}
