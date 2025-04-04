package com.example.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReturnValueHandlerAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* org.springframework.web.method.support.HandlerMethodReturnValueHandler+.handleReturnValue(..))")
    public Object logReturnValueHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Object returnValue = joinPoint.getArgs()[0];
        log.info("11. [ReturnValueHandler] 반환값 처리 시작: {}",
                returnValue != null ? returnValue.getClass().getSimpleName() : "null");
        joinPoint.proceed();
        log.info("11. [ReturnValueHandler] 반환값 처리 완료");
        return null; // void 메서드
    }

    // 추가적인 포인트컷
    @Around("execution(* org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor.handleReturnValue(..))")
    public Object logResponseBodyHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Object returnValue = joinPoint.getArgs()[0];
        log.info("11. [ReturnValueHandler] @ResponseBody 처리 시작: {}",
                returnValue != null ? returnValue.getClass().getSimpleName() : "null");
        joinPoint.proceed();
        log.info("11. [ReturnValueHandler] @ResponseBody 처리 완료");
        return null;
    }
}
