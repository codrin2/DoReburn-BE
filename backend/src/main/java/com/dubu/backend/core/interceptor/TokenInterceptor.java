package com.dubu.backend.core.interceptor;

import com.dubu.backend.member.application.TokenFacade;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenFacade tokenFacade;

    @Autowired
    public TokenInterceptor(TokenFacade tokenFacade) {
        this.tokenFacade = tokenFacade;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            String token = tokenFacade.resolveToken(request);
            Long memberId = tokenFacade.validateToken(token);

            request.setAttribute("memberId", memberId);
        }
        return true;
    }
}