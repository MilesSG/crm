package com.xxxx.crm.interceptor;

import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 非法拦截器异常
public class NoLoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private UserMapper userMapper;

    /**
     * 拦截用户是否登录，在目标资源访问（订单、个人中心等）之前执行
     * 返回true: 目标资源可以执行
     * 返回false: 阻止目标方法执行
     * <p>
     * 1. 检查cookie中是否存在用户信息，对照数据库中是否存在指定的用户ID值，如果用户是登录状态，则放行；否则抛未登录异常
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie中的用户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 判断用户ID是否为空，或数据库中不存在用户ID对应的记录
        if (userId == null || userMapper.selectByPrimaryKey(userId) == null) {
            // 抛出未登录异常
            throw new NoLoginException();
        }
        return true;
    }
}
