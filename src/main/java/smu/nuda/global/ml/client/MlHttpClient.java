package smu.nuda.global.ml.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class MlHttpClient {

    private final WebClient mlWebClient;

    @CircuitBreaker(name = "ml")
    public <T> T post(String uriTemplate, Object body, Class<T> responseType, Object... uriVariables) {
        T result = mlWebClient.post()
                .uri(uriTemplate, uriVariables)
                .bodyValue(body)
                .retrieve()

                // 4xx 에러 처리
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(String.class)
                                .map(errorBody -> new IllegalArgumentException(
                                        String.format("ML client error %s: %s", resp.statusCode(), errorBody))))
                // 5xx 에러 처리
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> resp.bodyToMono(String.class)
                                .map(errorBody -> new IllegalStateException(
                                        String.format("ML server error %s: %s", resp.statusCode(), errorBody))))
                .bodyToMono(responseType)
                .block(); // 동기 방식으로 결과 대기

        // 응답 바디가 비어있는 경우(null) 확인
        if (result == null) throw new IllegalStateException(String.format("ML 서버가 빈 응답을 반환했습니다. (URI: %s)", uriTemplate));

        return result;
    }

    @CircuitBreaker(name = "ml")
    public <T> T get(String uriTemplate, Class<T> responseType, Object... uriVariables) {
        T result = mlWebClient.get()
                .uri(uriTemplate, uriVariables)
                .retrieve()

                // 4xx 에러 처리
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(String.class)
                                .map(errorBody -> new IllegalArgumentException(
                                        String.format("ML client error %s: %s", resp.statusCode(), errorBody))))
                // 5xx 에러 처리
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> resp.bodyToMono(String.class)
                                .map(errorBody -> new IllegalStateException(
                                        String.format("ML server error %s: %s", resp.statusCode(), errorBody))))
                .bodyToMono(responseType)
                .block();

        if (result == null) {
            throw new IllegalStateException("ML 서버가 빈 응답을 반환했습니다.");
        }

        return result;
    }
}
