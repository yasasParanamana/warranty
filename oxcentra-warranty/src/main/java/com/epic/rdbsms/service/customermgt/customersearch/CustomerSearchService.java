package com.epic.rdbsms.service.customermgt.customersearch;


import com.epic.rdbsms.bean.common.TempAuthRecBean;
import com.epic.rdbsms.bean.customermgt.customersearch.CustomerSearchInputBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.customermgt.CustomerSearch;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.repository.customermgt.customersearch.CustomerSearchRepository;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.varlist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Scope("prototype")
public class CustomerSearchService {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    Common common;

    @Autowired
    Audittrace audittrace;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CustomerSearchRepository customerSearchRepository;

    private final String fields = "CustomerSearchInputBean Customer ID|Account No|Status|Waive-off Status|Created Time|Last Updated Time|Last Updated User";

    public long getCount(CustomerSearchInputBean customerSearchInputBean) {
        long count = 0;
        try {
            count = customerSearchRepository.getDataCount(customerSearchInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<CustomerSearch> getCustomerSearchResultList(CustomerSearchInputBean customerSearchInputBean) {
        List<CustomerSearch> customerSearchList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.CUSTOMER_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CUSTOMER_SEARCH_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get customer search list.");
            //Get section search list.
            customerSearchList = customerSearchRepository.getCustomerSearchList(customerSearchInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return customerSearchList;
    }

    public CustomerSearch getCustomerSearch(String customerId) {
        CustomerSearch customerSearch;
        try {
            customerSearch = customerSearchRepository.getCustomerSearch(customerId);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return customerSearch;
    }

    public StringBuffer makeCsvReport(CustomerSearchInputBean customerSearchInputBean) throws Exception{
        StringBuffer stringBuffer = new StringBuffer();
        try {
            List<CustomerSearch> customerSearchList = this.getCustomerSearchResultListForReport(customerSearchInputBean);
            if (!customerSearchList.isEmpty()) {
                stringBuffer.append("ID");
                stringBuffer.append(',');

                stringBuffer.append("Customer ID");
                stringBuffer.append(',');

                stringBuffer.append("Identification");
                stringBuffer.append(',');

                stringBuffer.append("Primary Account Number");
                stringBuffer.append(',');

                stringBuffer.append("Customer Category");
                stringBuffer.append(',');

                stringBuffer.append("Customer Name");
                stringBuffer.append(',');

                stringBuffer.append("DOB");
                stringBuffer.append(',');

                stringBuffer.append("Mobile Number");
                stringBuffer.append(',');

                stringBuffer.append("Status");
                stringBuffer.append(',');

                stringBuffer.append("Waive-off Status");
                stringBuffer.append(',');

                stringBuffer.append("Branch");
                stringBuffer.append(',');

                stringBuffer.append("Account Type");
                stringBuffer.append(',');

                stringBuffer.append("Created Date And Time");
                stringBuffer.append(',');

                stringBuffer.append("Last Updated Date And Time");
                stringBuffer.append('\n');

                int i = 0;
                for (CustomerSearch customerSearch : customerSearchList) {
                    i++;
                    try {
                        stringBuffer.append(i);
                        stringBuffer.append(",");
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getCustomerId() != null && !customerSearch.getCustomerId().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(customerSearch.getCustomerId()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getIdentification() != null && !customerSearch.getIdentification().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(customerSearch.getIdentification()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getAccountNo() != null && !customerSearch.getAccountNo().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(customerSearch.getAccountNo()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getCustomerCategory() != null && !customerSearch.getCustomerCategory().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(customerSearch.getCustomerCategory()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getCustomerName() != null && !customerSearch.getCustomerName().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(customerSearch.getCustomerName()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getDob() != null) {
                            String dob = common.formatDateToStringCsvFile(customerSearch.getDob());
                            stringBuffer.append(common.appendSpecialCharacterToCsv(dob));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getMobileNo() != null && !customerSearch.getMobileNo().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(customerSearch.getMobileNo()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getStatus() != null && !customerSearch.getStatus().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(customerSearch.getStatus()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getWaiveoffstatus() != null && !customerSearch.getWaiveoffstatus().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(customerSearch.getWaiveoffstatus()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getBranch() != null && !customerSearch.getBranch().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(customerSearch.getBranch()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getAccountType() != null && !customerSearch.getAccountType().isEmpty()) {
                            stringBuffer.append(common.appendSpecialCharacterToCsv(customerSearch.getAccountType()));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getCreatedTime() != null) {
                            String createdDateTime = common.formatDateToStringCsvFile(customerSearch.getCreatedTime());
                            stringBuffer.append(common.appendSpecialCharacterToCsv(createdDateTime));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    try {
                        if (customerSearch.getLastUpdatedTime() != null) {
                            String lastUpdatedTime = common.formatDateToStringCsvFile(customerSearch.getLastUpdatedTime());
                            stringBuffer.append(common.appendSpecialCharacterToCsv(lastUpdatedTime));
                            stringBuffer.append(",");
                        } else {
                            stringBuffer.append("--");
                            stringBuffer.append(",");
                        }
                    } catch (NullPointerException E) {
                        stringBuffer.append("--");
                        stringBuffer.append(",");
                    }

                    stringBuffer.append('\n');

                }
                stringBuffer.append('\n');

                stringBuffer.append("Identification : ");
                stringBuffer.append(common.replaceEmptyorNullStringToALL(customerSearchInputBean.getIdentification()));
                stringBuffer.append('\n');

                stringBuffer.append("Customer ID : ");
                stringBuffer.append(common.replaceEmptyorNullStringToALL(customerSearchInputBean.getCustomerid()));
                stringBuffer.append('\n');

                stringBuffer.append("Account Number : ");
                stringBuffer.append(common.replaceEmptyorNullStringToALL(customerSearchInputBean.getAccountno()));
                stringBuffer.append('\n');

                stringBuffer.append("Mobile Number : ");
                stringBuffer.append(common.replaceEmptyorNullStringToALL(customerSearchInputBean.getMobileno()));
                stringBuffer.append('\n');

                stringBuffer.append('\n');
            }
        } catch (Exception e) {
            throw e;
        }
        return stringBuffer;
    }

    private List<CustomerSearch> getCustomerSearchResultListForReport(CustomerSearchInputBean customerSearchInputBean) throws Exception {
        List<CustomerSearch> customerSearchList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.CUSTOMER_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CUSTOMER_SEARCH_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Download customer search list.");
            //get sms outbox search list for report
            customerSearchList = customerSearchRepository.getCustomerSearchListForReport(customerSearchInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return customerSearchList;
    }

    public long getDataCountDual(CustomerSearchInputBean customerSearchInputBean) {
        long count = 0;
        try {
            count = customerSearchRepository.getDataCountDual(customerSearchInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getSectionSearchResultsDual(CustomerSearchInputBean customerSearchInputBean) {
        List<TempAuthRec> sectionDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.CUSTOMER_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CUSTOMER_SEARCH_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get customer search dual authentication search list.");
            //Get section dual authentication search list
            sectionDualList = customerSearchRepository.getCustomerSearchResultsDual(customerSearchInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return sectionDualList;
    }

    public String confirmCustomer(String id) {
        String message = "";
        String auditDescription = "";
        CustomerSearchInputBean customerSearchInputBean = null;
        CustomerSearch existingCustomer = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                customerSearchInputBean = new CustomerSearchInputBean();
                customerSearchInputBean.setIdentification(tempAuthRecBean.getKey1());
                customerSearchInputBean.setCustomerid(tempAuthRecBean.getKey2());
                customerSearchInputBean.setAccountno(tempAuthRecBean.getKey3());
                customerSearchInputBean.setMobileno(tempAuthRecBean.getKey4());
                customerSearchInputBean.setStatus(tempAuthRecBean.getKey5());
                customerSearchInputBean.setWaiveoffstatus(tempAuthRecBean.getKey6());
                customerSearchInputBean.setRemark(tempAuthRecBean.getKey7());
                customerSearchInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                customerSearchInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing session
                try {
                    String customerid = customerSearchInputBean.getCustomerid();
                    existingCustomer = customerSearchRepository.getCustomerSearch(customerid);
                } catch (EmptyResultDataAccessException e) {
                    existingCustomer = null;
                }

                if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingCustomer != null) {
                        message = customerSearchRepository.updateCustomer(customerSearchInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                }

                //if task db operation success, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success,insert the audit
                    if (message.isEmpty()) {
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on task (task code: ").append(customerSearchInputBean.getCustomerid())
                                .append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" approved by ")
                                .append(sessionBean.getUsername());

                        auditDescription = auditDesBuilder.toString();
                    } else {
                        message = MessageVarList.COMMON_ERROR_PROCESS;
                    }
                } else {
                    message = MessageVarList.COMMON_ERROR_PROCESS;
                }
            } else {
                message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CUSTOMER_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
            message = MessageVarList.COMMON_ERROR_ALRADY_USE;
            System.out.println("service");
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.CUSTOMER_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.CUSTOMER_SEARCH_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getCustomerAsString(customerSearchInputBean, false));
                audittrace.setOldvalue(this.getCustomerAsString(existingCustomer, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String getCustomerAsString(CustomerSearchInputBean customerSearchInputBean, boolean checkChanges) {
        StringBuilder sectionStringBuilder = new StringBuilder();
        try {
            if (customerSearchInputBean != null) {

                if (customerSearchInputBean.getCustomerid() != null && !customerSearchInputBean.getCustomerid().isEmpty()) {
                    sectionStringBuilder.append(customerSearchInputBean.getCustomerid());
                } else {
                    sectionStringBuilder.append("error");
                }

                sectionStringBuilder.append("|");
                if (customerSearchInputBean.getAccountno() != null && !customerSearchInputBean.getAccountno().isEmpty()) {
                    sectionStringBuilder.append(customerSearchInputBean.getAccountno());
                } else {
                    sectionStringBuilder.append("--");
                }

                sectionStringBuilder.append("|");
                if (customerSearchInputBean.getStatus() != null && !customerSearchInputBean.getStatus().isEmpty()) {
                    sectionStringBuilder.append(customerSearchInputBean.getStatus());
                } else {
                    sectionStringBuilder.append("--");
                }

                sectionStringBuilder.append("|");
                if (customerSearchInputBean.getWaiveoffstatus() != null && !customerSearchInputBean.getWaiveoffstatus().isEmpty()) {
                    sectionStringBuilder.append(customerSearchInputBean.getWaiveoffstatus());
                } else {
                    sectionStringBuilder.append("--");
                }

                if (!checkChanges) {
                    sectionStringBuilder.append("|");
                    if (customerSearchInputBean.getCreatedTime() != null) {
                        sectionStringBuilder.append(common.formatDateToString(customerSearchInputBean.getCreatedTime()));
                    } else {
                        sectionStringBuilder.append("--");
                    }

                    sectionStringBuilder.append("|");
                    if (customerSearchInputBean.getLastUpdatedTime() != null) {
                        sectionStringBuilder.append(common.formatDateToString(customerSearchInputBean.getLastUpdatedTime()));
                    } else {
                        sectionStringBuilder.append("--");
                    }

                    sectionStringBuilder.append("|");
                    if (customerSearchInputBean.getLastUpdatedUser() != null) {
                        sectionStringBuilder.append(customerSearchInputBean.getLastUpdatedUser());
                    } else {
                        sectionStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return sectionStringBuilder.toString();
    }

    private String getCustomerAsString(CustomerSearch customerSearch, boolean checkChanges) {
        StringBuilder sectionStringBuilder = new StringBuilder();
        try {
            if (customerSearch != null) {
                if (customerSearch.getCustomerId()!= null && !customerSearch.getCustomerId().isEmpty()) {
                    sectionStringBuilder.append(customerSearch.getCustomerId());
                } else {
                    sectionStringBuilder.append("error");
                }

                sectionStringBuilder.append("|");
                if (customerSearch.getAccountNo() != null && !customerSearch.getAccountNo().isEmpty()) {
                    sectionStringBuilder.append(customerSearch.getAccountNo());
                } else {
                    sectionStringBuilder.append("--");
                }

                sectionStringBuilder.append("|");
                if (customerSearch.getStatuscode() != null && !customerSearch.getStatuscode().isEmpty()) {
                    sectionStringBuilder.append(customerSearch.getStatuscode());
                } else {
                    sectionStringBuilder.append("--");
                }

                sectionStringBuilder.append("|");
                if (customerSearch.getWaiveoffstatus() != null && !customerSearch.getWaiveoffstatus().isEmpty()) {
                    sectionStringBuilder.append(customerSearch.getWaiveoffstatus());
                } else {
                    sectionStringBuilder.append("--");
                }

                if (!checkChanges) {
                    sectionStringBuilder.append("|");
                    if (customerSearch.getCreatedTime() != null) {
                        sectionStringBuilder.append(common.formatDateToString(customerSearch.getCreatedTime()));
                    } else {
                        sectionStringBuilder.append("--");
                    }


                    sectionStringBuilder.append("|");
                    if (customerSearch.getLastUpdatedTime() != null) {
                        sectionStringBuilder.append(common.formatDateToString(customerSearch.getLastUpdatedTime()));
                    } else {
                        sectionStringBuilder.append("--");
                    }

                    sectionStringBuilder.append("|");
                    if (customerSearch.getLastUpdatedUser() != null) {
                        sectionStringBuilder.append(customerSearch.getLastUpdatedUser());
                    } else {
                        sectionStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return sectionStringBuilder.toString();
    }

    public String rejectCustomer(String id) {
        String message = "";
        String auditDescription = "";
        try {
            //get temp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on customer (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
                    auditDescription = auditDesBuilder.toString();
                } else {
                    message = MessageVarList.COMMON_ERROR_PROCESS;
                }
            } else {
                message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
            }
        } catch (Exception ex) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.CUSTOMER_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.CUSTOMER_SEARCH_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String updateCustomer(CustomerSearchInputBean customerSearchInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        CustomerSearch existingSection = null;
        try {
            existingSection = customerSearchRepository.getCustomerSearch(customerSearchInputBean.getCustomerid());
            if (existingSection != null) {
                //check changed values
                String oldValueAsString = this.getCustomerAsString(existingSection, true);
                String newValueAsString = this.getCustomerAsString(customerSearchInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();
                    customerSearchInputBean.setLastUpdatedTime(currentDate);
                    customerSearchInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CUSTOMER_SEARCH_MGT_PAGE)) {
                        auditDescription = "Requested to update customer (customerid: " + customerSearchInputBean.getCustomerid() + ")";
                        message = this.insertDualAuthRecord(customerSearchInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Customer (customerid: " + customerSearchInputBean.getCustomerid() + ") updated by " + sessionBean.getUsername();
                        message = customerSearchRepository.updateCustomer(customerSearchInputBean);
                    }
                }
            } else {
                message = MessageVarList.CUSTOMER_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.CUSTOMER_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CUSTOMER_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.CUSTOMER_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.CUSTOMER_SEARCH_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getCustomerAsString(existingSection, false));
                audittrace.setNewvalue(this.getCustomerAsString(customerSearchInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String insertDualAuthRecord(CustomerSearchInputBean customerSearchInputBean, String taskCode) throws Exception{
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(customerSearchInputBean.getCustomerid(), PageVarList.CUSTOMER_SEARCH_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.CUSTOMER_SEARCH_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(customerSearchInputBean.getIdentification());
                tempAuthRecBean.setKey2(customerSearchInputBean.getCustomerid());
                tempAuthRecBean.setKey3(customerSearchInputBean.getAccountno());
                tempAuthRecBean.setKey4(customerSearchInputBean.getMobileno());
                tempAuthRecBean.setKey5(customerSearchInputBean.getStatus());
                tempAuthRecBean.setKey6(customerSearchInputBean.getWaiveoffstatus());
                tempAuthRecBean.setKey7(customerSearchInputBean.getRemark());
                //insert dual auth record
                message = commonRepository.insertDualAuthRecordSQL(tempAuthRecBean);
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
}
