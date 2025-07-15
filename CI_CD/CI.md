# CI (Continuous Integration) - 지속적 통합

## 개요
CI는 여러 개발자가 작업한 코드를 지속적으로 통합하는 개발 방법론입니다. 코드 변경 사항이 메인 저장소에 자주 병합되며, 각 병합 시마다 자동화된 빌드와 테스트가 실행됩니다.

## 주요 개념

### 1. 자동화된 빌드
- **소스 코드 컴파일**: 코드를 실행 가능한 형태로 변환
- **종속성 관리**: 라이브러리 및 패키지 의존성 해결
- **아티팩트 생성**: 배포 가능한 파일 생성

### 2. 자동화된 테스트
- **단위 테스트**: 개별 컴포넌트 테스트
- **통합 테스트**: 컴포넌트 간 상호작용 테스트
- **기능 테스트**: 비즈니스 로직 검증

### 3. 코드 품질 관리
- **정적 분석**: 코드 구조 및 품질 검사
- **커버리지 측정**: 테스트 커버리지 확인
- **코딩 표준**: 일관된 코딩 스타일 유지

## 핵심 기술 스택

### 1. 버전 관리 시스템
#### Git
- **분산 버전 관리**: 로컬과 원격 저장소 동기화
- **브랜치 전략**: Feature, Develop, Release 브랜치
- **머지 전략**: Fast-forward, Merge commit, Squash

#### GitHub/GitLab
- **Pull Request**: 코드 리뷰 프로세스
- **Issue 연동**: 작업 추적 및 관리
- **Webhook**: 이벤트 기반 트리거

### 2. CI 플랫폼 비교

#### Jenkins
**장점:**
- 오픈소스로 무료 사용 가능
- 플러그인 생태계가 풍부
- 온프레미스 및 클라우드 지원
- 높은 커스터마이징 가능

**단점:**
- 초기 설정 복잡
- 서버 관리 부담
- 플러그인 호환성 문제

#### GitHub Actions
**장점:**
- GitHub과 완벽 통합
- 설정이 간단 (YAML 기반)
- 다양한 사전 구성된 액션
- 무료 사용량 제공

**단점:**
- GitHub 종속성
- 복잡한 워크플로우 제한
- 온프레미스 환경 제한

#### GitLab CI/CD
**장점:**
- GitLab과 완벽 통합
- Docker 기반 실행 환경
- 파이프라인 시각화
- 무료 사용량 제공

**단점:**
- GitLab 종속성
- 러너 관리 필요
- 메모리 사용량 높음

#### Bitbucket Pipelines
**장점:**
- Bitbucket과 완벽 통합
- YAML 기반 간단한 설정
- Docker 서비스 내장 지원
- 무료 사용량 제공

**단점:**
- 제한된 BuildKit 지원
- 1GB 캐시 크기 제한
- Multi-platform 빌드 미지원
- 빌드 성능 제약

**Docker 빌드 캐싱 전략:**

```yaml
# 기본 Docker 캐시 사용 (BuildKit 비활성화)
pipelines:
  default:
    - step:
        name: Build with Cache
        script:
          - docker build -t myapp .
        services:
          - docker
        caches:
          - docker
```

```yaml
# Registry 기반 캐싱 (BuildKit 활성화)
pipelines:
  default:
    - step:
        name: Build with Registry Cache
        script:
          - export DOCKER_BUILDKIT=1
          - docker pull $IMAGE:latest || true
          - docker build 
            --cache-from $IMAGE:latest 
            --build-arg BUILDKIT_INLINE_CACHE=1 
            -t $IMAGE:latest .
          - docker push $IMAGE:latest
        services:
          - docker
```

```yaml
# 커스텀 Docker 캐시 (파일 기반)
definitions:
  caches:
    docker-cache:
      key:
        files:
          - Dockerfile
      path: docker-cache

pipelines:
  default:
    - step:
        name: Build with Custom Cache
        script:
          - docker load -i docker-cache/* || echo "No cache"
          - docker build -t myapp .
          - mkdir -p docker-cache 
          - docker save $(docker images -aq) -o docker-cache/cache.tar
        services:
          - docker
        caches:
          - docker-cache
```

