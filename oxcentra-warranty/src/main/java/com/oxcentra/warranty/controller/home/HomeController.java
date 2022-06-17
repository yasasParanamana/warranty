package com.oxcentra.warranty.controller.home;

import com.oxcentra.warranty.bean.session.SessionBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@Controller
@Scope("request")
public class HomeController {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    SessionBean sessionBean;

    @RequestMapping(value = "/home", method = {RequestMethod.GET})
    public ModelAndView getHome(ModelMap modelMap, Locale locale) {
        logger.info("[" + sessionBean.getSessionid() + "]  HOME PAGE VIEW");
        return new ModelAndView("home/home", "homemap", new ModelMap());
    }
}
