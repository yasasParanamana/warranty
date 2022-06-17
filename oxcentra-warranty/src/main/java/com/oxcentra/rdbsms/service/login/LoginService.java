package com.oxcentra.rdbsms.service.login;

import com.oxcentra.rdbsms.bean.login.LoginBean;
import com.oxcentra.rdbsms.bean.session.SessionBean;
import com.oxcentra.rdbsms.mapping.audittrace.Audittrace;
import com.oxcentra.rdbsms.mapping.usermgt.Page;
import com.oxcentra.rdbsms.mapping.usermgt.PageTask;
import com.oxcentra.rdbsms.mapping.usermgt.Section;
import com.oxcentra.rdbsms.mapping.usermgt.User;
import com.oxcentra.rdbsms.repository.common.CommonRepository;
import com.oxcentra.rdbsms.repository.login.LoginRepository;
import com.oxcentra.rdbsms.util.common.Common;
import com.oxcentra.rdbsms.util.security.SHA256Algorithm;
import com.oxcentra.rdbsms.util.varlist.CommonVarList;
import com.oxcentra.rdbsms.util.varlist.MessageVarList;
import com.oxcentra.rdbsms.util.varlist.PageVarList;
import com.oxcentra.rdbsms.util.varlist.TaskVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope("prototype")
public class LoginService {

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Common common;

    @Autowired
    SHA256Algorithm sha256Algorithm;

    @Autowired
    LoginRepository loginRepository;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    ServletContext servletContext;

    public String getUser(LoginBean loginBean, HttpServletRequest httpServletRequest) throws Exception {
        String message = "";
        try {
            //get hash 256 password
            String hashPassword = sha256Algorithm.makeHash(loginBean.getPassword());
            if (loginBean.getUsername().equals(commonVarList.SYSTEMUSERNAME) && hashPassword.equals(commonVarList.SYSTEMUSERPWD)) {
                User user = new User();
                user.setUserName(commonVarList.SYSTEMUSERNAME);
                user.setPassword(commonVarList.SYSTEMUSERPWD);
                //set the user data to session bean
                sessionBean.setUser(user);
            } else {
                User user = loginRepository.getUser(loginBean);
                //check user validation from database
                if (user == null) {
                    message = MessageVarList.LOGIN_INVALID;

                } else if (!hashPassword.equals(user.getPassword())) {
                    int noOfAttempts = user.getNoofinvlidattempt() == null ? 1 : user.getNoofinvlidattempt() + 1;
                    //create audit trace
                    Audittrace audittrace = common.makeSystemaudit(httpServletRequest, user, TaskVarList.LOGIN_TASK, PageVarList.LOGIN_PAGE, "Login attempt " + noOfAttempts, null);
                    //set message
                    message = MessageVarList.LOGIN_INVALID;
                    //check user no of attempts
                    if (user.getNoofinvlidattempt() >= commonRepository.getPasswordParam(commonVarList.PARAMCODE_NOOF_INVALIDLOGINATTEMPTS, commonVarList.USERROLE_TYPE_WEB)) {
                        loginBean.setAttempts(noOfAttempts);
                        loginBean.setStatusCode(commonVarList.STATUS_DEACTIVE);
                        audittrace = common.makeSystemaudit(httpServletRequest, user, TaskVarList.LOGIN_TASK, PageVarList.LOGIN_PAGE, "Login de-activated", null);
                        message = MessageVarList.LOGIN_DEACTIVE;
                    } else {
                        loginBean.setAttempts(noOfAttempts);
                        loginBean.setStatusCode(user.getStatus());
                    }
                    //update the user
                    loginRepository.updateUser(loginBean, audittrace, false);
                } else if (user.getAd() == commonVarList.ENABLE_AD_AUTHENTICATION) {
                    //handle the ad authentication -> TODO

                } else if (user.getStatus().equals(commonVarList.STATUS_DEACTIVE)) {
                    message = MessageVarList.LOGIN_DEACTIVE;
                } else if (this.checkUserRoleDeactive(user.getUserrole())) {
                    message = MessageVarList.LOGIN_DEACTIVE;
                } else if (this.checkUserIncative(user)) {
                    //set message
                    message = MessageVarList.LOGIN_IDLEDEACTIVE;
                    //create audit trace
                    Audittrace audittrace = common.makeSystemaudit(httpServletRequest, user, TaskVarList.LOGIN_TASK, PageVarList.LOGIN_PAGE, "Login de-activated", null);
                    //update the user
                    loginBean.setStatusCode(commonVarList.STATUS_DEACTIVE);
                    loginRepository.updateUser(loginBean, audittrace, false);
                } else {
                    String remark;
                    if (user.getStatus().equalsIgnoreCase(commonVarList.STATUS_NEW)) {
                        sessionBean.setChangePwdMode(true);
                        remark = "Login successful | First Time Login";

                    } else if (user.getStatus().equalsIgnoreCase(commonVarList.STATUS_RESET)) {
                        sessionBean.setChangePwdMode(true);
                        remark = "Login Successful | Password Reset";

                    } else if (user.getStatus().equalsIgnoreCase(commonVarList.STATUS_CHANGED)) {
                        sessionBean.setChangePwdMode(true);
                        remark = "Login Successful | Password Change requested";

                    } else if (user.getStatus().equalsIgnoreCase(commonVarList.STATUS_EXPIRED)) {
                        sessionBean.setChangePwdMode(true);
                        remark = "Login Successful | Password Expired";

                    } else {
                        remark = "Login successfully";
                    }
                    //create audit record
                    Audittrace audittrace = common.makeSystemaudit(httpServletRequest, user, TaskVarList.LOGIN_TASK, PageVarList.LOGIN_PAGE, null, remark);
                    //update the user
                    loginBean.setAttempts(new Byte("0"));
                    loginBean.setStatusCode(user.getStatus());
                    loginRepository.updateUser(loginBean, audittrace, true);
                    //set the user data to session bean
                    HttpSession httpSession = httpServletRequest.getSession(true);
                    sessionBean.setSessionid(httpSession.getId());
                    sessionBean.setUsername(loginBean.getUsername());
                    sessionBean.setUser(user);
                    sessionBean.setAudittrace(audittrace);
                    //get the session map
                    Map<String, String> sessionMap = (Map<String, String>) servletContext.getAttribute("sessionMap");
                    if (sessionMap == null) {
                        // Create a sessionMap instance.
                        sessionMap = new HashMap<>();
                    }
                    sessionMap.put(loginBean.getUsername(), httpSession.getId());
                    servletContext.setAttribute("sessionMap", sessionMap);
                }
            }
        } catch (EmptyResultDataAccessException ex) {
            throw ex;
        } catch (Exception e) {
            throw e;
        }
        return message;
    }

