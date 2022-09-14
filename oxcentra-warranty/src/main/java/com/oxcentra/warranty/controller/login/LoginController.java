package com.oxcentra.warranty.controller.login;

import com.oxcentra.warranty.bean.home.HomeInputBean;
import com.oxcentra.warranty.bean.login.LoginBean;
import com.oxcentra.warranty.bean.profile.PasswordChangeBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.mapping.usermgt.Page;
import com.oxcentra.warranty.mapping.usermgt.PageTask;
import com.oxcentra.warranty.mapping.usermgt.Section;
import com.oxcentra.warranty.mapping.usermgt.User;
import com.oxcentra.warranty.service.home.HomeService;
import com.oxcentra.warranty.service.login.LoginService;
import com.oxcentra.warranty.service.sysconfigmgt.passwordpolicy.PasswordPolicyService;
import com.oxcentra.warranty.util.varlist.CommonVarList;
import com.oxcentra.warranty.util.varlist.MessageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import com.oxcentra.warranty.validators.RequestBeanValidation;
import com.oxcentra.warranty.validators.login.LoginValidator;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@Scope("request")
public class LoginController implements RequestBeanValidation<Object> {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    SessionBean sessionBean;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CommonVarList commonVarList;

    @Autowired
    LoginService loginService;

    @Autowired
    LoginValidator loginValidator;

    @Autowired
    PasswordPolicyService passwordPolicyService;

    @Autowired
    ServletContext servletContext;

    @Autowired
    HomeService homeService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView getLogin(@RequestParam(value = "error", required = false) Integer error, ModelMap modelMap, Locale locale) {
        return new ModelAndView("login/login", "loginform", new LoginBean());
    }

    @RequestMapping(value = "/checkuser", method = RequestMethod.GET)
    public ModelAndView getUserLogin(ModelMap modelMap, Locale locale) {
        return new ModelAndView("login/login", "loginform", new LoginBean());
    }

