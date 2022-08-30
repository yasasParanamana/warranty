package com.oxcentra.warranty.repository.warranty.claim;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.usermgt.task.TaskInputBean;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.mapping.warranty.RegWarrantyAttachments;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import javax.validation.ConstraintViolationException;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
@Scope("prototype")
public class ClaimRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from reg_warranty_claim t left outer join status s on s.statuscode=t.status where ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from web_tmpauthrec d where d.page=? and d.status=? and d.lastupdateduser <> ? and ";
    private final String SQL_INSERT_CLAIM = "insert into reg_warranty_claim (id,chassis,model,first_name,last_name,phone,email,address,surburb,state,postcode,dealership,description,failiure_type,failiure_area,repair_type,repair_description,cost_type,hours,labour_rate,cost_description) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String SQL_INSERT_CLAIM_ATTACHMENT = "insert into reg_warranty_attachments (warranty_id,file_name,file_format,attachment_file) values (?,?,?,?)";
    private final String SQL_UPDATE_CLAIM = "update reg_warranty_claim set status=? where id=?";
    private final String SQL_FIND_CLAIM = "select t.id,t.chassis,t.model,t.first_name,t.last_name,t.phone,t.email,t.address,t.surburb,t.state,t.postcode,t.dealership,t.purchasing_date,t.description,t.failiure_type,t.failiure_area,t.repair_type,t.repair_description,t.cost_type,t.hours,t.labour_rate,t.total_cost,t.cost_description  from reg_warranty_claim t where t.id = ? ";
    private final String SQL_DELETE_CLAIM = "delete from reg_warranty_claim where id=?";
    private final String SQL_STATUS_UPDATE_CLAIM = "update reg_warranty_claim set status=? where id=?";


    @Transactional(readOnly = true)
    public long getDataCount(ClaimInputBean claimInputBean) throws Exception {
        long count = 0;
        new StringBuilder();
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(claimInputBean, dynamicClause);
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
    public List<Claim> getClaimSearchResults(ClaimInputBean claimInputBean) throws Exception {
        List<Claim> claimList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(claimInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (claimInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by t.purchasing_date desc ";
            } else {
                sortingStr = " order by t.purchasing_date " + claimInputBean.sortDirections.get(0);
            }

            String sql = "" +
                    " select " +
                    " t.id, t.description, s.description as statusdes,t.chassis,t.model,t.first_name,t.phone from reg_warranty_claim t " +
                    " left outer join status s on s.statuscode=t.status " +
                    " where " + dynamicClause.toString() + sortingStr +
                    " limit " + claimInputBean.displayLength + " offset " + claimInputBean.displayStart;

            claimList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Claim claim = new Claim();

                try {
                    claim.setId(rs.getString("id"));
                } catch (Exception e) {
                    claim.setId(null);
                }

                try {
                    claim.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    claim.setDescription(null);
                }

                try {
                    claim.setStatus(rs.getString("statusdes"));
                } catch (Exception e) {
                    claim.setStatus(null);
                }

                return claim;
            });
        } catch (EmptyResultDataAccessException ex) {
            return claimList;
        } catch (Exception e) {
            throw e;
        }
        return claimList;
    }

    @Transactional(readOnly = true)
    public long getDataCountDual(ClaimInputBean claimInputBean) throws Exception {
        long count = 0;
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DUAL_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClauseDual(claimInputBean, dynamicClause);
            //create the query
            count = jdbcTemplate.queryForObject(dynamicClause.toString(), new Object[]{PageVarList.CLAIMS_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, Long.class);
        } catch (DataAccessException ex) {
            throw ex;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TempAuthRec> getClaimSearchResultsDual(ClaimInputBean claimInputBean) throws Exception {
        List<TempAuthRec> claimDualList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClauseDual(claimInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (claimInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by wta.lastupdatedtime desc ";
            } else {
                sortingStr = " order by wta.lastupdatedtime " + claimInputBean.sortDirections.get(0);
            }

            String sql =
                    " select wta.id, wta.key1, wta.key2, s.description key3, t.description task, wta.createdtime, wta.lastupdatedtime, wta.lastupdateduser " +
                            " from web_tmpauthrec wta" +
                            " left outer join status s on s.statuscode = wta.key3 " +
                            " left outer join web_task t on t.taskcode = wta.task " +
                            " where wta.page=? and wta.status=? and wta.lastupdateduser <> ? and " + dynamicClause.toString() + sortingStr +
                            " limit " + claimInputBean.displayLength + " offset " + claimInputBean.displayStart;

            claimDualList = jdbcTemplate.query(sql, new Object[]{PageVarList.CLAIMS_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN, sessionBean.getUsername()}, (rs, rowNum) -> {
                TempAuthRec tempAuthRec = new TempAuthRec();

                try {
                    tempAuthRec.setId(rs.getString("id"));
                } catch (Exception e) {
                    tempAuthRec.setId(null);
                }

                try {
                    tempAuthRec.setTask(rs.getString("task"));
                } catch (Exception e) {
                    tempAuthRec.setTask(null);
                }

                try {
                    //task code
                    tempAuthRec.setKey1(rs.getString("key1"));
                } catch (Exception e) {
                    tempAuthRec.setKey1(null);
                }

                try {
                    //description
                    tempAuthRec.setKey2(rs.getString("key2"));
                } catch (Exception e) {
                    tempAuthRec.setKey2(null);
                }

                try {
                    //status
                    tempAuthRec.setKey3(rs.getString("key3"));
                } catch (Exception e) {
                    tempAuthRec.setKey3(null);
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

                try {
                    tempAuthRec.setCreatedTime(rs.getString("createdtime"));
                } catch (Exception e) {
                    tempAuthRec.setCreatedTime(null);
                }
                return tempAuthRec;
            });
        } catch (Exception ex) {
            throw ex;
        }
        return claimDualList;
    }


    @Transactional
    public String insertClaim(ClaimInputBean claimInputBean) throws Exception {
        String message = "";
        FileInputStream fileInputStream = null;

        try {
            int value = 0;
            //insert query
            value = jdbcTemplate.update(SQL_INSERT_CLAIM,claimInputBean.getId(),
                    claimInputBean.getChassis(),
                    claimInputBean.getModel(),
                    claimInputBean.getFirstName(),
                    claimInputBean.getLastName(),
                    claimInputBean.getPhone(),
                    claimInputBean.getEmail(),
                    claimInputBean.getAddress(),
                    claimInputBean.getSurburb(),
                    claimInputBean.getState(),
                    claimInputBean.getPostcode(),
                    claimInputBean.getDealership(),
                    //claimInputBean.getPurchasingDate(),
                    claimInputBean.getDescription(),
                    claimInputBean.getFailureType(),
                    claimInputBean.getFailureArea(),
                    claimInputBean.getRepairType(),
                    claimInputBean.getRepairDescription(),
                    claimInputBean.getCostType(),
                    claimInputBean.getHours(),
                    claimInputBean.getLabourRate(),
                  //  claimInputBean.getTotalCost(),
                    claimInputBean.getDescription()
                    );

            System.out.println("Value + " + value);

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }


            //insert Attachments


            if (claimInputBean.getFile().size() > 0) {
                int i = 0;

                for (String file : claimInputBean.getFile()) {

                    try {
                        RegWarrantyAttachments newattach = new RegWarrantyAttachments();

                        /*System.out.println("Index>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+(file.indexOf(',')));

                        System.out.println("File>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+file);
                        System.out.println("Substring>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+(file.substring(file.indexOf(',')+1,file.length())));

*/
//                        newattach.setAttachmentFile(file.substring(file.indexOf(',')+1,file.length()));

                        String values = (file);
                        byte[] buff = values.getBytes();
                        Blob blob = new SerialBlob(buff);


                        newattach.setAttachmentFile(blob);
                        newattach.setWarrantyId(claimInputBean.getId());


//                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+newattach.getAttachmentFile());

//                        newattach.setCreatedDate(sysDate);
                        newattach.setFileFormat("jpg");
                        newattach.setFileName("Sample");


                        int valueA = 0;
                        //insert query
                        valueA = jdbcTemplate.update(SQL_INSERT_CLAIM_ATTACHMENT,
                                newattach.getWarrantyId(),
                                newattach.getFileName(),
                                newattach.getFileFormat(),
                                newattach.getAttachmentFile()

                        );

                        if (valueA != 1) {
                            message = MessageVarList.COMMON_ERROR_PROCESS;
                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw ex;
                    } finally {
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                    }
                    i++;
                }
            }


        } catch (DuplicateKeyException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }
        return message;
    }

    @Transactional(readOnly = true)
    public Claim getClaim(String id) throws Exception {
        System.out.println("Hi");
        Claim claim;
        try {
            claim = jdbcTemplate.queryForObject(SQL_FIND_CLAIM, new Object[]{id}, (rs, rowNum) -> {
                Claim t = new Claim();

                try {
                    t.setId(rs.getString("id"));
                } catch (Exception e) {
                    t.setId(null);
                }

                try {
                    t.setChassis(rs.getString("chassis"));
                } catch (Exception e) {
                    t.setChassis(null);
                }

                try {
                    t.setModel(rs.getString("model"));
                } catch (Exception e) {
                    t.setModel(null);
                }

                try {
                    t.setFirstName(rs.getString("first_name"));
                } catch (Exception e) {
                    t.setFirstName(null);
                }

                try {
                    t.setLastName(rs.getString("last_name"));
                } catch (Exception e) {
                    t.setLastName(null);
                }

                try {
                    t.setPhone(rs.getString("phone"));
                } catch (Exception e) {
                    t.setPhone(null);
                }

                try {
                    t.setEmail(rs.getString("email"));
                } catch (Exception e) {
                    t.setEmail(null);
                }

                try {
                    t.setAddress(rs.getString("address"));
                } catch (Exception e) {
                    t.setAddress(null);
                }

                try {
                    t.setSurburb(rs.getString("surburb"));
                } catch (Exception e) {
                    t.setSurburb(null);
                }

                try {
                    t.setState(rs.getString("state"));
                } catch (Exception e) {
                    t.setState(null);
                }

                try {
                    t.setPostcode(rs.getString("postcode"));
                } catch (Exception e) {
                    t.setPostcode(null);
                }

                try {
                    t.setDealership(rs.getString("dealership"));
                } catch (Exception e) {
                    t.setDealership(null);
                }

                try {
                    t.setClaimType(rs.getString("claim_type"));
                } catch (Exception e) {
                    t.setClaimType(null);
                }

                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    t.setPurchasingDate(formatter.parse(rs.getDate("purchasing_date").toString()));
                } catch (Exception e) {
                    t.setPurchasingDate(null);
                }

                try {
                    t.setDescription(rs.getString("description"));
                } catch (Exception e) {
                    t.setDescription(null);
                }

                try {
                    t.setFailureType(rs.getString("failiure_type"));
                } catch (Exception e) {
                    t.setFailureType(null);
                }

                try {
                    t.setFailureArea(rs.getString("failiure_area"));
                } catch (Exception e) {
                    t.setFailureArea(null);
                }

                try {
                    t.setRepairType(rs.getString("repair_type"));
                } catch (Exception e) {
                    t.setRepairType(null);
                }

                try {
                    t.setRepairDescription(rs.getString("repair_description"));
                } catch (Exception e) {
                    t.setRepairDescription(null);
                }

                try {
                    t.setCostType(rs.getString("cost_type"));
                } catch (Exception e) {
                    t.setCostType(null);
                }

                try {
                    t.setHours(rs.getString("hours"));
                } catch (Exception e) {
                    t.setHours(null);
                }

                try {
                    t.setLabourRate(rs.getString("labour_rate"));
                } catch (Exception e) {
                    t.setLabourRate(null);
                }

                try {
                    t.setTotalCost(rs.getBigDecimal("total_cost"));
                } catch (Exception e) {
                    t.setTotalCost(null);
                }

                try {
                    t.setCostDescription(rs.getString("cost_description"));
                } catch (Exception e) {
                    t.setCostDescription(null);
                }

                return t;
            });
        } catch (EmptyResultDataAccessException erse) {
            claim = null;
        } catch (Exception e) {
            throw e;
        }
        return claim;
    }

    @Transactional
    public String updateClaim(ClaimInputBean claimInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            value = jdbcTemplate.update(SQL_UPDATE_CLAIM, "WAR_APPROVE",claimInputBean.getId());
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

    @Transactional
    public String approveRequestClaim(ClaimInputBean claimInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            value = jdbcTemplate.update(SQL_STATUS_UPDATE_CLAIM, "WAR_APPROVE",claimInputBean.getId());
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

    @Transactional
    public String rejectRequestClaim(ClaimInputBean claimInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            value = jdbcTemplate.update(SQL_STATUS_UPDATE_CLAIM, "WAR_REJECTED_HO",claimInputBean.getId());
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

    @Transactional
    public String deleteClaim(String id) throws Exception {
        String message = "";
        try {
            int count = jdbcTemplate.update(SQL_DELETE_CLAIM, id);
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

    private StringBuilder setDynamicClause(ClaimInputBean claimInputBean , StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (claimInputBean.getId() != null && !claimInputBean.getId().isEmpty()) {
                dynamicClause.append("and lower(t.taskcode) like lower('%").append(claimInputBean.getId()).append("%')");
            }

            if (claimInputBean.getDescription() != null && !claimInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(t.description) like lower('%").append(claimInputBean.getDescription()).append("%')");
            }

            if (claimInputBean.getStatus() != null && !claimInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and t.status = '").append(claimInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }

    private StringBuilder setDynamicClauseDual(ClaimInputBean claimInputBean, StringBuilder dynamicClause) {
        dynamicClause.append(" 1=1 ");
        try {
            if (claimInputBean.getId() != null && !claimInputBean.getId().isEmpty()) {
                dynamicClause.append("and lower(d.key1) like lower('%").append(claimInputBean.getId()).append("%')");
            }

            if (claimInputBean.getDescription() != null && !claimInputBean.getDescription().isEmpty()) {
                dynamicClause.append("and lower(d.key2) like lower('%").append(claimInputBean.getDescription()).append("%')");
            }

            if (claimInputBean.getStatus() != null && !claimInputBean.getStatus().isEmpty()) {
                dynamicClause.append("and d.key3 = '").append(claimInputBean.getStatus()).append("'");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }
}
