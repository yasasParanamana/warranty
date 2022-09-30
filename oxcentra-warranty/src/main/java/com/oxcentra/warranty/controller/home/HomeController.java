package com.oxcentra.warranty.controller.home;

import com.oxcentra.warranty.annotation.accesscontrol.AccessControl;
import com.oxcentra.warranty.bean.home.HomeInputBean;
import com.oxcentra.warranty.bean.home.SummaryBean;
import com.oxcentra.warranty.bean.home.SummaryInputBean;
import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.bean.warranty.claim.ClaimInputBean;
import com.oxcentra.warranty.mapping.warranty.Claim;
import com.oxcentra.warranty.mapping.warranty.SpareParts;
import com.oxcentra.warranty.mapping.warranty.Supplier;
import com.oxcentra.warranty.mapping.warranty.WarrantyAttachments;
import com.oxcentra.warranty.repository.common.CommonRepository;
import com.oxcentra.warranty.service.common.CommonService;
import com.oxcentra.warranty.service.home.HomeService;
import com.oxcentra.warranty.util.common.Common;
import com.oxcentra.warranty.util.common.DataTablesResponse;
import com.oxcentra.warranty.util.varlist.PageVarList;
import com.oxcentra.warranty.util.varlist.StatusVarList;
import com.oxcentra.warranty.util.varlist.TaskVarList;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

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

    @GetMapping(value = "/getSummaryhome", headers = {"content-type=application/json"})
    public @ResponseBody
    HomeInputBean getStatusSummary(@RequestParam String fromDate, String toDate) {

        HomeInputBean homeInputBean = new HomeInputBean();
        logger.info("[" + sessionBean.getSessionid() + "]  SUMMARY GET");

        try {

            homeInputBean.setFromDate(fromDate);
            homeInputBean.setToDate(toDate);

            //Summary Status Count Chart
            List<SummaryBean> statusSummaryList = homeService.getStatusSummary(homeInputBean);

            //Summary failing Area Count Chart
            List<SummaryBean> failingAreaSummaryList = homeService.getFailingAreaSummary(homeInputBean);

            //Summary failing Area Cost Chart
            List<SummaryBean> failingAreaCostSummaryList = homeService.getFailingAreaCostSummary(homeInputBean);

            long fullRequestCount = homeService.getRequestTotalCount(homeInputBean);
            String fullRequestCost = homeService.getRequestTotalCost(homeInputBean);

            long pendingRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_PENDING ,homeInputBean);
            long inPurchaseRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_PRE_APPROVED,homeInputBean);
            long notedRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_NOTED,homeInputBean);

            //set values to claimInputBean bean

            homeInputBean.setStatusCountList(statusSummaryList);
            homeInputBean.setFailingAreaCountList(failingAreaSummaryList);
            homeInputBean.setFailingAreaCostCountList(failingAreaCostSummaryList);

            homeInputBean.setTotalCount(Long.toString(fullRequestCount));
            homeInputBean.setTotalCost(fullRequestCost);
            homeInputBean.setCountPending(Long.toString(pendingRequestCount));
            homeInputBean.setCountInPurchase(Long.toString(inPurchaseRequestCount));
            homeInputBean.setCountNoted(Long.toString(notedRequestCount));

        } catch (Exception e) {
            logger.error("Exception  :  ", e);
        }
        return homeInputBean;
    }


    @ModelAttribute
    public void getHomeBean(org.springframework.ui.Model map) throws Exception {
        HomeInputBean homeInputBean = new HomeInputBean();

        //pending request Count
//        long pendingRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_PENDING);
//        long inPurchaseRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_PRE_APPROVED);
//        long notedRequestCount = homeService.getRequestCount(StatusVarList.STATUS_CLAIM_NOTED);

//        //Summary Status Count Chart
//        List<SummaryBean> statusSummaryList = homeService.getStatusSummary(homeInputBean);
//
//        //Summary failing Area Count Chart
//        List<SummaryBean> failingAreaSummaryList = homeService.getFailingAreaSummary();
//
//        //Summary failing Area Cost Chart
//        List<SummaryBean> failingAreaCostSummaryList = homeService.getFailingAreaCostSummary();

        //set values to claimInputBean bean
//        homeInputBean.setCountPending(Long.toString(pendingRequestCount));
//        homeInputBean.setCountInPurchase(Long.toString(inPurchaseRequestCount));
//        homeInputBean.setCountNoted(Long.toString(notedRequestCount));
//
//        homeInputBean.setStatusCountList(statusSummaryList);
//        homeInputBean.setFailingAreaCountList(failingAreaSummaryList);
//        homeInputBean.setFailingAreaCostCountList(failingAreaCostSummaryList);

        //add values to model map
        map.addAttribute("homeform", homeInputBean);
    }


}