    @RequestMapping(value = "/checkuser", method = RequestMethod.POST)
    public ModelAndView postCheckUser(@ModelAttribute("loginform") LoginBean loginBean, ModelMap modelMap, HttpServletRequest httpServletRequest, Locale locale) {
        ModelAndView modelAndView = null;
        try {
            BindingResult bindingResult = validateRequestBean(loginBean);
            if (bindingResult.hasErrors()) {
                String errorMsg = bindingResult.getFieldErrors().stream().findFirst().get().getDefaultMessage();
                //set the error message to model map
                modelMap.put("msg", errorMsg);
                modelAndView = new ModelAndView("login/login", modelMap);
            } else {
                String message = loginService.getUser(loginBean, httpServletRequest);
                //check the return message from service
                if (!message.isEmpty()) {
                    modelMap.put("msg", messageSource.getMessage(message, null, locale));
                    modelAndView = new ModelAndView("login/login", modelMap);
                } else {
                    if (loginBean.getUsername().equals(commonVarList.SYSTEMUSERNAME)) {
                        //handle the user session
                        //get user section list and page list and set to session bean
                        List<Section> sectionList = loginService.getUserSectionListByUserRoleCode(commonVarList.USERROLE_CODE_ADMIN);
                        Map<String, List<Page>> pageList = loginService.getUserPageListByByUserRoleCode(commonVarList.USERROLE_CODE_ADMIN);
                        Map<String, PageTask> pageTaskList = loginService.getUserPageTaskListByByUserRoleCode(commonVarList.USERROLE_CODE_ADMIN);
                        sessionBean.setSectionList(sectionList);
                        sessionBean.setPageMap(pageList);
                        sessionBean.setPageTaskMap(pageTaskList);
                        //redirect to home page
                        modelAndView = new ModelAndView("home/home", modelMap);
                    } else {
                        User user = sessionBean.getUser();
                        if (user.getStatus().equalsIgnoreCase(commonVarList.STATUS_NEW)) {
                            //set section list and page list and to session bean
                            sessionBean.setSectionList(new ArrayList<>());
                            sessionBean.setPageMap(new HashMap<String, List<Page>>());
                            //redirect to change password page
                            modelMap.put("msg", messageSource.getMessage(MessageVarList.PASSWORDRESET_NEWUSER, null, locale));
                            modelAndView = new ModelAndView("profile/changepassword", "passwordchangeform", getPasswordPolicyBean());

                        } else if (user.getStatus().equalsIgnoreCase(commonVarList.STATUS_RESET)) {
                            //set section list and page list and to session bean
                            sessionBean.setSectionList(new ArrayList<>());
                            sessionBean.setPageMap(new HashMap<String, List<Page>>());
                            //redirect to change password page
                            modelMap.put("msg", messageSource.getMessage(MessageVarList.PASSWORDRESET_RESETUSER, null, locale));
                            modelAndView = new ModelAndView("profile/changepassword", "passwordchangeform", getPasswordPolicyBean());

                        } else if (user.getStatus().equalsIgnoreCase(commonVarList.STATUS_CHANGED)) {
                            //set section list and page list and to session bean
                            sessionBean.setSectionList(new ArrayList<>());
                            sessionBean.setPageMap(new HashMap<String, List<Page>>());
                            //redirect to change password page
                            modelMap.put("msg", messageSource.getMessage(MessageVarList.PASSWORDRESET_CHANGEPWD, null, locale));
                            modelAndView = new ModelAndView("profile/changepassword", "passwordchangeform", getPasswordPolicyBean());

                        } else if (user.getStatus().equalsIgnoreCase(commonVarList.STATUS_EXPIRED)) {
                            //set section list and page list and to session bean
                            sessionBean.setSectionList(new ArrayList<>());
                            sessionBean.setPageMap(new HashMap<String, List<Page>>());
                            //redirect to change password page
                            modelMap.put("msg", messageSource.getMessage(MessageVarList.PASSWORDRESET_EXPPWD, null, locale));
                            modelAndView = new ModelAndView("profile/changepassword", "passwordchangeform", getPasswordPolicyBean());

                        } else {
                            int daysToExpire = loginService.getPwdExpNotification();
                            //handle the user session
                            //get user section list and page list and set to session bean
                            //get user page task list and set to session bean
                            List<Section> sectionList = loginService.getUserSectionListByUserRoleCode(user.getUserrole());
                            Map<String, List<Page>> pageList = loginService.getUserPageListByByUserRoleCode(user.getUserrole());
                            Map<String, PageTask> pageTaskList = loginService.getUserPageTaskListByByUserRoleCode(user.getUserrole());
                            sessionBean.setSectionList(sectionList);
                            sessionBean.setPageMap(pageList);
                            sessionBean.setPageTaskMap(pageTaskList);
                            sessionBean.setDaysToExpire(daysToExpire);
                            //redirect to home page
                            modelAndView = new ModelAndView("home/home", "beanmap", new ModelMap());
                        }
                    }
                }
            }
        } catch (EmptyResultDataAccessException ex) {
            logger.error("Exception  :  ", ex);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.LOGIN_INVALID, null, locale));
            modelAndView = new ModelAndView("login/login", modelMap);
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
            //set the error message to model map
            modelMap.put("msg", messageSource.getMessage(MessageVarList.COMMON_ERROR_PROCESS, null, locale));
            modelAndView = new ModelAndView("login/login", modelMap);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/logout")
    public ModelAndView getLogout(@RequestParam(value = "error", required = false) Integer error, @RequestParam(value = "msg", required = false) String msg, ModelMap modelMap, HttpSession httpSession, Locale locale) {
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView("login/login", "loginform", new LoginBean());
            if(msg != null){
                modelMap.put("cmsg",  messageSource.getMessage(MessageVarList.PASSWORDRESET_SUCCESS, null, locale));
            }
            if (error != null) {
                if (error == 1) {
                    String userName = sessionBean.getUsername();
                    //remove the username from session map
                    Map<String, String> sessionMap = (Map<String, String>) servletContext.getAttribute("sessionMap");
                    sessionMap.remove(userName);
                    //set the session bean to null
                    this.cleanUp();
                    this.sessionBean = null;
                    //set the message to model and view
                    modelAndView.addObject("msg", messageSource.getMessage(MessageVarList.LOGIN_STATUSTIMEOUT, null, locale));

                } else if (error == 2) {
                    String userName = sessionBean.getUsername();
                    //remove the username from session map
                    Map<String, String> sessionMap = (Map<String, String>) servletContext.getAttribute("sessionMap");
                    sessionMap.remove(userName);
                    //set the session bean to null
                    this.cleanUp();
                    this.sessionBean = null;
                    //set the message to model and view
                    modelAndView.addObject("msg", messageSource.getMessage(MessageVarList.USER_NEWPASSWORD_RESET_ERROR, null, locale));

                } else if (error == 3) {
                    String userName = sessionBean.getUsername();
                    //remove the username from session map
                    Map<String, String> sessionMap = (Map<String, String>) servletContext.getAttribute("sessionMap");
                    sessionMap.remove(userName);
                    //set the session bean to null
                    this.cleanUp();
                    this.sessionBean = null;
                    //set the message to model and view
                    modelAndView.addObject("msg", messageSource.getMessage(MessageVarList.USER_SESSION_NOTFOUND, null, locale));

                } else if (error == 4) {
                    String userName = sessionBean.getUsername();
                    //remove the username from session map
                    Map<String, String> sessionMap = (Map<String, String>) servletContext.getAttribute("sessionMap");
                    sessionMap.remove(userName);
                    //set the session bean to null
                    this.cleanUp();
                    this.sessionBean = null;
                    //set the message to model and view
                    modelAndView.addObject("msg", messageSource.getMessage(MessageVarList.USER_SESSION_INVALID, null, locale));

                } else if (error == 5) {
                    String userName = sessionBean.getUsername();
                    //remove the username from session map
                    Map<String, String> sessionMap = (Map<String, String>) servletContext.getAttribute("sessionMap");
                    sessionMap.remove(userName);
                    //set the session bean to null
                    this.cleanUp();
                    this.sessionBean = null;
                    //set the message to model and view
                    modelAndView.addObject("msg", messageSource.getMessage(MessageVarList.USER_REQUESTED_PASSWORDCHANGE, null, locale));

                } else if (error == 6) {
                    String userName = sessionBean.getUsername();
                    //remove the username from session map
                    Map<String, String> sessionMap = (Map<String, String>) servletContext.getAttribute("sessionMap");
                    sessionMap.remove(userName);
                    //set the session bean to null
                    this.cleanUp();
                    this.sessionBean = null;
                    //set the message to model and view
                    modelAndView.addObject("msg", messageSource.getMessage(MessageVarList.USER_PRIVILEGE_INSUFFICIENT, null, locale));

                } else if (error == 7) {
                    String userName = sessionBean.getUsername();
                    //remove the username from session map
                    Map<String, String> sessionMap = (Map<String, String>) servletContext.getAttribute("sessionMap");
                    sessionMap.remove(userName);
                    //set the session bean to null
                    this.cleanUp();
                    this.sessionBean = null;
                    //set the message to model and view
                    modelAndView.addObject("msg", messageSource.getMessage(MessageVarList.USER_SESSION_TIMEOUT, null, locale));

                } else {
                    String userName = sessionBean.getUsername();
                    //remove the username from session map
                    Map<String, String> sessionMap = (Map<String, String>) servletContext.getAttribute("sessionMap");
                    sessionMap.remove(userName);
                    //set the session bean to null
                    this.cleanUp();
                    this.sessionBean = null;
                }
            } else {
                String userName = sessionBean.getUsername();
                //remove the username from session map
                Map<String, String> sessionMap = (Map<String, String>) servletContext.getAttribute("sessionMap");
                sessionMap.remove(userName);
                //set the session bean to null
                this.cleanUp();
                this.sessionBean = null;
            }
        } catch (Exception e) {
            //set the session bean to null
            this.cleanUp();
            this.sessionBean = null;
        }
        //set the http session to invalidate
        httpSession.invalidate();
        return modelAndView;
    }

    @ModelAttribute
    public void getLoginbean(Model map) throws Exception {

/*        LoginBean loginBean = new LoginBean();

        //pending request Count
        long pendingRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_PENDING);
        long inPurchaseRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_IN_PURCHASE);
        long notedRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_NOTED);

        //set values to claimInputBean bean
        loginBean.setCountPending(Long.toString(pendingRequestCount));
        loginBean.setCountInPurchase(Long.toString(inPurchaseRequestCount));
        loginBean.setCountNoted(Long.toString(notedRequestCount));*/

        map.addAttribute("loginform", new LoginBean());
    }

    private PasswordChangeBean getPasswordPolicyBean() {
        PasswordChangeBean passwordChangeBean = null;
        try {
            passwordChangeBean = new PasswordChangeBean();
            //set the password policy to session bean
            passwordPolicyService.getWebPasswordPolicy(commonVarList.WEB_PASSWORDPOLICY_ONE);
        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return passwordChangeBean;
    }

    public void cleanUp() {
        sessionBean.setSessionid(null);
        sessionBean.setUsername(null);
        sessionBean.setUser(null);
        sessionBean.setChangePwdMode(false);
        sessionBean.setDaysToExpire(0);
        sessionBean.setAudittrace(null);
        sessionBean.setSectionList(null);
        sessionBean.setPageMap(null);
        sessionBean.setPageTaskMap(null);
        sessionBean.setPasswordPolicy(null);
    }

    @Override
    public BindingResult validateRequestBean(Object object) {
        DataBinder dataBinder = new DataBinder(object);
        dataBinder.setValidator(loginValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
