package com.epic.rdbsms.service.usermgt.userrole;

import com.epic.rdbsms.bean.common.TempAuthRecBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.usermgt.userrole.UserRoleInputBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.mapping.usermgt.Page;
import com.epic.rdbsms.mapping.usermgt.Section;
import com.epic.rdbsms.mapping.usermgt.Task;
import com.epic.rdbsms.mapping.usermgt.UserRole;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.repository.usermgt.userrole.UserRoleRepository;
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
import java.util.*;

@Service
@Scope("prototype")
public class UserRoleService {

    @Autowired
    UserRoleRepository userRoleRepository;

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

    private final String fields = "Userrole Code|Description|Userrole Type|Status|Created Time|Last Updated Time|Last Updated User";

    public long getDataCount(UserRoleInputBean inputBean) throws Exception {
        long count = 0;
        try {
            count = userRoleRepository.getDataCount(inputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<UserRole> getUserRoleSearchResults(UserRoleInputBean inputBean) throws Exception {
        List<UserRole> pageList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_USER_MGT);
            audittrace.setPage(PageVarList.USERROLE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get user role search list.");
            //Get user role search list.
            pageList = userRoleRepository.getUserRoleSearchResults(inputBean);
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
        return pageList;
    }

    public long getDataCountDual(UserRoleInputBean inputBean) throws Exception {
        long count = 0;
        try {
            count = userRoleRepository.getDataCountDual(inputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getUserRoleSearchResultsDual(UserRoleInputBean userRoleInputBean) throws Exception {
        List<TempAuthRec> taskDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_USER_MGT);
            audittrace.setPage(PageVarList.USERROLE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get user role dual authentication search list.");
            //Get user role dual authentication search list
            taskDualList = userRoleRepository.getUserRoleSearchResultsDual(userRoleInputBean);
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

    public String insertUserRole(UserRoleInputBean userRoleInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            UserRole userRole = userRoleRepository.getUserRole(userRoleInputBean.getUserroleCode().trim());
            if (userRole == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String user = sessionBean.getUsername();

                userRoleInputBean.setCreatedTime(currentDate);
                userRoleInputBean.setLastUpdatedTime(currentDate);
                userRoleInputBean.setLastUpdatedUser(user);
                userRoleInputBean.setCreatedUser(user);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USERROLE_MGT_PAGE)) {
                    auditDescription = "Requested to add user role (code: " + userRoleInputBean.getUserroleCode() + ")";
                    message = this.insertDualAuthRecord(userRoleInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "User role (code: " + userRoleInputBean.getUserroleCode() + ") added by " + sessionBean.getUsername();
                    message = userRoleRepository.insertUserRole(userRoleInputBean);
                }
            } else {
                message = MessageVarList.USERROLE_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.USERROLE_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.USERROLE_MGT_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.USERROLE_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getUserRoleAsString(userRoleInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public UserRole getUserRole(String userroleCode) throws Exception {
        UserRole userRole;
        try {
            userRole = userRoleRepository.getUserRole(userroleCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return userRole;
    }

    public String updateUserRole(UserRoleInputBean userRoleInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        UserRole existingUserRole = null;
        try {
            existingUserRole = userRoleRepository.getUserRole(userRoleInputBean.getUserroleCode());
            if (existingUserRole != null) {
                //check changed values
                String oldValueAsString = this.getUserRoleAsString(existingUserRole, true);
                String newValueAsString = this.getUserRoleAsString(userRoleInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    userRoleInputBean.setLastUpdatedTime(currentDate);
                    userRoleInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USERROLE_MGT_PAGE)) {
                        auditDescription = "Requested to update user role (user role code: " + userRoleInputBean.getUserroleCode() + ")";
                        message = this.insertDualAuthRecord(userRoleInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "User role (code: " + userRoleInputBean.getUserroleCode() + ") updated by " + sessionBean.getUsername();
                        message = userRoleRepository.updateUserRole(userRoleInputBean);
                    }
                }
            } else {
                message = MessageVarList.USERROLE_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.USERROLE_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.USERROLE_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.USERROLE_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getUserRoleAsString(existingUserRole, false));
                audittrace.setNewvalue(this.getUserRoleAsString(userRoleInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }


    public String deleteUserRole(String userroleCode) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USERROLE_MGT_PAGE)) {
                //get the existing user role
                UserRole userRole = userRoleRepository.getUserRole(userroleCode);
                if (userRole != null) {
                    UserRoleInputBean userRoleInputBean = new UserRoleInputBean();
                    //set the values to input bean
                    userRoleInputBean.setUserroleCode(userRole.getUserroleCode());
                    userRoleInputBean.setDescription(userRole.getDescription());
                    userRoleInputBean.setUserroleType(userRole.getUserroleType());
                    userRoleInputBean.setStatus(userRole.getStatus());
                    userRoleInputBean.setSection(userRole.getSection());
                    userRoleInputBean.setPage(userRole.getPage());
                    userRoleInputBean.setCreatedTime(userRole.getCreatedTime());
                    userRoleInputBean.setLastUpdatedTime(userRole.getLastUpdatedTime());
                    userRoleInputBean.setLastUpdatedUser(userRole.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted user role (user role code: " + userroleCode + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(userRoleInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.USERROLE_MGT_NORECORD_FOUND;
                }
            } else {
                message = userRoleRepository.deleteUserRole(userroleCode);
                auditDescription = "User role (User role code: " + userroleCode + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.USERROLE_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.USERROLE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public List<Section> getAllSection() throws Exception {
        List<Section> sectionList;
        try {
            sectionList = userRoleRepository.getAllSection();
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return sectionList;
    }

    public List<Page> getAssignedPages(String userroleCode, String sectionCode) throws Exception {
        List<Page> assignedPageList;
        try {
            assignedPageList = userRoleRepository.getAssignedPages(userroleCode, sectionCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return assignedPageList;
    }

    public List<Page> getUnAssignedPages(String userroleCode) throws Exception {
        List<Page> unAssignPageList = new ArrayList<>();
        try {
            List<Page> allPageList = userRoleRepository.getAllPages();
            List<String> assignedUserrolePageList = userRoleRepository.getAssignedPagesForUserrole(userroleCode);

            for (Page page : allPageList) {
                if (!assignedUserrolePageList.contains(page.getPageCode())) {
                    unAssignPageList.add(page);
                }
            }
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return unAssignPageList;
    }

    public String assignPages(UserRoleInputBean userRoleInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            UserRole userRole = userRoleRepository.getUserRole(userRoleInputBean.getUserroleCode());
            if (userRole != null) {
                List<String> assignedPages = userRoleInputBean.getAssignedPages();
                List<String> alreadyAssignedList = userRoleRepository.getAssignedPageCodes(userRoleInputBean.getUserroleCode(), userRoleInputBean.getSection());

                if (assignedPages.size() == 0 && alreadyAssignedList.size() == 0) {
                    message = MessageVarList.USERROLE_MGT_EMPTY_PAGE;
                    auditDescription = messageSource.getMessage(MessageVarList.USERROLE_MGT_EMPTY_PAGE, null, locale);

                } else if (assignedPages.size() > 0 && alreadyAssignedList.size() > 0 && assignedPages.equals(alreadyAssignedList)) {
                    message = MessageVarList.USERROLE_MGT_ASSIGNED_PAGE;
                    auditDescription = messageSource.getMessage(MessageVarList.USERROLE_MGT_ASSIGNED_PAGE, null, locale);

                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();
                    userRoleInputBean.setCreatedTime(currentDate);
                    userRoleInputBean.setLastUpdatedTime(currentDate);
                    userRoleInputBean.setLastUpdatedUser(lastUpdatedUser);

                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USERROLE_MGT_PAGE)) {
                        auditDescription = "Requested to assign page to user role (user role code: " + userRole.getUserroleCode().trim() + ") by " + sessionBean.getUsername();
                        userRoleInputBean.setUserroleType(userRole.getUserroleType());
                        userRoleInputBean.setStatus(userRole.getStatus());
                        message = this.insertDualAuthRecord(userRoleInputBean, TaskVarList.ASSIGN_PAGE);
                    } else {
                        List<String> unAssignedPages = new ArrayList<>();
                        for (String pageCode : alreadyAssignedList) {
                            if (assignedPages.contains(pageCode)) {
                                assignedPages.remove(pageCode);
                            } else {
                                unAssignedPages.add(pageCode);
                            }
                        }
                        userRoleInputBean.setUnAssignList(unAssignedPages);
                        auditDescription = "Assign page to user role (user role code: " + userRole.getUserroleCode().trim() + ") by " + sessionBean.getUsername();
                        message = userRoleRepository.assignPages(userRoleInputBean);
                    }
                }
            } else {
                message = MessageVarList.USERROLE_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.USERROLE_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.USERROLE_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.USERROLE_MGT_PAGE);
                audittrace.setTask(TaskVarList.ASSIGN_PAGE);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public List<Section> getAssignedSection(String userroleCode) throws Exception {
        List<Section> sectionList;
        try {
            sectionList = userRoleRepository.getAssignedSection(userroleCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return sectionList;
    }

    public List<Task> getAssignedTasks(String userroleCode, String pageCode) throws Exception {
        List<Task> assignTaskList;
        try {
            assignTaskList = userRoleRepository.getAssignedTasks(userroleCode, pageCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return assignTaskList;
    }

    public List<Task> getUnAssignedTasks(String userroleCode, String pageCode) throws Exception {
        List<Task> unAssignTaskList = new ArrayList<>();
        try {
            List<Task> allPageTaskList = userRoleRepository.getAllPageTasks(pageCode);
            List<String> assignedUserroleTaskList = userRoleRepository.getAssignedTasksForUserrole(userroleCode, pageCode);

            for (Task task : allPageTaskList) {
                if (!assignedUserroleTaskList.contains(task.getTaskCode())) {
                    unAssignTaskList.add(task);
                }
            }
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return unAssignTaskList;
    }

    public String assignTasks(UserRoleInputBean userRoleInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        try {
            UserRole userRole = userRoleRepository.getUserRole(userRoleInputBean.getUserroleCode());
            if (userRole != null) {
                List<String> assignedTasks = userRoleInputBean.getAssignedTasks();
                List<String> alreadyAssignedList = userRoleRepository.getAssignedTaskCodes(userRoleInputBean.getUserroleCode(), userRoleInputBean.getPage());

                if (assignedTasks.size() == 0 && alreadyAssignedList.size() == 0) {
                    message = MessageVarList.USERROLE_MGT_EMPTY_TASK;
                    auditDescription = messageSource.getMessage(MessageVarList.USERROLE_MGT_EMPTY_TASK, null, locale);

                } else if (assignedTasks.size() > 0 && alreadyAssignedList.size() > 0 && assignedTasks.equals(alreadyAssignedList)) {
                    message = MessageVarList.USERROLE_MGT_ASSIGNED_TASK;
                    auditDescription = messageSource.getMessage(MessageVarList.USERROLE_MGT_ASSIGNED_TASK, null, locale);

                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    userRoleInputBean.setCreatedTime(currentDate);
                    userRoleInputBean.setLastUpdatedTime(currentDate);
                    userRoleInputBean.setLastUpdatedUser(lastUpdatedUser);

                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.USERROLE_MGT_PAGE)) {
                        auditDescription = "Requested to assign task to user role (user role code: " + userRole.getUserroleCode().trim() + ") by " + sessionBean.getUsername();
                        userRoleInputBean.setUserroleType(userRole.getUserroleType());
                        userRoleInputBean.setStatus(userRole.getStatus());
                        message = this.insertDualAuthRecord(userRoleInputBean, TaskVarList.ASSIGN_TASK);
                    } else {
                        List<String> unAssignedTasks = new ArrayList<>();
                        for (String taskCode : alreadyAssignedList) {
                            if (assignedTasks.contains(taskCode)) {
                                assignedTasks.remove(taskCode);
                            } else {
                                unAssignedTasks.add(taskCode);
                            }
                        }

                        auditDescription = "Assign task to user role (user role code: " + userRole.getUserroleCode().trim() + ") by " + sessionBean.getUsername();
                        userRoleInputBean.setUnAssignList(unAssignedTasks);
                        if (message.isEmpty()) {
                            message = userRoleRepository.assignTasks(userRoleInputBean);
                        }
                    }
                }
            } else {
                message = MessageVarList.USERROLE_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.USERROLE_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.USERROLE_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.USERROLE_MGT_PAGE);
                audittrace.setTask(TaskVarList.ASSIGN_PAGE);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String insertDualAuthRecord(UserRoleInputBean userRoleInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(userRoleInputBean.getUserroleCode().trim(), PageVarList.USERROLE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.USERROLE_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
//                tempAuthRecBean.setStatus(StatusVarList.STATUS_AUTH_PEN);
                tempAuthRecBean.setKey1(userRoleInputBean.getUserroleCode().trim());
                tempAuthRecBean.setKey2(userRoleInputBean.getDescription().trim());
                tempAuthRecBean.setKey3(userRoleInputBean.getUserroleType().trim());
                tempAuthRecBean.setKey4(userRoleInputBean.getStatus().trim());

                //insert dual auth record
                if (taskCode.equalsIgnoreCase(TaskVarList.ASSIGN_PAGE)) {
                    tempAuthRecBean.setKey5(userRoleInputBean.getSection().trim());
                    tempAuthRecBean.setTmpRecord(userRoleInputBean.getAssignedPages().toString().replace("[", "").replace("]", ""));
                } else if (taskCode.equalsIgnoreCase(TaskVarList.ASSIGN_TASK)) {
                    tempAuthRecBean.setKey5(userRoleInputBean.getSection().trim());
                    tempAuthRecBean.setKey6(userRoleInputBean.getPage().trim());
                    tempAuthRecBean.setTmpRecord(userRoleInputBean.getAssignedTasks().toString().replace("[", "").replace("]", ""));
                }
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


    public String confirmUserRole(String id) throws Exception {
        String message = "";
        String auditDescription = "";
        UserRoleInputBean userRoleInputBean = null;
        UserRole existingUserRole = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                userRoleInputBean = new UserRoleInputBean();
                userRoleInputBean.setUserroleCode(tempAuthRecBean.getKey1());
                userRoleInputBean.setDescription(tempAuthRecBean.getKey2());
                userRoleInputBean.setUserroleType(tempAuthRecBean.getKey3());
                userRoleInputBean.setStatus(tempAuthRecBean.getKey4());
                userRoleInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                userRoleInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing user role
                try {
                    String code = userRoleInputBean.getUserroleCode();
                    existingUserRole = userRoleRepository.getUserRole(code);
                } catch (EmptyResultDataAccessException ex) {
                    existingUserRole = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingUserRole == null) {
                        userRoleInputBean.setCreatedUser(sessionBean.getUsername());
                        userRoleInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        message = userRoleRepository.insertUserRole(userRoleInputBean);
                    } else {
                        message = MessageVarList.USERROLE_MGT_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingUserRole != null) {
                        message = userRoleRepository.updateUserRole(userRoleInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingUserRole != null) {
                        message = userRoleRepository.deleteUserRole(userRoleInputBean.getUserroleCode());
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.ASSIGN_PAGE.equals(tempAuthRecBean.getTask())) {
                    if (existingUserRole != null) {
                        List<String> assignedPages = new ArrayList<>();
                        if (tempAuthRecBean.getTmpRecord() != null && !tempAuthRecBean.getTmpRecord().isEmpty()) {
                            assignedPages = new ArrayList<>(Arrays.asList(tempAuthRecBean.getTmpRecord().replaceAll(" ", "").split(",")));
                        }

                        List<String> alreadyAssignedList = userRoleRepository.getAssignedPageCodes(tempAuthRecBean.getKey1().trim(), tempAuthRecBean.getKey5().trim());
                        List<String> unAssignedPages = new ArrayList<>();

                        for (String pageCode : alreadyAssignedList) {
                            if (assignedPages.contains(pageCode)) {
                                assignedPages.remove(pageCode);
                            } else {
                                unAssignedPages.add(pageCode);
                            }
                        }

                        userRoleInputBean.setSection(tempAuthRecBean.getKey5());
                        // userRoleInputBean.setAssignList(assignedPages);
                        userRoleInputBean.setAssignedPages(assignedPages);
                        userRoleInputBean.setUnAssignList(unAssignedPages);
                        //assign pages
                        message = userRoleRepository.assignPages(userRoleInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.ASSIGN_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingUserRole != null) {
                        List<String> assignedTasks = new ArrayList<>();
                        if (tempAuthRecBean.getTmpRecord() != null && !tempAuthRecBean.getTmpRecord().isEmpty()) {
                            assignedTasks = new ArrayList<>(Arrays.asList(tempAuthRecBean.getTmpRecord().replaceAll(" ", "").split(",")));
                        }

                        List<String> alreadyAssignedList = userRoleRepository.getAssignedTaskCodes(tempAuthRecBean.getKey1().trim(), tempAuthRecBean.getKey6().trim());
                        List<String> unAssignedTasks = new ArrayList<>();

                        for (String taskCode : alreadyAssignedList) {
                            if (assignedTasks.contains(taskCode)) {
                                assignedTasks.remove(taskCode);
                            } else {
                                unAssignedTasks.add(taskCode);
                            }
                        }

                        userRoleInputBean.setSection(tempAuthRecBean.getKey5());
                        userRoleInputBean.setPage(tempAuthRecBean.getKey6());
                        // userRoleInputBean.setAssignList(assignedTasks);
                        userRoleInputBean.setAssignedTasks(assignedTasks);
                        userRoleInputBean.setUnAssignList(unAssignedTasks);
                        //assign tasks
                        message = userRoleRepository.assignTasks(userRoleInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                }

                //if user role db operation success, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success,insert the audit
                    if (message.isEmpty()) {
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on user role (user role code: ").append(userRoleInputBean.getUserroleCode())
                                .append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" approved by ")
                                .append(sessionBean.getUsername());

                        audittrace.setDescription(auditDesBuilder.toString());
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
            message = MessageVarList.USERROLE_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.USERROLE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getUserRoleAsString(userRoleInputBean, false));
                audittrace.setOldvalue(this.getUserRoleAsString(existingUserRole, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;

    }

    public String rejectUserRole(String id) throws Exception {
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
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on userrole (userrole code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setSection(SectionVarList.SECTION_USER_MGT);
                audittrace.setPage(PageVarList.USERROLE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_REJECT_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String getUserRoleAsString(UserRoleInputBean userRoleInputBean, boolean checkChanges) {
        StringBuilder userRoleStringBuilder = new StringBuilder();
        try {
            if (userRoleInputBean != null) {
                if (userRoleInputBean.getUserroleCode() != null) {
                    userRoleStringBuilder.append(userRoleInputBean.getUserroleCode());
                } else {
                    userRoleStringBuilder.append("error");
                }

                userRoleStringBuilder.append("|");
                if (userRoleInputBean.getDescription() != null) {
                    userRoleStringBuilder.append(userRoleInputBean.getDescription());
                } else {
                    userRoleStringBuilder.append("--");
                }

                userRoleStringBuilder.append("|");
                if (userRoleInputBean.getUserroleType() != null) {
                    userRoleStringBuilder.append(userRoleInputBean.getUserroleType());
                } else {
                    userRoleStringBuilder.append("--");
                }

                userRoleStringBuilder.append("|");
                if (userRoleInputBean.getStatus() != null) {
                    userRoleStringBuilder.append(userRoleInputBean.getStatus());
                } else {
                    userRoleStringBuilder.append("--");
                }

                if (!checkChanges) {
                    userRoleStringBuilder.append("|");
                    if (userRoleInputBean.getCreatedTime() != null) {
                        userRoleStringBuilder.append(common.formatDateToString(userRoleInputBean.getCreatedTime()));
                    } else {
                        userRoleStringBuilder.append("--");
                    }

                    userRoleStringBuilder.append("|");
                    if (userRoleInputBean.getLastUpdatedUser() != null) {
                        userRoleStringBuilder.append(userRoleInputBean.getLastUpdatedUser());
                    } else {
                        userRoleStringBuilder.append("--");
                    }

                    userRoleStringBuilder.append("|");
                    if (userRoleInputBean.getLastUpdatedTime() != null) {
                        userRoleStringBuilder.append(common.formatDateToString(userRoleInputBean.getLastUpdatedTime()));
                    } else {
                        userRoleStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return userRoleStringBuilder.toString();
    }

    private String getUserRoleAsString(UserRole userRole, boolean checkChanges) {
        StringBuilder userRoleStringBuilder = new StringBuilder();
        try {
            if (userRole != null) {
                if (userRole.getUserroleCode() != null) {
                    userRoleStringBuilder.append(userRole.getUserroleCode());
                } else {
                    userRoleStringBuilder.append("error");
                }

                userRoleStringBuilder.append("|");
                if (userRole.getDescription() != null) {
                    userRoleStringBuilder.append(userRole.getDescription());
                } else {
                    userRoleStringBuilder.append("--");
                }

                userRoleStringBuilder.append("|");
                if (userRole.getUserroleType() != null) {
                    userRoleStringBuilder.append(userRole.getUserroleType());
                } else {
                    userRoleStringBuilder.append("--");
                }

                userRoleStringBuilder.append("|");
                if (userRole.getStatus() != null) {
                    userRoleStringBuilder.append(userRole.getStatus());
                } else {
                    userRoleStringBuilder.append("--");
                }

                if (!checkChanges) {
                    userRoleStringBuilder.append("|");
                    if (userRole.getCreatedTime() != null) {
                        userRoleStringBuilder.append(common.formatDateToString(userRole.getCreatedTime()));
                    } else {
                        userRoleStringBuilder.append("--");
                    }

                    userRoleStringBuilder.append("|");
                    if (userRole.getLastUpdatedUser() != null) {
                        userRoleStringBuilder.append(userRole.getLastUpdatedUser());
                    } else {
                        userRoleStringBuilder.append("--");
                    }

                    userRoleStringBuilder.append("|");
                    if (userRole.getLastUpdatedTime() != null) {
                        userRoleStringBuilder.append(common.formatDateToString(userRole.getLastUpdatedTime()));
                    } else {
                        userRoleStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return userRoleStringBuilder.toString();
    }
}
