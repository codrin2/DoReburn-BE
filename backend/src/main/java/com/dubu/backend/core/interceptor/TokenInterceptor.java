package com.dubu.backend.core.interceptor;

import com.dubu.backend.core.interceptor.context.TokenContext;
import com.dubu.backend.member.application.TokenFacade;
import com.dubu.backend.member.domain.repository.MemberRepository;
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

    private final TokenFacade tokenFacade;
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            String token = tokenFacade.resolveToken(request);
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
}