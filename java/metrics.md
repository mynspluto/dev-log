# JVM 모니터링 메트릭 가이드

## 목차
- [기본 JVM 메트릭](#기본-jvm-메트릭)
- [메모리 관련 메트릭](#메모리-관련-메트릭)
- [스레드 관련 메트릭](#스레드-관련-메트릭)
- [가비지 컬렉션 메트릭](#가비지-컬렉션-메트릭)
- [클래스 로딩 메트릭](#클래스-로딩-메트릭)
- [JIT 컴파일러 메트릭](#jit-컴파일러-메트릭)
- [JMX(Java Management Extensions)](#jmx-java-management-extensions)
- [주요 모니터링 도구](#주요-모니터링-도구)
- [스프링 부트 애플리케이션 모니터링](#스프링-부트-애플리케이션-모니터링)

## 기본 JVM 메트릭

| 메트릭 | 설명 | 중요도 |
|--------|------|--------|
| `uptime` | JVM이 실행된 시간(밀리초) | 중간 |
| `jvm.version` | JVM 버전 정보 | 낮음 |
| `jvm.spec.vendor` | JVM 벤더 정보 | 낮음 |
| `jvm.spec.version` | JVM 스펙 버전 | 낮음 |
| `jvm.start_time` | JVM 시작 시간(epoch 밀리초) | 낮음 |

## 메모리 관련 메트릭

| 메트릭 | 설명 | 중요도 |
|--------|------|--------|
| `jvm.memory.heap.used` | 사용 중인 힙 메모리(바이트) | 높음 |
| `jvm.memory.heap.max` | 최대 힙 메모리(바이트) | 높음 |
| `jvm.memory.heap.committed` | 할당된 힙 메모리(바이트) | 중간 |
| `jvm.memory.non_heap.used` | 사용 중인 비힙 메모리(바이트) | 중간 |
| `jvm.memory.non_heap.committed` | 할당된 비힙 메모리(바이트) | 중간 |
| `jvm.memory.pool.<pool_name>.used` | 특정 메모리 풀의 사용량 | 중간 |
| `jvm.memory.pool.<pool_name>.max` | 특정 메모리 풀의 최대 용량 | 중간 |
| `jvm.buffer.memory.used` | 버퍼 메모리 사용량 | 중간 |
| `jvm.buffer.count` | 버퍼 개수 | 낮음 |

## 스레드 관련 메트릭

| 메트릭 | 설명 | 중요도 |
|--------|------|--------|
| `jvm.threads.live` | 현재 활성 스레드 수 | 높음 |
| `jvm.threads.daemon` | 데몬 스레드 수 | 중간 |
| `jvm.threads.peak` | 피크 스레드 수 | 중간 |
| `jvm.threads.started.total` | 시작된 총 스레드 수 | 중간 |
| `jvm.threads.deadlocked` | 데드락 상태의 스레드 수 | 높음 |
| `jvm.threads.blocked` | 블록된 스레드 수 | 높음 |
| `jvm.threads.waiting` | 대기 중인 스레드 수 | 높음 |
| `jvm.threads.timed_waiting` | 시간 제한 대기 스레드 수 | 중간 |
| `jvm.threads.new` | 새로 생성된 스레드 수 | 낮음 |
| `jvm.threads.runnable` | 실행 가능한 스레드 수 | 중간 |
| `jvm.threads.terminated` | 종료된 스레드 수 | 낮음 |

## 가비지 컬렉션 메트릭

| 메트릭 | 설명 | 중요도 |
|--------|------|--------|
| `jvm.gc.collection.<gc_name>.count` | 특정 GC의 발생 횟수 | 높음 |
| `jvm.gc.collection.<gc_name>.time` | 특정 GC에 소요된 시간(밀리초) | 높음 |
| `jvm.gc.memory.promoted` | 프로모션된 메모리 양(바이트) | 중간 |
| `jvm.gc.memory.allocated` | GC 사이클 간 할당된 메모리 양 | 중간 |
| `jvm.gc.live.data.size` | 라이브 데이터 크기 | 중간 |
| `jvm.gc.max.data.size` | Old Generation의 최대 크기 | 중간 |
| `jvm.gc.pause` | GC 일시 중지 시간(히스토그램) | 높음 |
| `jvm.gc.concurrent.phase.time` | 동시 GC 단계 시간 | 중간 |

## 클래스 로딩 메트릭

| 메트릭 | 설명 | 중요도 |
|--------|------|--------|
| `jvm.classes.loaded` | 현재 로드된 클래스 수 | 중간 |
| `jvm.classes.unloaded` | 언로드된 클래스 수 | 낮음 |
| `jvm.classes.loaded.total` | 시작 이후 로드된 총 클래스 수 | 중간 |

## JIT 컴파일러 메트릭

| 메트릭 | 설명 | 중요도 |
|--------|------|--------|
| `jvm.compilation.time` | 컴파일에 소요된 총 시간(밀리초) | 중간 |
| `jvm.compilation.time.total` | 총 컴파일 시간 | 중간 |

## JMX(Java Management Extensions)

JMX는 Java 애플리케이션과 JVM을 모니터링하고 관리하기 위한 표준 API로, 대부분의 JVM 모니터링 도구의 기반 기술입니다.

### JMX 기본 구성 요소

| 구성 요소 | 설명 |
|-----------|------|
| `MBean` | Managed Bean, 리소스를 관리하는 자바 객체 |
| `MBeanServer` | MBean을 등록하고 관리하는 서버 |
| `JMX 커넥터` | 원격에서 JMX에 접근할 수 있게 해주는 인터페이스 |
| `Protocol Adapters` | HTTP, SNMP 등 다양한 프로토콜로 JMX 데이터 노출 |

### 주요 JMX MBean 및 JVM 모니터링 인터페이스

| MBean/인터페이스 | 클래스 | 제공하는 정보 |
|-----------------|-------|-------------|
| `RuntimeMXBean` | `java.lang.management.RuntimeMXBean` | JVM 런타임 정보(시작 시간, 실행 인자 등) |
| `MemoryMXBean` | `java.lang.management.MemoryMXBean` | 힙, 비힙 메모리 사용량 |
| `MemoryPoolMXBean` | `java.lang.management.MemoryPoolMXBean` | 개별 메모리 풀 정보(Eden, Survivor, Old Gen 등) |
| `GarbageCollectorMXBean` | `java.lang.management.GarbageCollectorMXBean` | GC 수행 횟수, 시간 |
| `ThreadMXBean` | `java.lang.management.ThreadMXBean` | 스레드 정보, CPU 사용 시간, 데드락 감지 |
| `ClassLoadingMXBean` | `java.lang.management.ClassLoadingMXBean` | 클래스 로딩 통계 |
| `CompilationMXBean` | `java.lang.management.CompilationMXBean` | JIT 컴파일러 정보 |
| `OperatingSystemMXBean` | `java.lang.management.OperatingSystemMXBean` | OS 정보 및 리소스 사용량 |

### JMX 활성화 및 설정

JVM 시작 옵션으로 JMX를 활성화하는 주요 매개변수:

```
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=9010
-Dcom.sun.management.jmxremote.local.only=false
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
```

### JMX와 모니터링 도구 연동

| 도구 | JMX 연동 방식 |
|------|--------------|
| `JConsole/VisualVM` | 직접 JMX에 연결하여 데이터 표시 |
| `Prometheus` | JMX Exporter를 통해 JMX 메트릭을 수집 |
| `Spring Boot Actuator` | JMX MBean을 자동으로 등록하고 HTTP로도 노출 |
| `Micrometer` | JMX Registry를 통해 JMX MBean 형태로 메트릭 노출 |
| `Jolokia` | JMX를 RESTful API로 변환 |

### JMX 프로그래밍 예제

**JMX 메트릭 조회 기본 코드**:

```java
// MemoryMXBean 조회 예제
MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
System.out.println("Heap Memory Used: " + heapMemoryUsage.getUsed() + " bytes");

// GC MXBean 조회 예제
List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
for (GarbageCollectorMXBean gcBean : gcBeans) {
    System.out.println(gcBean.getName() + " Collections: " + gcBean.getCollectionCount());
    System.out.println(gcBean.getName() + " Collection Time: " + gcBean.getCollectionTime() + " ms");
}
```

## 운영체제/리소스 관련 메트릭

| 메트릭 | 설명 | 중요도 |
|--------|------|--------|
| `process.cpu.usage` | 프로세스 CPU 사용률 | 높음 |
| `system.cpu.usage` | 시스템 CPU 사용률 | 높음 |
| `system.load.average.1m` | 시스템 부하 평균(1분) | 높음 |
| `process.files.open` | 열린 파일 디스크립터 수 | 중간 |
| `process.files.max` | 최대 파일 디스크립터 수 | 중간 |

## 주요 모니터링 도구

### 1. JDK 내장 도구
- **jstat**: JVM 통계 모니터링 도구
- **jps**: 실행 중인 JVM 프로세스 확인
- **jmap**: 힙 덤프 생성 및 메모리 분석
- **jstack**: 스레드 덤프 생성
- **jcmd**: JVM 진단 명령 도구
- **jconsole**: JMX 기반 GUI 모니터링 도구
- **jvisualvm**: 시각적 JVM 모니터링 및 프로파일링

### 2. 오픈소스 모니터링 도구
- **Prometheus + Grafana**: 메트릭 수집 및 시각화
- **Micrometer**: 애플리케이션 메트릭 파사드
- **Elastic APM**: 애플리케이션 성능 모니터링
- **Pinpoint**: 분산 시스템 추적 및 모니터링
- **Arthas**: Alibaba에서 개발한 Java 진단 도구

### 3. 상용 APM 도구
- **Dynatrace**: 자동 분산 추적 및 AI 기반 문제 감지
- **New Relic**: 종합 애플리케이션 모니터링
- **AppDynamics**: 비즈니스 트랜잭션 중심 모니터링
- **Datadog**: 클라우드 규모 모니터링

## JVM 모니터링 모범 사례

1. **주요 경고 신호 설정**:
   - 높은 GC 빈도 및 지속 시간
   - 지속적으로 증가하는 힙 사용량 (메모리 누수 가능성)
   - 높은 CPU 사용률
   - 데드락 스레드 발생

2. **GC 로그 활성화**: 세부적인 GC 분석을 위한 로깅 설정
   ```
   -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/path/to/gc.log
   ```

3. **힙 덤프 자동화**: 메모리 부족 오류 발생 시 자동 힙 덤프 생성
   ```
   -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/path/to/dumps
   ```

4. **프로파일링 계획**: 주기적인 프로파일링으로 성능 병목 현상 식별

5. **베이스라인 설정**: 정상 작동 시의 메트릭 베이스라인 설정 및 비교

## 스프링 부트 애플리케이션 모니터링

### 1. Spring Boot Actuator 사용

1. **의존성 추가**:
   ```gradle
   implementation 'org.springframework.boot:spring-boot-starter-actuator'
   ```

2. **주요 엔드포인트**:
   | 엔드포인트 | 설명 | URL |
   |----------|------|-----|
   | health | 애플리케이션 상태 점검 | /actuator/health |
   | info | 애플리케이션 정보 | /actuator/info |
   | metrics | 메트릭 정보 | /actuator/metrics |
   | env | 환경 변수 정보 | /actuator/env |
   | loggers | 로거 설정 조회 및 변경 | /actuator/loggers |
   | mappings | HTTP 요청 매핑 정보 | /actuator/mappings |
   | beans | 스프링 빈 정보 | /actuator/beans |
   | threaddump | 스레드 덤프 | /actuator/threaddump |
   | heapdump | 힙 덤프 생성 | /actuator/heapdump |

3. **설정 (application.yml)**:
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: "*"  # 모든 엔드포인트 활성화
     endpoint:
       health:
         show-details: always  # 상세 건강 정보 표시
     info:
       env:
         enabled: true  # 환경 정보 노출
   ```

### 2. Micrometer + Prometheus + Grafana 연동

1. **의존성 추가**:
   ```gradle
   implementation 'org.springframework.boot:spring-boot-starter-actuator'
   implementation 'io.micrometer:micrometer-registry-prometheus'
   ```

2. **Prometheus 설정 (prometheus.yml)**:
   ```yaml
   scrape_configs:
     - job_name: 'spring-boot-app'
       metrics_path: '/actuator/prometheus'
       scrape_interval: 5s
       static_configs:
         - targets: ['localhost:8080']
   ```

3. **주요 스프링 부트 메트릭**:
   | 메트릭 | 설명 | Prometheus 쿼리 |
   |---------|------|-----------------|
   | HTTP 요청 수 | 특정 엔드포인트 호출 횟수 | `http_server_requests_seconds_count{uri="/api/users"}` |
   | HTTP 응답 시간 | 엔드포인트 응답 시간 | `http_server_requests_seconds_sum{uri="/api/users"} / http_server_requests_seconds_count{uri="/api/users"}` |
   | HTTP 오류율 | HTTP 오류 비율 | `sum(http_server_requests_seconds_count{status="5xx"}) / sum(http_server_requests_seconds_count)` |
   | Tomcat 스레드 | 활성 Tomcat 스레드 수 | `tomcat_threads_current_threads` |
   | JDBC 연결 | 활성 데이터베이스 연결 수 | `hikaricp_connections_active` |
   | 시스템 CPU | 시스템 CPU 사용률 | `system_cpu_usage` |
   | JVM CPU | JVM CPU 사용률 | `process_cpu_usage` |

4. **Grafana 대시보드**:
   - JVM (Micrometer) - ID 4701 
   - Spring Boot 2.1+ - ID 10280
   - Micrometer Spring Throughput - ID 11955

### 3. Spring Boot Admin 사용

1. **Admin Server 설정**:
   ```gradle
   implementation 'de.codecentric:spring-boot-admin-starter-server:3.1.0'
   ```
   ```java
   @SpringBootApplication
   @EnableAdminServer
   public class AdminServerApplication {
       public static void main(String[] args) {
           SpringApplication.run(AdminServerApplication.class, args);
       }
   }
   ```

2. **클라이언트 설정**:
   ```gradle
   implementation 'de.codecentric:spring-boot-admin-starter-client:3.1.0'
   ```
   ```yaml
   spring:
     boot:
       admin:
         client:
           url: http://localhost:8080
   management:
     endpoints:
       web:
         exposure:
           include: "*"
   ```

### 4. 애플리케이션 성능 모니터링 (APM) 도구 연동

1. **Elastic APM 연동**:
   - 의존성 추가: `implementation 'co.elastic.apm:apm-agent-attach:1.43.0'`
   - JVM 옵션 설정: `-javaagent:/path/to/elastic-apm-agent.jar`
   - 설정 파일 생성: `elastic-apm.properties`

2. **Pinpoint 연동**:
   - 에이전트 다운로드 및 설정
   - JVM 옵션 추가: `-javaagent:/path/to/pinpoint-agent/pinpoint-bootstrap.jar`
   - profiler.config 설정

3. **상용 APM 도구 연동**:
   - Datadog: JVM 옵션으로 에이전트 추가
   - New Relic: 에이전트 설치 및 애플리케이션 키 설정
   - Dynatrace: OneAgent 설치

### 5. 실시간 로그 모니터링

1. **ELK 스택 연동**:
   ```gradle
   implementation 'net.logstash.logback:logstash-logback-encoder:7.4'
   ```
   
   ```xml
   <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
     <destination>localhost:5000</destination>
     <encoder class="net.logstash.logback.encoder.LogstashEncoder" />
   </appender>
   ```

2. **Loki + Grafana 연동**:
   ```xml
   <appender name="LOKI" class="com.github.loki4j.logback.Loki4jAppender">
     <http>
       <url>http://localhost:3100/loki/api/v1/push</url>
     </http>
   </appender>
   ```

### 6. 분산 추적 시스템 연동

1. **Spring Cloud Sleuth + Zipkin**:
   ```gradle
   implementation 'org.springframework.cloud:spring-cloud-starter-sleuth'
   implementation 'org.springframework.cloud:spring-cloud-sleuth-zipkin'
   ```
   ```yaml
   spring:
     zipkin:
       base-url: http://localhost:9411
     sleuth:
       sampler:
         probability: 1.0
   ```

2. **OpenTelemetry 연동**:
   ```gradle
   implementation 'io.opentelemetry:opentelemetry-api'
   implementation 'io.opentelemetry:opentelemetry-sdk'
   implementation 'io.opentelemetry:opentelemetry-exporter-jaeger'
   ```

### 7. 자주 모니터링해야 할 항목

1. **시스템 레벨**:
   - JVM 힙 메모리 사용량 및 GC 활동
   - CPU 사용률 및 시스템 부하
   - 디스크 I/O 및 네트워크 처리량

2. **애플리케이션 레벨**:
   - API 엔드포인트 응답 시간 및 처리량
   - 데이터베이스 쿼리 실행 시간 및 활성 연결 수
   - 외부 서비스 호출 응답 시간 및 오류율

3. **비즈니스 레벨**:
   - 사용자 요청 처리율 및 오류율
   - 트랜잭션 처리 시간 및 성공률
   - 사용자 세션 및 동시 사용자 수