# NUDA

> 우리가 쓰는 위생용품, 성분부터 안전까지 투명하게

## 📖 프로젝트 소개

여성 위생용품은 전성분이 공개되어 있어도 전문 화학 용어 중심으로 표기되기 때문에, 일반 소비자가 성분의 위험도와 제품의 안전성을 스스로 판단하기 어렵습니다.

NUDA는 이러한 정보 해석의 장벽을 낮추기 위해 성분 위험도를 직관적으로 시각화하고, 개인의 신체 조건과 선호를 반영한 추천과 성분 기반 탐색 기능을 제공하는 여성 위생용품 플랫폼입니다.

## ✨ 주요 기능

<table>
  <tr>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 36 55" src="https://github.com/user-attachments/assets/7697650d-88a1-45ba-b4e0-9c06ee5fcb2e" />
      <br />
      <b>개인 맞춤 추천</b>
      <br />
      <sub>사용자 설문과 선호를 바탕으로 생리대를 추천합니다.</sub>
    </td>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 34 22" src="https://github.com/user-attachments/assets/70b71fd8-0677-4ea0-b202-6197365529d2" />
      <br />
      <b>성분 위험도 시각화</b>
      <br />
      <sub>MSDS H-code 기준으로 성분 위험도를 직관적으로 보여줍니다.</sub>
    </td>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 38 38" src="https://github.com/user-attachments/assets/3a251d14-fd82-4d59-b301-3646d2168946" />
      <br />
      <b>AI 리뷰 요약</b>
      <br />
      <sub>리뷰의 핵심 키워드와 사용자 반응을 빠르게 파악할 수 있습니다.</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 41 34" src="https://github.com/user-attachments/assets/8d4349c8-f017-422c-9031-ff84ae51b6d6" />
      <br />
      <b>성분 기반 검색</b>
      <br />
      <sub>상품명, 브랜드명, 성분명 기준으로 원하는 제품을 찾을 수 있습니다.</sub>
    </td>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 35 30" src="https://github.com/user-attachments/assets/22eac259-051c-428a-85eb-d3bf2ef90080" />
      <br />
      <b>상품 성분 상세 조회</b>
      <br />
      <sub>성분의 위험도, 설명, 주의사항을 상세하게 확인할 수 있습니다.</sub>
    </td>
    <td align="center" width="33%">
      <img width="600" height="1167" alt="스크린샷 2026-06-23 오후 1 40 50" src="https://github.com/user-attachments/assets/b6ac53a7-4c2a-4571-8d0c-7267fd5e83f2" />
      <br />
      <b>사용자 마이페이지</b>
      <br />
      <sub>관심 성분과 선호 정보를 관리할 수 있습니다.</sub>
    </td>
  </tr>
</table>

