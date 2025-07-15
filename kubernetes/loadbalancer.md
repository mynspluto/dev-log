# Ingress Loadbalancer

```yml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  annotations:
    kubernetes.io/ingress.class: nginx  # 1. NGINX Ingress Controller가 처리
    kubernetes.io/ingress.class: alb  # 2. AWS Load Balancer Controller가 처리
spec:
  rules:
  - host: api.example.com
```

kubernetes.io/ingress.class 값에 따라 어떤 컨트롤러가 처리할지 결정됨
각 컨트롤러는 워커 노드에 설치되어있어야함, 노드에 AWS 권한 설정 필요
AWS 컨트롤러로 처리되는 경우 AWS내에 ALB가 생성됨
인그레스 하나당 ALB가 하나 생성되므로 ingress.yml 파일 하나로 통일하여 분기 로직 작성 필요

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