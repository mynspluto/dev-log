package com.example.demo.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerArgumentsLoggingAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // 컨트롤러 메서드 실행 전에 @RequestBody 파라미터 로깅
    @Around("execution(* com.example.demo.controller.*.*(@org.springframework.web.bind.annotation.RequestBody (*), ..)) && args(requestBody, ..)")
    public void logRequestBodyArgument(JoinPoint joinPoint, Object requestBody) {
        log.info("8. [ArgumentResolver] @RequestBody 처리 완료: {}",
                requestBody != null ? requestBody.getClass().getSimpleName() : "null");
        log.info("   - 요청 데이터: {}", requestBody);
    }
}
