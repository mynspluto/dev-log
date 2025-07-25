# 오토스케일링 구현 방법

## 메트릭 서버
CPU / 메모리 사용률 기준으로 파드 수 자동 조절
HPA <- API 서버 <-(메트릭 API) 메트릭 서버 <-(서머리 API) 쿠블릿 <- 파드
구성 요소
- Metric Server
  - 파드/노드의 CPU, 메모리 실시간 수집
- Deployment
  - 자동 확장 대상 (예: nginx)
- HPA 리소스
  - autoscaling/v2
  - CPU 사용률이 특정 수치를 넘으면 스케일 아웃

```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  labels:
    app: nginx
spec:
  replicas: 1  # 초기 파드 개수
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx
        resources:
          requests:
            cpu: "100m"
          limits:
            cpu: "500m"
        ports:
        - containerPort: 80

```

```yml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: nginx-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: nginx-deployment
  minReplicas: 1
  maxReplicas: 5
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 50  # CPU 사용률이 50% 이상이면 스케일 아웃

```

## 프로메테우스 어댑터
프로메테우스에 쿼리를 던져 메트릭 확인
```yml
apiVersion: v1
kind: ConfigMap
metadata:
  name: prometheus-adapter-config
  namespace: custom-metrics
data:
  config.yaml:
    rules:
      # 1. 커스텀 메트릭 (파드 단위 http_requests_total을 초당 요청 수로 변환)
      - seriesQuery: 'http_requests_total{kubernetes_namespace!="",kubernetes_pod_name!=""}'
        resources:
          overrides:
            kubernetes_namespace:
              resource: namespace
            kubernetes_pod_name:
              resource: pod
        name:
          matches: "^(.*)_total"
          as: "${1}_per_second"
        metricsQuery: 'sum(rate(http_requests_total{<<.LabelMatchers>>}[2m])) by (kubernetes_namespace, kubernetes_pod_name)'

      # 2. 네임스페이스 단위 CPU 사용량 (Node Exporter 메트릭 예시)
      - seriesQuery: 'namespace_cpu_usage_seconds_total{kubernetes_namespace!=""}'
        resources:
          overrides:
            kubernetes_namespace:
              resource: namespace
        name:
          as: "cpu_usage_seconds_total"
        metricsQuery: 'sum(rate(namespace_cpu_usage_seconds_total{<<.LabelMatchers>>}[5m])) by (kubernetes_namespace)'

      # 3. 외부 메트릭 예시: 전체 클러스터 HTTP 요청 수 (네임스페이스 무관)
      - seriesQuery: 'http_requests_total'
        resources: {}
        name:
          as: "cluster_http_requests_total"
        metricsQuery: 'sum(rate(http_requests_total[1m]))'

      # 4. 커스텀 레이블 기반 메트릭 (예: 앱 라벨)
      - seriesQuery: 'app_http_requests_total{app!=""}'
        resources:
          overrides:
            app:
              resource: "service"    # app 레이블을 서비스 리소스로 매핑
        name:
          as: "app_http_requests_total"
        metricsQuery: 'sum(rate(app_http_requests_total{<<.LabelMatchers>>}[2m])) by (app)'
```

## KEDA
KEDA는 이벤트 기반 오토스케일러로, Kubernetes에서 HPA로는 불가능했던 외부 이벤트 기반 자동 스케일링을 가능하게 해주는 애드온(Operator)

구성 요소
- ScaledObject
- ScaledJob
- TriggerAuthentication
- KEDA Operator

KEDA 트리거 메타데이터는 각 스케일러 종류별로 사전에 정의된 키(key) 필요요
Kafka 스케일러는 lagThreshold, topic, consumerGroup 같은 Kafka 관련 필수 메타데이터 키가 정해져 있죠.
Redis, Prometheus, RabbitMQ, Azure Queue 등도 마찬가지로 각 스케일러별로 요구하는 메타데이터 키가 다릅니다.
KEDA 공식 문서나 스케일러 소스코드에 각 스케일러별 지원하는 메타데이터 키와 옵션이 명시되어 있습니다.

```yml
apiVersion: keda.sh/v1alpha1
kind: ScaledObject
metadata:
  name: kafka-orders-scaledobject
  namespace: default          # 스케일 대상 네임스페이스
spec:
  scaleTargetRef:
    name: orders-consumer     # 스케일링할 Deployment/StatefulSet 이름
  minReplicaCount: 0           # 최소 파드 수 (0 가능)
  maxReplicaCount: 10          # 최대 파드 수
  cooldownPeriod: 300          # (초) 스케일 다운 시 대기 시간
  pollingInterval: 30          # (초) Kafka lag 체크 주기
  triggers:
  - type: kafka
    metadata:
      bootstrapServers: my-kafka:9092
      topic: orders
      consumerGroup: order-group
      lagThreshold: "100"               # lag이 100 이상일 때 스케일 아웃
      # 아래 옵션은 필요에 따라 설정 가능
      # lagThresholdNegativeOneToDisableScaling: "false"
      # activationLagThreshold: "10"    # 최소 스케일링 조건
      # offsetResetPolicy: "latest"     # earliest/latest (기본 latest)
      # allowIdleConsumers: "false"
```