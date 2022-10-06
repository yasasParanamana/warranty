package com.oxcentra.warranty.repository.warranty.critical;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.critical.CriticalInputBean;
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
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Scope("prototype")
public class CriticalRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_GET_LIST_DATA_COUNT = "select count(*) from reg_warranty_claim t left outer join status s on s.statuscode=t.status where is_critical='1' and ";
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
    private final String SQL_GET_LIST_ATTACHMENT_PDF = "select t.attachment_id,t.warranty_id,t.file_name,t.file_format,CONVERT(t.attachment_file USING UTF8) as attachmentFile,t.createdtime from reg_warranty_attachments t  where t.warranty_id = ? and t.attachment_type=? ";
    private final String SQL_GET_SPARE_PARTS = "select t.id,t.warranty_id,t.spare_part_type,t.spare_part_name,t.qty from reg_spare_part t  where t.id = ? ";


    @Transactional(readOnly = true)
    public long getDataCount(CriticalInputBean criticalInputBean) throws Exception {
        long count = 0;
        new StringBuilder();
        try {
            StringBuilder dynamicClause = new StringBuilder(SQL_GET_LIST_DATA_COUNT);
            //create the where clause
            dynamicClause = this.setDynamicClause(criticalInputBean, dynamicClause);
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
    public List<Claim> getClaimSearchResults(CriticalInputBean criticalInputBean) throws Exception {
        List<Claim> claimList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(criticalInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = "";
            if (criticalInputBean.sortedColumns.get(0) == 0) {
                sortingStr = " order by t.createdtime desc ";
            } else {
                sortingStr = " order by t.createdtime " + criticalInputBean.sortDirections.get(0);
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
                    " where t.is_critical = '1' and " + dynamicClause.toString() + sortingStr +
                    " limit " + criticalInputBean.displayLength + " offset " + criticalInputBean.displayStart;

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


    private StringBuilder setDynamicClause(CriticalInputBean criticalInputBean, StringBuilder dynamicClause) throws Exception {
        try {
            dynamicClause.append(" 1=1 ");

            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat formatter = new SimpleDateFormat("YYYY/MM/dd 00:00:00");

            if (criticalInputBean.getFromDate() != null && !criticalInputBean.getFromDate().isEmpty()) {
                String fromDate = criticalInputBean.getFromDate();
                Date fDate = parser.parse(fromDate);
                //format the from date
                String formattedFromDate = formatter.format(fDate);
                //add to dynamic clause
                dynamicClause.append(" and t.createdtime >='").append(formattedFromDate).append("'");
            } else {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 0);

                String toDate = simpleDateFormat.format(cal.getTime());
                Date tDate = parser.parse(toDate);
                String formattedFromDate = formatter.format(tDate);

                dynamicClause.append(" and t.createdtime >='").append(formattedFromDate).append("'");
            }

            if (criticalInputBean.getToDate() != null && !criticalInputBean.getToDate().isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(criticalInputBean.getToDate()));
                //add one to calender instance
                calendar.add(Calendar.DATE, 1);
                //format the from date
                String toDate = simpleDateFormat.format(calendar.getTime());
                Date tDate = parser.parse(toDate);
                String formattedToDate = formatter.format(tDate);
                //add to dynamic clause
                dynamicClause.append(" and t.createdtime <'").append(formattedToDate).append("'");
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);

                String toDate = simpleDateFormat.format(cal.getTime());
                Date tDate = parser.parse(toDate);
                String formattedToDate = formatter.format(tDate);

                dynamicClause.append(" and t.createdtime <'").append(formattedToDate).append("'");
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
    public List<WarrantyAttachments> getFileList(String warrantyId, String attachmentType) throws Exception {
        List<WarrantyAttachments> warrantyAttachmentsBeanList;
        try {
            List<Map<String, Object>> warrantyAttachmentsList = jdbcTemplate.queryForList(SQL_GET_LIST_ATTACHMENT_PDF, warrantyId, attachmentType);
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

    @Transactional(readOnly = true)
    public List<Claim> getClaimSearchResultListForReport(CriticalInputBean criticalInputBean) throws Exception {
        List<Claim> claimList = null;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(criticalInputBean, new StringBuilder());
            //create sorting order
            String sortingStr = " order by t.lastupdatedtime desc ";

            String sql =
                    "select " +
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
                            "st.description as statusdes,  " +
                            "t.createduser,  " +
                            "t.createdtime,  " +
                            "t.lastupdatedtime,  " +
                            "t.lastupdateduser,  " +
                            "s.supplier_phone,  " +
                            "s.supplier_email,  " +
                            "s.supplier_address,  " +
                            "t.is_in_house  " +
                            "from reg_warranty_claim t " +
                            "LEFT OUTER JOIN reg_dealership r ON r.dealership_code = t.dealership " +
                            "LEFT OUTER JOIN status st on st.statuscode=t.status " +
                            "LEFT OUTER JOIN reg_supplier s ON s.supplier_code = t.supplier " +
                            "where t.is_critical = '1' and " + dynamicClause.toString() + sortingStr;

            claimList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Claim claim = new Claim();

                try {
                    claim.setId(rs.getString("id"));
                } catch (Exception e) {
                    claim.setId("--");
                }

                try {
                    if (rs.getString("first_name") != null && !rs.getString("first_name").isEmpty()) {
                        claim.setFirstName(rs.getString("first_name"));
                    } else {
                        claim.setFirstName("--");
                    }
                } catch (Exception e) {
                    claim.setFirstName("--");
                }

                try {
                    if (rs.getString("last_name") != null && !rs.getString("last_name").isEmpty()) {
                        claim.setLastName(rs.getString("last_name"));
                    } else {
                        claim.setLastName("--");
                    }
                } catch (Exception e) {
                    claim.setLastName("--");
                }

                try {
                    if (rs.getString("phone") != null && !rs.getString("phone").isEmpty()) {
                        claim.setPhone(rs.getString("phone"));
                    } else {
                        claim.setPhone("--");
                    }
                } catch (Exception e) {
                    claim.setPhone("--");
                }

                try {
                    if (rs.getString("email") != null && !rs.getString("email").isEmpty()) {
                        claim.setEmail(rs.getString("email"));
                    } else {
                        claim.setEmail("--");
                    }
                } catch (Exception e) {
                    claim.setEmail(null);
                }

                try {
                    if (rs.getString("dealership") != null && !rs.getString("dealership").isEmpty()) {
                        claim.setDealership(rs.getString("dealership"));
                    } else {
                        claim.setDealership("--");
                    }
                } catch (Exception e) {
                    claim.setDealership("--");
                }

                try {
                    if (rs.getString("statusdes") != null && !rs.getString("statusdes").isEmpty()) {
                        claim.setStatus(rs.getString("statusdes"));
                    } else {
                        claim.setStatus("--");
                    }
                } catch (Exception e) {
                    claim.setStatus("--");
                }

                try {
                    if (rs.getString("createduser") != null && !rs.getString("createduser").isEmpty()) {
                        claim.setCreatedUser(rs.getString("createduser"));
                    } else {
                        claim.setCreatedUser("--");
                    }
                } catch (Exception e) {
                    claim.setCreatedUser("--");
                }

                try {
                    if (rs.getString("createdtime") != null && !rs.getString("createdtime").isEmpty()) {
                        claim.setCreatedTime(rs.getDate("createdtime"));
                    } else {
                        claim.setCreatedTime(null);
                    }
                } catch (Exception e) {
                    claim.setCreatedTime(null);
                }

                try {
                    if (rs.getString("lastupdateduser") != null && !rs.getString("lastupdateduser").isEmpty()) {
                        claim.setLastUpdatedUser(rs.getString("lastupdateduser"));
                    } else {
                        claim.setLastUpdatedUser("--");
                    }
                } catch (Exception e) {
                    claim.setLastUpdatedUser("--");
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

}
