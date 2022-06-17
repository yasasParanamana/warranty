package com.oxcentra.warranty.service.profile;

import com.oxcentra.warranty.bean.profile.PasswordChangeBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.repository.profile.ProfileRepository;
import com.oxcentra.warranty.service.common.CommonService;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.security.SHA256Algorithm;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.TaskVarList;
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