**성능 최적화 방법:**

1. **사전 구성된 Docker 이미지 사용:**
```dockerfile
# 의존성을 미리 설치한 베이스 이미지 생성
FROM node:18-alpine
COPY package*.json ./
RUN npm ci --only=production
WORKDIR /app
```

2. **Multi-stage 빌드 활용:**
```dockerfile
FROM node:18-alpine AS builder
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:18-alpine AS production
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
```

**Buildx 사용 시 주의사항:**

Bitbucket Pipeline에서는 `docker buildx` 명령어가 **완전히 비활성화**되어 있습니다:

```yaml
# ❌ 작동하지 않음 - buildx 비활성화
pipelines:
  default:
    - step:
        script:
          - docker buildx build --platform linux/amd64,linux/arm64 .
        # Error: unknown flag: --platform
```

**Buildx 대안 해결책:**

1. **Self-hosted Runner 사용:**
```yaml
# 자체 호스팅 러너에서 buildx 활용
definitions:
  services:
    docker:
      image: docker:dind
      memory: 4500

pipelines:
  default:
    - step:
        runs-on: linux
        services:
          - docker
        script:
          - docker buildx create --use
          - docker buildx build --platform linux/amd64,linux/arm64 .
```

2. **외부 빌드 서비스 활용 (Depot):**
```yaml
pipelines:
  default:
    - step:
        name: Build with Depot
        script:
          # Depot CLI 설치
          - curl -L https://depot.dev/install-cli.sh | DEPOT_INSTALL_DIR=/usr/local/bin sh
          # buildx 대신 depot 사용
          - depot build --platform linux/amd64,linux/arm64 .
        services:
          - docker
```

3. **Registry 기반 Multi-platform 빌드:**
```yaml
pipelines:
  default:
    - parallel:
        - step:
            name: Build AMD64
            image: atlassian/default-image:3
            script:
              - docker build --build-arg TARGETARCH=amd64 -t $IMAGE:amd64 .
              - docker push $IMAGE:amd64
        - step:
            name: Build ARM64
            image: atlassian/default-image:3
            script:
              - docker build --build-arg TARGETARCH=arm64 -t $IMAGE:arm64 .
              - docker push $IMAGE:arm64
    - step:
        name: Create Manifest
        script:
          # Multi-platform manifest 생성
          - docker manifest create $IMAGE:latest $IMAGE:amd64 $IMAGE:arm64
          - docker manifest push $IMAGE:latest
```

**Buildx vs 기본 Docker Build 비교:**

| 기능 | 기본 Docker | Docker Buildx | Bitbucket 지원 |
|------|-------------|---------------|----------------|
| 기본 빌드 | ✅ | ✅ | ✅ |
| 캐싱 최적화 | ⚠️ 제한적 | ✅ 우수 | ❌ |
| Multi-platform | ❌ | ✅ | ❌ |
| 병렬 빌드 | ❌ | ✅ | ❌ |
| 고급 캐시 옵션 | ❌ | ✅ | ❌ |

## 캐시 무효화 관리 전략

Docker 빌드 캐싱 사용 시 **변경사항이 반영되지 않는 문제**를 방지하고 관리하는 방법:

### 1. **파일 기반 캐시 키 전략**

#### 스마트 캐시 키 설계
```yaml
# GitHub Actions 예시
- name: Cache Docker layers
  uses: actions/cache@v3
  with:
    path: /tmp/.buildx-cache
    # 여러 파일의 해시를 조합한 캐시 키
    key: docker-${{ runner.os }}-${{ hashFiles('**/Dockerfile', '**/package*.json', '**/requirements.txt') }}-${{ github.sha }}
    restore-keys: |
      docker-${{ runner.os }}-${{ hashFiles('**/Dockerfile', '**/package*.json', '**/requirements.txt') }}-
      docker-${{ runner.os }}-
```

#### Bitbucket Pipeline 파일 기반 캐시
```yaml
definitions:
  caches:
    smart-docker-cache:
      key:
        files:
          - Dockerfile
          - package.json
          - package-lock.json
          - requirements.txt
          - pom.xml
          # 의존성 파일들 변경 시 캐시 무효화
      path: .docker-cache

pipelines:
  default:
    - step:
        name: Build with Smart Cache
        script:
          - docker load -i .docker-cache/cache.tar || echo "No cache"
          - docker build --no-cache-filter="app-layer" -t myapp .
          - docker save myapp -o .docker-cache/cache.tar
        caches:
          - smart-docker-cache
```