    private boolean checkUserRoleDeactive(String userrole) throws Exception {
        boolean isUserRoleDeactive = false;
        try {
            String userRoleStatusCode = commonRepository.getUserRoleStatusCode(userrole);
            if (commonVarList.STATUS_DEACTIVE.equals(userRoleStatusCode)) {
                isUserRoleDeactive = true;
            }
        } catch (Exception e) {
            throw e;
        }
        return isUserRoleDeactive;
    }

    private boolean checkUserIncative(User user) throws Exception {
        boolean isUserInactive = false;
        try {
            Date currentDate = commonRepository.getCurrentDate();
            int minInactiveDays = commonRepository.getPasswordParam(commonVarList.PARAMCODE_IDLEACCOUNT_EXPIRYPERIOD, commonVarList.USERROLE_TYPE_WEB);
            //calculate the max inactive time
            long maxInactiveTime = minInactiveDays * commonVarList.TIMESTAMP_VALUE_PERDAY;
            //check the user inactive times
            if (currentDate != null && user.getLoggeddate() != null && minInactiveDays > 0) {
                long currnetIncativeTime = currentDate.getTime() - user.getLoggeddate().getTime();
                if (currnetIncativeTime >= maxInactiveTime) {
                    isUserInactive = true;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return isUserInactive;
    }

    public int getPwdExpNotification() throws Exception {
        int daysToExpire = 0;
        try {
            Date currentDate = commonRepository.getCurrentDate();
            int notificationPeriod = commonRepository.getPasswordParam(commonVarList.PARAMCODE_PASSWORDEXPIRY_NOTIFICATIONPERIOD, commonVarList.USERROLE_TYPE_WEB);
            //calculate the max inactive time
            long maxInactiveTime = notificationPeriod * commonVarList.TIMESTAMP_VALUE_PERDAY;
            //get the user from the session beans
            User user = sessionBean.getUser();
            if (currentDate != null && user.getExpirydate() != null && notificationPeriod > 0) {
                long currentInactiveTime = user.getExpirydate().getTime() - currentDate.getTime();
                if (maxInactiveTime >= currentInactiveTime) {
                    long diffInSeconds = currentInactiveTime / 1000 % 60;
                    long diffInMinutes = currentInactiveTime / (60 * 1000) % 60;
                    long diffInHours = currentInactiveTime / (60 * 60 * 1000) % 24;
                    long diffInDays = currentInactiveTime / (24 * 60 * 60 * 1000);

                    if (diffInDays > 0) {
                        daysToExpire = Long.valueOf(diffInDays).intValue();
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return daysToExpire;
    }

    public List<Section> getUserSectionListByUserRoleCode(String userRoleCode) {
        List<Section> userRoleSectionList = null;
        try {
            userRoleSectionList = loginRepository.getUserSectionListByUserRoleCode(userRoleCode);
        } catch (Exception e) {
            throw e;
        }
        return userRoleSectionList;
    }

    public Map<String, List<Page>> getUserPageListByByUserRoleCode(String userRoleCode) {
        Map<String, List<Page>> userRolePageList = null;
        try {
            userRolePageList = loginRepository.getUserPageListByUserRoleCode(userRoleCode);
        } catch (Exception e) {
            throw e;
        }
        return userRolePageList;
    }

    public Map<String, PageTask> getUserPageTaskListByByUserRoleCode(String userRoleCode) {
        Map<String, PageTask> userRolePageTaskList = null;
        try {
            userRolePageTaskList = loginRepository.getUserPageTaskListByByUserRoleCode(userRoleCode);
        } catch (Exception e) {
            throw e;
        }
        return userRolePageTaskList;
    }
}
