package com.oxcentra.warranty.repository.home;

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
