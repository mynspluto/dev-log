package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("5. [DispatcherServlet => HandlerAdapter] HandlerInterceptor preHandle()");

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            log.debug("컨트롤러: {}, 메서드: {}",
                    handlerMethod.getBeanType().getSimpleName(),
                    handlerMethod.getMethod().getName());
        }

        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("10. [HandlerAdapter => DispatcherServlet] HandlerInterceptor postHandle()");

        if (modelAndView != null) {
            log.debug("뷰: {}, 모델 크기: {}",
                    modelAndView.getViewName(),
                    modelAndView.getModel().size());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();

        log.info("10. [HandlerAdapter => DispatcherServlet] HandlerInterceptor afterCompletion(), 소요시간: {}ms", (endTime - startTime));

        if (ex != null) {
            log.error("예외 발생: {}", ex.getMessage(), ex);
        }
    }
}
