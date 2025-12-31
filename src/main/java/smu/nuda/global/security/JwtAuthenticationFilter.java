package smu.nuda.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.auth.jwt.JwtProvider;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.global.error.DomainException;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            Authentication authentication = jwtProvider.extractAuthentication(request);

            if (authentication == null) {
                filterChain.doFilter(request, response);
                return;
            }

            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            Member member = memberRepository.findById(principal.getMember().getId())
                    .orElseThrow(() -> new DomainException(MemberErrorCode.MEMBER_NOT_FOUND));

            // 계정 활성화 여부 검사
            if (member.getStatus() != Status.ACTIVE) {
                throw new DomainException(AuthErrorCode.ACCOUNT_DISABLED);
            }
        } catch (Exception e) {
            // Todo. AuthenticationEntryPoint(401), AccessDeniedHandler(403) 구현하기
        }

        filterChain.doFilter(request, response);
    }
}
