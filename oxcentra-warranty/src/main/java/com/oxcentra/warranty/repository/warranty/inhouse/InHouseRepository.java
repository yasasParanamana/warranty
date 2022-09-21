package com.oxcentra.warranty.repository.warranty.inhouse;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.critical.CriticalInputBean;
import com.oxcentra.warranty.bean.warranty.inhouse.InHouseInputBean;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.mapping.warranty.SpareParts;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.mapping.warranty.WarrantyAttachments;
import com.oxcentra.warranty.repository.common.CommonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Scope("prototype")
public class InHouseRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from reg_warranty_claim t left outer join status s on s.statuscode=t.status where is_in_house='1' and status ='WAR_APPROVE' and ";
    private final String SQL_GET_LIST_DUAL_DATA_COUNT = "select count(*) from web_tmpauthrec d where d.page=? and d.status=? and d.lastupdateduser <> ? and ";
    private final String SQL_FIND_SUPPLIER = "select t.supplier_code,t.supplier_name,t.supplier_phone,t.supplier_email,t.supplier_address,t.status from reg_supplier t  where t.supplier_code = ? ";
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
            "t.is_in_house  " +
            "from reg_warranty_claim t " +
            "LEFT OUTER JOIN reg_dealership r ON r.dealership_code = t.dealership " +
            "LEFT OUTER JOIN reg_supplier s ON s.supplier_code = t.supplier " +
            "where t.id = ? ";

    private final String SQL_GET_LIST_SPARE_PARTS = "select t.id,t.warranty_id,t.spare_part_type,t.spare_part_name,t.qty from reg_spare_part t  where t.warranty_id = ? ";
    private final String SQL_GET_LIST_ATTACHMENT_PDF = "select t.attachment_id,t.warranty_id,t.file_name,t.file_format,CONVERT(t.attachment_file USING UTF8) as attachmentFile,t.createdtime from reg_warranty_attachments t  where t.warranty_id = ? ";
    private final String SQL_GET_SPARE_PARTS = "select t.id,t.warranty_id,t.spare_part_type,t.spare_part_name,t.qty from reg_spare_part t  where t.id = ? ";



    @Transactional(readOnly = true)
    public long getDataCount(InHouseInputBean inHouseInputBean) throws Exception {
        long count = 0;
        new StringBuilder();
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(inHouseInputBean, dynamicClause);
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
    public List<Claim> getClaimSearchResults(InHouseInputBean inHouseInputBean) throws Exception {
        List<Claim> claimList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(inHouseInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (inHouseInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by t.createdtime desc ";
            } else {
                sortingStr = " order by t.createdtime " + inHouseInputBean.sortDirections.get(0);
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
                    " where t.is_in_house='1' and t.status ='WAR_APPROVE' and " + dynamicClause.toString() + sortingStr +
                    " limit " + inHouseInputBean.displayLength + " offset " + inHouseInputBean.displayStart;

            System.out.println("String Sql : "+sql);

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
                    System.out.println("Purchasing Date :" + rs.getDate("purchasing_date").toString());
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
//                    t.setCostType(rs.getString("cost_type").replace("LABOUR","labour").replace("MATERIALS","materials").replace("SUBLET","sublet"));
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
                    t.setClaimType(rs.getString("claim_type").replace("STOCK_VAN","stock van(Consignment)").replace("TO_BE_DELIVERED","to be delivered").replace("SOLD","Sold"));
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
                    System.out.println(rs.getBoolean("is_in_house"));
                    t.setInHouse(rs.getBoolean("is_in_house"));
                } catch (Exception e) {
                    t.setInHouse(false);
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

   

    private StringBuilder setDynamicClause(InHouseInputBean inHouseInputBean, StringBuilder dynamicClause) {
        try {
            if (inHouseInputBean.getId() != null && !inHouseInputBean.getId().isEmpty()) {
                dynamicClause.append(" 1=0 ");
                dynamicClause.append("or lower(t.id) like lower('%").append(inHouseInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.first_name) like lower('%").append(inHouseInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.last_name) like lower('%").append(inHouseInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.phone) like lower('%").append(inHouseInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.email) like lower('%").append(inHouseInputBean.getId()).append("%')");
                dynamicClause.append("or lower(t.dealership) like lower('%").append(inHouseInputBean.getId()).append("%')");
                dynamicClause.append("or t.status = '").append(inHouseInputBean.getStatus()).append("'");
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
    public List<WarrantyAttachments> getPdfFileList(String warrantyId) throws Exception {
        List<WarrantyAttachments> warrantyAttachmentsBeanList;
        try {
            List<Map<String, Object>> warrantyAttachmentsList = jdbcTemplate.queryForList(SQL_GET_LIST_ATTACHMENT_PDF, warrantyId);
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
    
}
