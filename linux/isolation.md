# namespace
프로세스 격리 기술로 컨테이너의 핵심 기술 중 하나
프로세스가 볼 수 있는 리소스를 격리
종류
- mnt: 마운트(파일시스템) 네임스페이스
- pid: 프로세스 id 네임스페이스
- net: 네트워크 인터페이스/라우팅 등 격리
- uts: hostname, domain name 격리
- ipc: 프로세스 간 통신(세마포어, 메시지 큐 등) 격리
- user: UID/GID 격리
- cgroup: 리소스 제어(보통 namespace로는 직접 다루지 않음)
예시: unshare --net --pid bash => 새 네트워크, PID 네임스페이스에서 bash 실행

# chroot
파일시스템 루트 디렉토리 변경(파일시스템 격리)
한 프로세스(또는 그 하위 프로세스)가 특정 디렉토리를 /로 인식하도록 만듦
완전한 격리는 아니며 namespace 보다 단순
예시: chroot /my/jail /bin/bash => /my/jail을 루트(/)로 간주하고 bash 실행

# cgroup(Control Group)
CPU, 메모리, I/O 등의 리소스를 그룹 단위로 제한하거나 모니터링
하나의 프로세스 그룹에 대해 리소스 사용량을 제어
Docker, Kubernetes가 사용하는 리눅스 기능 중 하나
관련 디렉토리: /sys/fs/cgroup/
예시: echo $$ > /sys/fs/cgroup/cpu/mygroup/cgroup.procs

# iptables
패킷 필터링 및 NAT 등 방화벽 역할(네트워크 레벨)
- 커널 네트워크 스택에서 동작하는 패킷 필터링/라우팅 규칙 엔진
- 주로 포트 차단, 허용 IP, 필터링, NAT 등 설정
예시: iptables -A INPUT -p tcp --dport 22 -j ACCEPT

# routes(라우팅 테이블)
목적지 IP에 따라 어느 인터페이스로 패킷을 전달할지 정의
- OS가 어떤 네트워크 인터페이스로 패킷을 전송할지 결정
- 일반적으로 ip route나 route 명령어로 확인/설정
예시: ip route, ip route add 10.0.0.0/24 via 192.168.1.1

| 개념        | 격리 대상      | 사용 목적           | 도구/명령어                        |
| --------- | ---------- | --------------- | ----------------------------- |
| namespace | 프로세스 리소스   | 컨테이너 격리         | `unshare`, `clone`, `nsenter` |
| chroot    | 파일 시스템     | 제한된 디렉토리로 격리    | `chroot`                      |
| cgroup    | 리소스(CPU 등) | 리소스 제한 및 모니터링   | `/sys/fs/cgroup/`             |
| iptables  | 네트워크 패킷    | 방화벽, NAT, 패킷 제어 | `iptables`, `nft`             |
| routes    | 네트워크 경로    | IP 라우팅 결정       | `ip route`, `route`           |
