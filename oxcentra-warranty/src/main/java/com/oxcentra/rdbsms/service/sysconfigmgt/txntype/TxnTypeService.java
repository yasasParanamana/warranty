package com.oxcentra.rdbsms.service.sysconfigmgt.txntype;

import com.oxcentra.rdbsms.bean.common.TempAuthRecBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.sysconfigmgt.txntype.TxnTypeInputBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.txntype.TxnType;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.sysconfigmgt.txntype.TxnTypeRepository;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.varlist.*;
import lombok.AllArgsConstructor;
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

/**
 * @author Namila Withanage on 11/15/2021
 */
@Service
@Scope("prototype")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TxnTypeService {

    Common common;
    Audittrace audittrace;
    SessionBean sessionBean;
    MessageSource messageSource;
    CommonRepository commonRepository;
    TxnTypeRepository txnTypeRepository;

    private final String fields = "TxnTypeInputBean TxnType|Description|Status|Created Time|Created User|Last Updated Time|Last Updated User";

    public long getCount(TxnTypeInputBean txnTypeInputBean) {
        long count = 0;
        try {
            count = txnTypeRepository.getDataCount(txnTypeInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TxnType> getTxnTypeSearchResultList(TxnTypeInputBean txnTypeInputBean) {
        List<TxnType> txnTypeList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.TXN_TYPE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get txn type search list.");
            //Get txn type search list.
            txnTypeList = txnTypeRepository.getTxnTypeSearchList(txnTypeInputBean);
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
        return txnTypeList;
    }


    public long getDataCountDual(TxnTypeInputBean txnTypeInputBean) {
        long count = 0;
        try {
            count = txnTypeRepository.getDataCountDual(txnTypeInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getTxnTypeSearchResultsDual(TxnTypeInputBean txnTypeInputBean) {
        List<TempAuthRec> txnTypeDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.TXN_TYPE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get txn type dual authentication search list.");
            //Get txn type dual authentication search list
            txnTypeDualList = txnTypeRepository.getTxnTypeSearchResultsDual(txnTypeInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return txnTypeDualList;
    }

    public String insertTxnType(TxnTypeInputBean txnTypeInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            TxnType txnType = txnTypeRepository.getTxnType(txnTypeInputBean.getTxntype().trim());
            if (txnType == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                txnTypeInputBean.setCreatedTime(currentDate);
                txnTypeInputBean.setCreatedUser(lastUpdatedUser);
                txnTypeInputBean.setLastUpdatedTime(currentDate);
                txnTypeInputBean.setLastUpdatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.TXN_TYPE_MGT_PAGE)) {
                    auditDescription = "Requested to add txnType (code: " + txnTypeInputBean.getTxntype() + ")";
                    message = this.insertDualAuthRecord(txnTypeInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "TxnType (code: " + txnTypeInputBean.getTxntype() + ") added by " + sessionBean.getUsername();
                    message = txnTypeRepository.insertTxnType(txnTypeInputBean);
                }
            } else {
                message = MessageVarList.TXN_TYPE_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.TXN_TYPE_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.TXN_TYPE_MGT_ALREADY_EXISTS;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception x) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.TXN_TYPE_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getTxnTypeAsString(txnTypeInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public TxnType getTxnType(String code) {
        TxnType txnType;
        try {
            txnType = txnTypeRepository.getTxnType(code);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return txnType;
    }

    public String updateTxnType(TxnTypeInputBean departmentInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        TxnType existingTxnType = null;
        try {
            existingTxnType = txnTypeRepository.getTxnType(departmentInputBean.getTxntype());
            if (existingTxnType != null) {
                //check changed values
                String oldValueAsString = this.getTxnTypeAsString(existingTxnType, true);
                String newValueAsString = this.getTxnTypeAsString(departmentInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    departmentInputBean.setLastUpdatedTime(currentDate);
                    departmentInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.TXN_TYPE_MGT_PAGE)) {
                        auditDescription = "Requested to update txn type (code: " + departmentInputBean.getTxntype() + ")";
                        message = this.insertDualAuthRecord(departmentInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "TxnType (code: " + departmentInputBean.getTxntype() + ") updated by " + sessionBean.getUsername();
                        message = txnTypeRepository.updateTxnType(departmentInputBean);
                    }
                }
            } else {
                message = MessageVarList.TXN_TYPE_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.TXN_TYPE_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.TXN_TYPE_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.TXN_TYPE_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getTxnTypeAsString(existingTxnType, false));
                audittrace.setNewvalue(this.getTxnTypeAsString(departmentInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteTxnType(String code) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.TXN_TYPE_MGT_PAGE)) {
                //get the existing txn type
                TxnType txnType = txnTypeRepository.getTxnType(code);
                if (txnType != null) {
                    TxnTypeInputBean txnTypeInputBean = new TxnTypeInputBean();
                    //set the values to input bean
                    txnTypeInputBean.setTxntype(txnType.getTxntype());
                    txnTypeInputBean.setDescription(txnType.getDescription());
                    txnTypeInputBean.setStatus(txnType.getStatus());
                    txnTypeInputBean.setCreatedTime(txnType.getCreatedTime());
                    txnTypeInputBean.setCreatedUser(txnType.getCreatedUser());
                    txnTypeInputBean.setLastUpdatedTime(txnType.getLastUpdatedTime());
                    txnTypeInputBean.setLastUpdatedUser(txnType.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted txn type (code: " + code + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(txnTypeInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.TXN_TYPE_MGT_NORECORD_FOUND;
                }
            } else {
                message = txnTypeRepository.deleteTxnType(code);
                auditDescription = "TxnType (Code: " + code + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.TXN_TYPE_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
            message = MessageVarList.COMMON_ERROR_ALRADY_USE;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.TXN_TYPE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmTxnType(String id) {
        String message = "";
        String auditDescription = "";
        TxnTypeInputBean departmentInputBean = null;
        TxnType existingTxnType = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                departmentInputBean = new TxnTypeInputBean();
                departmentInputBean.setTxntype(tempAuthRecBean.getKey1());
                departmentInputBean.setDescription(tempAuthRecBean.getKey2());
                departmentInputBean.setStatus(tempAuthRecBean.getKey3());

                departmentInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                departmentInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing txn type
                try {
                    String code = departmentInputBean.getTxntype();
                    existingTxnType = txnTypeRepository.getTxnType(code);
                } catch (EmptyResultDataAccessException e) {
                    existingTxnType = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingTxnType == null) {
                        departmentInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        departmentInputBean.setCreatedUser(sessionBean.getUsername());
                        message = txnTypeRepository.insertTxnType(departmentInputBean);
                    } else {
                        message = MessageVarList.TXN_TYPE_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingTxnType != null) {
                        message = txnTypeRepository.updateTxnType(departmentInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingTxnType != null) {
                        message = txnTypeRepository.deleteTxnType(departmentInputBean.getTxntype());
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                }

                //if txn type db operation success, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success,insert the audit
                    if (message.isEmpty()) {
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on txn type (txn type code: ").append(departmentInputBean.getTxntype())
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
            message = MessageVarList.TXN_TYPE_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (DataIntegrityViolationException | ConstraintViolationException cve) {
            message = MessageVarList.COMMON_ERROR_ALRADY_USE;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.TXN_TYPE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getTxnTypeAsString(departmentInputBean, false));
                audittrace.setOldvalue(this.getTxnTypeAsString(existingTxnType, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectTxnType(String code) {
        String message = "";
        String auditDescription = "";
        try {
            //get temp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(code);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(code, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on txn type (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
                audittrace.setPage(PageVarList.TXN_TYPE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_REJECT_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String insertDualAuthRecord(TxnTypeInputBean txnTypeInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(txnTypeInputBean.getTxntype(), PageVarList.TXN_TYPE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.TXN_TYPE_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(txnTypeInputBean.getTxntype());
                tempAuthRecBean.setKey2(txnTypeInputBean.getDescription());
                tempAuthRecBean.setKey3(txnTypeInputBean.getStatus());
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

    private String getTxnTypeAsString(TxnTypeInputBean txnTypeInputBean, boolean checkChanges) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (txnTypeInputBean != null) {

                if (txnTypeInputBean.getTxntype() != null && !txnTypeInputBean.getTxntype().isEmpty()) {
                    stringBuilder.append(txnTypeInputBean.getTxntype());
                } else {
                    stringBuilder.append("error");
                }

                stringBuilder.append("|");
                if (txnTypeInputBean.getDescription() != null && !txnTypeInputBean.getDescription().isEmpty()) {
                    stringBuilder.append(txnTypeInputBean.getDescription());
                } else {
                    stringBuilder.append("--");
                }

                stringBuilder.append("|");
                if (txnTypeInputBean.getStatus() != null && !txnTypeInputBean.getStatus().isEmpty()) {
                    stringBuilder.append(txnTypeInputBean.getStatus());
                } else {
                    stringBuilder.append("--");
                }

                if (!checkChanges) {
                    stringBuilder.append("|");
                    if (txnTypeInputBean.getCreatedTime() != null) {
                        stringBuilder.append(common.formatDateToString(txnTypeInputBean.getCreatedTime()));
                    } else {
                        stringBuilder.append("--");
                    }

                    stringBuilder.append("|");
                    if (txnTypeInputBean.getCreatedUser() != null) {
                        stringBuilder.append(txnTypeInputBean.getCreatedUser());
                    } else {
                        stringBuilder.append("--");
                    }

                    stringBuilder.append("|");
                    if (txnTypeInputBean.getLastUpdatedTime() != null) {
                        stringBuilder.append(common.formatDateToString(txnTypeInputBean.getLastUpdatedTime()));
                    } else {
                        stringBuilder.append("--");
                    }

                    stringBuilder.append("|");
                    if (txnTypeInputBean.getLastUpdatedUser() != null) {
                        stringBuilder.append(txnTypeInputBean.getLastUpdatedUser());
                    } else {
                        stringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return stringBuilder.toString();
    }

    private String getTxnTypeAsString(TxnType txnType, boolean checkChanges) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (txnType != null) {
                if (txnType.getTxntype() != null && !txnType.getTxntype().isEmpty()) {
                    stringBuilder.append(txnType.getTxntype());
                } else {
                    stringBuilder.append("error");
                }

                stringBuilder.append("|");
                if (txnType.getDescription() != null && !txnType.getDescription().isEmpty()) {
                    stringBuilder.append(txnType.getDescription());
                } else {
                    stringBuilder.append("--");
                }

                stringBuilder.append("|");
                if (txnType.getStatus() != null && !txnType.getStatus().isEmpty()) {
                    stringBuilder.append(txnType.getStatus());
                } else {
                    stringBuilder.append("--");
                }

                if (!checkChanges) {
                    stringBuilder.append("|");
                    if (txnType.getCreatedTime() != null) {
                        stringBuilder.append(common.formatDateToString(txnType.getCreatedTime()));
                    } else {
                        stringBuilder.append("--");
                    }

                    stringBuilder.append("|");
                    if (txnType.getCreatedUser() != null) {
                        stringBuilder.append(txnType.getCreatedUser());
                    } else {
                        stringBuilder.append("--");
                    }

                    stringBuilder.append("|");
                    if (txnType.getLastUpdatedTime() != null) {
                        stringBuilder.append(common.formatDateToString(txnType.getLastUpdatedTime()));
                    } else {
                        stringBuilder.append("--");
                    }

                    stringBuilder.append("|");
                    if (txnType.getLastUpdatedUser() != null) {
                        stringBuilder.append(txnType.getLastUpdatedUser());
                    } else {
                        stringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return stringBuilder.toString();
    }

}
