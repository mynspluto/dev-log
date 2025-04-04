package com.example.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FlashMapAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* org.springframework.web.servlet.FlashMapManager.*(..))")
    public Object logFlashMap(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("15. [FlashMap] {} 메서드 실행", joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        return result;
    }
}
