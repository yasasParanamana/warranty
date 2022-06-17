package com.epic.rdbsms.service.alerttypetelcomgt.categorytelcomgt;

import com.epic.rdbsms.bean.alerttypetelcomgt.categorytelco.CategoryTelcoInputBean;
import com.epic.rdbsms.bean.common.TempAuthRecBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.categorytelcomgt.CategoryTelco;
import com.epic.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.epic.rdbsms.repository.alerttypetelcomgt.categorytelcomgt.CategoryTelcoRepository;
import com.epic.rdbsms.repository.common.CommonRepository;
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
public class CategoryTelcoService {

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
    CategoryTelcoRepository categoryTelcoRepository;

    private final String fields = "CategoryTelco Category|Telco|MtPort|Status|Created Time|Created User|Last Updated Time|Last Updated User";


    public long getCount(CategoryTelcoInputBean categoryTelcoInputBean) {
        long count = 0;
        try {
            count = categoryTelcoRepository.getDataCount(categoryTelcoInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public long getDataCountDual(CategoryTelcoInputBean categoryTelcoInputBean) {
        long count = 0;
        try {
            count = categoryTelcoRepository.getDataCountDual(categoryTelcoInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getCategoryTelcoSearchResultsDual(CategoryTelcoInputBean categoryTelcoInputBean) {
        List<TempAuthRec> categoryTelcoDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CATEGORY_TELCO_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get category telco dual authentication search list.");
            //Get category telco dual authentication search list
            categoryTelcoDualList = categoryTelcoRepository.getCategoryTelcoSearchResultsDual(categoryTelcoInputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return categoryTelcoDualList;
    }

    public String updateCategoryTelco(CategoryTelcoInputBean categoryTelcoInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        CategoryTelco existingCategoryTelco = null;
        try {
            existingCategoryTelco = categoryTelcoRepository.getCategoryTelco(categoryTelcoInputBean.getCategory());
            if (existingCategoryTelco != null) {
                //check changed values
                String oldValueAsString = this.getCategoryTelcoAsString(existingCategoryTelco, true);
                String newValueAsString = this.getCategoryTelcoAsString(categoryTelcoInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    categoryTelcoInputBean.setCreatedTime(currentDate);
                    categoryTelcoInputBean.setLastUpdatedTime(currentDate);
                    categoryTelcoInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CATEGORY_TELCO_MGT_PAGE)) {
                        auditDescription = "Requested to update category telco (category: " + categoryTelcoInputBean.getCategory() + ")";
                        message = this.insertDualAuthRecord(categoryTelcoInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Category Telco (category: " + categoryTelcoInputBean.getCategory() + ") updated by " + sessionBean.getUsername();
                        message = categoryTelcoRepository.updateCategoryTelco(categoryTelcoInputBean);
                    }
                }
            } else {
                message = MessageVarList.CATEGORY_TELCO_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.CATEGORY_TELCO_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CATEGORY_TELCO_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CATEGORY_TELCO_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getCategoryTelcoAsString(existingCategoryTelco, false));
                audittrace.setNewvalue(this.getCategoryTelcoAsString(categoryTelcoInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String deleteCategory(String category) {
        String message = "";
        String auditDescription = "";
        try {
            //check the page dual auth enable or disable
            if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CATEGORY_TELCO_MGT_PAGE)) {
                //get the existing category telco
                CategoryTelco categoryTelco = categoryTelcoRepository.getCategoryTelco(category);
                if (categoryTelco != null) {
                    CategoryTelcoInputBean categoryTelcoInputBean = new CategoryTelcoInputBean();
                    //set the values to input bean
                    categoryTelcoInputBean.setCategory(categoryTelco.getCategory());
                    categoryTelcoInputBean.setTelco(categoryTelco.getTelco());
                    categoryTelcoInputBean.setMtPort(categoryTelco.getMtPort());
                    categoryTelcoInputBean.setStatus(categoryTelco.getStatus());
                    categoryTelcoInputBean.setCreatedTime(categoryTelco.getCreatedTime());
                    categoryTelcoInputBean.setCreatedUser(categoryTelco.getCreatedUser());
                    categoryTelcoInputBean.setLastUpdatedTime(categoryTelco.getLastUpdatedTime());
                    categoryTelcoInputBean.setLastUpdatedUser(categoryTelco.getLastUpdatedUser());
                    //set audit description
                    auditDescription = "Requested to deleted category telco (category: " + category + ")";
                    //insert the record to dual auth table
                    message = this.insertDualAuthRecord(categoryTelcoInputBean, TaskVarList.DELETE_TASK);
                } else {
                    message = MessageVarList.CATEGORY_TELCO_NORECORD_FOUND;
                }
            } else {
                message = categoryTelcoRepository.deleteCategoryTelco(category);
                auditDescription = "CATEGORY TELCO (Category: " + category + ") deleted by " + sessionBean.getUsername();
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.CATEGORY_TELCO_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CATEGORY_TELCO_MGT_PAGE);
                audittrace.setTask(TaskVarList.DELETE_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public CategoryTelco getCategoryTelco(String category) {
        CategoryTelco categoryTelco;
        try {
            categoryTelco = categoryTelcoRepository.getCategoryTelco(category);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return categoryTelco;
    }

    public List<CategoryTelco> getCategoryTelcoResultList(CategoryTelcoInputBean categoryTelcoInputBean) {
        List<CategoryTelco> categoryTelcoList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_SYS_CONFIGURATION_MGT);
            audittrace.setPage(PageVarList.CATEGORY_TELCO_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get category telco search list.");
            //Get category telco search list.
            categoryTelcoList = categoryTelcoRepository.getCategoryTelcoSearchList(categoryTelcoInputBean);
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
        return categoryTelcoList;
    }

    public String insertCategoryTelco(CategoryTelcoInputBean categoryTelcoInputBean, Locale locale) {
        String message = "";
        String auditDescription = "";
        try {
            CategoryTelco categoryTelco = categoryTelcoRepository.getCategoryTelco(categoryTelcoInputBean.getCategory().trim());
            if (categoryTelco == null) {
                //set the other values to input bean
                Date currentDate = commonRepository.getCurrentDate();
                String lastUpdatedUser = sessionBean.getUsername();

                categoryTelcoInputBean.setCreatedTime(currentDate);
                categoryTelcoInputBean.setLastUpdatedTime(currentDate);
                categoryTelcoInputBean.setLastUpdatedUser(lastUpdatedUser);
                categoryTelcoInputBean.setCreatedUser(lastUpdatedUser);

                //check the page dual auth enable or disable
                if (commonRepository.checkPageIsDualAuthenticate(PageVarList.CATEGORY_TELCO_MGT_PAGE)) {
                    auditDescription = "Requested to add category telco (category: " + categoryTelcoInputBean.getMtPort() + ")";
                    message = this.insertDualAuthRecord(categoryTelcoInputBean, TaskVarList.ADD_TASK);
                } else {
                    auditDescription = "CategoryTelco (category: " + categoryTelcoInputBean.getCategory() + ") added by " + sessionBean.getUsername();
                    message = categoryTelcoRepository.insertCategoryTelco(categoryTelcoInputBean);
                }
            } else {
                message = MessageVarList.CATEGORY_TELCO_ALREADY_EXISTS;
                auditDescription = messageSource.getMessage(MessageVarList.CATEGORY_TELCO_NORECORD_FOUND, null, locale);
            }
        } catch (DuplicateKeyException ex) {
            message = MessageVarList.CATEGORY_TELCO_ALREADY_EXISTS;
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
                audittrace.setPage(PageVarList.CATEGORY_TELCO_MGT_PAGE);
                audittrace.setTask(TaskVarList.ADD_TASK);
                //set values to audit record
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getCategoryTelcoAsString(categoryTelcoInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String rejectCategoryTelco(String category) {
        String message = "";
        String auditDescription = "";
        try {
            //get temp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(category);
            if (tempAuthRecBean != null) {
                message = commonRepository.updateTempAuthRecord(category, StatusVarList.STATUS_AUTH_REJ);
                if (message.isEmpty()) {
                    //create audit description
                    StringBuilder auditDesBuilder = new StringBuilder();
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on category telco (category: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.CATEGORY_TELCO_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmCategoryTelco(String id) {
        String message = "";
        String auditDescription = "";
        CategoryTelcoInputBean categoryTelcoInputBean = null;
        CategoryTelco existingCategoryTelco = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                categoryTelcoInputBean = new CategoryTelcoInputBean();
                categoryTelcoInputBean.setCategory(tempAuthRecBean.getKey1());
                categoryTelcoInputBean.setTelco(tempAuthRecBean.getKey2());
                categoryTelcoInputBean.setMtPort(tempAuthRecBean.getKey3());
                categoryTelcoInputBean.setStatus(tempAuthRecBean.getKey4());

                categoryTelcoInputBean.setLastUpdatedUser(sessionBean.getUsername());
                categoryTelcoInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());

                //get the existing category telco
                try {
                    String category = categoryTelcoInputBean.getCategory();
                    existingCategoryTelco = categoryTelcoRepository.getCategoryTelco(category);
                } catch (EmptyResultDataAccessException e) {
                    existingCategoryTelco = null;
                }

                if (TaskVarList.ADD_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingCategoryTelco == null) {
                        categoryTelcoInputBean.setCreatedUser(sessionBean.getUsername());
                        categoryTelcoInputBean.setCreatedTime(commonRepository.getCurrentDate());
                        message = categoryTelcoRepository.insertCategoryTelco(categoryTelcoInputBean);
                    } else {
                        message = MessageVarList.CATEGORY_TELCO_ALREADY_EXISTS;
                    }
                } else if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingCategoryTelco != null) {
                        message = categoryTelcoRepository.updateCategoryTelco(categoryTelcoInputBean);
                    } else {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    }
                } else if (TaskVarList.DELETE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingCategoryTelco != null) {
                        message = categoryTelcoRepository.deleteCategoryTelco(categoryTelcoInputBean.getCategory());
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
                                .append("' operation on task (task category: ").append(categoryTelcoInputBean.getCategory())
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
            message = MessageVarList.CATEGORY_TELCO_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.CATEGORY_TELCO_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getCategoryTelcoAsString(categoryTelcoInputBean, false));
                audittrace.setOldvalue(this.getCategoryTelcoAsString(categoryTelcoInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    private String getCategoryTelcoAsString(CategoryTelco categoryTelco, boolean checkChanges) {
        StringBuilder categoryTelcoStringBuilder = new StringBuilder();
        try {
            if (categoryTelco != null) {
                if (categoryTelco.getCategory() != null && !categoryTelco.getCategory().isEmpty()) {
                    categoryTelcoStringBuilder.append(categoryTelco.getCategory());
                } else {
                    categoryTelcoStringBuilder.append("error");
                }

                if (categoryTelco.getTelco() != null && !categoryTelco.getTelco().isEmpty()) {
                    categoryTelcoStringBuilder.append(categoryTelco.getTelco());
                } else {
                    categoryTelcoStringBuilder.append("error");
                }

                if (categoryTelco.getMtPort() != null && !categoryTelco.getMtPort().isEmpty()) {
                    categoryTelcoStringBuilder.append(categoryTelco.getMtPort());
                } else {
                    categoryTelcoStringBuilder.append("error");
                }

                categoryTelcoStringBuilder.append("|");
                if (categoryTelco.getStatus() != null && !categoryTelco.getStatus().isEmpty()) {
                    categoryTelcoStringBuilder.append(categoryTelco.getStatus());
                } else {
                    categoryTelcoStringBuilder.append("--");
                }

                if (!checkChanges) {
                    categoryTelcoStringBuilder.append("|");
                    if (categoryTelco.getCreatedTime() != null) {
                        categoryTelcoStringBuilder.append(common.formatDateToString(categoryTelco.getCreatedTime()));
                    } else {
                        categoryTelcoStringBuilder.append("--");
                    }

                    categoryTelcoStringBuilder.append("|");
                    if (categoryTelco.getCreatedUser() != null) {
                        categoryTelcoStringBuilder.append(categoryTelco.getCreatedUser());
                    } else {
                        categoryTelcoStringBuilder.append("--");
                    }

                    categoryTelcoStringBuilder.append("|");
                    if (categoryTelco.getLastUpdatedTime() != null) {
                        categoryTelcoStringBuilder.append(common.formatDateToString(categoryTelco.getLastUpdatedTime()));
                    } else {
                        categoryTelcoStringBuilder.append("--");
                    }

                    categoryTelcoStringBuilder.append("|");
                    if (categoryTelco.getLastUpdatedUser() != null) {
                        categoryTelcoStringBuilder.append(categoryTelco.getLastUpdatedUser());
                    } else {
                        categoryTelcoStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return categoryTelcoStringBuilder.toString();
    }

    private String getCategoryTelcoAsString(CategoryTelcoInputBean categoryTelcoInputBean, boolean checkChanges) {
        StringBuilder categoryTelcoStringBuilder = new StringBuilder();
        try {
            if (categoryTelcoInputBean != null) {

                if (categoryTelcoInputBean.getCategory() != null && !categoryTelcoInputBean.getCategory().isEmpty()) {
                    categoryTelcoStringBuilder.append(categoryTelcoInputBean.getCategory());
                } else {
                    categoryTelcoStringBuilder.append("error");
                }

                if (categoryTelcoInputBean.getTelco() != null && !categoryTelcoInputBean.getTelco().isEmpty()) {
                    categoryTelcoStringBuilder.append(categoryTelcoInputBean.getTelco());
                } else {
                    categoryTelcoStringBuilder.append("error");
                }

                if (categoryTelcoInputBean.getMtPort() != null && !categoryTelcoInputBean.getMtPort().isEmpty()) {
                    categoryTelcoStringBuilder.append(categoryTelcoInputBean.getMtPort());
                } else {
                    categoryTelcoStringBuilder.append("error");
                }

                categoryTelcoStringBuilder.append("|");
                if (categoryTelcoInputBean.getStatus() != null && !categoryTelcoInputBean.getStatus().isEmpty()) {
                    categoryTelcoStringBuilder.append(categoryTelcoInputBean.getStatus());
                } else {
                    categoryTelcoStringBuilder.append("--");
                }

                if (!checkChanges) {
                    categoryTelcoStringBuilder.append("|");
                    if (categoryTelcoInputBean.getCreatedTime() != null) {
                        categoryTelcoStringBuilder.append(common.formatDateToString(categoryTelcoInputBean.getCreatedTime()));
                    } else {
                        categoryTelcoStringBuilder.append("--");
                    }

                    categoryTelcoStringBuilder.append("|");
                    if (categoryTelcoInputBean.getCreatedUser() != null) {
                        categoryTelcoStringBuilder.append(categoryTelcoInputBean.getCreatedUser());
                    } else {
                        categoryTelcoStringBuilder.append("--");
                    }

                    categoryTelcoStringBuilder.append("|");
                    if (categoryTelcoInputBean.getLastUpdatedTime() != null) {
                        categoryTelcoStringBuilder.append(common.formatDateToString(categoryTelcoInputBean.getLastUpdatedTime()));
                    } else {
                        categoryTelcoStringBuilder.append("--");
                    }

                    categoryTelcoStringBuilder.append("|");
                    if (categoryTelcoInputBean.getLastUpdatedUser() != null) {
                        categoryTelcoStringBuilder.append(categoryTelcoInputBean.getLastUpdatedUser());
                    } else {
                        categoryTelcoStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return categoryTelcoStringBuilder.toString();
    }

    private String insertDualAuthRecord(CategoryTelcoInputBean categoryTelcoInputBean, String taskCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(categoryTelcoInputBean.getCategory(), PageVarList.CATEGORY_TELCO_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.CATEGORY_TELCO_MGT_PAGE);
                tempAuthRecBean.setTask(taskCode);
                tempAuthRecBean.setKey1(categoryTelcoInputBean.getCategory());
                tempAuthRecBean.setKey2(categoryTelcoInputBean.getTelco());
                tempAuthRecBean.setKey3(categoryTelcoInputBean.getMtPort());
                tempAuthRecBean.setKey4(categoryTelcoInputBean.getStatus());
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
