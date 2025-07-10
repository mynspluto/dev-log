# 컨테이너 런타임
쿠버네티스나 Docker 같은 플랫폼이 컨테이너를 실행하려고 할 때,
실제로 컨테이너 프로세스를 만들고 실행하는 일을 담당하는 게 컨테이너 런타임입니다.

# 요약
컨테이너 런타임: 컨테이너를 생성, 실행, 중지, 삭제하는 소프트웨어
예시: Docker, containerd, CRI-O, runc 등
쿠버네티스와 관계: 쿠버네티스는 컨테이너를 직접 실행하지 않고 런타임을 통해 실행, 쿠버네티스는 컨테이너들을 자동으로 배포하고 관리하는 오케스트레이션 도구, Docker도 Compose 쓰면 어느정도 배포 관리 가능은함

# 계층 구조로 이해
쿠버네티스 => Container Runtime Interface(CRI) => Container Runtime(e.g., containerd, CRI-O) => 저수준 런타임(e.g, runc)
- 쿠버네티스는 CRI를 통해 컨테이너 런타임과 통신
- containerd, CRI-O 같은 런타임이 컨테이너를 실제 실행
- runc는 리눅스에서 컨테이너를 실행하는 가장 기본적인 런타임(containerd나 Docker가 내부적으로 사용)

# 주요 컨테이터 런타임
Docker: 예전에는 가장 널리 쓰인 런타임. containerd 기반
containerd: Docker에서 분리된 고성능 컨테이너 런타임, CNCF 프로젝트
CRI-O: Kubernetes에 최적화된 컨테이너 런타임(Redhat 주도)
runc: 리눅스에서 OCI 스펙을 따라 실제 컨테이너 프로세스를 실행
gVisor, Kata Containers: 보안 강화 목적의 특수한 런타임들

# containerd와 runc 관계
containerd는 고수준 런타임이고, 내부적으로 runc를 호출해서 컨테이너 실행
즉 containerd는 컨테이너 이미지 관리, 네트워크 설정, 컨테이너 라이프사이클을 관리하고
runc는 실제로 fork/exec해서 프로세스를 분리하고, 리눅스 네임스페이스/컨트롤 그룹을 적용