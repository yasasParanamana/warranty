package com.epic.rdbsms.service.profile;

import com.epic.rdbsms.bean.profile.PasswordChangeBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.mapping.audittrace.Audittrace;
import com.epic.rdbsms.repository.profile.ProfileRepository;
import com.epic.rdbsms.service.common.CommonService;
import com.epic.rdbsms.util.common.Common;
import com.epic.rdbsms.util.security.SHA256Algorithm;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.TaskVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
@Scope("prototype")
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    SHA256Algorithm sha256Algorithm;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    Common common;

    @Autowired
    SessionBean sessionBean;

    @Autowired
    CommonService commonService;

    public String changePassword(PasswordChangeBean passwordChangeBean) throws Exception {
        String message;
        try {
            Date userPasswordExpiryDate = commonService.getPasswordExpiryDate();
            //create the hash password
            String oldHashPassword = sha256Algorithm.makeHash(passwordChangeBean.getOldPassword());
            String newHashPassword = sha256Algorithm.makeHash(passwordChangeBean.getNewPassword());
            passwordChangeBean.setOldHashPassword(oldHashPassword);
            passwordChangeBean.setNewHashPassword(newHashPassword);
            passwordChangeBean.setPasswordExpiryDate(userPasswordExpiryDate);
            //create audit record
            Audittrace audittrace = common.makeSystemaudit(httpServletRequest, TaskVarList.PW_RESET_TASK, PageVarList.LOGIN_PAGE, "User password reset", null);
            //update the user password
            message = profileRepository.changeUserPassword(passwordChangeBean, audittrace);
            if (message.isEmpty()) {
                audittrace.setDescription("User password reset success");
            } else {
                audittrace.setDescription("User password reset" + message);
            }
            //set audit trace to session bean
            sessionBean.setAudittrace(audittrace);
        } catch (EmptyResultDataAccessException ere) {
            throw ere;
        } catch (Exception e) {
            throw e;
        }
        return message;
    }
}
