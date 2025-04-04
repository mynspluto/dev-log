package com.example.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ViewResolverAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* org.springframework.web.servlet.ViewResolver.resolveViewName(..))")
    public Object logViewResolver(ProceedingJoinPoint joinPoint) throws Throwable {
        String viewName = (String) joinPoint.getArgs()[0];
        log.info("14. [ViewResolver] 뷰 이름 해석 시작: {}", viewName);
        Object result = joinPoint.proceed();
        log.info("14. [ViewResolver] 뷰 이름 해석 완료: {}",
                result != null ? result.getClass().getSimpleName() : "null");
        return result;
    }
}
