# VM vs container

VM 메모리 할당 예시
- 메모리 할당 호출 => VM내 운영체제 커널 호출 => 호스트 커널 호출
- 하이퍼바이저가 VM 커널과 호스트 커널 연결

container 메모리 할당 예시
- 메모리 할당 호출 => 호스트 커널 호출
- 컨테이너 런타임이 VM 운영체제 커널 호출 없이 바로 호스트 커널 호출하도록 해줌

# 컨트롤 플레인 구성 요소

kube-apiserver
클러스터의 유일한 API 진입점
모든 컴포넌트가 이 API를 통해 소통함(REST 기반)
모든 Kubernetes 요청이 통과하는 프론트 도어
kubectl, 다른 컴포넌트, 외부 클라이언트 모두 이 API를 통해 클러스터와 상호작용
인증/인가, 유효성 검사, 요청 처리 등도 담당

etcd
분산 키-값 저장소(Raft consensus 기반)
모든 클러스터 상태/구성 정보 저장(파드, 서비스, 시크릿, 설정 등)
kube-apiserver가 읽고 쓰는 대상

kube-scheduler
Pending 상태의 파드에게 적절한 노드를 할당
파드가 생성되면 초기엔 어느 노드에도 배치되지 않은 상태(Pending)
스케줄러가 파드의 리소스 요청, 제약 조건, 현재 노드 상태 등을 고려하여 적절한 노드를 선택하고 할당

kube-controller-manager
다양한 컨트롤러를 실행하여 리소스의 실제 상태를 원하는 상태로 맞춤(예: ReplicaSet, Node, Namespace 등)
여러 컨트롤러를 실행하는 관리자 컴포넌트(예: ReplicaSet Controller, nodeController, jobController)
실제 상태 != 원하는 상태일 때 자동으로 수정(예: Pod 3개 있어야 하는데 2개만 있으면 1개 추가 생성)

cloud-controller-manager
클라우드 공급자 API와 연동하여 노드, 로드밸런서, 볼륨 등 클라우드 리소스를 자동 제어
클라우드 제공자 API와 연동(예: AWS, GCP, Azure)
다음 리소스 자동 처리
- 노드 등록 시, 클라우드 API 통해 존재 확인
- Service 타입 LoadBalancer 생성 => 클라우드 Load Balancer 생성
- 볼륨 마운트, 디스크 동적 프로비저닝 등

# 노드 구성 요소

kubelet - 실행 관리자
해당 노드에 할당된 파드 정의(PodSpec)를 받아서 컨테이너를 실행함
실행 중인 파드가 원하는 상태를 유지하도록 모니터링
kube-apiserver와 통신하여 상태 보고 및 명령 수신
파드 상태, 컨테이너 상태, 헬스 체크 등도 수행
예: 파드 2개 띄워야 한다는 지시를 받으면 컨테이너 런타임을 호출해서 실제로 띄움

kube-proxy - 트래픽 분배기
클러스터 내 service => pod 간의 라우팅 담당
iptables, ipvs 등을 사용해서 트래픽을 올바른 pod로 전달
ClusterIP, NodePort, LoadBalancer 타입 서비스도 여기서 처리
일부 상황에서는 외부에서 들어온 요청도 적절한 Pod로 라우팅
예: my-service:80으로 들어온 요청을 그 서비스에 연결된 Pod 중 하나로 분산시켜 전달

container runtime - 컨테이너 실행기
실제로 컨테이너를 생성, 시작, 중지, 삭제하는 프로그램
kubelet이 명령을 내려서 컨테이너를 띄움
대표적인 런타임
- containerd
- CRI-O
- Docker
예: kubelet이 Ubuntu 기반 파드 실행해줘하면 containerd가 컨테이너 생성 및 실행

# addons

DNS
Web UI
Container Resource Monitoring
Cluster-level Logging