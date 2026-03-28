package smu.nuda.global.ml.client;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import smu.nuda.global.error.MlErrorCode;
import smu.nuda.global.ml.dto.MlErrorResponse;
import smu.nuda.global.ml.exception.MlApiException;

@Component
@RequiredArgsConstructor
@Slf4j
public class MlHttpClient {

    private final WebClient mlWebClient;

    @CircuitBreaker(name = "mlApi", fallbackMethod = "handleFallback")
    public <T> T post(String uriTemplate, Object body, Class<T> responseType, Object... uriVariables) {
        T result = mlWebClient.post()
                .uri(uriTemplate, uriVariables)
                .bodyValue(body)
                .retrieve()

                // 4xx 에러 처리
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.warn("ML client error %s: %s", resp.statusCode(), error);
                                    return Mono.error(new MlApiException(MlErrorCode.CLIENT_ERROR));
                                }))
                // 5xx 에러 처리
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.warn("ML client error %s: %s", resp.statusCode(), error);
                                    return Mono.error(new MlApiException(MlErrorCode.INTERNAL_SERVER_ERROR));
                                }))
                .bodyToMono(responseType)
                .block(); // 동기 방식으로 결과 대기

        // 응답 바디가 비어있는 경우(null) 확인
        if (result == null) throw new MlApiException(MlErrorCode.EMPTY_RESPONSE);

        return result;
    }

    @CircuitBreaker(name = "mlApi", fallbackMethod = "handleFallback")
    public <T> T get(String uriTemplate, Class<T> responseType, Object... uriVariables) {
        T result = mlWebClient.get()
                .uri(uriTemplate, uriVariables)
                .retrieve()

                // 4xx 에러 처리
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(MlErrorResponse.class)
                                .flatMap(error -> {

                                    // 리뷰가 부족할 때 비즈니스 예외 처리
                                    if (resp.statusCode().value() == 422) {
                                        log.warn("ML Review Insufficient: {}", error.getDetail());
                                        return Mono.error(new MlApiException(MlErrorCode.REVIEW_INSUFFICIENT));
                                    }

                                    log.error("ML client error {}: {}", resp.statusCode(), error.getDetail());
                                    return Mono.error(new MlApiException(MlErrorCode.CLIENT_ERROR));
                                }))
                // 5xx 에러 처리
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> resp.bodyToMono(MlErrorResponse.class)
                                .flatMap(error -> {
                                    log.warn("ML server error %s: %s", resp.statusCode(), error.getDetail());
                                    return Mono.error(new MlApiException(MlErrorCode.INTERNAL_SERVER_ERROR));
                                }))
                .bodyToMono(responseType)
                .block();

        if (result == null) {
            throw new MlApiException(MlErrorCode.EMPTY_RESPONSE);
        }

        return result;
    }

    private <T> T handleFallback(CallNotPermittedException e) {
        log.error("ML Circuit Breaker is OPEN", e);
        throw new MlApiException(MlErrorCode.UNAVAILABLE);
    }

    private <T> T handleFallback(java.util.concurrent.TimeoutException e) {
        log.error("ML Request Timeout", e);
        throw new MlApiException(MlErrorCode.TIMEOUT);
    }

    private <T> T handleFallback(Exception e) {
        if (e instanceof MlApiException mlApiException) throw mlApiException;
        log.error("ML Unexpected Error", e);

        throw new MlApiException(MlErrorCode.INTERNAL_SERVER_ERROR);
    }
}