### 2. **단계별 캐시 무효화**

#### Dockerfile 최적화로 선택적 캐시 무효화
```dockerfile
FROM node:18-alpine AS base

# 1단계: 시스템 의존성 (거의 변경되지 않음)
RUN apk add --no-cache git python3 make g++

# 2단계: 패키지 의존성 (package.json 변경 시만 무효화)
COPY package*.json ./
RUN npm ci --only=production

# 3단계: 소스 코드 (코드 변경 시마다 무효화)
COPY . .
RUN npm run build

# 캐시 무효화 확인을 위한 레이블
LABEL cache.invalidate="$CACHE_BUST"
```

#### 조건부 캐시 무효화
```yaml
# GitHub Actions - 특정 조건에서 캐시 무효화
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Determine cache strategy
        id: cache-strategy
        run: |
          if [[ "${{ github.event_name }}" == "schedule" ]] || [[ "${{ contains(github.event.head_commit.message, '[rebuild]') }}" == "true" ]]; then
            echo "cache-suffix=$(date +%Y%m%d)" >> $GITHUB_OUTPUT
            echo "cache-mode=no-cache" >> $GITHUB_OUTPUT
          else
            echo "cache-suffix=" >> $GITHUB_OUTPUT  
            echo "cache-mode=max" >> $GITHUB_OUTPUT
          fi
          
      - name: Build with conditional caching
        uses: docker/build-push-action@v5
        with:
          cache-from: type=gha,scope=build${{ steps.cache-strategy.outputs.cache-suffix }}
          cache-to: type=gha,scope=build${{ steps.cache-strategy.outputs.cache-suffix }},mode=${{ steps.cache-strategy.outputs.cache-mode }}
          build-args: |
            CACHE_BUST=${{ steps.cache-strategy.outputs.cache-suffix }}
```

### 3. **환경별 캐시 분리**

#### 브랜치/환경별 캐시 격리
```yaml
# Bitbucket Pipeline
definitions:
  caches:
    docker-main:
      key:
        files:
          - Dockerfile
        prefix: main
      path: .docker-cache
    docker-develop:
      key:
        files:
          - Dockerfile  
        prefix: develop
      path: .docker-cache

pipelines:
  branches:
    main:
      - step:
          caches:
            - docker-main
    develop:
      - step:
          caches:
            - docker-develop
```

#### GitHub Actions 환경별 캐시
```yaml
- name: Build with environment-specific cache
  uses: docker/build-push-action@v5
  with:
    cache-from: type=gha,scope=${{ github.ref_name }}-${{ github.workflow }}
    cache-to: type=gha,scope=${{ github.ref_name }}-${{ github.workflow }},mode=max
```

### 4. **캐시 검증 및 모니터링**

#### 캐시 히트율 모니터링
```yaml
pipelines:
  default:
    - step:
        name: Build with Cache Monitoring
        script:
          # 캐시 상태 확인
          - |
            if [ -f .docker-cache/cache.tar ]; then
              echo "✅ Cache found - size: $(du -h .docker-cache/cache.tar | cut -f1)"
              CACHE_STATUS="HIT"
            else
              echo "❌ No cache found"
              CACHE_STATUS="MISS"
            fi
          
          # 빌드 시간 측정
          - start_time=$(date +%s)
          - docker load -i .docker-cache/cache.tar || echo "No cache to load"
          - docker build -t myapp .
          - end_time=$(date +%s)
          
          # 결과 리포팅
          - build_time=$((end_time - start_time))
          - echo "🏗️ Build completed in ${build_time}s with cache ${CACHE_STATUS}"
          
          # 새 캐시 저장
          - docker save myapp -o .docker-cache/cache.tar
```

