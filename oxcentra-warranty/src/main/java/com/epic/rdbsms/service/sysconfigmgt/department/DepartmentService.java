package com.epic.rdbsms.service.sysconfigmgt.department;

import com.epic.rdbsms.bean.common.TempAuthRecBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.department.DepartmentInputBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.department.Department;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.repository.sysconfigmgt.department.DepartmentRepository;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.varlist.*;
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

@Service
@Scope("prototype")
public class DepartmentService {

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
    DepartmentRepository departmentRepository;

    private final String fields = "DepartmentInputBean Code|Description|Status|Created Time|Created User|Last Updated Time|Last Updated User";

    public long getCount(DepartmentInputBean departmentInputBean) {
        long count = 0;
        try {
            count = departmentRepository.getDataCount(departmentInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Department> getDepartmentSearchResultList(DepartmentInputBean departmentInputBean) {
        List<Department> departmentList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.DEPARTMENT_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get department search list.");
            //Get department search list.
            departmentList = departmentRepository.getDepartmentSearchList(departmentInputBean);
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
        return departmentList;
    }


    public long getDataCountDual(DepartmentInputBean departmentInputBean) {
        long count = 0;
        try {
            count = departmentRepository.getDataCountDual(departmentInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getDepartmentSearchResultsDual(DepartmentInputBean departmentInputBean) {
        List<TempAuthRec> departmentDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.DEPARTMENT_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get department dual authentication search list.");
            //Get department dual authentication search list
            departmentDualList = departmentRepository.getDepartmentSearchResultsDual(departmentInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return departmentDualList;
    }

    public String insertDepartment(DepartmentInputBean departmentInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            Department department = departmentRepository.getDepartment(departmentInputBean.getCode().trim());
            if (department == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                departmentInputBean.setCreatedUser(lastUpdatedUser);
                departmentInputBean.setCreatedTime(currentDate);
                departmentInputBean.setLastUpdatedTime(currentDate);
                departmentInputBean.setLastUpdatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.DEPARTMENT_MGT_PAGE)) {
                    auditDescription = "Requested to add department (code: " + departmentInputBean.getCode() + ")";
                    message = this.insertDualAuthRecord(departmentInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Department (code: " + departmentInputBean.getCode() + ") added by " + sessionBean.getUsername();
                    message = departmentRepository.insertDepartment(departmentInputBean);
                }
            } else {
                message = MessageVarList.DEPARTMENT_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.DEPARTMENT_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.DEPARTMENT_MGT_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.DEPARTMENT_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getDepartmentAsString(departmentInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public Department getDepartment(String code) {
        Department department;
        try {
            department = departmentRepository.getDepartment(code);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return department;
    }

    public String updateDepartment(DepartmentInputBean departmentInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        Department existingDepartment = null;
        try {
            existingDepartment = departmentRepository.getDepartment(departmentInputBean.getCode());
            if (existingDepartment != null) {
                //check changed values
                String oldValueAsString = this.getDepartmentAsString(existingDepartment, true);
                String newValueAsString = this.getDepartmentAsString(departmentInputBean, true);
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
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.DEPARTMENT_MGT_PAGE)) {
                        auditDescription = "Requested to update department (code: " + departmentInputBean.getCode() + ")";
                        message = this.insertDualAuthRecord(departmentInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Department (code: " + departmentInputBean.getCode() + ") updated by " + sessionBean.getUsername();
                        message = departmentRepository.updateDepartment(departmentInputBean);
                    }
                }
            } else {
                message = MessageVarList.DEPARTMENT_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.DEPARTMENT_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.DEPARTMENT_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.DEPARTMENT_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getDepartmentAsString(existingDepartment, false));
                audittrace.setNewvalue(this.getDepartmentAsString(departmentInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteDepartment(String code) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.DEPARTMENT_MGT_PAGE)) {
                //get the existing department
                Department department = departmentRepository.getDepartment(code);
                if (department != null) {
                    DepartmentInputBean departmentInputBean = new DepartmentInputBean();
                    //set the values to input bean
                    departmentInputBean.setCode(department.getCode());
                    departmentInputBean.setDescription(department.getDescription());
                    departmentInputBean.setStatus(department.getStatus());
                    departmentInputBean.setCreatedTime(department.getCreatedTime());
                    departmentInputBean.setCreatedUser(department.getCreatedUser());
                    departmentInputBean.setLastUpdatedTime(department.getLastUpdatedTime());
                    departmentInputBean.setLastUpdatedUser(department.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted department (code: " + code + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(departmentInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.DEPARTMENT_MGT_NORECORD_FOUND;
                }
            } else {
                message = departmentRepository.deleteDepartment(code);
                auditDescription = "Department (Code: " + code + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.DEPARTMENT_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.DEPARTMENT_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmDepartment(String id) {
        String message = "";
        String auditDescription = "";
        DepartmentInputBean departmentInputBean = null;
        Department existingDepartment = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                departmentInputBean = new DepartmentInputBean();
                departmentInputBean.setCode(tempAuthRecBean.getKey1());
                departmentInputBean.setDescription(tempAuthRecBean.getKey2());
                departmentInputBean.setStatus(tempAuthRecBean.getKey3());

                departmentInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                departmentInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing department
                try {
                    String code = departmentInputBean.getCode();
                    existingDepartment = departmentRepository.getDepartment(code);
                } catch (EmptyResultDataAccessException e) {
                    existingDepartment = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingDepartment == null) {
                        departmentInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        departmentInputBean.setCreatedUser(sessionBean.getUsername());
                        message = departmentRepository.insertDepartment(departmentInputBean);
                    } else {
                        message = MessageVarList.DEPARTMENT_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingDepartment != null) {
                        message = departmentRepository.updateDepartment(departmentInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingDepartment != null) {
                        message = departmentRepository.deleteDepartment(departmentInputBean.getCode());
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                }

                //if department db operation success, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success,insert the audit
                    if (message.isEmpty()) {
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on department (department code: ").append(departmentInputBean.getCode())
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
            message = MessageVarList.DEPARTMENT_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.DEPARTMENT_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getDepartmentAsString(departmentInputBean, false));
                audittrace.setOldvalue(this.getDepartmentAsString(existingDepartment, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectDepartment(String code) {
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
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on department (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.DEPARTMENT_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_REJECT_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String insertDualAuthRecord(DepartmentInputBean departmentInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(departmentInputBean.getCode(), PageVarList.DEPARTMENT_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.DEPARTMENT_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(departmentInputBean.getCode());
                tempAuthRecBean.setKey2(departmentInputBean.getDescription());
                tempAuthRecBean.setKey3(departmentInputBean.getStatus());
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

    private String getDepartmentAsString(DepartmentInputBean departmentInputBean, boolean checkChanges) {
        StringBuilder departmentStringBuilder = new StringBuilder();
        try {
            if (departmentInputBean != null) {

                if (departmentInputBean.getCode() != null && !departmentInputBean.getCode().isEmpty()) {
                    departmentStringBuilder.append(departmentInputBean.getCode());
                } else {
                    departmentStringBuilder.append("error");
                }

                departmentStringBuilder.append("|");
                if (departmentInputBean.getDescription() != null && !departmentInputBean.getDescription().isEmpty()) {
                    departmentStringBuilder.append(departmentInputBean.getDescription());
                } else {
                    departmentStringBuilder.append("--");
                }

                departmentStringBuilder.append("|");
                if (departmentInputBean.getStatus() != null && !departmentInputBean.getStatus().isEmpty()) {
                    departmentStringBuilder.append(departmentInputBean.getStatus());
                } else {
                    departmentStringBuilder.append("--");
                }

                if (!checkChanges) {
                    departmentStringBuilder.append("|");
                    if (departmentInputBean.getCreatedTime() != null) {
                        departmentStringBuilder.append(common.formatDateToString(departmentInputBean.getCreatedTime()));
                    } else {
                        departmentStringBuilder.append("--");
                    }

                    departmentStringBuilder.append("|");
                    if (departmentInputBean.getCreatedUser() != null) {
                        departmentStringBuilder.append(departmentInputBean.getCreatedUser());
                    } else {
                        departmentStringBuilder.append("--");
                    }

                    departmentStringBuilder.append("|");
                    if (departmentInputBean.getLastUpdatedTime() != null) {
                        departmentStringBuilder.append(common.formatDateToString(departmentInputBean.getLastUpdatedTime()));
                    } else {
                        departmentStringBuilder.append("--");
                    }

                    departmentStringBuilder.append("|");
                    if (departmentInputBean.getLastUpdatedUser() != null) {
                        departmentStringBuilder.append(departmentInputBean.getLastUpdatedUser());
                    } else {
                        departmentStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return departmentStringBuilder.toString();
    }

    private String getDepartmentAsString(Department department, boolean checkChanges) {
        StringBuilder departmentStringBuilder = new StringBuilder();
        try {
            if (department != null) {
                if (department.getCode() != null && !department.getCode().isEmpty()) {
                    departmentStringBuilder.append(department.getCode());
                } else {
                    departmentStringBuilder.append("error");
                }

                departmentStringBuilder.append("|");
                if (department.getDescription() != null && !department.getDescription().isEmpty()) {
                    departmentStringBuilder.append(department.getDescription());
                } else {
                    departmentStringBuilder.append("--");
                }

                departmentStringBuilder.append("|");
                if (department.getStatus() != null && !department.getStatus().isEmpty()) {
                    departmentStringBuilder.append(department.getStatus());
                } else {
                    departmentStringBuilder.append("--");
                }

                if (!checkChanges) {
                    departmentStringBuilder.append("|");
                    if (department.getCreatedTime() != null) {
                        departmentStringBuilder.append(common.formatDateToString(department.getCreatedTime()));
                    } else {
                        departmentStringBuilder.append("--");
                    }

                    departmentStringBuilder.append("|");
                    if (department.getCreatedUser() != null) {
                        departmentStringBuilder.append(department.getCreatedUser());
                    } else {
                        departmentStringBuilder.append("--");
                    }

                    departmentStringBuilder.append("|");
                    if (department.getLastUpdatedTime() != null) {
                        departmentStringBuilder.append(common.formatDateToString(department.getLastUpdatedTime()));
                    } else {
                        departmentStringBuilder.append("--");
                    }

                    departmentStringBuilder.append("|");
                    if (department.getLastUpdatedUser() != null) {
                        departmentStringBuilder.append(department.getLastUpdatedUser());
                    } else {
                        departmentStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return departmentStringBuilder.toString();
    }

}
