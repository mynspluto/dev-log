# Liveness Probe
컨테이너가 살아있는지 확인
실패하면 컨테이너 재시작
애플리케이션이 데드락 걸려 응답은 안 하는데 프로세스는 살아있을 때
```yml
livenessProbe:
  httpGet:
    path: /healthz
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5

```

# Readiness Probe
컨테이너가 서비스 트래픽을 받을 준비가 되었는지 확인
실패하면 service의 엔드포인트에서 제외됨(트래픽 전달 안 됨)
초기 설정/캐시 로딩이 끝나야 외부 요청을 받을 수 있는 경우
```yml
readinessProbe:
  tcpSocket:
    port: 3306
  initialDelaySeconds: 5
  periodSeconds: 10

```

# Startup Probe
애플리케이션이 초기 시작되었는지 확인
이 프로브가 성공할 때까지는 Liveness/Readiness 동작하지 않음
부팅 시간이 오래 걸리는 애플리케이션에 사용
실패하면 컨테이너는 재시작
```yml
startupProbe:
  httpGet:
    path: /startup
    port: 8080
  failureThreshold: 30
  periodSeconds: 10

```