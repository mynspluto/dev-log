# 📘 Domain-Driven Design (DDD) 정리

---

## 1. DDD의 주요 개념

| 구성 요소             | 설명 |
|----------------------|------|
| **Entity**            | 고유 식별자를 가지며 상태를 가지는 객체 (예: `User`) |
| **Value Object (VO)** | 식별자가 없고 값 자체로 의미를 가지는 객체 (예: `Address`) |
| **Aggregate**         | 연관된 Entity, VO들의 집합. 트랜잭션 단위로 묶임 |
| **Aggregate Root**    | Aggregate의 대표. 외부는 이를 통해서만 접근 가능 |
| **Domain Service**    | 복수의 Entity/VO 간의 비즈니스 규칙 담당 |
| **Application Service** | 유스케이스 단위의 조율자. 트랜잭션, 도메인 객체 호출 담당 |
| **Repository**        | Aggregate Root의 저장/조회 인터페이스 |
| **Controller**        | HTTP 요청 처리. Application Service 호출만 담당 |

---

## 2. DDD 계층 구조 (MVC 대비)

[Presentation Layer]
└── Controller (입출력 처리)

[Application Layer]
└── ApplicationService (유스케이스 조율, 트랜잭션 처리)

[Domain Layer]
├── Entity, VO, Aggregate Root
└── DomainService

[Infrastructure Layer]
└── Repository 구현, 외부 시스템 연동

yaml
복사
편집

---

## 3. 핵심 설계 원칙

- ✅ 한 요청은 **하나의 Aggregate Root**만 수정해야 한다.
- ✅ Aggregate 내부 상태는 반드시 **Root를 통해서만 변경**되어야 한다.
- ✅ **Application Layer는 Root만 접근**하고, 내부 엔티티에 직접 접근하지 않는다.
- ✅ 읽기 작업은 여러 Aggregate를 조회해도 되지만, 쓰기 작업은 Aggregate 경계를 넘지 않도록 해야 한다.
- ✅ **도메인 로직은 Domain Layer에**, Application Layer는 **오케스트레이션만** 수행한다.

---


## 4. DDD 디렉토리 구조 예시

com/example/
└── user/ ← Bounded Context (회원 도메인)
├── controller/ ← Controller (입출력 처리)
│ └── UserController.java
├── application/ ← Application Layer
│ └── UserApplicationService.java
├── domain/ ← Domain Layer
│ ├── User.java ← Aggregate Root
│ ├── UserId.java ← VO
│ ├── UserRepository.java ← Repository 인터페이스
│ └── UserDomainService.java ← (선택) Domain Service
├── infrastructure/ ← 기술 구현 (DB, API 등)
│ └── UserRepositoryImpl.java
└── dto/ ← Request/Response 객체
├── RegisterUserRequest.java
└── UserResponse.java

yaml
복사
편집

---

## 5. 기타

- ✔ `UserApplicationService`는 Application Layer에 존재하며 Aggregate Root만 조작한다.
- ✔ `User` 클래스는 도메인 계층의 Aggregate Root로 별도 정의된 Entity이다.
- ✔ 읽기 전용 조회는 여러 Aggregate 접근이 허용되며, 트랜잭션을 요구하지 않는다.
- ✔ Controller는 Application Service를 호출하며, 도메인 계층에는 직접 접근하지 않는다.
- ✔ 여러 Aggregate를 수정하려면 SAGA 또는 도메인 서비스 등으로 조율이 필요하다.
- ✔ 컨텍스트(Bounded Context)는 마치 네임스페이스처럼 이름 충돌을 방지하고, 책임을 분리한다. 주로 package로 물리적 구분(java 기준)
