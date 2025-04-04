// 1. 요청 시작부터 필터까지의 로깅을 위한 필터
package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import jakarta.servlet.Filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 1. 요청 시작부터 필터까지의 로깅을 위한 필터
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements Filter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        log.info("1. [클라이언트 요청] 시작: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        log.info("2. [서블릿 컨테이너] 요청 수신");
        log.info("3. [필터 체인] 시작");

        // 요청 헤더 로깅
        Collections.list(httpRequest.getHeaderNames()).forEach(headerName ->
                log.debug("요청 헤더: {} = {}", headerName, httpRequest.getHeader(headerName))
        );

        // 요청 파라미터 로깅
        httpRequest.getParameterMap().forEach((key, value) ->
                log.debug("요청 파라미터: {} = {}", key, String.join(", ", value))
        );

        // 래퍼를 사용하여 요청 본문 캡처 가능하도록 설정
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        try {
            // 필터 체인 계속 진행
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            log.info("18. [필터 체인] 응답 처리 완료");

            // 응답 상태 및 헤더 로깅
            log.debug("응답 상태: {}", responseWrapper.getStatus());
            responseWrapper.getHeaderNames().forEach(headerName ->
                    log.debug("응답 헤더: {} = {}", headerName, responseWrapper.getHeader(headerName))
            );

            // 응답 본문 로깅 (필요시)
            byte[] content = responseWrapper.getContentAsByteArray();
            if (content.length > 0) {
                String contentType = responseWrapper.getContentType();
                if (contentType != null && contentType.contains("application/json")) {
                    log.debug("응답 본문: {}", new String(content, StandardCharsets.UTF_8));
                } else {
                    log.debug("응답 본문 크기: {} bytes", content.length);
                }
            }

            log.info("19. [클라이언트 응답] 완료");

            // 반드시 원래 응답으로 복사해야 함
            responseWrapper.copyBodyToResponse();
        }
    }
}
