package com.epic.rdbsms.service.sysconfigmgt.category;

import com.epic.rdbsms.bean.common.TempAuthRecBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.bean.sysconfigmgt.category.CategoryInputBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.category.Category;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.repository.sysconfigmgt.category.CategoryRepository;
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
public class CategoryService {

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
    CategoryRepository categoryRepository;

    private final String fields = "CategoryInputBean Category Code|Description|Bulk Enable|Status|Priority|Created Time|Created User|Last Updated Time|Last Updated User";

    public long getCount(CategoryInputBean categoryInputBean) {
        long count = 0;
        try {
            count = categoryRepository.getDataCount(categoryInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Category> getCategorySearchResultList(CategoryInputBean categoryInputBean) {
        List<Category> categoryList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CATEGORY_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get category search list.");
            //get category search list
            categoryList = categoryRepository.getCategorySearchList(categoryInputBean);
        } catch (EmptyResultDataAccessException ere) {
            audittrace.setSkip(true);
            throw ere;
        } catch (Exception e) {
            audittrace.setSkip(true);
            throw e;
        }
        return categoryList;
    }

    public long getDataCountDual(CategoryInputBean categoryInputBean) {
        long count = 0;
        try {
            count = categoryRepository.getDataCountDual(categoryInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getCategorySearchResultsDual(CategoryInputBean categoryInputBean) {
        List<TempAuthRec> categoryDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CATEGORY_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get category dual authentication search list.");
            //get category dual authentication search list
            categoryDualList = categoryRepository.getCategorySearchResultsDual(categoryInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return categoryDualList;
    }

    public String insertCategory(CategoryInputBean categoryInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            Category category = categoryRepository.getCategory(categoryInputBean.getCategoryCode().trim());
            if (category == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                categoryInputBean.setCreatedTime(currentDate);
                categoryInputBean.setLastUpdatedTime(currentDate);
                categoryInputBean.setLastUpdatedUser(lastUpdatedUser);
                categoryInputBean.setCreatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CATEGORY_MGT_PAGE)) {
                    auditDescription = "Requested to add category (code: " + categoryInputBean.getCategoryCode() + ")";
                    message = this.insertDualAuthRecord(categoryInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "Category (code: " + categoryInputBean.getCategoryCode() + ") added by " + sessionBean.getUsername();
                    message = categoryRepository.insertCategory(categoryInputBean);
                }
            } else {
                message = MessageVarList.CATEGORY_MGT_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.CATEGORY_MGT_ALREADY_EXISTS, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.CATEGORY_MGT_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.CATEGORY_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getCategoryAsString(categoryInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public Category getCategory(String categoryCode) {
        Category category;
        try {
            category = categoryRepository.getCategory(categoryCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return category;
    }

    public String updateCategory(CategoryInputBean categoryInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        Category existingCategory = null;
        try {
            existingCategory = categoryRepository.getCategory(categoryInputBean.getCategoryCode());
            if (existingCategory != null) {
                //check changed values
                String oldValueAsString = this.getCategoryAsString(existingCategory, true);
                String newValueAsString = this.getCategoryAsString(categoryInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    categoryInputBean.setLastUpdatedTime(currentDate);
                    categoryInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CATEGORY_MGT_PAGE)) {
                        auditDescription = "Requested to update category (code: " + categoryInputBean.getCategoryCode() + ")";
                        message = this.insertDualAuthRecord(categoryInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Category (code: " + categoryInputBean.getCategoryCode() + ") updated by " + sessionBean.getUsername();
                        message = categoryRepository.updateCategory(categoryInputBean);
                    }
                }
            } else {
                message = MessageVarList.CATEGORY_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.CATEGORY_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CATEGORY_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CATEGORY_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getCategoryAsString(existingCategory, false));
                audittrace.setNewvalue(this.getCategoryAsString(categoryInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteCategory(String code) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CATEGORY_MGT_PAGE)) {
                //get the existing department
                Category category = categoryRepository.getCategory(code);
                if (category != null) {
                    CategoryInputBean categoryInputBean = new CategoryInputBean();
                    categoryInputBean.setCategoryCode(category.getCategory());
                    categoryInputBean.setDescription(category.getDescription());
                    categoryInputBean.setBulkEnable(category.getIsBulk());
                    categoryInputBean.setStatus(category.getStatus());
                    categoryInputBean.setPriority(category.getPriority());
                    categoryInputBean.setAckwait(category.getAckwait());
                    categoryInputBean.setUnsubscribe(category.getUnsubscribe());
                    categoryInputBean.setTtlqueue(category.getTtlqueue());
                    categoryInputBean.setCreatedTime(category.getCreatedTime());
                    categoryInputBean.setCreatedUser(category.getCreatedUser());
                    categoryInputBean.setLastUpdatedTime(category.getLastUpdatedTime());
                    categoryInputBean.setLastUpdatedUser(category.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted category (code: " + code + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(categoryInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.CATEGORY_MGT_NORECORD_FOUND;
                }
            } else {
                message = categoryRepository.deleteCategory(code);
                auditDescription = "Category (Code: " + code + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CATEGORY_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CATEGORY_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmCategory(String id) {
        String message = "";
        String auditDescription = "";
        CategoryInputBean categoryInputBean = null;
        Category existingCategory = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                categoryInputBean = new CategoryInputBean();
                categoryInputBean.setCategoryCode(tempAuthRecBean.getKey1());
                categoryInputBean.setDescription(tempAuthRecBean.getKey2());
                categoryInputBean.setBulkEnable(tempAuthRecBean.getKey3());
                categoryInputBean.setStatus(tempAuthRecBean.getKey4());
                categoryInputBean.setPriority(tempAuthRecBean.getKey5());
                categoryInputBean.setAckwait(tempAuthRecBean.getKey6());
                categoryInputBean.setUnsubscribe(tempAuthRecBean.getKey7());
                categoryInputBean.setTtlqueue(Integer.valueOf(tempAuthRecBean.getKey8()));
                categoryInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                categoryInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing category
                try {
                    String categoryCode = categoryInputBean.getCategoryCode();
                    existingCategory = categoryRepository.getCategory(categoryCode);
                } catch (EmptyResultDataAccessException e) {
                    existingCategory = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingCategory == null) {
                        categoryInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        categoryInputBean.setCreatedUser(sessionBean.getUsername());
                        message = categoryRepository.insertCategory(categoryInputBean);
                        System.out.println("message @categoryService ADD_TASK= " + message);
                    } else {
                        message = MessageVarList.CATEGORY_MGT_ALREADY_EXISTS;
                    }

                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingCategory != null) {
                        message = categoryRepository.updateCategory(categoryInputBean);
                        System.out.println("message @categoryService UPDATE_TASK= " + message);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }

                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingCategory != null) {
                        message = categoryRepository.deleteCategory(categoryInputBean.getCategoryCode());
                        System.out.println("message @categoryService DELETE_TASK= " + message);
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
                                .append("' operation on category (category code: ").append(categoryInputBean.getCategoryCode())
                                .append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" approved by ")
                                .append(sessionBean.getUsername());

                        auditDescription = auditDesBuilder.toString();
                        System.out.println("auditDescription == " + auditDescription);
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
            message = MessageVarList.CATEGORY_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CATEGORY_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getCategoryAsString(categoryInputBean, false));
                audittrace.setOldvalue(this.getCategoryAsString(existingCategory, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        System.out.println("return message; >>>>>>>>>>>>>>>> " + message);
        return message;
    }

    public String rejectCategory(String code) {
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
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on category (category code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.CATEGORY_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String insertDualAuthRecord(CategoryInputBean categoryInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(categoryInputBean.getCategoryCode(), PageVarList.CATEGORY_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.CATEGORY_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(categoryInputBean.getCategoryCode());
                tempAuthRecBean.setKey2(categoryInputBean.getDescription());
                tempAuthRecBean.setKey3(categoryInputBean.getBulkEnable());
                tempAuthRecBean.setKey4(categoryInputBean.getStatus());
                tempAuthRecBean.setKey5(categoryInputBean.getPriority());
                tempAuthRecBean.setKey6(categoryInputBean.getAckwait());
                tempAuthRecBean.setKey7(categoryInputBean.getUnsubscribe());
                tempAuthRecBean.setKey8(String.valueOf(categoryInputBean.getTtlqueue()));
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

    private String getCategoryAsString(CategoryInputBean categoryInputBean, boolean checkChanges) {
        StringBuilder categoryStringBuilder = new StringBuilder();
        try {
            if (categoryInputBean != null) {
                if (categoryInputBean.getCategoryCode() != null && !categoryInputBean.getCategoryCode().isEmpty()) {
                    categoryStringBuilder.append(categoryInputBean.getCategoryCode());
                } else {
                    categoryStringBuilder.append("error");
                }

                categoryStringBuilder.append("|");
                if (categoryInputBean.getDescription() != null && !categoryInputBean.getDescription().isEmpty()) {
                    categoryStringBuilder.append(categoryInputBean.getDescription());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                categoryStringBuilder.append(categoryInputBean.getBulkEnable());

                categoryStringBuilder.append("|");
                if (categoryInputBean.getStatus() != null && !categoryInputBean.getStatus().isEmpty()) {
                    categoryStringBuilder.append(categoryInputBean.getStatus());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                if (categoryInputBean.getPriority() != null && !categoryInputBean.getPriority().isEmpty()) {
                    categoryStringBuilder.append(categoryInputBean.getPriority());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                categoryStringBuilder.append(categoryInputBean.getUnsubscribe());

                categoryStringBuilder.append("|");
                categoryStringBuilder.append(categoryInputBean.getAckwait());

                categoryStringBuilder.append("|");
                if (categoryInputBean.getTtlqueue() != null) {
                    categoryStringBuilder.append(categoryInputBean.getTtlqueue());
                } else {
                    categoryStringBuilder.append("--");
                }

                if (!checkChanges) {
                    categoryStringBuilder.append("|");
                    if (categoryInputBean.getCreatedTime() != null) {
                        categoryStringBuilder.append(common.formatDateToString(categoryInputBean.getCreatedTime()));
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (categoryInputBean.getCreatedUser() != null) {
                        categoryStringBuilder.append(categoryInputBean.getCreatedUser());
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (categoryInputBean.getLastUpdatedTime() != null) {
                        categoryStringBuilder.append(common.formatDateToString(categoryInputBean.getLastUpdatedTime()));
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (categoryInputBean.getLastUpdatedUser() != null) {
                        categoryStringBuilder.append(categoryInputBean.getLastUpdatedUser());
                    } else {
                        categoryStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return categoryStringBuilder.toString();
    }

    private String getCategoryAsString(Category category, boolean checkChanges) {
        StringBuilder categoryStringBuilder = new StringBuilder();
        try {
            if (category != null) {
                if (category.getCategory() != null && !category.getCategory().isEmpty()) {
                    categoryStringBuilder.append(category.getCategory());
                } else {
                    categoryStringBuilder.append("error");
                }

                categoryStringBuilder.append("|");
                if (category.getDescription() != null && !category.getDescription().isEmpty()) {
                    categoryStringBuilder.append(category.getDescription());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                categoryStringBuilder.append(category.getIsBulk());

                categoryStringBuilder.append("|");
                if (category.getStatus() != null && !category.getStatus().isEmpty()) {
                    categoryStringBuilder.append(category.getStatus());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                if (category.getPriority() != null && !category.getPriority().isEmpty()) {
                    categoryStringBuilder.append(category.getPriority());
                } else {
                    categoryStringBuilder.append("--");
                }

                categoryStringBuilder.append("|");
                categoryStringBuilder.append(category.getUnsubscribe());

                categoryStringBuilder.append("|");
                categoryStringBuilder.append(category.getAckwait());

                categoryStringBuilder.append("|");
                if (category.getTtlqueue() != null) {
                    categoryStringBuilder.append(category.getTtlqueue());
                } else {
                    categoryStringBuilder.append("--");
                }

                if (!checkChanges) {
                    categoryStringBuilder.append("|");
                    if (category.getCreatedTime() != null) {
                        categoryStringBuilder.append(common.formatDateToString(category.getCreatedTime()));
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (category.getCreatedUser() != null) {
                        categoryStringBuilder.append(category.getCreatedUser());
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (category.getLastUpdatedTime() != null) {
                        categoryStringBuilder.append(common.formatDateToString(category.getLastUpdatedTime()));
                    } else {
                        categoryStringBuilder.append("--");
                    }

                    categoryStringBuilder.append("|");
                    if (category.getLastUpdatedUser() != null) {
                        categoryStringBuilder.append(category.getLastUpdatedUser());
                    } else {
                        categoryStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return categoryStringBuilder.toString();
    }
}
