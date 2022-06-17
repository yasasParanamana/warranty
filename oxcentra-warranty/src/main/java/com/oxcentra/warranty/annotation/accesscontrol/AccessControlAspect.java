package com.oxcentra.warranty.annotation.accesscontrol;

import com.oxcentra.warranty.bean.session.SessionBean;
import com.oxcentra.warranty.mapping.audittrace.Audittrace;
import com.oxcentra.warranty.mapping.usermgt.Page;
import com.oxcentra.warranty.mapping.usermgt.PageTask;
import com.oxcentra.warranty.mapping.usermgt.Task;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Aspect
@Component
public class AccessControlAspect {

    @Autowired
    SessionBean sessionBean;

    @Autowired
    Audittrace audittrace;

    @Around(value = "@annotation(accessControl)")
    public Object checkAccessControl(ProceedingJoinPoint proceedingJoinPoint, AccessControl accessControl) {
        try {
            if (sessionBean != null) {
                Map<String, List<Page>> pageMap = sessionBean.getPageMap();
                Map<String, PageTask> pageTaskMap = sessionBean.getPageTaskMap();
                if (pageTaskMap != null && !pageTaskMap.isEmpty() && pageTaskMap.size() > 0) {
                    String pageCode = accessControl.pageCode();
                    String taskCode = accessControl.taskCode();
                    if (pageCode != null && taskCode != null) {
                        //get the page task object
                        PageTask pageTask = pageTaskMap.get(pageCode);
                        if (pageTask != null) {
                            //get the corresponding task list for page
                            List<Task> taskList = pageTask.getTaskList();
                            if (taskList != null && !taskList.isEmpty() && taskList.size() > 0) {
                                Task task = taskList.stream().filter(t -> t.getTaskCode().equals(taskCode)).findFirst().orElse(null);
                                if (task != null && task.getTaskCode().equals(taskCode.trim())) {

                                    return proceedingJoinPoint.proceed();
                                } else {
                                    return new ModelAndView("redirect:/logout.htm?error=6");
                                }
                            } else {
                                return new ModelAndView("redirect:/logout.htm?error=6");
                            }
                        } else {
                            return new ModelAndView("redirect:/logout.htm?error=6");
                        }
                    } else {
                        return new ModelAndView("redirect:/logout.htm?error=6");
                    }
                } else {
                    return new ModelAndView("redirect:/logout.htm?error=6");
                }
            } else {
                return new ModelAndView("redirect:/logout.htm?error=6");
            }
        } catch (Exception e) {
            return new ModelAndView("redirect:/logout.htm?error=6");
        } catch (Throwable throwable) {
            return new ModelAndView("redirect:/logout.htm?error=6");
        }
    }
}