#### 캐시 무효화 강제 실행
```yaml
# 수동 캐시 무효화 옵션
pipelines:
  custom:
    force-rebuild:
      - step:
          name: Force Rebuild (No Cache)
          script:
            # 캐시 완전 무시
            - rm -rf .docker-cache/* || true
            - docker build --no-cache -t myapp .
            - docker save myapp -o .docker-cache/cache.tar
          caches:
            - docker-cache
```

### 5. **캐시 문제 디버깅**

#### 캐시 레이어 검사
```yaml
steps:
  - name: Debug cache layers
    run: |
      # 캐시된 이미지 히스토리 확인
      docker history myapp:latest
      
      # 레이어별 크기 확인  
      docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"
      
      # 캐시 무효화 지점 찾기
      docker build --progress=plain --no-cache -t myapp-debug . 2>&1 | grep -E "(CACHED|RUN|COPY)"
```

#### 캐시 건전성 검사
```bash
#!/bin/bash
# cache-health-check.sh

echo "🔍 Cache Health Check"

# 1. 캐시 파일 존재 여부
if [ -f .docker-cache/cache.tar ]; then
    echo "✅ Cache file exists"
    echo "📦 Cache size: $(du -h .docker-cache/cache.tar | cut -f1)"
else
    echo "❌ No cache file found"
    exit 0
fi

# 2. 캐시 파일 유효성
if docker load -i .docker-cache/cache.tar >/dev/null 2>&1; then
    echo "✅ Cache file is valid"
else
    echo "❌ Cache file is corrupted, removing..."
    rm -f .docker-cache/cache.tar
    exit 1
fi

# 3. 의존성 파일 변경 확인
if [ -f .cache-deps-hash ]; then
    OLD_HASH=$(cat .cache-deps-hash)
    NEW_HASH=$(find . -name "package*.json" -o -name "requirements.txt" -o -name "Dockerfile" | xargs cat | sha256sum | cut -d' ' -f1)
    
    if [ "$OLD_HASH" != "$NEW_HASH" ]; then
        echo "⚠️ Dependencies changed, cache may be stale"
        echo "$NEW_HASH" > .cache-deps-hash
    else
        echo "✅ Dependencies unchanged, cache is fresh"
    fi
else
    echo "🆕 First run, creating dependency hash"
    find . -name "package*.json" -o -name "requirements.txt" -o -name "Dockerfile" | xargs cat | sha256sum | cut -d' ' -f1 > .cache-deps-hash
fi
```

### 6. **Best Practices 요약**

```yaml
# 종합적인 캐시 관리 예시
pipelines:
  default:
    - step:
        name: Smart Cached Build
        script:
          # 캐시 건전성 검사
          - chmod +x ./scripts/cache-health-check.sh
          - ./scripts/cache-health-check.sh
          
          # 조건부 캐시 무효화
          - |
            if [[ "$BITBUCKET_COMMIT_MESSAGE" == *"[rebuild]"* ]]; then
              echo "🔄 Force rebuild requested"
              rm -rf .docker-cache/*
            fi
            
          # 스마트 빌드
          - docker load -i .docker-cache/cache.tar || echo "Starting fresh build"
          - docker build -t myapp:$BITBUCKET_COMMIT .
          
          # 캐시 저장 (성공 시에만)
          - docker save myapp:$BITBUCKET_COMMIT -o .docker-cache/cache.tar
          
        caches:
          - docker-cache
        artifacts:
          - .cache-deps-hash  # 다음 빌드에서 사용
```

이렇게 관리하면 **캐시 효율성과 정확성**을 동시에 보장할 수 있습니다! [[memory:2970851]]

### 3. 빌드 도구

#### Maven (Java)
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>11</source>
                <target>11</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```

#### Gradle (Java/Kotlin)
```gradle
plugins {
    id 'java'
    id 'jacoco'
    id 'org.sonarqube'
}

