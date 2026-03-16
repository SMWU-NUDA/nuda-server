package smu.nuda.global.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;
import smu.nuda.domain.auth.error.AuthErrorCode;
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
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 비로그인 요청
        String bearer = request.getHeader(JwtConstants.AUTH_HEADER);
        if (bearer == null || !bearer.startsWith(JwtConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 로그인 요청
        try {
            String token = bearer.substring(JwtConstants.TOKEN_PREFIX.length());

            TokenType tokenType = jwtProvider.extractTokenType(token);
            Long memberId = jwtProvider.extractMemberId(token);

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new JwtAuthenticationException(MemberErrorCode.MEMBER_NOT_FOUND));

            // 비활성 계정 차단
            if (member.getStatus() == Status.INACTIVE) {
                throw new JwtAuthenticationException(MemberErrorCode.ACCOUNT_DISABLED);
            }

            // 최종 탈퇴 계정은 모든 요청 차단
            if (member.getStatus() == Status.WITHDRAWN) {
                throw new JwtAuthenticationException(MemberErrorCode.MEMBER_WITHDRAWN);
            }

            // 탈퇴 요청 중인 계정은 탈퇴 취소 API 만 허용
            if (member.getStatus() == Status.WITHDRAW_REQUESTED) {
                boolean isCancelWithdrawRequest = "DELETE".equalsIgnoreCase(request.getMethod())
                        && "/members/withdraw".equals(request.getRequestURI());
                if (!isCancelWithdrawRequest) {
                    throw new JwtAuthenticationException(MemberErrorCode.WITHDRAW_IN_PROGRESS);
                }
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

        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new JwtAuthenticationException(AuthErrorCode.EXPIRED_TOKEN, e)
            );
        }
        catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new JwtAuthenticationException(AuthErrorCode.INVALID_ACCESS_TOKEN, e)
            );
        }
        catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
        }
    }
}
