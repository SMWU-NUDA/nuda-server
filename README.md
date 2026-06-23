# NUDA Server

> 우리가 쓰는 위생용품, 성분부터 안전까지 투명하게

## 📖 프로젝트 소개

여성 위생용품은 전성분 공개가 의무화되어 있지만, 화학 용어 중심의 표기만으로는 일반 소비자가 성분의 위험도를 판단하기 어렵습니다.

NUDA는 MSDS H-code 기반 성분 위험도 시각화와 AI 개인화 추천을 통해, 사용자가 성분을 비교하고 자신에게 맞는 제품을 찾을 수 있도록 돕는 여성 위생용품 커머스 플랫폼입니다.

## ✨ 주요 기능

<table>
  <tr>
    <td align="center" width="33%"><b>개인 맞춤 추천</b></td>
    <td align="center" width="33%"><b>성분 위험도 시각화</b></td>
    <td align="center" width="33%"><b>AI 리뷰 요약</b></td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 36 55" src="https://github.com/user-attachments/assets/7697650d-88a1-45ba-b4e0-9c06ee5fcb2e" />
      <br />
      <sub>키워드별 선호도를 분석해 AI가 제품을 추천합니다.</sub>
    </td>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 34 22" src="https://github.com/user-attachments/assets/70b71fd8-0677-4ea0-b202-6197365529d2" />
      <br />
      <sub>MSDS H-code 기준으로 위험 성분을 등급별로 시각화합니다.</sub>
    </td>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 38 38" src="https://github.com/user-attachments/assets/3a251d14-fd82-4d59-b301-3646d2168946" />
      <br />
      <sub>트렌드·감정·키워드를 AI가 분석해 리뷰 핵심을 요약합니다.</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%"><b>성분 기반 검색</b></td>
    <td align="center" width="33%"><b>상품 성분 상세 조회</b></td>
    <td align="center" width="33%"><b>사용자 마이페이지</b></td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 41 34" src="https://github.com/user-attachments/assets/8d4349c8-f017-422c-9031-ff84ae51b6d6" />
      <br />
      <sub>상품명·브랜드·성분명 통합 검색 및 자동완성을 제공합니다.</sub>
    </td>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 35 30" src="https://github.com/user-attachments/assets/22eac259-051c-428a-85eb-d3bf2ef90080" />
      <br />
      <sub>성분별 위험도·설명·주의사항을 상세하게 확인합니다.</sub>
    </td>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 40 50" src="https://github.com/user-attachments/assets/b6ac53a7-4c2a-4571-8d0c-7267fd5e83f2" />
      <br />
      <sub>관심 성분·선호 키워드·주문 내역을 통합 관리합니다.</sub>
    </td>
  </tr>
</table>

---

## 🛠 기술 스택

| 분류 | 기술 |
|---------|----|
| **언어** | `Java 17` `Kotlin` |
| **애플리케이션** | `Spring Boot 3.2.5` `Spring Security` `Spring Data JPA` `Spring WebFlux` `QueryDSL` |
| **데이터베이스** | `PostgreSQL` `Flyway` |
| **검색 / 캐시** | `Elasticsearch` `Redis` `Caffeine` `pgvector` |
| **인프라 / 배포** | `AWS EC2` `AWS RDS` `AWS S3` `AWS CloudFront` `AWS SSM Parameter Store` `Docker` `GitHub Actions` `Nginx` |
| **모니터링** | `Prometheus` `Grafana` `Alertmanager` |

<details>
<summary>기술 선정 이유</summary>

- **Java 17와 Kotlin**: 비즈니스 로직은 Java, 에러코드 enum·ApiResponse 등 간결한 표현이 필요한 곳만 Kotlin `enum class`, `data class` 사용
- **Spring WebFlux**: ML 서버 HTTP 클라이언트(`WebClient`)에만 적용. 추론 대기 시간이 길어 non-blocking이 유리한 영역에만 한정
- **pgvector**: ML 추천 2-stage 검색의 1단계 ANN(Approximate Nearest Neighbor) 검색에 사용. PostgreSQL 확장으로 별도 벡터 DB 추가 없이 유사도 검색 구현
- **Caffeine**: 자주 변경되지 않는 공통 데이터(상품, 브랜드, 이미지)에 적용. Redis 없이 순수하게 DB 부하 감소 효과를 측정하기 위해 의도적으로 분리
- **AWS SSM Parameter Store**: 시크릿을 이미지에 포함하지 않고 EC2 IAM 역할로만 접근. GitHub Actions에 장기 시크릿을 보관하지 않도록 함