---

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| **언어** | `Java 17` `Kotlin` |
| **애플리케이션** | `Spring Boot` `Spring Security` `Spring Data JPA` `Spring WebFlux` |
| **데이터베이스** | `PostgreSQL` `Flyway` |
| **검색 / 캐시** | `Elasticsearch` `Redis` `Caffeine` `pgvector` |
| **인프라 / 배포** | `AWS EC2` `AWS RDS` `AWS S3` `AWS CloudFront` `AWS SSM Parameter Store` `Docker` `GitHub Actions` `Nginx` |
| **모니터링** | `Prometheus` `Grafana` `Alertmanager` |


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
│       │   │   ├── auth/           # 로그인, 토큰 발급·재발급, JWT 처리
│       │   │   ├── member/         # 회원 정보, 회원 탈퇴
│       │   │   ├── signupdraft/    # 회원가입 Draft 저장 및 Commit 플로우
│       │   │   ├── product/        # 상품, 카테고리, 관리자 상품 업로드
│       │   │   ├── brand/          # 브랜드 관리
│       │   │   ├── ingredient/     # 성분 정보, 위험도 분류, 성분 데이터 처리
│       │   │   ├── review/         # 리뷰 CRUD, 리뷰 분석 연동
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
└── docs/                   # 운영 및 성능 개선 문서
```

## 🧩 주요 설계와 구현

### 도메인 설계
- [회원가입 Draft-Commit 구조](./docs/implementation/signup-draft-commit.md)
- [장바구니 동시성 제어](./docs/implementation/cart-concurrency.md)

### 애플리케이션 구조
- [보안 계층 공통 응답 포맷 통일](./docs/implementation/security-response-format.md)
- [이벤트 후처리와 ML 추론 실행 풀 분리](./docs/implementation/async-event-ml-executor.md)
- [Elasticsearch 동기화 실패 격리](./docs/implementation/elasticsearch-sync-isolation.md)

### 데이터 처리
- [관리자 CSV 대량 업로드 배치 설계](./docs/implementation/csv-batch-upload.md)

### 성능 최적화
- [ML 리뷰 분석 API 성능 최적화](./docs/implementation/ml-review-api-optimization.md)
- [상품 상세 조회 캐시 전략](./docs/implementation/product-detail-cache.md)

---

## ☁️ 운영 환경 및 배포
AWS EC2 기반 단일 서버 환경에서 PostgreSQL, Redis, Elasticsearch, Nginx, Docker Compose를 사용하며, `app-blue`, `app-green` 두 슬롯을 운영하는 Blue-Green 배포 구조와 GitHub Actions 기반 자동 배포를 적용했습니다.

### 서버 초기 세팅
> 새 환경에서 처음 서버를 구성할 때는 EC2에 접속한 뒤 Docker, 레포지토리, Nginx upstream, 모니터링 스택을 순서대로 준비합니다.

```bash
# 1. EC2 접속
ssh -i <key.pem> ubuntu@<EC2_HOST>

# 2. 레포지토리 이동
cd ~/nuda-server

# 3. 인프라 컨테이너 기동
docker compose up -d redis elasticsearch

# 4. 모니터링 스택 기동
GRAFANA_PASSWORD=<password> \
DISCORD_WEBHOOK_URL=<webhook> \
bash scripts/start-monitoring.sh
```

### 수동 배포

> 자동 배포와 별개로 서버에서 직접 배포해야 할 때는 EC2에 접속한 뒤 `deploy.sh`를 실행합니다.  
이 스크립트는 SSM 환경 변수 로드, 신규 슬롯 기동, readiness 확인, 트래픽 전환, 안정성 검증까지 포함합니다.

```bash
# 1. EC2 접속
ssh -i <key.pem> ubuntu@<EC2_HOST>

# 2. 레포지토리 이동
cd ~/nuda-server

# 3. 배포 실행
export IMAGE_NAME=ghcr.io/smwu-nuda/nuda-server
export IMAGE_TAG=<image-tag>
bash scripts/deploy.sh
```

### 롤백

> 배포 후 문제가 발생하면 EC2에 접속한 뒤 이전 슬롯으로 롤백할 수 있습니다.

```bash
# 1. EC2 접속
ssh -i <key.pem> ubuntu@<EC2_HOST>

# 2. 레포지토리 이동
cd ~/nuda-server

# 3. 롤백 실행
bash scripts/rollback.sh
```

### 수동 트래픽 전환

> 테스트나 비상 상황에서 특정 슬롯으로 직접 트래픽을 넘길 때 사용합니다.

```bash
# 1. EC2 접속
ssh -i <key.pem> ubuntu@<EC2_HOST>

# 2. 레포지토리 이동
cd ~/nuda-server

# 3. 특정 슬롯으로 전환
bash scripts/switch.sh <blue|green>
```

### 모니터링 운영

> 서버에서 모니터링 스택을 다시 올리거나 Grafana 대시보드를 import할 때 사용합니다.

```bash
# 1. EC2 접속
ssh -i <key.pem> ubuntu@<EC2_HOST>

# 2. 레포지토리 이동
cd ~/nuda-server

# 3. 모니터링 스택 기동
GRAFANA_PASSWORD=<password> \
DISCORD_WEBHOOK_URL=<webhook> \
bash scripts/start-monitoring.sh

# 4. Grafana 대시보드 import
GRAFANA_PASSWORD=<password> \
bash scripts/grafana-import.sh
```
