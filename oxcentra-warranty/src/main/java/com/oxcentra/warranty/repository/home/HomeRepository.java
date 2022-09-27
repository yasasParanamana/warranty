package com.oxcentra.warranty.repository.home;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private final String SQL_COUNT_STATUS = "select count(*) from reg_warranty_claim where status=?";
    private final String SQL_GET_LIST_STATUS_COUNT = "SELECT wc.status as sts , count(wc.id) as stsCt FROM reg_warranty_claim wc GROUP BY wc.status ";
    private final String SQL_GET_LIST_FAILING_AREA_COUNT = "SELECT wc.failing_area AS fail_area, count(wc.id) AS failcount FROM reg_warranty_claim wc GROUP BY wc.failing_area";
    private final String SQL_GET_LIST_FAILING_AREA_COST_COUNT = "SELECT wc.failing_area AS fail_area, SUM(wc.total_cost) AS cost_count FROM reg_warranty_claim wc GROUP BY wc.failing_area";

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

    @Transactional(readOnly = true)
    public List<SummaryBean> getStatusSummaryList() throws Exception {
        List<SummaryBean> statusBeanList;
        try {
            List<Map<String, Object>> statusSummaryList = jdbcTemplate.queryForList(SQL_GET_LIST_STATUS_COUNT);
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

    @Transactional(readOnly = true)
    public List<SummaryBean> getFailingAreaSummaryList() throws Exception {
        List<SummaryBean> failingAreaBeanList;
        try {
            List<Map<String, Object>> statusSummaryList = jdbcTemplate.queryForList(SQL_GET_LIST_FAILING_AREA_COUNT);
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
    public List<SummaryBean> getFailingAreaCostSummaryList() throws Exception {
        List<SummaryBean> failingAreaCostBeanList;
        try {
            List<Map<String, Object>> statusSummaryList = jdbcTemplate.queryForList(SQL_GET_LIST_FAILING_AREA_COST_COUNT);
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
