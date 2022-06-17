package com.epic.rdbsms.service.usermgt.task;

import com.epic.rdbsms.bean.common.TempAuthRecBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.usermgt.task.TaskInputBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.repository.usermgt.task.TaskRepository;
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
public class TaskService {

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
    TaskRepository taskRepository;

    private final String fields = "Task Code|Description|Status|Created Time|Last Updated Time|Last Updated User";

    public long getDataCount(TaskInputBean taskInputBean) throws Exception {
        long count = 0;
        try {
            count = taskRepository.getDataCount(taskInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Task> getTaskSearchResults(TaskInputBean taskInputBean) throws Exception {
        List<Task> taskList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_USER_MGT);
            audittrace.setPage(PageVarList.TASK_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get Task search list.");
            //Get task search search list.
            taskList = taskRepository.getTaskSearchResults(taskInputBean);
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
        return taskList;
    }

    public long getDataCountDual(TaskInputBean taskInputBean) throws Exception {
        long count = 0;
        try {
            count = taskRepository.getDataCountDual(taskInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getTaskSearchResultsDual(TaskInputBean taskInputBean) throws Exception {
        List<TempAuthRec> taskDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_USER_MGT);
            audittrace.setPage(PageVarList.TASK_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get task dual authentication search list.");
            //Get task dual authentication search list
            taskDualList = taskRepository.getTaskSearchResultsDual(taskInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return taskDualList;
    }

    public String insertTask(TaskInputBean taskInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            //check task code is already exist or not
            Task existingTask = taskRepository.getTask(taskInputBean.getTaskCode().trim());
            if (existingTask == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String user = sessionBean.getUsername();

                taskInputBean.setCreatedTime(currentDate);
                taskInputBean.setLastUpdatedTime(currentDate);
                taskInputBean.setLastUpdatedUser(user);
                taskInputBean.setCreatedUser(user);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.TASK_MGT_PAGE)) {
                    auditDescription = "Requested to add task (task code: " + taskInputBean.getTaskCode() + ")";
                    message = this.insertDualAuthRecord(taskInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Task (Task code: " + taskInputBean.getTaskCode() + ") added by " + sessionBean.getUsername();
                    message = taskRepository.insertTask(taskInputBean);
                }
            } else {
                message = MessageVarList.TASK_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.SMPP_CONFIGURATION_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.TASK_MGT_ALREADY_EXISTS;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception x) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.USER_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getTaskAsString(taskInputBean, false));
            }
        }
        return message;
    }

    public Task getTask(String taskCode) throws Exception {
        Task task;
        try {
            task = taskRepository.getTask(taskCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return task;
    }

    public String updateTask(TaskInputBean taskInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        Task existingTask = null;
        try {
            existingTask = taskRepository.getTask(taskInputBean.getTaskCode());
            if (existingTask != null) {
                //check changed values
                String oldValueAsString = this.getTaskAsString(existingTask, true);
                String newValueAsString = this.getTaskAsString(taskInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    taskInputBean.setLastUpdatedTime(currentDate);
                    taskInputBean.setLastUpdatedUser(lastUpdatedUser);
                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.TASK_MGT_PAGE)) {
                        auditDescription = "Requested to update task (Task code: " + taskInputBean.getTaskCode() + ")";
                        message = this.insertDualAuthRecord(taskInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Task Mgt (Task code: " + taskInputBean.getTaskCode() + ") updated by " + sessionBean.getUsername();
                        message = taskRepository.updateTask(taskInputBean);
                    }
                }
            } else {
                message = MessageVarList.TASK_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.TASK_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.TASK_MGT_NORECORD_FOUND;
            //skip audit trace
            audittrace.setSkip(true);
        } catch (Exception e) {
            message = MessageVarList.COMMON_ERROR_PROCESS;
            //skip audit trace
            audittrace.setSkip(true);
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.USER_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getTaskAsString(existingTask, false));
                audittrace.setNewvalue(this.getTaskAsString(taskInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteTask(String taskCode) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.SMPPCONFIG_MGT_PAGE)) {
                //get the existing task
                Task task = taskRepository.getTask(taskCode);
                if (task != null) {
                    TaskInputBean taskInputBean = new TaskInputBean();
                    //set the values to input bean
                    taskInputBean.setTaskCode(task.getTaskCode());
                    taskInputBean.setDescription(task.getDescription());
                    taskInputBean.setStatus(task.getStatus());
                    taskInputBean.setCreatedTime(task.getCreatedTime());
                    taskInputBean.setCreatedUser(task.getCreatedUser());
                    taskInputBean.setLastUpdatedTime(task.getLastUpdatedTime());
                    taskInputBean.setLastUpdatedUser(task.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted Task (Task code: " + taskCode + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(taskInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.TASK_MGT_NORECORD_FOUND;
                }
            } else {
                message = taskRepository.deleteTask(taskCode);
                auditDescription = "Task Mgt (Task code: " + taskCode + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.TASK_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.TASK_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmTask(String id) throws Exception {
        String message = "";
        String auditDescription = "";
        TaskInputBean taskInputBean = null;
        Task existingTask = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                taskInputBean = new TaskInputBean();
                taskInputBean.setTaskCode(tempAuthRecBean.getKey1());
                taskInputBean.setDescription(tempAuthRecBean.getKey2());
                taskInputBean.setStatus(tempAuthRecBean.getKey3());
                taskInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                taskInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing task
                try {
                    String code = taskInputBean.getTaskCode();
                    existingTask = taskRepository.getTask(code);
                } catch (EmptyResultDataAccessException e) {
                    existingTask = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingTask == null) {
                        taskInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        taskInputBean.setCreatedUser(sessionBean.getUsername());
                        message = taskRepository.insertTask(taskInputBean);
                    } else {
                        message = MessageVarList.TASK_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingTask != null) {
                        message = taskRepository.updateTask(taskInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingTask != null) {
                        message = taskRepository.deleteTask(taskInputBean.getTaskCode());
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
                                .append("' operation on task (task code: ").append(taskInputBean.getTaskCode())
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
            message = MessageVarList.TASK_MGT_NORECORD_FOUND;
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
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.TASK_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getTaskAsString(taskInputBean, false));
                audittrace.setOldvalue(this.getTaskAsString(existingTask, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectTask(String code) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(code);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(code, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on task mgt (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
                    auditDescription = auditDesBuilder.toString();
                } else {
                    message = MessageVarList.COMMON_ERROR_PROCESS;
                }
            } else {
                message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (message.isEmpty()) {
                //set the audit trace values
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.TASK_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;

    }

    public String insertDualAuthRecord(TaskInputBean taskInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(taskInputBean.getTaskCode().trim(), PageVarList.TASK_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.TASK_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(taskInputBean.getTaskCode().trim().toUpperCase());
                tempAuthRecBean.setKey2(taskInputBean.getDescription().trim());
                tempAuthRecBean.setKey3(taskInputBean.getStatus().trim());
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

    private String getTaskAsString(Task task, boolean checkChanges) {
        StringBuilder taskStringBuilder = new StringBuilder();
        try {
            if (task != null) {
                if (task.getTaskCode() != null) {
                    taskStringBuilder.append(task.getTaskCode());
                } else {
                    taskStringBuilder.append("error");
                }

                taskStringBuilder.append("|");
                if (task.getDescription() != null) {
                    taskStringBuilder.append(task.getDescription());
                } else {
                    taskStringBuilder.append("--");
                }

                taskStringBuilder.append("|");
                if (task.getStatus() != null) {
                    taskStringBuilder.append(task.getStatus());
                } else {
                    taskStringBuilder.append("--");
                }

                if (!checkChanges) {
                    taskStringBuilder.append("|");
                    if (task.getCreatedUser() != null) {
                        taskStringBuilder.append(task.getCreatedUser());
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (task.getCreatedTime() != null) {
                        taskStringBuilder.append(common.formatDateToString(task.getCreatedTime()));
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (task.getLastUpdatedUser() != null) {
                        taskStringBuilder.append(task.getLastUpdatedUser());
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (task.getLastUpdatedTime() != null) {
                        taskStringBuilder.append(common.formatDateToString(task.getLastUpdatedTime()));
                    } else {
                        taskStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return taskStringBuilder.toString();
    }

    private String getTaskAsString(TaskInputBean taskInputBean, boolean checkChanges) {
        StringBuilder taskStringBuilder = new StringBuilder();
        try {
            if (taskInputBean != null) {
                if (taskInputBean.getTaskCode() != null) {
                    taskStringBuilder.append(taskInputBean.getTaskCode());
                } else {
                    taskStringBuilder.append("error");
                }

                taskStringBuilder.append("|");
                if (taskInputBean.getDescription() != null) {
                    taskStringBuilder.append(taskInputBean.getDescription());
                } else {
                    taskStringBuilder.append("--");
                }

                taskStringBuilder.append("|");
                if (taskInputBean.getStatus() != null) {
                    taskStringBuilder.append(taskInputBean.getStatus());
                } else {
                    taskStringBuilder.append("--");
                }

                if (!checkChanges) {
                    taskStringBuilder.append("|");
                    if (taskInputBean.getCreatedUser() != null) {
                        taskStringBuilder.append(taskInputBean.getCreatedUser());
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (taskInputBean.getCreatedTime() != null) {
                        taskStringBuilder.append(common.formatDateToString(taskInputBean.getCreatedTime()));
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (taskInputBean.getLastUpdatedUser() != null) {
                        taskStringBuilder.append(taskInputBean.getLastUpdatedUser());
                    } else {
                        taskStringBuilder.append("--");
                    }

                    taskStringBuilder.append("|");
                    if (taskInputBean.getLastUpdatedTime() != null) {
                        taskStringBuilder.append(common.formatDateToString(taskInputBean.getLastUpdatedTime()));
                    } else {
                        taskStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return taskStringBuilder.toString();
    }
}
