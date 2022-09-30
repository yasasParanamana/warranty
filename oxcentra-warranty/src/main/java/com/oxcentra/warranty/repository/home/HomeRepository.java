package com.oxcentra.warranty.repository.home;

import com.oxcentra.warranty.bean.audit.AuditTraceInputBean;
import com.oxcentra.warranty.bean.home.HomeInputBean;
import com.oxcentra.warranty.bean.home.SummaryBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.mapping.warranty.SpareParts;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.mapping.warranty.WarrantyAttachments;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import lombok.val;
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
import java.math.BigDecimal;
import java.sql.Blob;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Scope("prototype")
public class HomeRepository {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    private final String SQL_TOTAL_COUNT = "select count(*) from reg_warranty_claim ";
    private final String SQL_TOTAL_COST = "select sum(total_cost) from reg_warranty_claim ";
    private final String SQL_COUNT_STATUS = "select count(*) from reg_warranty_claim where status=?";
    private final String SQL_GET_LIST_STATUS_COUNT = "SELECT wc.status as sts , count(wc.id) as stsCt FROM reg_warranty_claim wc GROUP BY wc.status ";
    private final String SQL_GET_LIST_FAILING_AREA_COUNT = "SELECT wc.failing_area AS fail_area, count(wc.id) AS failcount FROM reg_warranty_claim wc GROUP BY wc.failing_area";
    private final String SQL_GET_LIST_FAILING_AREA_COST_COUNT = "SELECT wc.failing_area AS fail_area, SUM(wc.total_cost) AS cost_count FROM reg_warranty_claim wc GROUP BY wc.failing_area";


    /**
     * @Author yasas_p
     * @CreatedTime 2022-09-13 12:49:37 PM
     * @Version V1.00
     * @MethodName getRequestTotalCount
     * @MethodParams [ status]
     * @MethodDescription - This method get the request count
     */
    @Transactional(readOnly = true)
    public long getRequestTotalCount(HomeInputBean homeInputBean) throws Exception {

        long count = 0;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(homeInputBean, new StringBuilder());

            String sql
                    = "select count(*) "
                    + "FROM reg_warranty_claim wc "
                    + "where " + dynamicClause.toString() ;

            count = jdbcTemplate.queryForObject(sql, new Object[]{

            }, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception ex) {
            throw ex;
        }
        return count;
    }

