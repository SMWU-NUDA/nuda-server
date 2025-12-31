package smu.nuda.global.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.auth.jwt.JwtConstants;
import smu.nuda.domain.auth.jwt.JwtProvider;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.global.error.DomainException;

import java.io.IOException;

@RequiredArgsConstructor
public class SignupJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String bearer = request.getHeader(JwtConstants.AUTH_HEADER);

        if (bearer == null || !bearer.startsWith(JwtConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = bearer.substring(JwtConstants.TOKEN_PREFIX.length());

        try {
            jwtProvider.validateSignupTokenOrThrow(token);

            Claims claims = jwtProvider.parseClaimsOrThrow(token);
            Long memberId = Long.valueOf(claims.getSubject());

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new DomainException(MemberErrorCode.MEMBER_NOT_FOUND));

            if (member.getStatus() != Status.SIGNUP_IN_PROGRESS) {
                throw new DomainException(AuthErrorCode.INVALID_SIGNUP_FLOW);
            }

            CustomUserDetails principal = new CustomUserDetails(member);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            token,
                            null
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (DomainException e) {
            // SIGNUP 토큰이 아니면 그냥 넘김(ACCESS 필터에서 처리)
        }

        filterChain.doFilter(request, response);
    }
}

