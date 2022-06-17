package com.epic.rdbsms.controller.profile;

import com.epic.rdbsms.annotation.accesscontrol.AccessControl;
import com.epic.rdbsms.bean.profile.PasswordChangeBean;
import com.epic.rdbsms.bean.session.SessionBean;
import com.epic.rdbsms.repository.common.CommonRepository;
import com.epic.rdbsms.service.profile.ProfileService;
import com.epic.rdbsms.util.common.ResponseBean;
import com.epic.rdbsms.util.varlist.CommonVarList;
import com.epic.rdbsms.util.varlist.MessageVarList;
import com.epic.rdbsms.util.varlist.PageVarList;
import com.epic.rdbsms.util.varlist.TaskVarList;
import com.epic.rdbsms.validators.RequestBeanValidation;
import com.epic.rdbsms.validators.profile.ChangePasswordValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
@Scope("request")
public class ProfileController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    SessionBean sessionBean;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    ChangePasswordValidator changePasswordValidator;

    @Autowired
    ProfileService profileService;

    @Autowired
    CommonRepository commonRepository;

    @GetMapping("/viewPasswordChange")
    @AccessControl(pageCode = PageVarList.PASSWORD_CHANGE_MGT_PAGE, taskCode = TaskVarList.VIEW_TASK)
    public ModelAndView getPasswordChangePage(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  PASSWORD CHANGE PAGE VIEW");
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("passwordchangeview", "beanmap", new ModelMap());
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("passwordchangeview", modelMap);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/passwordchange", method = RequestMethod.POST)
    public ModelAndView postPasswordChange(@ModelAttribute("passwordchangeform") PasswordChangeBean passwordChangeBean, ModelMap modelMap, HttpServletRequest httpServletRequest, Locale locale) {
        ModelAndView modelAndView;
        try {
            BindingResult bindingResult = validateRequestBean(passwordChangeBean);
            if (bindingResult.hasErrors()) {
                String errorMsg = bindingResult.getFieldErrors().stream().findFirst().get().getDefaultMessage();
                //set the error message to model map
                modelMap.put("msg", errorMsg);
                modelAndView = new ModelAndView("profile/changepassword", modelMap);
            } else {
                String message = profileService.changePassword(passwordChangeBean);
                if (message.isEmpty()) {
                    modelMap.put("msg",  messageSource.getMessage(MessageVarList.PASSWORDRESET_SUCCESS, null, locale));
                    modelAndView = new ModelAndView("redirect:/logout.htm", modelMap);
                } else {
                    modelAndView = new ModelAndView("profile/changepassword", modelMap);
                }
            }
        } catch (EmptyResultDataAccessException ex) {
            logger.error("Exception  :  ", ex);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_RECORD_DOESNOT_EXISTS, null, locale));
            modelAndView = new ModelAndView("profile/changepassword", modelMap);
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("profile/changepassword", modelMap);
        }
        return modelAndView;
    }


    @ModelAttribute
    public void getPasswordChange(Model map) throws Exception {
        map.addAttribute("passwordchangeform", new PasswordChangeBean());
    }

    @Override
    public BindingResult validateRequestBean(Object o) {
        DataBinder dataBinder = new DataBinder(o);
        dataBinder.setValidator(changePasswordValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
