package com.spg.cv.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.spg.common.dateutil.MyDateUtil;
import com.spg.cv.common.CommonEnum.DataType;
import com.spg.cv.service.UserCountService;

/**
 * 项目名称：countView
 *
 * @description: 用户量相关统计
 * @author Wind-spg
 * @create_time：2016年1月10日 下午2:28:44
 * @version V1.0.0
 */
@Controller
@RequestMapping(value="userCtrl")
public class UserCountController extends BaseController
{
    private static final Log LOGGER = LogFactory.getLog(UserCountController.class);

    @Resource
    UserCountService userCountManager;
    
    /**
     * @description: 一次用户活跃
     * @author: Wind-spg
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "userActive", produces =
    { "appliction/json; charset=UTF-8" })
    public String userActive(HttpServletRequest request)
    {
        String sessionId = request.getSession().getId();
        try
        {
            String key = MyDateUtil.getFormatDate(new Date(System.currentTimeMillis()), "yyyyMMdd")
                    + DataType.USER_ACTIVE.getName();
            Long result = userCountManager.addActiveUser(key, sessionId);
            return buildSuccessResultInfo(result);
        } catch (Exception e)
        {
            LOGGER.error("add user active error!", e);
            return buildFailedResultInfo(-100, e.getMessage());
        }
    }
    
    /**
     * @description: 查询活跃用户量
     * @author: Wind-spg
     * @param request
     * @return
     */
    @RequestMapping("queryActiveUser")
    public ModelAndView queryActiveUser(HttpServletRequest request)
    {
        ModelAndView view = new ModelAndView("activeUser");
        // 获取x轴数据
        List<Integer> xData = getBeforeDaysList();
        view.addObject("xData", xData);

        // 某天用户活跃数量
        List<Long> resultData = new ArrayList<Long>();

        String tempKey = null;
        Long userCount = 0L;
        for (Integer days : xData)
        {
            tempKey = MyDateUtil.getFormatDate(new Date(System.currentTimeMillis()), "yyyyMM");
            if (days < 10)
            {
                userCount = userCountManager.queryOneDayActiveUserCount(
                        tempKey + "0" + days + DataType.USER_ACTIVE.getName());
            } else
            {
                userCount = userCountManager.queryOneDayActiveUserCount(
                        tempKey + days + DataType.USER_ACTIVE.getName());
            }
            resultData.add(userCount);
        }
        view.addObject("resultData", resultData);
        return view;
    }
}