    /**
     * @Author yasas_p
     * @CreatedTime 2022-09-13 12:49:37 PM
     * @Version V1.00
     * @MethodName getRequestTotalCost
     * @MethodParams [ status]
     * @MethodDescription - This method get the request count
     */
    @Transactional(readOnly = true)
    public String getRequestTotalCost(HomeInputBean homeInputBean) throws Exception {

        String cost = "0";
        try {
            StringBuilder dynamicClause = this.setDynamicClause(homeInputBean, new StringBuilder());

            String sql
                    = "select sum(total_cost) "
                    + "FROM reg_warranty_claim wc "
                    + "where " + dynamicClause.toString() ;

            cost = jdbcTemplate.queryForObject(sql, new Object[]{

            }, String.class);
        } catch (EmptyResultDataAccessException ere) {
            cost = "0";
        } catch (Exception ex) {
            throw ex;
        }
        return cost;
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
    public long getRequestCount(String status, HomeInputBean homeInputBean) throws Exception {

        long count = 0;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(homeInputBean, new StringBuilder());

            String sql
                    = "select count(*) "
                    + "FROM reg_warranty_claim wc "
                    + "where " + dynamicClause.toString() ;

            count = jdbcTemplate.queryForObject(sql, new Object[]{
            }, Long.class);
        } catch (EmptyResultDataAccessException ere) {
            count = 0;
        } catch (Exception ex) {
            throw ex;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<SummaryBean> getStatusSummaryList(HomeInputBean homeInputBean) throws Exception {
        List<SummaryBean> statusBeanList;
        try {

            StringBuilder dynamicClause = this.setDynamicClause(homeInputBean, new StringBuilder());
            //create groupBy
            String groupByStr = " GROUP BY wc.status ";

            String sql
                    = "SELECT wc.status as sts , count(wc.id) as stsCt "
                    + "FROM reg_warranty_claim wc "
                    + "where " + dynamicClause.toString() + groupByStr;

            List<Map<String, Object>> statusSummaryList = jdbcTemplate.queryForList(sql);
            statusBeanList = statusSummaryList.stream().map((record) -> {

                SummaryBean summaryBean = new SummaryBean();
                summaryBean.setStatus(record.get("sts").toString());
                summaryBean.setStatusCount(record.get("stsCt").toString());

                return summaryBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            statusBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return statusBeanList;
    }

    private StringBuilder setDynamicClause(HomeInputBean homeInputBean, StringBuilder dynamicClause) throws Exception {

        dynamicClause.append(" 1=1 ");
        try {
            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat formatter = new SimpleDateFormat("YYYY/MM/dd 00:00:00");

            if (homeInputBean.getFromDate() != null && !homeInputBean.getFromDate().isEmpty()) {
                String fromDate = homeInputBean.getFromDate();
                Date fDate = parser.parse(fromDate);
                //format the from date
                String formattedFromDate = formatter.format(fDate);
                //add to dynamic clause
                dynamicClause.append(" and wc.createdtime >='").append(formattedFromDate).append("'");
            }else{

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -30);

                String toDate = simpleDateFormat.format(cal.getTime());
                Date tDate = parser.parse(toDate);
                String formattedFromDate = formatter.format(tDate);

                dynamicClause.append(" and wc.createdtime >='").append(formattedFromDate).append("'");
            }

            if (homeInputBean.getToDate() != null && !homeInputBean.getToDate().isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(homeInputBean.getToDate()));
                //add one to calender instance
                calendar.add(Calendar.DATE, 1);
                //format the from date
                String toDate = simpleDateFormat.format(calendar.getTime());
                Date tDate = parser.parse(toDate);
                String formattedToDate = formatter.format(tDate);
                //add to dynamic clause
                dynamicClause.append(" and wc.createdtime <'").append(formattedToDate).append("'");
            }else{
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);

                String toDate = simpleDateFormat.format(cal.getTime());
                Date tDate = parser.parse(toDate);
                String formattedToDate = formatter.format(tDate);

                dynamicClause.append(" and wc.createdtime <'").append(formattedToDate).append("'");
            }

            if (homeInputBean.getStatus() != null && !homeInputBean.getStatus().isEmpty()) {
                dynamicClause.append(" and lower(wc.status) like lower('%").append(homeInputBean.getStatus()).append("%')");
            }

        } catch (Exception ex) {
            throw ex;
        }
        return dynamicClause;
    }


    @Transactional(readOnly = true)
    public List<SummaryBean> getFailingAreaSummaryList(HomeInputBean homeInputBean) throws Exception {
        List<SummaryBean> failingAreaBeanList;
        try {
            StringBuilder dynamicClause = this.setDynamicClause(homeInputBean, new StringBuilder());
            //create groupBy
            String groupByStr = " GROUP BY wc.failing_area ";

            String sql
                    = "SELECT wc.failing_area AS fail_area, count(wc.id) AS failcount "
                    + "FROM reg_warranty_claim wc "
                    + "where " + dynamicClause.toString() + groupByStr;

            List<Map<String, Object>> statusSummaryList = jdbcTemplate.queryForList(sql);
            failingAreaBeanList = statusSummaryList.stream().map((record) -> {

                SummaryBean summaryBean = new SummaryBean();
                summaryBean.setFailingArea(record.get("fail_area").toString());
                summaryBean.setFailingAreaCount(record.get("failcount").toString());

                return summaryBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            failingAreaBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return failingAreaBeanList;
    }

    @Transactional(readOnly = true)
    public List<SummaryBean> getFailingAreaCostSummaryList(HomeInputBean homeInputBean) throws Exception {
        List<SummaryBean> failingAreaCostBeanList;
        try {

            StringBuilder dynamicClause = this.setDynamicClause(homeInputBean, new StringBuilder());
            //create sorting order
            String groupByStr = " GROUP BY wc.failing_area ";

            String sql
                    = "SELECT wc.failing_area AS fail_area, SUM(wc.total_cost) AS cost_count "
                    + "FROM reg_warranty_claim wc "
                    + "where " + dynamicClause.toString() + groupByStr;

            List<Map<String, Object>> statusSummaryList = jdbcTemplate.queryForList(sql);
            failingAreaCostBeanList = statusSummaryList.stream().map((record) -> {

                SummaryBean summaryBean = new SummaryBean();
                summaryBean.setFailingAreaCost(record.get("fail_area").toString());
                summaryBean.setFailingAreaCostCount(record.get("cost_count").toString());

                return summaryBean;
            }).collect(Collectors.toList());
        } catch (EmptyResultDataAccessException ere) {
            //handle the empty result data access exception
            failingAreaCostBeanList = new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
        return failingAreaCostBeanList;
    }


}
