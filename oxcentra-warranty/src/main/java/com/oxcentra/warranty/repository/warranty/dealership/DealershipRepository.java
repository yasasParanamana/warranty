package com.oxcentra.warranty.repository.warranty.dealership;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.dealership.DealershipInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.warranty.Dealership;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
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
import java.util.Date;
import java.util.List;

@Repository
@Scope("prototype")
public class DealershipRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from reg_dealership rs left outer join status s on s.statuscode=rs.status where ";
    private final String SQL_INSERT_SECTION = "insert into reg_dealership(dealership_code,dealership_name,dealership_phone,dealership_email,dealership_address,status,createduser,createdtime,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?,?,?,?) ";
    private final String SQL_UPDATE_SECTION = "update reg_dealership rs set rs.dealership_name = ? , rs.dealership_phone = ?, rs.dealership_email=?, rs.dealership_address=?, rs.status=?, rs.lastupdateduser=?, rs.lastupdatedtime=? where rs.dealership_code = ?";
    private final String SQL_FIND_SECTION = "select dealership_code,dealership_name,dealership_phone,dealership_email,dealership_address,status,createduser,createdtime,lastupdatedtime,lastupdateduser from reg_dealership rs where rs.dealership_code = ?";
    private final String SQL_DELETE_SECTION = "delete from reg_dealership where dealership_code = ?";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from web_tmpauthrec wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";


    @Transactional(readOnly = true)
    public long getDataCount(DealershipInputBean dealershipInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(dealershipInputBean, dynamicClause);
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
    public List<Dealership> getDealershipSearchList(DealershipInputBean dealershipInputBean) {
        List<Dealership> dealershipList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(dealershipInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (dealershipInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by rs.lastupdatedtime desc ";
            } else {
                sortingStr = " order by rs.lastupdatedtime " + dealershipInputBean.sortDirections.get(0);
            }

            String sql =
                    " select rs.dealership_code as dealership_code" +
                            ",rs.dealership_name as dealership_name" +
                            ",rs.dealership_phone as dealership_phone" +
                            ",rs.dealership_email as dealership_email" +
                            ",rs.dealership_address as dealership_address" +
                            ",rs.status as status" +
                            ",s.description as statusdescription" +
                            ",rs.createdtime as createdtime" +
                            ",rs.createduser as createduser" +
                            ",rs.lastupdatedtime as lastupdatedtime" +
                            ",rs.lastupdateduser as lastupdateduser from reg_dealership rs " +
                            "left outer join status s on s.statuscode = rs.status " +
                            "where " + dynamicClause.toString() + sortingStr +
                            " limit " + dealershipInputBean.displayLength + " offset " + dealershipInputBean.displayStart;

            dealershipList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Dealership section = new Dealership();


                try {
                    section.setDealershipCode(rs.getString("dealership_code"));
                } catch (Exception e) {
                    section.setDealershipCode(null);
                }

                try {
                    section.setDealershipName(rs.getString("dealership_name"));
                } catch (Exception e) {
                    section.setDealershipName(null);
                }

                try {
                    section.setDealershipPhone(rs.getString("dealership_phone"));
                } catch (Exception e) {
                    section.setDealershipPhone(null);
                }

                try {
                    section.setDealershipEmail(rs.getString("dealership_email"));
                } catch (Exception e) {
                    section.setDealershipEmail(null);
                }

                try {
                    section.setDealershipAddress(rs.getString("dealership_address"));
                } catch (Exception e) {
                    section.setDealershipAddress(null);
                }

                try {
                    section.setStatus(rs.getString("statusdescription"));
                } catch (Exception e) {
                    section.setStatus(null);
                }

                try {
                    section.setCreatedTime(rs.getDate("createdTime"));
                } catch (Exception e) {
                    section.setCreatedTime(null);
                }

                try {
                    section.setCreatedUser(rs.getString("createdUser"));
                } catch (Exception e) {
                    section.setCreatedUser(null);
                }

                try {
                    section.setLastUpdatedTime(rs.getDate("lastUpdatedTime"));
                } catch (Exception e) {
                    section.setLastUpdatedTime(null);
                }

                try {
                    section.setLastUpdatedUser(rs.getString("lastUpdatedUser"));
                } catch (Exception e) {
                    section.setLastUpdatedUser(null);
                }

                return section;
            });
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (Exception e) {
            throw e;
        }
        return dealershipList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(DealershipInputBean DealershipInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(DealershipInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.SUPPLIER_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getSectionSearchResultsDual(DealershipInputBean dealershipInputBean) {
        List<TempAuthRec> sectionDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(dealershipInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (dealershipInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + dealershipInputBean.sortDirections.get(0);
            }
            String sql = "" +
                    " select wta.dealership_code,wta.key1,wta.key2,wta.key3,wta.key4, s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                    " from web_tmpauthrec wta" +
                    " left outer join status s on s.statuscode = wta.key3 " +
                    " left outer join web_task t on t.taskcode = wta.task " +
                    " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                    " limit " + dealershipInputBean.displayLength + " offset " + dealershipInputBean.displayStart;

            sectionDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.SECTION_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRec = new TempAuthRec();

                try {
                    tempAuthRec.setId(rs.getString("dealership_code"));
                } catch (Exception e) {
                    tempAuthRec.setId(null);
                }

                try {
                    tempAuthRec.setKey1(rs.getString("key1"));
                } catch (Exception e) {
                    tempAuthRec.setKey1(null);
                }

                try {
                    tempAuthRec.setKey2(rs.getString("key2"));
                } catch (Exception e) {
                    tempAuthRec.setKey2(null);
                }

                try {
                    tempAuthRec.setKey3(rs.getString("statusdescription"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
                }

                try {
                    tempAuthRec.setKey4(rs.getString("key4"));
                } catch (Exception e) {
                    tempAuthRec.setKey4(null);
                }

                try {
                    tempAuthRec.setTask(rs.getString("taskdescription"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    tempAuthRec.setCreatedTime(rs.getString("createdTime"));
                } catch (Exception e) {
                    tempAuthRec.setCreatedTime(null);
                }

                try {
                    tempAuthRec.setLastUpdatedTime(rs.getString("lastUpdatedTime"));
                } catch (Exception e) {
                    tempAuthRec.setLastUpdatedTime(null);
                }

                try {
                    tempAuthRec.setLastUpdatedUser(rs.getString("lastUpdatedUser"));
                } catch (Exception e) {
                    tempAuthRec.setLastUpdatedUser(null);
                }
                return tempAuthRec;
            });
        } catch (Exception e) {
            throw e;
        }
        return sectionDualList;
    }

    @Transactional
    public String insertDealership(DealershipInputBean DealershipInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_SECTION,
                    DealershipInputBean.getDealershipCode(),
                    DealershipInputBean.getDealershipName(),
                    DealershipInputBean.getDealershipPhone(),
                    DealershipInputBean.getDealershipEmail(),
                    DealershipInputBean.getDealershipAddress(),
                    DealershipInputBean.getStatus(),
                    DealershipInputBean.getCreatedUser(),
                    DealershipInputBean.getCreatedTime(),
                    DealershipInputBean.getLastUpdatedTime(),
                    DealershipInputBean.getLastUpdatedUser());
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

    @Transactional(readOnly = true)
    public Dealership getDealership(String dealershipCode) {
        Dealership dealership = null;
        try {
            dealership = jdbcTemplate.queryForObject(SQL_FIND_SECTION, new Object[]{dealershipCode}, new RowMapper<Dealership>() {
                @Override
                public Dealership mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Dealership dealership = new Dealership();

                    try {
                        dealership.setDealershipCode(rs.getString("dealership_code"));
                    } catch (Exception e) {
                        dealership.setDealershipCode(null);
                    }

                    try {
                        dealership.setDealershipName(rs.getString("dealership_name"));
                    } catch (Exception e) {
                        dealership.setDealershipName(null);
                    }

                    try {
                        dealership.setDealershipPhone(rs.getString("dealership_phone"));
                    } catch (Exception e) {
                        dealership.setDealershipPhone(null);
                    }

                    try {
                        dealership.setDealershipEmail(rs.getString("dealership_email"));
                    } catch (Exception e) {
                        dealership.setDealershipEmail(null);
                    }

                    try {
                        dealership.setDealershipAddress(rs.getString("dealership_address"));
                    } catch (Exception e) {
                        dealership.setDealershipAddress(null);
                    }

                    try {
                        dealership.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        dealership.setStatus(null);
                    }

                    try {
                        dealership.setCreatedTime(new Date(rs.getDate("createdTime").getTime()));
                    } catch (Exception e) {
                        dealership.setCreatedTime(null);
                    }

                    try {
                        dealership.setCreatedUser(rs.getString("createdUser"));
                    } catch (SQLException e) {
                        dealership.setCreatedUser(null);
                    }

                    try {
                        dealership.setLastUpdatedTime(new Date(rs.getDate("lastUpdatedTime").getTime()));
                    } catch (SQLException e) {
                        dealership.setLastUpdatedTime(null);
                    }

                    try {
                        dealership.setLastUpdatedUser(rs.getString("lastUpdatedUser"));
                    } catch (SQLException e) {
                        dealership.setLastUpdatedUser(null);
                    }

                    return dealership;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            dealership = null;
        } catch (Exception e) {
            throw e;
        }
        return dealership;
    }

    @Transactional
    public String updateDealership(DealershipInputBean DealershipInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_SECTION,
                    DealershipInputBean.getDealershipName(),
                    DealershipInputBean.getDealershipPhone(),
                    DealershipInputBean.getDealershipEmail(),
                    DealershipInputBean.getDealershipAddress(),
                    DealershipInputBean.getStatus(),
                    DealershipInputBean.getLastUpdatedUser(),
                    DealershipInputBean.getLastUpdatedTime(),
                    DealershipInputBean.getDealershipCode());

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteDealership(String sectionCode) {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_SECTION, sectionCode);
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

    private StringBuilder setDynamicClause(DealershipInputBean DealershipInputBean, StringBuilder dynamicClause) {

        try {
            if (DealershipInputBean.getDealershipCode() != null && !DealershipInputBean.getDealershipCode().isEmpty()) {
                dynamicClause.append(" 1=0 ");
                dynamicClause.append("or lower(rs.dealership_code) like lower('%").append(DealershipInputBean.getDealershipCode()).append("%')");
                dynamicClause.append("or lower(rs.dealership_name) like lower('%").append(DealershipInputBean.getDealershipCode()).append("%')");
                dynamicClause.append("or lower(rs.dealership_phone) like lower('%").append(DealershipInputBean.getDealershipCode()).append("%')");
                dynamicClause.append("or lower(rs.dealership_email) like lower('%").append(DealershipInputBean.getDealershipCode()).append("%')");
                dynamicClause.append("or lower(rs.dealership_address) like lower('%").append(DealershipInputBean.getDealershipCode()).append("%')");
                dynamicClause.append("or rs.status = '").append(DealershipInputBean.getStatus()).append("'");
            }else{
                dynamicClause.append(" 1=1 ");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(DealershipInputBean DealershipInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
//            if (DealershipInputBean.getId() != null && !DealershipInputBean.getId().isEmpty()) {
//                dynamicClause.append("and lower(wta.key1) like lower('%").append(DealershipInputBean.getSectionCode()).append("%')");
//            }

            if (DealershipInputBean.getDealershipName() != null && !DealershipInputBean.getDealershipName().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(DealershipInputBean.getDealershipName()).append("%')");
            }

            if (DealershipInputBean.getStatus() != null && !DealershipInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key3 = '").append(DealershipInputBean.getStatus()).append("'");
            }


        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

}