</details>


## 📡 시스템 아키텍처
<img width="1121" height="468" alt="스크린샷 2026-04-27 오전 5 00 52" src="https://github.com/user-attachments/assets/989aacba-58c6-4a19-987d-58fa1d7711e6" width="100%" />


## 🗂️ ERD
<img width="3472" height="1993" alt="nuda erd (1)" src="https://github.com/user-attachments/assets/22b2b278-ce83-44da-820a-b316041322d5" width="100%" />

---

## 📁 프로젝트 구조

```text
nuda-server/
├── src/
│   └── main/
│       ├── java/smu/nuda/
│       │   ├── domain/
│       │   │   ├── auth/           # 로그인, 토큰 발급/재발급, JWT 처리
│       │   │   ├── member/         # 회원 정보, 회원 탈퇴
│       │   │   ├── signupdraft/    # 온보딩 Draft 플로우
│       │   │   ├── product/        # 상품, 카테고리, 관리자 상품 업로드
│       │   │   ├── brand/          # 브랜드
│       │   │   ├── ingredient/     # 성분, 위험도 분류, 관리자 성분 업로드
│       │   │   ├── review/         # 리뷰, 리뷰 분석 연동
│       │   │   ├── keyword/        # 온보딩 설문, 선호 키워드, 추천 입력 데이터
│       │   │   ├── search/         # Elasticsearch 기반 상품 검색 및 동기화
│       │   │   ├── like/           # 상품·브랜드·성분·리뷰 좋아요
│       │   │   ├── cart/           # 장바구니
│       │   │   ├── order/          # 주문, 주문 아이템
│       │   │   ├── payment/        # 결제
│       │   │   ├── file/           # 파일 업로드
│       │   │   ├── log/            # 커머스 이벤트 로그
│       │   │   └── common/         # 공통 엔티티 및 DTO
│       │   ├── global/
│       │   │   ├── batch/          # CSV 배치 처리 공통 지원
│       │   │   ├── cache/          # 캐시 구성 및 공통 캐시 처리
│       │   │   ├── config/         # 전역 설정
│       │   │   ├── error/          # 공통 에러 코드
│       │   │   ├── exception/      # 전역 예외 처리
│       │   │   ├── guard/          # 인증 기반 접근 제어
│       │   │   ├── mail/           # 이메일 발송
│       │   │   ├── ml/             # ML 서버 연동 및 오케스트레이션
│       │   │   ├── response/       # 공통 API 응답 포맷
│       │   │   ├── scheduler/      # 전역 스케줄링 작업
│       │   │   ├── security/       # Spring Security 설정, 필터, 핸들러
│       │   │   ├── swagger/        # Swagger/OpenAPI 설정
│       │   │   └── util/           # 공통 유틸리티
│       │   └── NudaApplication
│       └── resources/
│           ├── application.yml
│           ├── csv/                # 성분, 상품, 리뷰 시드/적재용 CSV 리소스
│           ├── db/migration/       # Flyway 마이그레이션
│           └── elasticsearch/      # 검색 인덱스 설정
├── docker/                 # 애플리케이션 실행 관련 Docker 설정
├── monitoring/             # Prometheus, Grafana, Alertmanager 설정
├── nginx/                  # Blue-Green 라우팅 및 리버스 프록시 설정
├── scripts/                # 배포, 롤백, 운영 스크립트
└── docs/                   # 설계 및 성능 개선 문서
```

## 🧩 주요 설계와 구현

### 도메인 설계

- [**회원가입 Draft-Commit 구조**](./docs/implementation/signup-draft-commit.md)
  단계별 회원가입 도중 이탈 후 재진입과 단계 수정이 가능해야 했습니다. `SignupDraft` Aggregate Root 내부에서 `currentStep`과 `completedSteps`를 직접 관리하는 Draft-Commit 구조로 전환해 해결했습니다.

- [**장바구니 동시성 제어**](./docs/implementation/cart-concurrency.md)
  동시 요청으로 장바구니 수량 변경 충돌이 발생했습니다. `Cart`를 Aggregate Root로 보고 `PESSIMISTIC_WRITE`를 적용했으며, 회원가입 시 Cart를 미리 생성해 생성 경쟁까지 제거했습니다.

### 애플리케이션 구조

