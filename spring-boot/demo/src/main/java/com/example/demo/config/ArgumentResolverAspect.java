package com.example.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ArgumentResolverAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // RequestBody 처리를 위한 구체적인 포인트컷
    @Around("execution(* org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor.resolveArgument(..))")
    public Object logRequestBodyResolver(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("8. [ArgumentResolver] @RequestBody 처리 시작");
        Object result = joinPoint.proceed();
        log.info("8. [ArgumentResolver] @RequestBody 처리 완료: {}",
                result != null ? result.getClass().getSimpleName() : "null");
        return result;
    }

    // HandlerMethodArgumentResolverComposite 처리
    @Around("execution(* org.springframework.web.method.support.HandlerMethodArgumentResolverComposite.resolveArgument(..))")
    public Object logCompositeResolver(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodParameter parameter = (MethodParameter)joinPoint.getArgs()[0];
        log.info("8. [ArgumentResolver] 컴포지트 처리 시작: {}", parameter.getParameterName());
        Object result = joinPoint.proceed();
        log.info("8. [ArgumentResolver] 컴포지트 처리 완료");
        return result;
    }

    // ServletInvocableHandlerMethod에서의 파라미터 해석
    @Around("execution(* org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(..))")
    public Object logMethodInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("8. [ArgumentResolver] 컨트롤러 메서드 호출 준비 시작");
        Object result = joinPoint.proceed();
        log.info("8. [ArgumentResolver] 컨트롤러 메서드 호출 준비 완료");
        return result;
    }
}
