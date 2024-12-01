package animores.diary.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static animores.diary.common.RequestConstants.ACCOUNT_ID;
import static animores.diary.common.RequestConstants.ACCOUNT_ROLE;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader(ACCOUNT_ID) == null ? "anonymous" : request.getHeader(ACCOUNT_ID);
//        String userId = "13";
        String userRole = request.getHeader(ACCOUNT_ROLE) == null ? "anonymous" : request.getHeader(ACCOUNT_ROLE);

        RequestContextHolder.getRequestAttributes().setAttribute(ACCOUNT_ID, userId, RequestAttributes.SCOPE_REQUEST);
        RequestContextHolder.getRequestAttributes().setAttribute(ACCOUNT_ROLE, userRole, RequestAttributes.SCOPE_REQUEST);

        User user = new User(userId, "", List.of(new SimpleGrantedAuthority(userRole)));
        AbstractAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(user, "", user.getAuthorities());
        authenticated.setDetails(new WebAuthenticationDetails(request));// 토큰 객체 생성
        SecurityContextHolder.getContext().setAuthentication(authenticated);// 토큰 객체 설정 하여 사용자 인증
        filterChain.doFilter(request, response);
    }
}
