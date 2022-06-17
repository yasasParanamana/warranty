package com.oxcentra.rdbsms.service.usermgt.page;

import com.oxcentra.rdbsms.bean.common.TempAuthRecBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.bean.usermgt.page.PageInputBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.tmpauthrec.TempAuthRec;
import com.oxcentra.rdbsms.mapping.usermgt.Page;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.usermgt.page.PageRepository;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.varlist.*;
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
public class PageService {

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
    PageRepository pageRepository;

    private final String fields = "Page Code|Description|Url|Sort Key|Actual Flag|Current Flag|Status|Created Time|Last Updated Time|Last Updated User";

    public long getDataCount(PageInputBean inputBean) throws Exception {
        long count = 0;
        try {
            count = pageRepository.getDataCount(inputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<Page> getPageSearchResults(PageInputBean pageInputBean) throws Exception {
        List<Page> pageList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_USER_MGT);
            audittrace.setPage(PageVarList.PAGE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get page search list.");
            //Get department search list.
            pageList = pageRepository.getPageSearchResults(pageInputBean);
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

    public long getDataCountDual(PageInputBean inputBean) throws Exception {
        long count = 0;
        try {
            count = pageRepository.getDataCountDual(inputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public List<TempAuthRec> getPageSearchResultsDual(PageInputBean inputBean) throws Exception {
        List<TempAuthRec> pageDualList;
        try {
            //set audit trace values
            audittrace.setSection(SectionVarList.SECTION_USER_MGT);
            audittrace.setPage(PageVarList.PAGE_MGT_PAGE);
            audittrace.setTask(TaskVarList.VIEW_TASK);
            audittrace.setDescription("Get page dual authentication search list.");
            //Get page dual authentication search list
            pageDualList = pageRepository.getPageSearchResultsDual(inputBean);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        } finally {
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return pageDualList;
    }

    public Page getPage(String pageCode) throws Exception {
        Page page;
        try {
            page = pageRepository.getPage(pageCode);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return page;
    }

    public String updatePage(PageInputBean pageInputBean, Locale locale) throws Exception {
        String message = "";
        String auditDescription = "";
        Page existingPage = null;
        try {
            existingPage = pageRepository.getPage(pageInputBean.getPageCode());
            if (existingPage != null) {
                //check changed values
                String oldValueAsString = this.getPageAsString(existingPage, true);
                String newValueAsString = this.getPageAsString(pageInputBean, true);
                //check the old value and new value
                if (oldValueAsString.equals(newValueAsString)) {
                    message = MessageVarList.COMMON_ERROR_NO_VALUE_CHANGE;
                } else {
                    //set the other values to input bean
                    Date currentDate = commonRepository.getCurrentDate();
                    String lastUpdatedUser = sessionBean.getUsername();

                    pageInputBean.setLastUpdatedTime(currentDate);
                    pageInputBean.setLastUpdatedUser(lastUpdatedUser);

                    //check the page dual auth enable or disable
                    if (commonRepository.checkPageIsDualAuthenticate(PageVarList.PAGE_MGT_PAGE)) {
                        auditDescription = "Requested to update page (code: " + pageInputBean.getPageCode() + ")";
                        message = this.insertDualAuthRecord(pageInputBean, TaskVarList.UPDATE_TASK);
                    } else {
                        auditDescription = "Page (code: " + pageInputBean.getPageCode() + ") updated by " + sessionBean.getUsername();
                        message = pageRepository.updatePage(pageInputBean);
                    }
                }
            } else {
                message = MessageVarList.PAGE_MGT_NORECORD_FOUND;
                auditDescription = messageSource.getMessage(MessageVarList.PAGE_MGT_NORECORD_FOUND, null, locale);
            }
        } catch (EmptyResultDataAccessException ex) {
            message = MessageVarList.PAGE_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.PAGE_MGT_PAGE);
                audittrace.setTask(TaskVarList.UPDATE_TASK);
                audittrace.setDescription(auditDescription);
                //create audit record
                audittrace.setField(fields);
                audittrace.setOldvalue(this.getPageAsString(existingPage, false));
                audittrace.setNewvalue(this.getPageAsString(pageInputBean, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String confirmPage(String id) throws Exception {
        String message = "";
        String auditDescription = "";
        PageInputBean pageInputBean = null;
        Page existingPage = null;
        try {
            //get tmp auth record
            TempAuthRecBean tempAuthRecBean = commonRepository.getTempAuthRecord(id);
            if (tempAuthRecBean != null) {
                pageInputBean = new PageInputBean();
                pageInputBean.setPageCode(tempAuthRecBean.getKey1());
                pageInputBean.setDescription(tempAuthRecBean.getKey2());
                pageInputBean.setUrl(tempAuthRecBean.getKey3());
                pageInputBean.setSortKey(Integer.parseInt(tempAuthRecBean.getKey4()));
                pageInputBean.setActualFalg(new Boolean(tempAuthRecBean.getKey5()));
                pageInputBean.setCurrentFlag(new Boolean(tempAuthRecBean.getKey6()));
                pageInputBean.setStatus(tempAuthRecBean.getKey7());
                pageInputBean.setLastUpdatedTime(commonRepository.getCurrentDate());
                pageInputBean.setLastUpdatedUser(sessionBean.getUsername());

                //get the existing page
                try {
                    String code = pageInputBean.getPageCode();
                    existingPage = pageRepository.getPage(code);
                } catch (EmptyResultDataAccessException e) {
                    existingPage = null;
                }

                if (TaskVarList.UPDATE_TASK.equals(tempAuthRecBean.getTask())) {
                    if (existingPage == null) {
                        message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
                    } else if (pageRepository.checkSortKeyExist(tempAuthRecBean.getKey1(), Integer.parseInt(tempAuthRecBean.getKey4()))) {
                        message = MessageVarList.PAGE_MGT_SORTKEY_EXIST;
                    } else {
                        message = pageRepository.updatePage(pageInputBean);
                    }
                }

                //if page db operation success, update temp auth record
                if (message.isEmpty()) {
                    message = commonRepository.updateTempAuthRecord(id, StatusVarList.STATUS_AUTH_CON);
                    //if temp auth db operation success,insert the audit
                    if (message.isEmpty()) {
                        StringBuilder auditDesBuilder = new StringBuilder();
                        auditDesBuilder.append("Approved performing  '").append(tempAuthRecBean.getTask())
                                .append("' operation on page (page code: ").append(pageInputBean.getPageCode())
                                .append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" approved by ")
                                .append(sessionBean.getUsername());

                        auditDescription = auditDesBuilder.toString();
                    } else {
                        message = MessageVarList.COMMON_ERROR_PROCESS;
                    }
                }
            } else {
                message = MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS;
            }
        } catch (EmptyResultDataAccessException ere) {
            message = MessageVarList.PAGE_MGT_NORECORD_FOUND;
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
                audittrace.setPage(PageVarList.PAGE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_CONFIRM_TASK);
                audittrace.setDescription(auditDescription);
                audittrace.setField(fields);
                audittrace.setNewvalue(this.getPageAsString(pageInputBean, false));
                audittrace.setOldvalue(this.getPageAsString(existingPage, false));
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;

    }

    public String rejectPage(String id) throws Exception {
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
                    auditDesBuilder.append("Rejected performing  '").append(tempAuthRecBean.getTask()).append("' operation on page (code: ").append(tempAuthRecBean.getKey1()).append(") inputted by ").append(tempAuthRecBean.getLastUpdatedUser()).append(" rejected by ").append(sessionBean.getUsername());
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
                audittrace.setPage(PageVarList.PAGE_MGT_PAGE);
                audittrace.setTask(TaskVarList.DUAL_AUTH_REJECT_TASK);
                audittrace.setDescription(auditDescription);
            }
            //set audit to session bean
            sessionBean.setAudittrace(audittrace);
        }
        return message;
    }

    public String insertDualAuthRecord(PageInputBean pageInputBean, String pageCode) throws Exception {
        String message = "";
        try {
            long count = commonRepository.getTempAuthRecordCount(pageInputBean.getPageCode(), PageVarList.PAGE_MGT_PAGE, StatusVarList.STATUS_AUTH_PEN);
            if (count > 0) {
                message = MessageVarList.TMP_RECORD_ALREADY_EXISTS;
            } else if (pageRepository.checkTempSortKeyExist(PageVarList.PAGE_MGT_PAGE, pageInputBean.getSortKey())) {
                message = MessageVarList.PAGE_MGT_SORTKEY_TEMP_EXIST;
            } else {
                TempAuthRecBean tempAuthRecBean = new TempAuthRecBean();
                tempAuthRecBean.setPage(PageVarList.PAGE_MGT_PAGE);
                tempAuthRecBean.setTask(pageCode);
                tempAuthRecBean.setKey1(pageInputBean.getPageCode().toUpperCase());
                tempAuthRecBean.setKey2(pageInputBean.getDescription().trim());
                tempAuthRecBean.setKey3(pageInputBean.getUrl().trim());
                tempAuthRecBean.setKey4(String.valueOf(pageInputBean.getSortKey()));
                tempAuthRecBean.setKey5(String.valueOf(pageInputBean.isActualFalg()));
                tempAuthRecBean.setKey6(String.valueOf(pageInputBean.isCurrentFlag()));
                tempAuthRecBean.setKey7(pageInputBean.getStatus());
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

    private String getPageAsString(Page page, boolean checkChanges) {
        StringBuilder pageStringBuilder = new StringBuilder();
        try {
            if (page != null) {
                if (page.getPageCode() != null) {
                    pageStringBuilder.append(page.getPageCode());
                } else {
                    pageStringBuilder.append("error");
                }

                pageStringBuilder.append("|");
                if (page.getDescription() != null) {
                    pageStringBuilder.append(page.getDescription());
                } else {
                    pageStringBuilder.append("--");
                }

                pageStringBuilder.append("|");
                if (page.getUrl() != null) {
                    pageStringBuilder.append(page.getUrl());
                } else {
                    pageStringBuilder.append("--");
                }

                pageStringBuilder.append("|");
                if (String.valueOf(page.getSortKey()) != null) {
                    pageStringBuilder.append(page.getSortKey());
                } else {
                    pageStringBuilder.append("--");
                }

                pageStringBuilder.append("|");
                if (String.valueOf(page.isActualFalg()) != null) {
                    pageStringBuilder.append(page.isActualFalg());
                } else {
                    pageStringBuilder.append("--");
                }

                pageStringBuilder.append("|");
                if (String.valueOf(page.isCurrentFlag()) != null) {
                    pageStringBuilder.append(page.isCurrentFlag());
                } else {
                    pageStringBuilder.append("--");
                }

                pageStringBuilder.append("|");
                if (page.getStatusCode() != null) {
                    pageStringBuilder.append(page.getStatusCode());
                } else {
                    pageStringBuilder.append("--");
                }

                if (!checkChanges) {
                    pageStringBuilder.append("|");
                    if (page.getCreatedTime() != null) {
                        pageStringBuilder.append(common.formatDateToString(page.getCreatedTime()));
                    } else {
                        pageStringBuilder.append("--");
                    }

                    pageStringBuilder.append("|");
                    if (page.getLastUpdatedTime() != null) {
                        pageStringBuilder.append(common.formatDateToString(page.getLastUpdatedTime()));
                    } else {
                        pageStringBuilder.append("--");
                    }

                    pageStringBuilder.append("|");
                    if (page.getLastUpdatedUser() != null) {
                        pageStringBuilder.append(page.getLastUpdatedUser());
                    } else {
                        pageStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return pageStringBuilder.toString();
    }

    private String getPageAsString(PageInputBean pageInputBean, boolean checkChanges) {
        StringBuilder pageStringBuilder = new StringBuilder();
        try {
            if (pageInputBean != null) {
                if (pageInputBean.getPageCode() != null) {
                    pageStringBuilder.append(pageInputBean.getPageCode());
                } else {
                    pageStringBuilder.append("error");
                }

                pageStringBuilder.append("|");
                if (pageInputBean.getDescription() != null) {
                    pageStringBuilder.append(pageInputBean.getDescription());
                } else {
                    pageStringBuilder.append("--");
                }

                pageStringBuilder.append("|");
                if (pageInputBean.getUrl() != null) {
                    pageStringBuilder.append(pageInputBean.getUrl());
                } else {
                    pageStringBuilder.append("--");
                }

                pageStringBuilder.append("|");
                if (String.valueOf(pageInputBean.getSortKey()) != null) {
                    pageStringBuilder.append(pageInputBean.getSortKey());
                } else {
                    pageStringBuilder.append("--");
                }

                pageStringBuilder.append("|");
                if (String.valueOf(pageInputBean.isActualFalg()) != null) {
                    pageStringBuilder.append(pageInputBean.isActualFalg());
                } else {
                    pageStringBuilder.append("--");
                }

                pageStringBuilder.append("|");
                if (String.valueOf(pageInputBean.isCurrentFlag()) != null) {
                    pageStringBuilder.append(pageInputBean.isCurrentFlag());
                } else {
                    pageStringBuilder.append("--");
                }

                pageStringBuilder.append("|");
                if (pageInputBean.getStatus() != null) {
                    pageStringBuilder.append(pageInputBean.getStatus());
                } else {
                    pageStringBuilder.append("--");
                }

                if (!checkChanges) {
                    pageStringBuilder.append("|");
                    if (pageInputBean.getCreatedTime() != null) {
                        pageStringBuilder.append(common.formatDateToString(pageInputBean.getCreatedTime()));
                    } else {
                        pageStringBuilder.append("--");
                    }

                    pageStringBuilder.append("|");
                    if (pageInputBean.getLastUpdatedTime() != null) {
                        pageStringBuilder.append(common.formatDateToString(pageInputBean.getLastUpdatedTime()));
                    } else {
                        pageStringBuilder.append("--");
                    }

                    pageStringBuilder.append("|");
                    if (pageInputBean.getLastUpdatedUser() != null) {
                        pageStringBuilder.append(pageInputBean.getLastUpdatedUser());
                    } else {
                        pageStringBuilder.append("--");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return pageStringBuilder.toString();
    }
}
