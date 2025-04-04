package com.example.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HandlerAdapterAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* org.springframework.web.servlet.HandlerAdapter.handle(..))")
    public Object logHandlerAdapter(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("7. [HandlerAdapter] 핸들러 어댑터 호출");
        Object result = joinPoint.proceed();
        log.info("11. [HandlerAdapter] 핸들러 어댑터 완료");
        return result;
    }
}
