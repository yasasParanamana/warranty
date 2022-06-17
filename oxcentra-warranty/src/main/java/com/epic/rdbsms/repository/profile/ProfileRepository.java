package com.epic.rdbsms.repository.profile;

import com.epic.rdbsms.bean.profile.PasswordChangeBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.mapping.usermgt.User;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.security.SHA256Algorithm;
import com.epic.rdbsms.util.varlist.CommonVarList;
import com.epic.rdbsms.util.varlist.MessageVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Scope("prototype")
public class ProfileRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    Common common;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    SHA256Algorithm sha256Algorithm;

    private final String SQL_UPDATE_SYSTEMUSER = "update WEB_SYSTEMUSER set password=? , expirydate=? ,loggeddate=?, status=? , lastupdateduser=? , lastupdatedtime=? where username=?";
    private final String SQL_INSERT_PASSWORDHISTORY = "insert into WEB_PASSWORDHISTORY(username,password,lastupdateduser) values(?,?,?)";

    @Transactional
    public String changeUserPassword(PasswordChangeBean passwordChangeBean, Audittrace audittrace) throws Exception {
        String message = "";
        try {
            User user = sessionBean.getUser();
            if (user != null) {
                //create old user string
                String before = getUserString(user);
                //set the new values to user oject
                Date currentDate = commonRepository.getCurrentDate();
                user.setPassword(passwordChangeBean.getNewHashPassword());
                user.setExpirydate(passwordChangeBean.getPasswordExpiryDate());
                user.setLoggeddate(currentDate);
                user.setStatus(commonVarList.STATUS_ACTIVE);
                user.setLastupdateduser(sessionBean.getUsername());
                user.setLastupdatedtime(currentDate);
                //create new user string
                String after = getUserString(user);
                //update the system user table
                int i = jdbcTemplate.update(SQL_UPDATE_SYSTEMUSER, user.getPassword(), user.getExpirydate(), user.getLoggeddate(), user.getStatus(), user.getLastupdateduser(), user.getLastupdatedtime(), user.getUserName());
                if (i == 1) {
                    int j = jdbcTemplate.update(SQL_INSERT_PASSWORDHISTORY, user.getUserName(), passwordChangeBean.getOldHashPassword(), user.getUserName());
                    if (j == 1) {
                        audittrace.setOldvalue(before);
                        audittrace.setNewvalue(after);
                        audittrace.setCreatedtime(currentDate);
                        audittrace.setLastupdatedtime(currentDate);
                    } else {
                        message = MessageVarList.USER_NEWPASSWORD_RESET_ERROR;
                    }
                } else {
                    message = MessageVarList.USER_NEWPASSWORD_RESET_ERROR;
                }
            }
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return message;
    }

    private String getUserString(User user) {
        StringBuilder userString = new StringBuilder();
        try {
            if (user.getUserName() != null) {
                userString = userString.append(user.getUserName());
            } else {
                userString = userString.append(user.getUserName());
                return userString.toString();
            }
            userString = userString.append("|");


            if (user.getPassword() != null) {
                userString = userString.append(user.getPassword());
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getUserrole() != null) {
                userString = userString.append(user.getUserrole());
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getExpirydate() != null) {
                userString = userString.append(common.formatDateToString(user.getExpirydate()));
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getFullname() != null) {
                userString = userString.append(user.getFullname());
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getFullname() != null) {
                userString = userString.append(user.getEmail());
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getMobile() != null) {
                userString = userString.append(user.getMobile());
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getNoofinvlidattempt() != null) {
                userString = userString.append(user.getNoofinvlidattempt().toString());
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getLoggeddate() != null) {
                userString = userString.append(common.formatDateToString(user.getLoggeddate()));
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getInitialloginstatus() != null) {
                userString = userString.append(user.getInitialloginstatus());
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getAd() != null) {
                userString = userString.append(user.getAd().toString());
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getStatus() != null) {
                userString = userString.append(user.getStatus());
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getLastupdateduser() != null) {
                userString = userString.append(user.getLastupdateduser());
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getLastupdatedtime() != null) {
                userString = userString.append(common.formatDateToString(user.getLastupdatedtime()));
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");


            if (user.getCreatetime() != null) {
                userString = userString.append(common.formatDateToString(user.getCreatetime()));
            } else {
                userString = userString.append("--");
            }
            userString = userString.append("|");
        } catch (Exception e) {
            throw e;
        }
        return userString.toString();
    }
}
