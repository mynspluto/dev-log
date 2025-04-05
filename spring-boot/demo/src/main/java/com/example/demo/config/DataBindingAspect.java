package com.example.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataBindingAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // @Valid 어노테이션 처리를 위한 추가 포인트컷
    @Around("execution(* org.springframework.validation.beanvalidation.SpringValidatorAdapter.validate(..))")
    public Object logBeanValidation(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getArgs()[0];
        log.info("6. [HandlerAdapter => Controller] @valid 검사 시작: {}",
                target != null ? target.getClass().getSimpleName() : "null");
        Object result = joinPoint.proceed();
        log.info("6. [HandlerAdapter => Controller] @valid 검사 완료");
        return result;
    }

    // WebDataBinder 추가
//    @Around("execution(* org.springframework.web.bind.WebDataBinder.doBind(..))")
//    public Object logWebDataBinding(ProceedingJoinPoint joinPoint) throws Throwable {
//        log.info("9-1. [데이터 바인딩] WebDataBinder 바인딩 시작");
//        Object result = joinPoint.proceed();
//        log.info("9-1. [데이터 바인딩] WebDataBinder 바인딩 완료");
//        return result;
//    }

    // 기존 코드
//    @Around("execution(* org.springframework.validation.DataBinder.doBind(..))")
//    public Object logDataBinding(ProceedingJoinPoint joinPoint) throws Throwable {
//        log.info("9-2. [데이터 바인딩] DataBinder 바인딩 시작");
//        Object result = joinPoint.proceed();
//        log.info("9-2. [데이터 바인딩] DataBinder 바인딩 완료");
//        return result;
//    }

    // ServletRequestDataBinder도 추가
//    @Around("execution(* org.springframework.web.bind.ServletRequestDataBinder.bind(..))")
//    public Object logServletRequestDataBinding(ProceedingJoinPoint joinPoint) throws Throwable {
//        Object target = joinPoint.getArgs()[0];
//        log.info("9-3. [데이터 바인딩] 서블릿 요청 바인딩 시작: {}",
//                target != null ? target.getClass().getSimpleName() : "null");
//        Object result = joinPoint.proceed();
//        log.info("9-4. [데이터 바인딩] 서블릿 요청 바인딩 완료");
//        return result;
//    }

    // Validator 표현식 수정
//    @Around("execution(* org.springframework.validation.Validator+.validate(..))")
//    public Object logValidation(ProceedingJoinPoint joinPoint) throws Throwable {
//        Object target = joinPoint.getArgs()[0];
//        log.info("9-5. [유효성 검증] 시작: {}",
//                target != null ? target.getClass().getSimpleName() : "null");
//        Object result = joinPoint.proceed();
//        log.info("9-5. [유효성 검증] 완료");
//        return result;
//    }
}
