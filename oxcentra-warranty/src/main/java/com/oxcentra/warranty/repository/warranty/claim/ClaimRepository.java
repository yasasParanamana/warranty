package com.oxcentra.warranty.repository.warranty.claim;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.mapping.warranty.SpareParts;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.mapping.warranty.WarrantyAttachments;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final String SQL_INSERT_CLAIM = "insert into reg_warranty_claim (id,chassis,model,first_name,last_name,phone,email,address,surburb,state,postcode,dealership,description,failiure_type,failiure_area,repair_type,repair_description,cost_type,hours,labour_rate,cost_description,status,createdtime,createduser,lastupdatedtime,lastupdateduser,total_cost,purchasing_date,claim_type) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String SQL_INSERT_CLAIM_ATTACHMENT = "insert into reg_warranty_attachments (warranty_id,file_name,file_format,attachment_file,createdtime,attachment_type) values (?,?,?,?,?,?)";
    private final String SQL_INSERT_SPARE_PART = "insert into reg_spare_part (warranty_id,spare_part_type,qty) values (?,?,?)";
    private final String SQL_UPDATE_CLAIM = "update reg_warranty_claim set status=? where id=?";
    private final String SQL_FIND_SUPPLIER = "select t.supplier_code,t.supplier_name,t.supplier_phone,t.supplier_email,t.supplier_address,t.status from reg_supplier t  where t.supplier_code = ? ";
    private final String SQL_DELETE_CLAIM = "delete from reg_warranty_claim where id=?";
    private final String SQL_DELETE_CLAIM_SPARE_PART = "delete from reg_spare_part where warranty_id=?";
    private final String SQL_DELETE_CLAIM_ATTACHMENT = "delete from reg_warranty_attachments where warranty_id=?";
    private final String SQL_STATUS_UPDATE_CLAIM = "update reg_warranty_claim set status=?,is_in_house=?,lastupdateduser=?,lastupdatedtime=? ,cost_type=? ,hours=? ,labour_rate=? ,total_cost=?, cost_description=?, failing_area=? where id=? ";
    private final String SQL_EMAIL_SEND_UPDATE_CLAIM = "update reg_warranty_claim set status=?,supplier=?,is_in_house=?,lastupdateduser=?,lastupdatedtime=?,comment=?,supplier_url_token=?  ,cost_type=? ,hours=? ,labour_rate=? ,total_cost=?, cost_description=?, failing_area=?, claim_on_supplier=? where id=?";
    private final String SQL_FIND_CLAIM = "select " +
            "t.id," +
            "t.chassis," +
            "t.model," +
            "t.first_name," +
            "t.last_name," +
            "t.phone," +
            "t.email," +
            "t.address," +
            "t.surburb," +
            "t.state," +
            "t.postcode," +
            "t.dealership," +
            "t.purchasing_date," +
            "t.description," +
            "t.failiure_type," +
            "t.failiure_area," +
            "t.repair_type," +
            "t.repair_description," +
            "t.cost_type," +
            "t.hours," +
            "t.labour_rate," +
            "t.total_cost," +
            "t.cost_description," +
            "r.dealership_name," +
            "r.dealership_phone," +
            "r.dealership_email," +
            "r.dealership_address," +
            "t.claim_type,  " +
            "t.failing_area,  " +
            "t.comment,  " +
            "t.supplier,  " +
            "s.supplier_phone,  " +
            "s.supplier_email,  " +
            "s.supplier_address,  " +
            "t.is_in_house,  " +
            "t.status  " +
            "from reg_warranty_claim t " +
            "LEFT OUTER JOIN reg_dealership r ON r.dealership_code = t.dealership " +
            "LEFT OUTER JOIN reg_supplier s ON s.supplier_code = t.supplier " +
            "where t.id = ? ";

    private final String SQL_GET_LIST_SPARE_PARTS = "select t.id,t.warranty_id,t.spare_part_type,t.spare_part_name,t.qty from reg_spare_part t  where t.warranty_id = ? ";
    private final String SQL_GET_LIST_ATTACHMENT_PDF = "select t.attachment_id,t.warranty_id,t.file_name,t.file_format,CONVERT(t.attachment_file USING UTF8) as attachmentFile,t.createdtime from reg_warranty_attachments t  where t.warranty_id = ? and t.attachment_type=? ";
    private final String SQL_GET_SPARE_PARTS = "select t.id,t.warranty_id,t.spare_part_type,t.spare_part_name,t.qty from reg_spare_part t  where t.id = ? ";

    private final String SQL_COUNT_STATUS = "select count(*) from reg_warranty_claim where status=?";


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

            String dealerhip ="";
            String userRole = sessionBean.getUser().getUserrole();

            String dealershipString = "";
            if(!(userRole.equalsIgnoreCase("HEADOFFICE") || userRole.equalsIgnoreCase("ADMIN"))){
                dealerhip = sessionBean.getUser().getDealership();
                dealershipString = "where  b.dealership = '"+dealerhip+"'";
            }else{
                dealershipString =" ";
            }

            String sql = "select " +
                    " t.id, " +
                    "t.first_name, " +
                    "t.last_name, " +
                    "t.phone, " +
                    "t.email, " +
                    "rd.dealership_name, " +
                    "s.description as statusdes, " +
                    "t.createdtime, " +
                    "t.createduser, " +
                    "t.lastupdatedtime, " +
                    "t.lastupdateduser,  " +
                    "t.chassis  " +
                    "from (SELECT * " +
                    "from reg_warranty_claim b   " +
                     dealershipString+") AS t " +
                    "left outer join status s on s.statuscode=t.status " +
                    "left outer join reg_dealership rd on rd.dealership_code=t.dealership " +
                    "where "+dynamicClause.toString() + sortingStr +
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
                    claim.setDealership(rs.getString("dealership_name"));
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
                try {
                    claim.setChassis(rs.getString("chassis"));
                } catch (Exception e) {
                    claim.setChassis(null);
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


    @Transactional
    public String insertClaim(ClaimInputBean claimInputBean) throws Exception {
        String message = "";

        try {
            int value = 0;
            //insert in to reg_warranty_claim
            value = jdbcTemplate.update(SQL_INSERT_CLAIM, claimInputBean.getId(),
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
                    claimInputBean.getDescription(),
                    claimInputBean.getFailureType(),
                    claimInputBean.getFailureArea(),
                    claimInputBean.getRepairType(),
                    claimInputBean.getRepairDescription(),
                    claimInputBean.getCostType(),
                    claimInputBean.getHours(),
                    claimInputBean.getLabourRate(),
                    claimInputBean.getDescription(),
                    claimInputBean.getStatus(),
                    claimInputBean.getCreatedTime(),
                    claimInputBean.getCreatedUser(),
                    claimInputBean.getLastUpdatedTime(),
                    claimInputBean.getLastUpdatedUser(),
                    claimInputBean.getTotalCost(),
                    claimInputBean.getPurchasingDate(),
                    claimInputBean.getClaimType()
            );

            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }

            /*insert Spare Parts*/


            //insert in to reg_spare_part

            if (claimInputBean.getSpareParts().size() > 0) {

                int valueParts = 0;

                for (SpareParts spareParts : claimInputBean.getSpareParts()) {

                    valueParts = jdbcTemplate.update(SQL_INSERT_SPARE_PART,
                            claimInputBean.getId(),
                            spareParts.getSparePartType(),
                            spareParts.getQty()
                    );

                }
                if (valueParts != 1) {
                    message = MessageVarList.COMMON_ERROR_PROCESS;
                }
            }

           /* insert Attachments
            insert in to reg_warranty_attachments*/

            if (claimInputBean.getFile().size() > 0) {
                int i = 0;

                for (String file : claimInputBean.getFile()) {

                    try {
                        WarrantyAttachments newAttachments = new WarrantyAttachments();

                        String fileName = "";
                        String fileSize = "";
                        String fileType = "";
                        String attachmentType = "";

                        String[] parts = file.split("FileDetails");
                        String imageFile = parts[0];
                        String othersDetails = parts[1];

                        String[] imageOtherDetails = othersDetails.split("\\|");

                        fileName = imageOtherDetails[0];
                        fileSize = imageOtherDetails[1];
                        fileType = imageOtherDetails[2];
                        attachmentType = imageOtherDetails[3];

                        String values = (imageFile);
                        byte[] buff = values.getBytes();
                        Blob blob = new SerialBlob(buff);

                        newAttachments.setAttachmentFile(blob);
                        newAttachments.setWarrantyId(claimInputBean.getId());

                        newAttachments.setCreatedDate(claimInputBean.getCreatedTime());
                        newAttachments.setFileFormat(fileType);
                        newAttachments.setFileName(fileName);
                        newAttachments.setAttachmentType(attachmentType);

                        int valueA = 0;
                        valueA = jdbcTemplate.update(SQL_INSERT_CLAIM_ATTACHMENT,
                                newAttachments.getWarrantyId(),
                                newAttachments.getFileName(),
                                newAttachments.getFileFormat(),
                                newAttachments.getAttachmentFile(),
                                newAttachments.getCreatedDate(),
                                newAttachments.getAttachmentType()

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
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
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

                try {
                    t.setClaimType(rs.getString("claim_type"));
                } catch (Exception e) {
                    t.setClaimType(null);
                }

                try {
                    t.setFailingArea(rs.getString("failing_area"));
                } catch (Exception e) {
                    t.setFailingArea(null);
                }

                try {
                    t.setSupplier(rs.getString("supplier"));
                } catch (Exception e) {
                    t.setSupplier(null);
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
                    t.setComment(rs.getString("comment"));
                } catch (Exception e) {
                    t.setComment(null);
                }

                try {
                    t.setInHouse(rs.getBoolean("is_in_house"));
                } catch (Exception e) {
                    t.setInHouse(false);
                }

                try {
                    t.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    t.setStatus(null);
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
    public SpareParts getSparePart(String id) throws Exception {
        SpareParts spareParts;
        try {
            spareParts = jdbcTemplate.queryForObject(SQL_GET_SPARE_PARTS, new Object[]{id}, (rs, rowNum) -> {
                SpareParts t = new SpareParts();

                try {
                    t.setId(rs.getString("id"));
                } catch (Exception e) {
                    t.setId(null);
                }

                try {
                    t.setSparePartType(rs.getString("chassis"));
                } catch (Exception e) {
                    t.setSparePartType(null);
                }

                try {
                    t.setQty(rs.getString("model"));
                } catch (Exception e) {
                    t.setQty(null);
                }

                return t;
            });
        } catch (EmptyResultDataAccessException erse) {
            spareParts = null;
        } catch (Exception e) {
            throw e;
        }
        return spareParts;
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
    public String updateClaimStatus(ClaimInputBean claimInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            value = jdbcTemplate.update(SQL_UPDATE_CLAIM,claimInputBean.getStatus(), claimInputBean.getId());
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
            value = jdbcTemplate.update(SQL_STATUS_UPDATE_CLAIM,
                    claimInputBean.getStatus(),
                    claimInputBean.getIsInHouse(),
                    claimInputBean.getLastUpdatedUser(),
                    claimInputBean.getLastUpdatedTime(),
                    claimInputBean.getCostType(),
                    claimInputBean.getHours(),
                    claimInputBean.getLabourRate(),
                    claimInputBean.getTotalCost(),
                    claimInputBean.getCostDescription(),
                    claimInputBean.getFailingArea(),
                    claimInputBean.getId()
            );
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }

            //Delete Spare Part

            int countSpare = 0;
            countSpare = jdbcTemplate.update(SQL_DELETE_CLAIM_SPARE_PART, claimInputBean.getId());
            if (countSpare < 0) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }

            //insert in to reg_spare_part

            if (claimInputBean.getSpareParts().size() > 0) {

                int valueParts = 0;

                for (SpareParts spareParts : claimInputBean.getSpareParts()) {

                    valueParts = jdbcTemplate.update(SQL_INSERT_SPARE_PART,
                            claimInputBean.getId(),
                            spareParts.getSparePartType(),
                            spareParts.getQty()
                    );

                }
                if (valueParts != 1) {
                    message = MessageVarList.COMMON_ERROR_PROCESS;
                }
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
            value = jdbcTemplate.update(SQL_STATUS_UPDATE_CLAIM,
                    claimInputBean.getStatus(),
                    claimInputBean.getIsInHouse(),
                    claimInputBean.getLastUpdatedUser(),
                    claimInputBean.getLastUpdatedTime(),
                    claimInputBean.getCostType(),
                    claimInputBean.getHours(),
                    claimInputBean.getLabourRate(),
                    claimInputBean.getTotalCost(),
                    claimInputBean.getCostDescription(),
                    claimInputBean.getFailingArea(),
                    claimInputBean.getId()
            );
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }

            //Delete Spare Part

            int countSpare = 0;
            countSpare = jdbcTemplate.update(SQL_DELETE_CLAIM_SPARE_PART, claimInputBean.getId());
            if (countSpare < 0) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }


            //insert in to reg_spare_part

            if (claimInputBean.getSpareParts().size() > 0) {

                int valueParts = 0;

                for (SpareParts spareParts : claimInputBean.getSpareParts()) {

                    valueParts = jdbcTemplate.update(SQL_INSERT_SPARE_PART,
                            claimInputBean.getId(),
                            spareParts.getSparePartType(),
                            spareParts.getQty()
                    );

                }
                if (valueParts != 1) {
                    message = MessageVarList.COMMON_ERROR_PROCESS;
                }
            }

        } catch (DuplicateKeyException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return message;
    }

   @Transactional
    public String sendEmail(ClaimInputBean claimInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            value = jdbcTemplate.update(SQL_EMAIL_SEND_UPDATE_CLAIM,
                    claimInputBean.getStatus(),
                    claimInputBean.getSupplier(),
                    claimInputBean.getIsInHouse(),
                    claimInputBean.getLastUpdatedUser(),
                    claimInputBean.getLastUpdatedTime(),
                    claimInputBean.getComment(),
                    claimInputBean.getSupplierUrlToken(),
                    claimInputBean.getCostType(),
                    claimInputBean.getHours(),
                    claimInputBean.getLabourRate(),
                    claimInputBean.getTotalCost(),
                    claimInputBean.getCostDescription(),
                    claimInputBean.getFailingArea(),
                    claimInputBean.getTotalCost(),
                    claimInputBean.getId());
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
    public String notedRequestClaim(ClaimInputBean claimInputBean) throws Exception {
        String message = "";
        try {
            int value = 0;
            value = jdbcTemplate.update(SQL_STATUS_UPDATE_CLAIM,
                    claimInputBean.getStatus(),
                    claimInputBean.getIsInHouse(),
                    claimInputBean.getLastUpdatedUser(),
                    claimInputBean.getLastUpdatedTime(),
                    claimInputBean.getCostType(),
                    claimInputBean.getHours(),
                    claimInputBean.getLabourRate(),
                    claimInputBean.getTotalCost(),
                    claimInputBean.getCostDescription(),
                    claimInputBean.getFailingArea(),
                    claimInputBean.getId()
            );
            if (value != 1) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }

            //Delete Spare Part

            int countSpare = 0;
            countSpare = jdbcTemplate.update(SQL_DELETE_CLAIM_SPARE_PART, claimInputBean.getId());
            if (countSpare < 0) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }

            //insert in to reg_spare_part

            if (claimInputBean.getSpareParts().size() > 0) {

                int valueParts = 0;

                for (SpareParts spareParts : claimInputBean.getSpareParts()) {

                    valueParts = jdbcTemplate.update(SQL_INSERT_SPARE_PART,
                            claimInputBean.getId(),
                            spareParts.getSparePartType(),
                            spareParts.getQty()
                    );

                }
                if (valueParts != 1) {
                    message = MessageVarList.COMMON_ERROR_PROCESS;
                }
            }
        } catch (DuplicateKeyException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception e) {
            e.printStackTrace();
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

            int countAttach = 0;
            countAttach = jdbcTemplate.update(SQL_DELETE_CLAIM_ATTACHMENT, id);
            if (countAttach < 0) {
                message = MessageVarList.COMMON_ERROR_PROCESS;
            }
            int countSpare = 0;
            countSpare = jdbcTemplate.update(SQL_DELETE_CLAIM_SPARE_PART, id);
            if (countSpare < 0) {
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

    private StringBuilder setDynamicClause(ClaimInputBean claimInputBean, StringBuilder dynamicClause) {
        try {

            if (claimInputBean.getId() != null && !claimInputBean.getId().isEmpty()) {
                dynamicClause.append(" 1=0 ");
                dynamicClause.append("or lower(t.id) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.first_name) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.last_name) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.phone) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.email) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.dealership) like lower('%").append(claimInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.status) like lower('%").append(claimInputBean.getId()).append("%')");

            } else {
                dynamicClause.append(" 1=1 ");
            }
        } catch (Exception e) {
            throw e;
        }
        return dynamicClause;
    }



    @Transactional(readOnly = true)
    public List<SpareParts> getSparePartList(String warrantyId) throws Exception {
        List<SpareParts> sparePartsBeanList;
        try {
            List<Map<String, Object>> sparePartList = jdbcTemplate.queryForList(SQL_GET_LIST_SPARE_PARTS, warrantyId);
            sparePartsBeanList = sparePartList.stream().map((record) -> {
                SpareParts sparePartBean = new SpareParts();
                sparePartBean.setId(record.get("id").toString());
                sparePartBean.setWarrantyId(record.get("warranty_id").toString());
                sparePartBean.setSparePartType(record.get("spare_part_type").toString());
                sparePartBean.setQty(record.get("qty").toString());
                return sparePartBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            sparePartsBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return sparePartsBeanList;
    }

    @Transactional(readOnly = true)
    public List<WarrantyAttachments> getFileList(String warrantyId , String attachmentType) throws Exception {
        List<WarrantyAttachments> warrantyAttachmentsBeanList;
        try {
            List<Map<String, Object>> warrantyAttachmentsList = jdbcTemplate.queryForList(SQL_GET_LIST_ATTACHMENT_PDF, warrantyId ,attachmentType);
            warrantyAttachmentsBeanList = warrantyAttachmentsList.stream().map((record) -> {
                WarrantyAttachments claimValueBean = new WarrantyAttachments();
                claimValueBean.setId(new BigDecimal(record.get("attachment_id").toString()));
                claimValueBean.setWarrantyId(record.get("warranty_id").toString());
                claimValueBean.setFileName(record.get("file_name").toString());
                claimValueBean.setFileFormat(record.get("file_format").toString());
                claimValueBean.setBase64value(record.get("attachmentFile").toString());
                return claimValueBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            warrantyAttachmentsBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return warrantyAttachmentsBeanList;
    }

    /**
     * @Author yasas_p
     * @CreatedTime 2022-09-13 12:49:37 PM
     * @Version V1.00
     * @MethodName getRequestCount
     * @MethodParams [ status]
     * @MethodDescription - This method get the request count
     */
    @Transactional(readOnly = true)
    public long getRequestCount(String status) throws Exception {

        long count = 0;
        try {
            count = jdbcTemplate.queryForObject(SQL_COUNT_STATUS, new Object[]{
                    status
            }, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception ex) {
            throw ex;
        }
        return count;
    }


}
