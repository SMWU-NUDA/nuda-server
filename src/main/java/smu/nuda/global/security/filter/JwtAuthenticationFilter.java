package smu.nuda.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import smu.nuda.domain.auth.jwt.JwtConstants;
import smu.nuda.domain.auth.jwt.JwtProvider;
import smu.nuda.domain.auth.jwt.TokenType;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.global.security.exception.JwtAuthenticationException;
import smu.nuda.global.security.principal.CustomUserDetails;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 비로그인 요청
        String bearer = request.getHeader(JwtConstants.AUTH_HEADER);
        if (bearer == null || !bearer.startsWith(JwtConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 로그인 요청
            String token = bearer.substring(JwtConstants.TOKEN_PREFIX.length());

            TokenType tokenType = jwtProvider.extractTokenType(token);
            Long memberId = jwtProvider.extractMemberId(token);

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new JwtAuthenticationException(MemberErrorCode.MEMBER_NOT_FOUND));

            // 비활성 계정은 인증 실패 (AuthenticationEntryPoint에서 401 반환)
            if (member.getStatus() != Status.ACTIVE) {
                throw new JwtAuthenticationException(MemberErrorCode.ACCOUNT_DISABLED);
            }

            CustomUserDetails principal = new CustomUserDetails(member, tokenType);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            token,
                            principal.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw e;
        }
    }
}
