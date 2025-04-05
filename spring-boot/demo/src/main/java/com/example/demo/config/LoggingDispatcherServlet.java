// 2. DispatcherServlet 동작 감시를 위한 커스텀 DispatcherServlet
package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;

import org.slf4j.Logger;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoggingDispatcherServlet extends DispatcherServlet {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("2. [Tomcat => DispatcherServlet] 받음");
        try {
            // Handler Mapping 호출, Handler Adapter 실행
            super.doDispatch(request, response);
        } finally {
            log.info("10. [HandlerAdapter => DispatcherServlet] 받음");
        }
    }

    @Override
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        log.info("3. [DispatcherServlet => HandlerMapping] 보냄");
        HandlerExecutionChain chain = super.getHandler(request);
        if (chain != null) {
            log.info("4. [HandlerMapping => DispatcherServlet] 받음: {}", chain.getHandler());
        } else {
            log.warn("4. [HandlerMapping => DispatcherServlet] 받음: 적합한 핸들러를 찾지 못함");
        }
        return chain;
    }

    @Override
    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("14. [View Resolver] 호출");
        super.render(mv, request, response);
        log.info("16. [View 렌더링] 완료");
    }
}
