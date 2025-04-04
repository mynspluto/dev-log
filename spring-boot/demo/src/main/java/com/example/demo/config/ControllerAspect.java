package com.example.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // HandlerMethod로 직접 호출되는 경우 캡처
    @Around("execution(* org.springframework.web.method.HandlerMethod.invokeForRequest(..))")
    public Object logHandlerMethodInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("10. [Controller] 핸들러 메서드 호출 시작");
        Object result = joinPoint.proceed();
        log.info("10. [Controller] 핸들러 메서드 호출 완료");
        return result;
    }

    // 컨트롤러 메서드가 있는 패키지의 모든 public 메서드를 캡처
    @Around("execution(public * com.example.demo.controller.*.*(..))")
    public Object logControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("10. [Controller] 컨트롤러 실행 시작: {}.{}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        log.info("10. [Controller] 컨트롤러 실행 완료");
        return result;
    }
}
