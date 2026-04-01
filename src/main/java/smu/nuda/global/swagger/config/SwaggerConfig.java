package smu.nuda.global.swagger.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "NUDA API",
                description = "NUDA 서비스 백엔드 API 문서",
                version = "v1"
        )
)
@io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = "JWT",
        description = "AccessToken만 입력해주세요.",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

        private static final List<Tag> OPEN_API_TAGS = List.of(
                new Tag().name("[AUTH] 인증 API").description("이메일 로그인 및 토큰 재발급 관련 API"),
                new Tag().name("[SIGNUP] 회원가입 API").description("회원가입 유스케이스 관련 API"),
                new Tag().name("[MEMBER] 회원 API").description("회원 관련 API"),
                new Tag().name("[KEYWORD] 키워드 API").description("사용자의 키워드 API"),
                new Tag().name("[SEARCH] 검색 API").description("검색 API"),
                new Tag().name("[PRODUCT] 생리대 상품 API").description("상품 관련 API"),
                new Tag().name("[INGREDIENT] 성분 API").description("성분 관련 API"),
                new Tag().name("[REVIEW] 리뷰 API").description("리뷰 API"),
                new Tag().name("[CART] 장바구니 API").description("장바구니 관련 API"),
                new Tag().name("[ORDER] 주문 API").description("상품 주문 관련 API"),
                new Tag().name("[PAYMENT] 결제 API").description("결제 관련 API"),
                new Tag().name("[FILE] 파일 업로드 API").description("파일 업로드 API"),
                new Tag().name("[PRODUCT LIKE] 생리대 상품 찜하기 API").description("상품 찜하기 API"),
                new Tag().name("[BRAND LIKE] 생리대 브랜드 찜하기 API").description("브랜드 찜하기 API"),
                new Tag().name("[INGREDIENTS LIKE] 성분 찜하기 API").description("성분 즐겨찾기 API"),
                new Tag().name("[REVIEW LIKE] 리뷰 좋아요 API").description("리뷰 좋아요 API"),
                new Tag().name("[ADMIN] 상품 관리 API").description("관리자 상품 CSV 업로드 API"),
                new Tag().name("[ADMIN] 브랜드 관리 API").description("관리자 브랜드 CSV 업로드 API"),
                new Tag().name("[ADMIN] 성분 관리 API").description("관리자 성분 CSV 업로드 API"),
                new Tag().name("[ADMIN] 리뷰 관리 API").description("관리자 리뷰 CSV 업로드 API")
        );

        @Bean
        public OpenAPI openAPI() {
                return new OpenAPI()
                        .tags(OPEN_API_TAGS);
        }
}
