# 업비트 종목 토론방 프로젝트

[2024.08.01-2024.10.01]

--- 

## OverView

---

- 업비트 웹소켓을 통해 실시간 현재가 정보를 가져와 차트를 생성
- 소셜 시스템을 구축하여 팔로워의 뉴스피드를 업데이트해서 볼 수 있도록 구현

## 기술 스택 및 아키텍처

---

### 기술 스택

- Language : Java 17
- FrameWork :
    - SpringBoot (ver 3.3.2)
    - Spring Cloud (2023.0.02)
    - Spring Data JPA
    - Spring Webflux Security
        - JWT
        - Oauth2.0
- DB : MySQL
- Caching : Redis, Nginx
- Monitoring and Testing Tools : JMeter
- Message Brokers : Apache Kafka
- Load Balance : Netflix Eureka
- DevOps / Containerization : Docker
- API Clients : Feign Client

### 아키텍처

![아키텍처.png](..%2F..%2FPictures%2F%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98.png)

## 명세 자료

---

<details><summary><strong>ERD</strong></summary>

![스크린샷_4-9-2024_224324_www.erdcloud.com.jpeg](..%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7_4-9-2024_224324_www.erdcloud.com.jpeg)
</details>

<details><summary><strong>API 명세서</strong></summary>

스웨거 링크 추가 예정
</details>

## 주요 기능

---

### 로그인 서비스

- Gateway에 Spring Webflux Security를 통해 토큰 검사
- JWT, Oauth2.0을 통한

### 소셜 서비스

- **Apache Kafka 사용**
    - 비동기 이벤트 기반 데이터 처리를 구현
- **Redis 캐싱 및 fan-out-on-write 전략**
    - 대규모 사용자 뉴스 피드의 읽기 성능 최적화 및 db 부하 줄이기
    - 데이터 일관성을 유지하면서 데이터베이스 부하를 최소화하기 위해 중간 테이블과 TTL 기법을 적용
- **뉴스피드의 동기, 비동기 기능을 추가 정리 필요**

### 주식 서비스

- **WebSocket 사용**
    - 실시간 데이터 수신을 위해 WebSocket을 사용하여 Upbit 거래소에서 실시간 ticker 정보를 받아옴
    - WebSocket Stomp를 사용하여 클라이언트에서 구독한 코인의 정보를 실시간으로 전달
- **Apache Kafka 사용**
    - 비동기적인 대용량 데이터를 실시간으로 안전하게 처리하기 위해 사용
    - 빠른 데이터 처리를 위해 종목 별 topic을 생성하여 병렬적으로 작업
    - 실시간 데이터 분석에 대한 확장성을 고려
- **nginx 정적 페이지 저장 및 캐싱**
    - 일봉의 경우 동적 처리의 경우 비효율적이라 판단
    - nginx를 통해 매일 일봉 페이지를 저장하고 캐싱을 하여 서버에 도달하기 전에 데이터를 받을 수 있도록 처리

## 기술적 의사결정

---

### 프로젝트 규모 가정:

- 프로젝트 규모 산정 과정에서 소셜 플랫폼에 주식 차트를 접목한 프로젝트이기 때문에, 소셜 플랫폼 특성상 사용자 활동이 많고, 평균 세션 수가 높기 때문에 평균 QPS를 약 10,000~50,000으로 가정
- 대규모 회사에서 팀별로 분리된 작업하는 상황과 회사의 성장 가능성을 고려하여 확장성 및 독립성을 갖춘 아키텍처 채택

### 마이크로서비스 아키텍처(MSA) 도입:

- 각 서비스의 독립성을 보장하고, 확장성을 극대화하기 위해 MSA를 도입
- Spring Cloud와 Netflix Eureka를 사용하여 서비스 디스커버리와 로드 밸런싱을 구현
- API Gateway 서비스에서 JWT 기반 인증 및 보안 모듈을 설정
- 각 마이크로서비스 간의 통신은 Feign Client를 사용하여 REST API 기반으로 비동기 처리하였으며, 서비스 간의 높은 응답 시간을 해결하기 위해 병렬 처리(CompletableFuture)를 활용

## 트러블 슈팅 및 성능 개선

---

카프카 토픽 1개 컨슈머 1개
카프카 토픽 10개 컨슈머 1개 + 컨텍스트 스위칭
카프카 토픽 10개 컨슈머 10개

동기 처리:

- 순차적으로 실행되는 방식
- 블로킹 방식
- 단순성

<<<<<<< Updated upstream

- 아키텍처 구상
=======
- 뉴스 피드 성능 개선
>>>>>>> Stashed changes
    - 뉴스피드 속도 문제
    - fegin client의 동기 처리의 속도 문제
    - non-blocking 처리 시 에러 처리 롤백 문제 인지
    - 동기 처리를 병렬적으로 처리(computable future)
    - 서비스의 독립성을 위해 포스트 서비스 및 뉴스피드를 분리하고
<<<<<<< Updated upstream
    - 뉴스피드에 redis 중간 테이블을 두어 각 유저의 뉴스피드 post id정보를 저장
=======
    - 뉴스피드에 redis 중간 테이블을 두어 각 유저의 뉴스피드 post id 정보를 저장
>>>>>>> Stashed changes

- 인덱싱 문제
    - 순환, 해시, 테이블 분리X
    - 트랜잭션을 통해 한번에 인덱스를 삭제 생성을 고민
    - b-tree 시간으로 인덱스 생성 및 삭제를 통한 실시간 데이터 저장
    - 주식 데이터의 특성 상 최대한 빠른 데이터 삭제 및 체결 틱 기준으로 데이터를 가져오다보니 시간이 불규칙
- websocket을 통한 구독 정보
    - 클라이언트가 구독 정보에 따라 원하는 코인의 정보를 받을 수 있도록 처리