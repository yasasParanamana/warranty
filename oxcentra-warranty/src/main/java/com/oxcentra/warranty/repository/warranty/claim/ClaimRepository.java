package com.oxcentra.warranty.repository.warranty.claim;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.usermgt.task.TaskInputBean;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.usermgt.Task;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.mapping.warranty.RegWarrantyAttachments;
import com.oxcentra.warranty.mapping.warranty.SpareParts;
import com.oxcentra.warranty.mapping.warranty.Supplier;
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
    private final String SQL_INSERT_CLAIM = "insert into reg_warranty_claim (id,chassis,model,first_name,last_name,phone,email,address,surburb,state,postcode,dealership,description,failiure_type,failiure_area,repair_type,repair_description,cost_type,hours,labour_rate,cost_description,status,createdtime,createduser,lastupdatedtime,lastupdateduser) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String SQL_INSERT_CLAIM_ATTACHMENT = "insert into reg_warranty_attachments (warranty_id,file_name,file_format,attachment_file,createdtime) values (?,?,?,?,?)";
    private final String SQL_INSERT_SPARE_PART = "insert into reg_spare_part (warranty_id,spare_part_type,qty) values (?,?,?)";
    private final String SQL_UPDATE_CLAIM = "update reg_warranty_claim set status=? where id=?";
    private final String SQL_FIND_CLAIM = "select t.id,t.chassis,t.model,t.first_name,t.last_name,t.phone,t.email,t.address,t.surburb,t.state,t.postcode,t.dealership,t.purchasing_date,t.description,t.failiure_type,t.failiure_area,t.repair_type,t.repair_description,t.cost_type,t.hours,t.labour_rate,t.total_cost,t.cost_description,r.dealership_name,r.dealership_phone,r.dealership_email,r.dealership_address  from reg_warranty_claim t LEFT OUTER JOIN reg_dealership r ON r.dealership_code = t.dealership  where t.id = ? ";
    private final String SQL_FIND_SUPPLIER = "select t.supplier_code,t.supplier_name,t.supplier_phone,t.supplier_email,t.supplier_address,t.status from reg_supplier t  where t.supplier_code = ? ";
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
                sortingStr = " order by t.createdtime desc ";
            } else {
                sortingStr = " order by t.createdtime " + claimInputBean.sortDirections.get(0);
            }

            String sql = "" +
                    " select " +
                    "t.id," +
                    "t.first_name," +
                    "t.last_name," +
                    "t.phone," +
                    "t.email," +
                    "t.dealership," +
                    "s.description as statusdes," +
                    "t.createdtime," +
                    "t.createduser," +
                    "t.lastupdatedtime," +
                    "t.lastupdateduser " +
                    "from reg_warranty_claim t " +
                    "left outer join status s on s.statuscode=t.status " +
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
                    claim.setFirstName(rs.getString("first_name"));
                } catch (Exception e) {
                    claim.setFirstName(null);
                }

                try {
                    claim.setLastName(rs.getString("last_name"));
                } catch (Exception e) {
                    claim.setLastName(null);
                }

                try {
                    claim.setPhone(rs.getString("phone"));
                } catch (Exception e) {
                    claim.setPhone(null);
                }

                try {
                    claim.setEmail(rs.getString("email"));
                } catch (Exception e) {
                    claim.setEmail(null);
                }

                try {
                    claim.setDealership(rs.getString("dealership"));
                } catch (Exception e) {
                    claim.setDealership(null);
                }

                try {
                    claim.setStatus(rs.getString("statusdes"));
                } catch (Exception e) {
                    claim.setStatus(null);
                }

                try {
                    claim.setCreatedTime(rs.getDate("createdtime"));
                } catch (Exception e) {
                    claim.setCreatedTime(null);
                }
                try {
                    claim.setCreatedUser(rs.getString("createduser"));
                } catch (Exception e) {
                    claim.setCreatedUser(null);
                }
                try {
                    claim.setLastUpdatedTime(rs.getDate("lastupdatedtime"));
                } catch (Exception e) {
                    claim.setLastUpdatedTime(null);
                }
                try {
                    claim.setLastUpdatedUser(rs.getString("lastupdateduser"));
                } catch (Exception e) {
                    claim.setLastUpdatedUser(null);
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

    @Transactional
    public String insertClaim(ClaimInputBean claimInputBean) throws Exception {
        String message = "";

        try {
            int value = 0;
            //insert in to reg_warranty_claim
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
                    claimInputBean.getDescription(),
                    "WAR_PEND",
                    claimInputBean.getCreatedTime(),
                    claimInputBean.getCreatedUser(),
                    claimInputBean.getLastUpdatedTime(),
                    claimInputBean.getLastUpdatedUser()
                    );

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }

            /*insert Spare Parts*/

            int valueParts = 0;
            //insert in to reg_spare_part

            if(claimInputBean.getSpareParts().size() > 0){
                for(SpareParts spareParts : claimInputBean.getSpareParts()){

                    valueParts = jdbcTemplate.update(SQL_INSERT_SPARE_PART,
                            claimInputBean.getId(),
                            spareParts.getSparePartType(),
                            spareParts.getQty()
                    );

                }
            }

            if (valueParts != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }


            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }


           /* insert Attachments
            insert in to reg_warranty_attachments*/

            if (claimInputBean.getFile().size() > 0) {
                int i = 0;

                for (String file : claimInputBean.getFile()) {

                    try {
                        RegWarrantyAttachments newAttachments = new RegWarrantyAttachments();

                        /*System.out.println("File Index>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+(file.indexOf(',')));
                        System.out.println("File Base64 String>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+file);
                        System.out.println("Substring Result>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+(file.substring(file.indexOf(',')+1,file.length())));

*/
//                      newAttachments.setAttachmentFile(file.substring(file.indexOf(',')+1,file.length()));

                        String values = (file);
                        byte[] buff = values.getBytes();
                        Blob blob = new SerialBlob(buff);

                        newAttachments.setAttachmentFile(blob);
                        newAttachments.setWarrantyId(claimInputBean.getId());

//                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+newattach.getAttachmentFile());

                        System.out.println(claimInputBean.getCreatedTime());

                        newAttachments.setCreatedDate(claimInputBean.getCreatedTime());
                        newAttachments.setFileFormat("jpg");
                        newAttachments.setFileName("Sample");


                        int valueA = 0;
                        valueA = jdbcTemplate.update(SQL_INSERT_CLAIM_ATTACHMENT,
                                newAttachments.getWarrantyId(),
                                newAttachments.getFileName(),
                                newAttachments.getFileFormat(),
                                newAttachments.getAttachmentFile(),
                                newAttachments.getCreatedDate()

                        );

                        if (valueA != 1) {
                            message = MessageVarList.COMMON_ERROR_PROCESS;
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw ex;
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
                    System.out.println("Purchasing Date :"+rs.getDate("purchasing_date").toString());
//                    System.out.println("Format Purchasing Date :"+formatter.parse(rs.getDate("purchasing_date").toString()));
//                    t.setPurchasingDate(formatter.parse(rs.getDate("purchasing_date").toString()));

                    t.setPurchasingDate(rs.getDate("purchasing_date"));
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

                try {
                    t.setDealershipName(rs.getString("dealership_name"));
                } catch (Exception e) {
                    t.setDealershipName(null);
                }

                try {
                    t.setDealershipPhone(rs.getString("dealership_phone"));
                } catch (Exception e) {
                    t.setDealershipPhone(null);
                }

                try {
                    t.setDealershipEmail(rs.getString("dealership_email"));
                } catch (Exception e) {
                    t.setDealershipEmail(null);
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

    @Transactional(readOnly = true)
    public Supplier getSupplierDetails(String supplierCode) throws Exception {
        Supplier supplier;
        try {
            supplier = jdbcTemplate.queryForObject(SQL_FIND_SUPPLIER, new Object[]{supplierCode}, (rs, rowNum) -> {
                Supplier t = new Supplier();

                try {
                    t.setSupplierCode(rs.getString("supplier_code"));
                } catch (Exception e) {
                    t.setSupplierCode(null);
                }

                try {
                    t.setSupplierName(rs.getString("supplier_name"));
                } catch (Exception e) {
                    t.setSupplierName(null);
                }

                try {
                    t.setSupplierPhone(rs.getString("supplier_phone"));
                } catch (Exception e) {
                    t.setSupplierPhone(null);
                }

                try {
                    t.setSupplierEmail(rs.getString("supplier_email"));
                } catch (Exception e) {
                    t.setSupplierEmail(null);
                }

                try {
                    t.setSupplierAddress(rs.getString("supplier_address"));
                } catch (Exception e) {
                    t.setSupplierAddress(null);
                }
                try {
                    t.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    t.setStatus(null);
                }

                return t;
            });
        } catch (EmptyResultDataAccessException erse) {
            supplier = null;
        } catch (Exception e) {
            throw e;
        }
        return supplier;
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
        try {
            if (claimInputBean.getId() != null && !claimInputBean.getId().isEmpty()) {
                dynamicClause.append(" 1=0 ");
                dynamicClause.append("or lower(t.id) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.first_name) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.last_name) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.phone) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.email) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.dealership) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or t.status = '").append(claimInputBean.getStatus()).append("'");
            }else{
                dynamicClause.append(" 1=1 ");
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
