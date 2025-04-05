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

    @Around("execution(* org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(..))")
    public Object logRequestMappingHandlerAdapter(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("5. [DispatcherServlet => HandlerAdapter] 받음");
        log.info("6. [HandlerAdapter => Controller] 보냄");
        Object result = joinPoint.proceed();
        log.info("9. [Controller => HandlerAdapter] 받음"); //컨트롤러, 서비스, 레포지토리 종료
        log.info("10. [HandlerAdapter => DispatcherServlet] 보냄"); //컨트롤러, 서비스, 레포지토리 종료
        return result;
    }

    // 다른 어댑터 구현체들도 포함
//    @Around("execution(* org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(..))")
//    public Object logHttpRequestHandlerAdapter(ProceedingJoinPoint joinPoint) throws Throwable {
//        log.info("7-2. [HandlerAdapter] HttpRequestHandlerAdapter 호출");
//        Object result = joinPoint.proceed();
//        log.info("11-2. [HandlerAdapter] HttpRequestHandlerAdapter 완료");
//        return result;
//    }

//    @Around("execution(* org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter.handle(..))")
//    public Object logSimpleControllerHandlerAdapter(ProceedingJoinPoint joinPoint) throws Throwable {
//        log.info("7-3. [HandlerAdapter] SimpleControllerHandlerAdapter 호출");
//        Object result = joinPoint.proceed();
//        log.info("11-3. [HandlerAdapter] SimpleControllerHandlerAdapter 완료");
//        return result;
//    }
}
