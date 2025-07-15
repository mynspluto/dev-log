# Ingress Loadbalancer

```yml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  annotations:
    kubernetes.io/ingress.class: nginx  # 1. NGINX Ingress Controller가 처리
    kubernetes.io/ingress.class: alb  # 2. AWS Load Balancer Controller가 처리
    nginx.ingress.kubernetes.io/affinity: "cookie" # 특정 사용자 같은 노드의 같은 파드로 요청 유지(cookie, none, ip 등 속성 가능)
    nginx.ingress.kubernetes.io/session-cookie-name: "route" # 쿠키 중 사용자를 분류할 키
    nginx.ingress.kubernetes.io/session-cookie-hash: "sha1" # 세션 쿠키 값을 생성할 때 해시 알고리즘 어떤 걸 사용할지
spec:
  rules:
  - host: api.example.com
```

kubernetes.io/ingress.class 값에 따라 어떤 컨트롤러가 처리할지 결정됨
각 컨트롤러는 워커 노드에 설치되어있어야함, 노드에 AWS 권한 설정 필요
AWS 컨트롤러로 처리되는 경우 AWS내에 ALB가 생성됨
인그레스 하나당 ALB가 하나 생성되므로 ingress.yml 파일 하나로 통일하여 분기 로직 작성 필요

## 특정 사용자 연결 유지
특정 사용자를 계속 같은 노드의 파드로 요청하도록 시키려면 ingress에서 설정 필요 (nginx.ingress.kubernetes.io/affinity)
l7 로드밸런서가 없어도 클러스터 내부 Ingress Controller(예: Nginx Ingress Controller)가 충분히 세션 유지 가능
사용자를 ip 기반으로 분류할거면 ingress보다 service에서 설정하는게 나음

### 세션 유지 비교
항목	Ingress (L7)	                                    Service (L4)
목적	HTTP 쿠키 기반 세션 유지	                            클라이언트 IP 기반 세션 유지
설정 방식	nginx.ingress.kubernetes.io/affinity: cookie	sessionAffinity: ClientIP
동작 위치	Ingress Controller (예: NGINX)	                kube-proxy / 서비스 레벨에서 처리
실제 파드 라우팅 기준	쿠키 값	                              클라이언트의 IP 주소
장점	HTTP 환경에서 유연한 라우팅 가능	간단함, 설정 한 줄이면 끝
단점	HTTP 쿠키 필요, HTTPS 뚫기 어려움	NAT, 프록시 환경에선 IP가 변동될 수 있음

### Ingress affinity vs Service sessionAffinity 차이
둘다 ip 기반으로 분기 가능, nginx.ingress.kubernetes.io/affinity: "ip"로 설정 시 ingress에서 ip로 분기 가능

항목	                    Ingress (affinity: ip)	                            Service (sessionAffinity: ClientIP)
IP 식별 위치	              Ingress 컨트롤러 내부 (예: NGINX)	                    kube-proxy (노드에서 직접 IP 확인)
클라이언트 IP 신뢰 방식	      HTTP 헤더 기반 (X-Forwarded-For 등) 필요	            패킷의 소스 IP를 직접 확인
NAT 환경에 민감도	높음        (Ingress 앞에 L7/L4 LB 있으면 영향 큼)	              매우 높음 (NAT되면 무조건 동일 IP로 보임)
TLS 종료 후 IP 확인 가능 여부	가능 (Ingress가 TLS를 종료하면 HTTP 파싱 가능)	    불가능 (L4 수준에서는 암호화된 TLS를 못 염)

### X-Forwarded-For
X-Forwarded-For는 NAT나 프록시로 인해 출발지 IP가 가려지는 상황에서도, 원래 클라이언트의 IP를 전달하기 위해 사용되는 HTTP 헤더입니다.

### Service에서 IP 기반 세션 유지 예시
```yml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: my-app
  ports:
    - port: 80
      targetPort: 8080
  sessionAffinity: ClientIP # 이 속성으로 유지

```

### 세션 유지(Session Persistence, Stickey Session)
클라이언트를 식별할 수 있는 쿠키, 세션 ID, IP 등 특정 정보를 기반으로 요청을 계속해서 동일한 서버(파드)로 보내는 것을 말합니다.

대표적으로는 HTTP 쿠키를 이용해 클라이언트를 구분하고, 그 쿠키 값에 따라 항상 같은 파드로 트래픽을 라우팅합니다.


# Service Loadbalancer

```yml
apiVersion: v1
kind: Service
metadata:
  name: my-app-service
spec:
  type: LoadBalancer  # 핵심!
  ports:
    - port: 80
      targetPort: 8080
      protocol: TCP
  selector:
    app: my-app
```
AWS에서는 type: LoadBalancer 서비스를 만들면, 클라우드 컨트롤러가 L4 로드밸런서(NLB)를 생성하고, 각 노드의 NodePort로 트래픽을 전달합니다.
내부적으로는 NodePort 서비스가 자동 생성되며, 로드밸런서는 노드 단위로 분기한 후, kube-proxy를 통해 실제 Pod로 트래픽이 전달됩니다.

```yml
# Service의 NodePort(31234)로 오는 트래픽을 모든 파드로 분산
iptables -t nat -A KUBE-SERVICES -d 10.100.1.100/32 -p tcp -m tcp --dport 80 -j KUBE-SVC-XXXX
iptables -t nat -A KUBE-SVC-XXXX -j KUBE-SEP-AAAA  # pod-A로 
iptables -t nat -A KUBE-SVC-XXXX -j KUBE-SEP-BBBB  # pod-B로 (다른 노드)
iptables -t nat -A KUBE-SVC-XXXX -j KUBE-SEP-CCCC  # pod-C로 (다른 노드)

# 실제 분기 과정에서 kube-proxy가 라우팅 테이블 역할을하며 로드밸런싱 까지 해줌
NLB → Node-1:31234 → kube-proxy → pod-B (Node-2로 네트워크 전달)
NLB → Node-2:31234 → kube-proxy → pod-A (Node-1로 네트워크 전달)
NLB → Node-3:31234 → kube-proxy → pod-C (같은 노드)
```

NLB(각 워커 노드 IP로 분기) => 워커 노드가 externalTrafficPolicy: Cluster인 경우 자신의 노드안에서 처리할 수 있는 Pod가 있으며 readiness 정상이면 자신이 처리하며 처리 안되는 경우만 다른 노드로 보냄, externalTrafficPolicy: Local인 경우 본인의 노드안에서만 처리