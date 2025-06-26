package com.dubu.backend.core.interceptor;

import com.dubu.backend.core.interceptor.context.TokenContext;
import com.dubu.backend.member.application.TokenFacade;
import com.dubu.backend.member.core.exception.InvalidTokenHeaderException;
import com.dubu.backend.member.core.exception.TokenMissingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenFacade tokenFacade;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            String token = resolveToken(request);
            Long memberId = tokenFacade.validateToken(token);
            TokenContext.setToken(token);
            request.setAttribute("memberId", memberId);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        TokenContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(AUTHORIZATION_HEADER);
        if (jwtToken == null) {
            throw new TokenMissingException();
        }

        if (jwtToken.startsWith(BEARER_PREFIX)) {
            return jwtToken.substring(7);
        } else {
            throw new InvalidTokenHeaderException();
        }
    }
}
