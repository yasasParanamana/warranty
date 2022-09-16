package com.oxcentra.warranty.controller.home;

import com.oxcentra.warranty.bean.home.HomeInputBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.service.common.CommonService;
import com.oxcentra.warranty.service.home.HomeService;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;

@Controller
@Scope("request")
public class HomeController {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    SessionBean sessionBean;

    @Autowired
    CommonService commonService;

    @Autowired
    HomeService homeService;

    @Autowired
    CommonRepository commonRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    Common common;

    @RequestMapping(value = "/home", method = {RequestMethod.GET})
    public ModelAndView getHome( ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  HOME PAGE VIEW");
        return new ModelAndView("home/home", "homemap", new ModelMap());
    }

    @ModelAttribute
    public void getHomeBean(org.springframework.ui.Model map) throws Exception {
        HomeInputBean homeInputBean = new HomeInputBean();

        //pending request Count
        long pendingRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_PENDING);
        long inPurchaseRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_PRE_APPROVED);
        long notedRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_NOTED);

        //set values to claimInputBean bean
        homeInputBean.setCountPending(Long.toString(pendingRequestCount));
        homeInputBean.setCountInPurchase(Long.toString(inPurchaseRequestCount));
        homeInputBean.setCountNoted(Long.toString(notedRequestCount));

        //add values to model map
        map.addAttribute("homeform", homeInputBean);
    }
}
