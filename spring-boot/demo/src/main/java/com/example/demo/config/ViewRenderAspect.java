package com.example.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class ViewRenderAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* org.springframework.web.servlet.View.render(..))")
    public Object logViewRender(ProceedingJoinPoint joinPoint) throws Throwable {
        Map<String, ?> model = (Map<String, ?>) joinPoint.getArgs()[0];
        log.info("16. [View] 렌더링 시작, 모델 크기: {}", model.size());

        Object result = joinPoint.proceed();

        log.info("16. [View] 렌더링 완료");
        return result;
    }
}