- [**보안 계층 공통 응답 포맷 통일**](./docs/implementation/security-response-format.md)
  Spring Security 예외와 도메인 예외의 응답 포맷이 달랐습니다. `AuthenticationEntryPoint`와 `AccessDeniedHandler`를 커스텀 구현해 401/403도 `ApiResponse`와 구체적인 `errorCode`로 통일했습니다.

- [**이벤트 후처리와 ML 추론 실행 풀 분리**](./docs/implementation/async-event-ml-executor.md)
  HTTP 요청, 이벤트 후처리, ML 추론이 같은 실행 경로를 공유하면 병목이 전파될 수 있었습니다. Tomcat, `eventExecutor`, `mlExecutor`로 실행 풀을 분리해 응답 경로와 무거운 비동기 작업을 격리했습니다.

- [**Elasticsearch 동기화 실패 격리**](./docs/implementation/elasticsearch-sync-isolation.md)
  서버 기동 시 ES 동기화 실패가 전체 기동 실패로 번질 수 있었습니다. `ApplicationReadyEvent`와 `@Async`로 기동과 동기화를 분리하고, `@Retry`와 fallback으로 일시 장애를 격리했습니다.

### 데이터 처리

- [**관리자 CSV 대량 업로드 배치 설계**](./docs/implementation/csv-batch-upload.md)
  40,028행 CSV를 단건 저장하던 구조를 배치 처리로 전환했습니다. `CsvRow`와 도메인 객체를 분리하고, 참조 데이터 preload와 `flush/clear`를 적용해 **207.8s → 5.4s (38.7배)로** 단축했습니다.

### 성능 최적화

- [**ML 리뷰 분석 API 성능 최적화**](./docs/implementation/ml-review-api-optimization.md)
  리뷰 트렌드·감정 분석·키워드 추출 3개 ML 엔드포인트를 병렬 호출하고 Redis 캐시를 적용했습니다. 실측 평균 응답 시간을 **80~90ms → 14.5ms (6배)로** 줄였습니다.

- [**상품 상세 조회 캐시 전략**](./docs/implementation/product-detail-cache.md)
  상품·브랜드·이미지 같은 공통 데이터와 좋아요 여부 같은 사용자별 데이터를 분리해 캐시했습니다. Caffeine 로컬 캐시를 적용해 **50회 반복 기준 552ms → 160ms (3.5배)** 단축했습니다.

---

## ☁️ 운영 환경 및 배포

### 구성

- AWS EC2 단일 서버
- PostgreSQL, Redis, Elasticsearch
- Nginx, Docker Compose
- Prometheus, Grafana, Alertmanager

### 배포 전략

- `app-blue`, `app-green` 두 슬롯을 운영하는 Blue-Green 배포 구조
- GitHub Actions에서 이미지를 빌드해 GHCR에 Push한 뒤, EC2에서 `deploy.sh`를 실행
- 신규 슬롯이 `readiness`를 통과하면 Nginx upstream을 전환하고, 이후 안정성을 확인한 뒤 기존 슬롯을 정리
- Prometheus 기준 5xx 에러율이 10%를 초과하면 자동으로 이전 슬롯으로 롤백
- 운영 시크릿은 AWS SSM Parameter Store에서 런타임에 주입

### 초기 세팅

> 아래 명령은 모두 EC2 접속(`ssh -i <key.pem> ubuntu@<EC2_HOST>`) 후 `~/nuda-server`에서 실행합니다.

```bash
# 인프라 컨테이너 기동
docker compose up -d redis elasticsearch

# 모니터링 스택 기동
GRAFANA_PASSWORD=<password> DISCORD_WEBHOOK_URL=<webhook> bash scripts/start-monitoring.sh

# Grafana 대시보드 import
GRAFANA_PASSWORD=<password> bash scripts/grafana-import.sh
```

### 수동 배포

```bash
# Docker 이미지 정보 지정
export IMAGE_NAME=ghcr.io/smwu-nuda/nuda-server IMAGE_TAG=<tag>

# 배포 실행
bash scripts/deploy.sh
```

### 롤백 및 슬롯 전환

```bash
# 롤백
bash scripts/rollback.sh

# 특정 슬롯으로 수동 전환
bash scripts/switch.sh <blue|green>
```

### 모니터링 운영

- Prometheus가 15초 간격으로 blue / green 두 슬롯을 모두 수집
- Alertmanager가 5xx 에러율, p99 지연시간, JVM 힙 사용량, GC 중단 시간을 기준으로 알림 전송
- 배포 후에는 Grafana 대시보드와 알림 상태를 기준으로 안정성을 확인
