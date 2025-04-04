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

//    @Around("execution(* org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor.resolveArgument(..))")
//    public Object logRequestBodyProcessing(ProceedingJoinPoint joinPoint) throws Throwable {
//        log.info("8. [ArgumentResolver] @RequestBody 처리 시작");
//
//        // 메서드 파라미터 정보 출력
//        Object[] args = joinPoint.getArgs();
//        if (args != null && args.length > 0 && args[0] instanceof MethodParameter) {
//            MethodParameter methodParameter = (MethodParameter) args[0];
//            log.info("   - 대상 파라미터: {}.{}",
//                    methodParameter.getDeclaringClass().getSimpleName(),
//                    methodParameter.getParameterName());
//        }
//
//        // 실제 메서드 실행
//        Object result = joinPoint.proceed();
//
//        // 변환 결과 출력
//        log.info("8. [ArgumentResolver] @RequestBody 처리 완료: {} 타입으로 변환됨",
//                result != null ? result.getClass().getSimpleName() : "null");
//
//        return result;
//    }
//
//    // HTTP 메시지 컨버터의 읽기 처리 로깅
//    @Around("execution(* org.springframework.http.converter.HttpMessageConverter.read(..))")
//    public Object logMessageConverterReading(ProceedingJoinPoint joinPoint) throws Throwable {
//        log.info("8. [ArgumentResolver] HttpMessageConverter 읽기 시작");
//        Object result = joinPoint.proceed();
//        log.info("8. [ArgumentResolver] HttpMessageConverter 읽기 완료");
//        return result;
//    }
}
