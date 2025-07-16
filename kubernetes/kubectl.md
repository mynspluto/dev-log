# kubectl apply, create 등 내부 요청 흐름
kubectl 명령 실행
API Server에 HTTP 요청(kubectl => kube-apiserver)
인증 => 누가? 
인가(RBAC 등) => 권한 있는가?
어드미션 컨트롤(Mutating Webhook: 수정, Validating Webhook: 검사)
etcd에 리소스 저장
해당 리소스 감시 중이던 컨트롤러가 동작(예: DeploymentController)
실제 리소스(Pod, Replicaset 등) 생성

kubectl apply
   ↓
kube-apiserver
   ├─ 인증(Authentication)
   ├─ 인가(Authorization - RBAC)
   ├─ 어드미션 컨트롤(Mutating/Validating Webhook)
   ↓
etcd 저장
   ↓
컨트롤러 매니저가 감지 → 리소스 생성
   ↓
스케줄러가 Pod 배치
   ↓
kubelet이 컨테이너 생성


## 어드미션 컨트롤의 웹훅
              Mutating Webhook                              Validating Webhook
목적	          리소스를 자동 수정 (보완)	                        리소스를 검사/검증

예시	          - 사이드카 자동 삽입                             - privileged 컨테이너 차단
               - 라벨 자동 추가	                               - 라벨 누락 검사

응답 형식	       patch를 반환	                                  allowed: true/false를 반환

실행 순서	        항상 먼저 실행	                                Mutating 후에 실행

필수 여부	        아니지만 있으면 유용함	                          정책 강제에 필수