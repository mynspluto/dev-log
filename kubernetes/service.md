# port, nodePort, targetPort
port: 클러스터 내부에서 접근하는 포트
nodePort: 클러스터 외부에서 접근하는 포트
targetPort: pod container가 사용하는 포트

# ingress를 통해 노출하는 경우
nodePort로 직접 노출하지 않고 ingress로 노출하는 경우 nodePort 잘 사용하지 않음
ingress에서는 서비스의 이름과 서비스에서 port로 설정된 port로 접근
```yml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: example-ingress
spec:
  rules:
  - host: example.com
    http:
      paths:
      - path: /app
        pathType: Prefix
        backend:
          service:
            name: my-service     # 여기서 서비스 이름으로 지정
            port:
              number: 80         # 서비스의 포트 번호

```