test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}
```

#### npm/yarn (JavaScript)
```json
{
  "scripts": {
    "build": "webpack --mode production",
    "test": "jest",
    "lint": "eslint src/",
    "coverage": "jest --coverage"
  }
}
```

### 4. 테스트 자동화

#### 단위 테스트
- **JUnit 5**: Java 최신 테스트 프레임워크
- **Jest**: JavaScript 테스트 프레임워크
- **PyTest**: Python 테스트 프레임워크

#### 통합 테스트
- **TestContainers**: 도커 기반 통합 테스트
- **Spring Boot Test**: Spring 통합 테스트
- **Supertest**: Node.js API 테스트

#### E2E 테스트
- **Selenium**: 웹 UI 자동화
- **Cypress**: 모던 웹 테스트 프레임워크
- **Playwright**: 크로스 브라우저 테스트

### 5. 코드 품질 도구

#### SonarQube
- **정적 분석**: 코드 스멜, 버그, 보안 취약점 탐지
- **품질 게이트**: 품질 기준 통과 여부 확인
- **기술 부채**: 수정 시간 추정

#### ESLint (JavaScript)
- **코드 스타일**: 일관된 코딩 스타일 강제
- **오류 탐지**: 잠재적 오류 사전 발견
- **자동 수정**: 일부 규칙 자동 수정

## CI 파이프라인 구성

### 1. 기본 파이프라인 단계
```yaml
# GitHub Actions 예시
name: CI Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'temurin'
      - name: Cache Maven dependencies
        uses: actions/cache@v2
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
      - name: Run tests
        run: mvn test
      - name: Generate coverage report
        run: mvn jacoco:report
      - name: SonarQube analysis
        run: mvn sonar:sonar
```

### 2. 병렬 처리 최적화
```yaml
jobs:
  test:
    strategy:
      matrix:
        java: [8, 11, 17]
        os: [ubuntu-latest, windows-latest]
    runs-on: ${{ matrix.os }}
    steps:
      - name: Test with Java ${{ matrix.java }}
        run: mvn test
```

### 3. 조건부 실행
```yaml
jobs:
  test:
    if: github.event_name == 'push' || github.event_name == 'pull_request'
    steps:
      - name: Run unit tests
        run: mvn test
      
  integration-test:
    needs: test
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Run integration tests
        run: mvn integration-test
```

## 모범 사례

### 1. 브랜치 전략
- **Feature 브랜치**: 새로운 기능 개발
- **Develop 브랜치**: 통합 및 테스트
- **Main 브랜치**: 안정적인 배포 버전

### 2. 테스트 전략
- **피라미드 구조**: 단위 테스트 > 통합 테스트 > E2E 테스트
- **빠른 피드백**: 단위 테스트 우선 실행
- **격리된 테스트**: 테스트 간 의존성 제거

### 3. 빌드 최적화
- **증분 빌드**: 변경된 부분만 빌드
- **병렬 빌드**: 독립적인 모듈 병렬 처리
- **캐싱**: 의존성 및 빌드 결과 캐시

### 4. 알림 및 보고
- **실패 알림**: 빌드 실패 시 즉시 알림
- **리포트 생성**: 테스트 결과 및 커버리지 보고서
- **트렌드 분석**: 품질 지표 추적

## 일반적인 문제와 해결책

### 1. 빌드 시간 증가
**원인**: 의존성 다운로드, 복잡한 테스트
**해결책**: 캐싱, 병렬 처리, 테스트 최적화

### 2. 불안정한 테스트
**원인**: 환경 의존성, 타이밍 이슈
**해결책**: 모킹, 테스트 격리, 재시도 메커니즘

### 3. 복잡한 설정
**원인**: 다양한 도구 통합, 환경 차이
**해결책**: 도커 컨테이너, 표준화된 설정

### 4. 보안 문제
**원인**: 시크릿 노출, 권한 관리
**해결책**: 시크릿 관리 도구, 최소 권한 원칙

## 성과 측정

### 1. 핵심 지표
- **빌드 성공률**: 성공한 빌드 비율
- **빌드 시간**: 평균 빌드 소요 시간
- **테스트 커버리지**: 코드 커버리지 비율
- **결함 발견 시간**: 버그 발견까지 소요 시간

### 2. 품질 지표
- **코드 품질**: SonarQube 점수
- **기술 부채**: 수정 필요 시간
- **중복 코드**: 중복 코드 비율
- **복잡도**: 순환 복잡도

### 3. 생산성 지표
- **배포 빈도**: 배포 횟수
- **리드 타임**: 개발부터 배포까지 시간
- **복구 시간**: 장애 복구 시간
- **변경 실패율**: 배포 실패 비율
