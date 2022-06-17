package com.oxcentra.rdbsms.validators.sysconfigmgt.channeltxntype;

import com.oxcentra.rdbsms.bean.sysconfigmgt.channeltxntype.ChannelTxnTypeInputBean;
import com.oxcentra.rdbsms.util.validation.Validation;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Namila Withanage on 11/19/2021
 */
@Component
public class ChannelTxnTypeValidator implements Validator {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Validation validation;

    @Override
    public boolean supports(Class<?> aClass) {
        return ChannelTxnTypeInputBean.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {

            ValidationUtils.rejectIfEmpty(errors, "txntype", MessageVarList.CHANNEL_TXN_TYPE_EMPTY_TXNTYPE, "Txntype can not be empty.");
            ValidationUtils.rejectIfEmpty(errors, "channel", MessageVarList.CHANNEL_TXN_TYPE_EMPTY_CHANNEL, "Channel can not be empty.");
            ValidationUtils.rejectIfEmpty(errors, "template", MessageVarList.CHANNEL_TXN_TYPE_EMPTY_TEMPLATE, "Template can not be empty.");
            ValidationUtils.rejectIfEmpty(errors, "status", MessageVarList.CHANNEL_TXN_TYPE_EMPTY_STATUS, "Status can not be empty.");

            if (o.getClass().equals(ChannelTxnTypeInputBean.class)) {

            } else {
                errors.reject(commonVarList.COMMON_VALIDATION_INVALID_BEANTYPE);
            }
        } catch (Exception ex) {
            logger.error("Exception : ", ex);
            errors.reject(commonVarList.COMMON_VALIDATION_FAIL_CODE);
        }
    }

}
