package com.oxcentra.warranty.repository.warranty.supplier;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.supplier.SupplierInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.warranty.Supplier;
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
public class SupplierRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from reg_supplier rs left outer join status s on s.statuscode=rs.status where ";
    private final String SQL_INSERT_SECTION = "insert into reg_supplier(supplier_code,supplier_name,supplier_phone,supplier_email,supplier_address,status,createduser,createdtime,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?,?,?,?) ";
    private final String SQL_UPDATE_SECTION = "update reg_supplier rs set rs.supplier_name = ? , rs.supplier_phone = ?, rs.supplier_email=?, rs.supplier_address=?, rs.status=?, rs.lastupdateduser=?, rs.lastupdatedtime=? where rs.supplier_code = ?";
    private final String SQL_FIND_SECTION = "select supplier_code,supplier_name,supplier_phone,supplier_email,supplier_address,status,createduser,createdtime,lastupdatedtime,lastupdateduser from reg_supplier rs where rs.supplier_code = ?";
    private final String SQL_DELETE_SECTION = "delete from reg_supplier where supplier_code = ?";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from web_tmpauthrec wta where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and ";


    @Transactional(readOnly = true)
    public long getDataCount(SupplierInputBean SupplierInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(SupplierInputBean, dynamicClause);
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
    public List<Supplier> getSupplierSearchList(SupplierInputBean supplierInputBean) {
        List<Supplier> supplierList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(supplierInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (supplierInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by rs.lastupdatedtime desc ";
            } else {
                sortingStr = " order by rs.lastupdatedtime " + supplierInputBean.sortDirections.get(0);
            }

            String sql =
                    " select rs.supplier_code as supplier_code" +
                            ",rs.supplier_name as supplier_name" +
                            ",rs.supplier_phone as supplier_phone" +
                            ",rs.supplier_email as supplier_email" +
                            ",rs.supplier_address as supplier_address" +
                            ",rs.status as status" +
                            ",s.description as statusdescription" +
                            ",rs.createdtime as createdtime" +
                            ",rs.createduser as createduser" +
                            ",rs.lastupdatedtime as lastupdatedtime" +
                            ",rs.lastupdateduser as lastupdateduser from reg_supplier rs " +
                            "left outer join status s on s.statuscode = rs.status " +
                            "where " + dynamicClause.toString() + sortingStr +
                            " limit " + supplierInputBean.displayLength + " offset " + supplierInputBean.displayStart;

            supplierList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Supplier section = new Supplier();


                try {
                    section.setSupplierCode(rs.getString("supplier_code"));
                } catch (Exception e) {
                    section.setSupplierCode(null);
                }

                try {
                    section.setSupplierName(rs.getString("supplier_name"));
                } catch (Exception e) {
                    section.setSupplierName(null);
                }

                try {
                    section.setSupplierPhone(rs.getString("supplier_phone"));
                } catch (Exception e) {
                    section.setSupplierPhone(null);
                }

                try {
                    section.setSupplierEmail(rs.getString("supplier_email"));
                } catch (Exception e) {
                    section.setSupplierEmail(null);
                }

                try {
                    section.setSupplierAddress(rs.getString("supplier_address"));
                } catch (Exception e) {
                    section.setSupplierAddress(null);
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
        return supplierList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(SupplierInputBean SupplierInputBean) {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(SupplierInputBean, dynamicClause);
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
    public List<TempAuthRec> getSectionSearchResultsDual(SupplierInputBean supplierInputBean) {
        List<TempAuthRec> sectionDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(supplierInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (supplierInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + supplierInputBean.sortDirections.get(0);
            }
            String sql = "" +
                    " select wta.supplier_code,wta.key1,wta.key2,wta.key3,wta.key4, s.description as statusdescription ,t.description as taskdescription ,wta.createdtime,wta.lastupdatedtime,wta.lastupdateduser " +
                    " from web_tmpauthrec wta" +
                    " left outer join status s on s.statuscode = wta.key3 " +
                    " left outer join web_task t on t.taskcode = wta.task " +
                    " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause + sortingStr +
                    " limit " + supplierInputBean.displayLength + " offset " + supplierInputBean.displayStart;

            sectionDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.SECTION_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRec = new TempAuthRec();

                try {
                    tempAuthRec.setId(rs.getString("supplier_code"));
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
    public String insertSupplier(SupplierInputBean SupplierInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_SECTION,
                    SupplierInputBean.getSupplierCode(),
                    SupplierInputBean.getSupplierName(),
                    SupplierInputBean.getSupplierPhone(),
                    SupplierInputBean.getSupplierEmail(),
                    SupplierInputBean.getSupplierAddress(),
                    SupplierInputBean.getStatus(),
                    SupplierInputBean.getCreatedUser(),
                    SupplierInputBean.getCreatedTime(),
                    SupplierInputBean.getLastUpdatedTime(),
                    SupplierInputBean.getLastUpdatedUser());
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
    public Supplier getSupplier(String supplierCode) {
        Supplier supplier = null;
        try {
            supplier = jdbcTemplate.queryForObject(SQL_FIND_SECTION, new Object[]{supplierCode}, new RowMapper<Supplier>() {
                @Override
                public Supplier mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Supplier supplier = new Supplier();

                    try {
                        supplier.setSupplierCode(rs.getString("supplier_code"));
                    } catch (Exception e) {
                        supplier.setSupplierCode(null);
                    }

                    try {
                        supplier.setSupplierName(rs.getString("supplier_name"));
                    } catch (Exception e) {
                        supplier.setSupplierName(null);
                    }

                    try {
                        supplier.setSupplierPhone(rs.getString("supplier_phone"));
                    } catch (Exception e) {
                        supplier.setSupplierPhone(null);
                    }

                    try {
                        supplier.setSupplierEmail(rs.getString("supplier_email"));
                    } catch (Exception e) {
                        supplier.setSupplierEmail(null);
                    }

                    try {
                        supplier.setSupplierAddress(rs.getString("supplier_address"));
                    } catch (Exception e) {
                        supplier.setSupplierAddress(null);
                    }

                    try {
                        supplier.setStatus(rs.getString("status"));
                    } catch (Exception e) {
                        supplier.setStatus(null);
                    }

                    try {
                        supplier.setCreatedTime(new Date(rs.getDate("createdTime").getTime()));
                    } catch (Exception e) {
                        supplier.setCreatedTime(null);
                    }

                    try {
                        supplier.setCreatedUser(rs.getString("createdUser"));
                    } catch (SQLException e) {
                        supplier.setCreatedUser(null);
                    }

                    try {
                        supplier.setLastUpdatedTime(new Date(rs.getDate("lastUpdatedTime").getTime()));
                    } catch (SQLException e) {
                        supplier.setLastUpdatedTime(null);
                    }

                    try {
                        supplier.setLastUpdatedUser(rs.getString("lastUpdatedUser"));
                    } catch (SQLException e) {
                        supplier.setLastUpdatedUser(null);
                    }

                    return supplier;
                }
            });
        } catch (EmptyResultDataAccessException erse) {
            supplier = null;
        } catch (Exception e) {
            throw e;
        }
        return supplier;
    }

    @Transactional
    public String updateSupplier(SupplierInputBean SupplierInputBean) {
        String message = "";
        try {
            int value = jdbcTemplate.update(SQL_UPDATE_SECTION,
                    SupplierInputBean.getSupplierName(),
                    SupplierInputBean.getSupplierPhone(),
                    SupplierInputBean.getSupplierEmail(),
                    SupplierInputBean.getSupplierAddress(),
                    SupplierInputBean.getStatus(),
                    SupplierInputBean.getLastUpdatedUser(),
                    SupplierInputBean.getLastUpdatedTime(),
                    SupplierInputBean.getSupplierCode());

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional
    public String deleteSupplier(String sectionCode) {
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

    private StringBuilder setDynamicClause(SupplierInputBean SupplierInputBean, StringBuilder dynamicClause) {

        try {
            if (SupplierInputBean.getSupplierCode() != null && !SupplierInputBean.getSupplierCode().isEmpty()) {
                dynamicClause.append(" 1=0 ");
                dynamicClause.append("or lower(rs.supplier_code) like lower('%").append(SupplierInputBean.getSupplierCode()).append("%')");
                dynamicClause.append("or lower(rs.supplier_name) like lower('%").append(SupplierInputBean.getSupplierCode()).append("%')");
                dynamicClause.append("or lower(rs.supplier_phone) like lower('%").append(SupplierInputBean.getSupplierCode()).append("%')");
                dynamicClause.append("or lower(rs.supplier_email) like lower('%").append(SupplierInputBean.getSupplierCode()).append("%')");
                dynamicClause.append("or lower(rs.supplier_address) like lower('%").append(SupplierInputBean.getSupplierCode()).append("%')");
                dynamicClause.append("or rs.status = '").append(SupplierInputBean.getStatus()).append("'");
            }else{
                dynamicClause.append(" 1=1 ");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(SupplierInputBean SupplierInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
//            if (SupplierInputBean.getId() != null && !SupplierInputBean.getId().isEmpty()) {
//                dynamicClause.append("and lower(wta.key1) like lower('%").append(SupplierInputBean.getSectionCode()).append("%')");
//            }

            if (SupplierInputBean.getSupplierName() != null && !SupplierInputBean.getSupplierName().isEmpty()) {
                dynamicClause.append("and lower(wta.key2) like lower('%").append(SupplierInputBean.getSupplierName()).append("%')");
            }

            if (SupplierInputBean.getStatus() != null && !SupplierInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and wta.key3 = '").append(SupplierInputBean.getStatus()).append("'");
            }


        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

}
