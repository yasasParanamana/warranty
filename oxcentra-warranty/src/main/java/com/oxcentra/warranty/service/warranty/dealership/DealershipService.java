package com.oxcentra.warranty.service.warranty.dealership;

import com.oxcentra.warranty.bean.common.TempAuthRecBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.dealership.DealershipInputBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.warranty.mapping.warranty.Dealership;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.repository.warranty.dealership.DealershipRepository;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.varlist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.oxcentra.warranty.util.varlist.MessageVarList.*;
import static com.oxcentra.warranty.util.varlist.PageVarList.DEALERSHIP_MGT_PAGE;
import static com.oxcentra.warranty.util.varlist.SectionVarList.SECTION_SYS_CONFIGURATION_MGT;
import static com.oxcentra.warranty.util.varlist.TaskVarList.VIEW_TASK;

@Service
@Scope("prototype")
public class DealershipService {

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
    DealershipRepository dealershipRepository;

    private final String fields = "DealershipInputBean Dealership Code|Description|Status|Sort Key|Created Time|Last Updated Time|Last Updated User";

    public long getCount(DealershipInputBean dealershipInputBean) {
        long count = 0;
        try {
            count = dealershipRepository.getDataCount(dealershipInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Dealership> getDealershipSearchResultList(DealershipInputBean dealershipInputBean) {
        List<Dealership> dealershipList;
        try {
            //set audit trace values
            audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(DEALERSHIP_MGT_PAGE);
            audittrace.setTask(VIEW_TASK);
            audittrace.setDescription("Get dealership search list.");
            //Get dealership search list.
            dealershipList = dealershipRepository.getDealershipSearchList(dealershipInputBean);
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
        return dealershipList;
    }

    public long getDataCountDual(DealershipInputBean dealershipInputBean) {
        long count = 0;
        try {
            count = dealershipRepository.getDataCountDual(dealershipInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getDealershipSearchResultsDual(DealershipInputBean dealershipInputBean) {
        List<TempAuthRec> dealershipDualList = null;
        try {
            //set audit trace values
            audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(DEALERSHIP_MGT_PAGE);
            audittrace.setTask(VIEW_TASK);
            audittrace.setDescription("Get dealership dual authentication search list.");
            //Get dealership dual authentication search list
//            dealershipDualList = dealershipRepository.getDealershipSearchResultsDual(dealershipInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return dealershipDualList;
    }

    public String insertDealership(DealershipInputBean dealershipInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            Dealership dealership = dealershipRepository.getDealership(dealershipInputBean.getDealershipCode());
            if (dealership == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                dealershipInputBean.setCreatedTime(currentDate);
                dealershipInputBean.setLastUpdatedTime(currentDate);
                dealershipInputBean.setLastUpdatedUser(lastUpdatedUser);
                dealershipInputBean.setCreatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(DEALERSHIP_MGT_PAGE)) {
                    auditDescription = "Requested to add dealership (code: " + dealershipInputBean.getDealershipCode() + ")";
//                    message = this.insertDualAuthRecord(DealershipInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Dealership (code: " + dealershipInputBean.getDealershipCode()+ ") added by " + sessionBean.getUsername();
                    message = dealershipRepository.insertDealership(dealershipInputBean);
                }
            } else {
                message = SUPPLIER_MGT_ADDED_SUCCESSFULLY;
                auditDescription = messageSource.getMessage(MessageVarList.SUPPLIER_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = SUPPLIER_MGT_ALREADY_EXISTS;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception x) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(DEALERSHIP_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
//                audittrace.setNewvalue(this.getDealershipAsString(DealershipInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public Dealership getDealership(String dealershipCode) {
        Dealership dealership;
        try {
            dealership = dealershipRepository.getDealership(dealershipCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return dealership;
    }

    public String updateDealership(DealershipInputBean dealershipInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        Dealership existingDealership = null;
        try {
            existingDealership = dealershipRepository.getDealership(dealershipInputBean.getDealershipCode());
            if (existingDealership != null) {
                //check changed values
                String oldValueAsString = this.getDealershipAsString(existingDealership, true);
                String newValueAsString = this.getDealershipAsString(dealershipInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();
                    dealershipInputBean.setLastUpdatedTime(currentDate);
                    dealershipInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(DEALERSHIP_MGT_PAGE)) {
                        auditDescription = "Requested to update dealership (code: " + dealershipInputBean.getDealershipCode() + ")";
//                        message = this.insertDualAuthRecord(DealershipInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Dealership (code: " + dealershipInputBean.getDealershipCode() + ") updated by " + sessionBean.getUsername();
                        message = dealershipRepository.updateDealership(dealershipInputBean);
                    }
                }
            } else {
                message = MessageVarList.SUPPLIER_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.SUPPLIER_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.SUPPLIER_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(DEALERSHIP_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getDealershipAsString(existingDealership, false));
                audittrace.setNewvalue(this.getDealershipAsString(dealershipInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteDealership(String dealershipCode) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(DEALERSHIP_MGT_PAGE)) {
                //get the existing dealership
                Dealership dealership = dealershipRepository.getDealership(dealershipCode);
                if (dealership != null) {
                    DealershipInputBean dealershipInputBean = new DealershipInputBean();
                    //set the values to input bean
                    dealershipInputBean.setDealershipCode(dealership.getDealershipCode());
                    dealershipInputBean.setDealershipName(dealership.getDealershipName());
                    dealershipInputBean.setStatus(dealership.getStatus());
                    dealershipInputBean.setCreatedTime(dealership.getCreatedTime());
                    dealershipInputBean.setLastUpdatedTime(dealership.getLastUpdatedTime());
                    dealershipInputBean.setLastUpdatedUser(dealership.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted dealership (code: " + dealershipCode + ")";
                    //insert the record to dual auth table
//                    message = this.insertDualAuthRecord(DealershipInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = SUPPLIER_MGT_NORECORD_FOUND;
                }
            } else {
                message = dealershipRepository.deleteDealership(dealershipCode);
                auditDescription = "Dealership (Code: " + dealershipCode + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = SUPPLIER_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
            message = MessageVarList.COMMON_ERROR_ALRADY_USE;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
//                audittrace.setSection(SectionVarList.SUPPLIER_SYS_CONFIGURATION_MGT);
                audittrace.setPage(DEALERSHIP_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmDealership(String dealershipCode) {
        String message = "";
        String auditDescription = "";
        DealershipInputBean dealershipInputBean = null;
        Dealership existingDealership = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(dealershipCode);
            if (tempAuthRecBean != null) {
                dealershipInputBean = new DealershipInputBean();
//                dealershipInputBean.setId(tempAuthRecBean.getKey1());
//                dealershipInputBean.setDescription(tempAuthRecBean.getKey2());
//                dealershipInputBean.setStatus(tempAuthRecBean.getKey3());
//                dealershipInputBean.setSortKey(tempAuthRecBean.getKey4());
                dealershipInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                dealershipInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing session
                try {
//                    String code = DealershipInputBean.getId();
                    existingDealership = dealershipRepository.getDealership(dealershipCode);
                } catch (EmptyResultDataAccessException e) {
                    existingDealership = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingDealership == null) {
                        dealershipInputBean.setCreatedUser(sessionBean.getUsername());
                        dealershipInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        message = dealershipRepository.insertDealership(dealershipInputBean);
                    } else {
                        message = MessageVarList.SUPPLIER_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingDealership != null) {
                        message = dealershipRepository.updateDealership(dealershipInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingDealership != null) {
                        message = dealershipRepository.deleteDealership(dealershipInputBean.getDealershipCode());
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                }

                //if task db operation success, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(dealershipCode, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success,insert the audit
                    if (message.isEmpty()) {
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on task (task code: ").append(dealershipInputBean.getDealershipCode())
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
            message = MessageVarList.SUPPLIER_MGT_NORECORD_FOUND;
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
                audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(DEALERSHIP_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getDealershipAsString(dealershipInputBean, false));
                audittrace.setOldvalue(this.getDealershipAsString(existingDealership, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectDealership(String dealershipCode) {
        String message = "";
        String auditDescription = "";
        try {
            //get temp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(dealershipCode);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(dealershipCode, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on dealership (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setSection(SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(DEALERSHIP_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

//    private String insertDualAuthRecord(DealershipInputBean DealershipInputBean, String taskCode) throws Exception {
//        String message = "";
//        try {
//            long count = commonRepository.getTempAuthRecordCount(DealershipInputBean.getId(), DEALERSHIP_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
//            if (count > 0) {
//                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
//            } else {
//                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
//                tempAuthRecBean.setPage(DEALERSHIP_MGT_PAGE);
//                tempAuthRecBean.setTask(taskCode);
//                tempAuthRecBean.setKey1(DealershipInputBean.getDealershipCode());
//                tempAuthRecBean.setKey2(DealershipInputBean.getDescription());
//                tempAuthRecBean.setKey3(DealershipInputBean.getStatus());
//                tempAuthRecBean.setKey4(String.valueOf(DealershipInputBean.getSortKey()));
//                //insert dual auth record
//                message = commonRepository.insertDualAuthRecordSQL(tempAuthRecBean);
//            }
//        } catch (EmptyResultDataAccessException ere) {
//            throw ere;
//        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
//            throw cve;
//        } catch (Exception e) {
//            throw e;
//        }
//        return message;
//    }

    private String getDealershipAsString(DealershipInputBean dealershipInputBean, boolean checkChanges) {
        StringBuilder dealershipStringBuilder = new StringBuilder();
        try {
            if (dealershipInputBean != null) {

                if (dealershipInputBean.getDealershipName()!= null && !dealershipInputBean.getDealershipName().isEmpty()) {
                    dealershipStringBuilder.append(dealershipInputBean.getDealershipName());
                } else {
                    dealershipStringBuilder.append("error");
                }

                dealershipStringBuilder.append("|");
                if (dealershipInputBean.getDealershipPhone() != null && !dealershipInputBean.getDealershipPhone().isEmpty()) {
                    dealershipStringBuilder.append(dealershipInputBean.getDealershipPhone());
                } else {
                    dealershipStringBuilder.append("--");
                }

                dealershipStringBuilder.append("|");
                if (dealershipInputBean.getStatus() != null && !dealershipInputBean.getStatus().isEmpty()) {
                    dealershipStringBuilder.append(dealershipInputBean.getStatus());
                } else {
                    dealershipStringBuilder.append("--");
                }

                if (!checkChanges) {
                    dealershipStringBuilder.append("|");
                    if (dealershipInputBean.getCreatedTime() != null) {
                        dealershipStringBuilder.append(common.formatDateToString(dealershipInputBean.getCreatedTime()));
                    } else {
                        dealershipStringBuilder.append("--");
                    }

                    dealershipStringBuilder.append("|");
                    if (dealershipInputBean.getLastUpdatedTime() != null) {
                        dealershipStringBuilder.append(common.formatDateToString(dealershipInputBean.getLastUpdatedTime()));
                    } else {
                        dealershipStringBuilder.append("--");
                    }

                    dealershipStringBuilder.append("|");
                    if (dealershipInputBean.getLastUpdatedUser() != null) {
                        dealershipStringBuilder.append(dealershipInputBean.getLastUpdatedUser());
                    } else {
                        dealershipStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return dealershipStringBuilder.toString();
    }

    private String getDealershipAsString(Dealership dealership, boolean checkChanges) {
        StringBuilder dealershipStringBuilder = new StringBuilder();
        try {
            if (dealership != null) {
                if (dealership.getDealershipName()!= null && !dealership.getDealershipName().isEmpty()) {
                    dealershipStringBuilder.append(dealership.getDealershipName());
                } else {
                    dealershipStringBuilder.append("error");
                }

                dealershipStringBuilder.append("|");
                if (dealership.getDealershipPhone() != null && !dealership.getDealershipPhone().isEmpty()) {
                    dealershipStringBuilder.append(dealership.getDealershipPhone());
                } else {
                    dealershipStringBuilder.append("--");
                }

                dealershipStringBuilder.append("|");
                if (dealership.getStatus() != null && !dealership.getStatus().isEmpty()) {
                    dealershipStringBuilder.append(dealership.getStatus());
                } else {
                    dealershipStringBuilder.append("--");
                }


                if (!checkChanges) {
                    dealershipStringBuilder.append("|");
                    if (dealership.getCreatedTime() != null) {
                        dealershipStringBuilder.append(common.formatDateToString(dealership.getCreatedTime()));
                    } else {
                        dealershipStringBuilder.append("--");
                    }


                    dealershipStringBuilder.append("|");
                    if (dealership.getLastUpdatedTime() != null) {
                        dealershipStringBuilder.append(common.formatDateToString(dealership.getLastUpdatedTime()));
                    } else {
                        dealershipStringBuilder.append("--");
                    }

                    dealershipStringBuilder.append("|");
                    if (dealership.getLastUpdatedUser() != null) {
                        dealershipStringBuilder.append(dealership.getLastUpdatedUser());
                    } else {
                        dealershipStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return dealershipStringBuilder.toString();
    }


}

