package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.AuthException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

// 全局异常 统一处理
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    /**
     * 异常处理方法：
     * 1. 返回的是视图
     * 2. 返回的是JSON数据
     * 如何判断方法的返回值？
     * 可以看方法上是否声明@ResponceBody注解，如果未声明，则返回视图；如果声明了则返回JSON数据
     *
     * @param request  request请求对象
     * @param response response返回对象
     * @param handler  方法对象
     * @param ex       异常对象
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        // 默认的异常情况
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("code", 500);
        modelAndView.addObject("msg", "系统异常，请重试...");

        // 判断handlerMethod
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler; // 强转
            // 获取方法上的带有@ResponseBody注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            // 判断responseBody是否为空，如果为空，则表示返回的是视图(也就是其实没有@ResponseBody注解)，不为空，则返回的是数据
            if (responseBody == null) {
                // 返回视图
                // 判断异常类型
                if (ex instanceof ParamsException) {
                    ParamsException p = (ParamsException) ex;
                    // 设置异常信息
                    modelAndView.addObject("code", p.getCode());
                    modelAndView.addObject("msg", p.getMsg());

                } else if (ex instanceof AuthException) { // 认证异常
                    AuthException a = (AuthException) ex;
                    // 设置异常信息
                    modelAndView.addObject("code", a.getCode());
                    modelAndView.addObject("msg", a.getMsg());
                }

                return modelAndView;
            } else {
                // 返回JSON数据
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("系统异常，请重试...");
                // 判断异常类型是否是自定义异常
                if (ex instanceof ParamsException) {
                    ParamsException p = (ParamsException) ex;
                    resultInfo.setCode(p.getCode());
                    resultInfo.setMsg(p.getMsg());

                } else if (ex instanceof AuthException) { // 认证异常
                    AuthException a = (AuthException) ex;
                    resultInfo.setCode(a.getCode());
                    resultInfo.setMsg(a.getMsg());
                }

                // 设置响应类型及编码格式（响应JSON格式的数据）
                response.setContentType("application/json;charset=UTF-8");
                // 得到字符输出流
                PrintWriter out = null;
                try {
                    // 得到输出流
                    out = response.getWriter();
                    // 将需要返回的对象转换成JOSN格式的字符
                    String json = JSON.toJSONString(resultInfo);
                    // 输出数据
                    out.write(json);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // 如果对象不为空，则关闭
                    if (out != null) {
                        out.close();
                    }
                }
                return null;
            }
        }
        return modelAndView;
    }
}
