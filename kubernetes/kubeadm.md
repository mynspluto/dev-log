# kubeadm이란?
kubeadm은 쿠버네티스 클러스터를 손쉽게 "부트스트랩"하는 CLI 도구
즉, 쿠버네티스의 핵심 컴포넌트들(API 서버, 컨트롤러, etcd 등)을 자동으로 설치하고 초기화해주는 유틸리티

# 주요 기능
kubeadm init: 마스터 노드 초기화(컨트롤 플레인 설치)
kubeadm join: 워커 노드를 기존 클러스터에 가입
kubeadm config: 클러스터 설정을 커스터마이징
kubeadm reset: 클러스터 초기화/삭제
kubeadm upgrade: 쿠버네티스 클러스터 버전 업그레이드

# kubeadm이 설치하는 것들
kubeadm init을 실행하면 아래와 같은 핵심 컴포넌트를 설치/구성합니다.
kube-apiserver
kube-controller-manager
kube-scheduler
etcd(내장 또는 외부)
인증서, 키, 토큰 등 초기 보안 구성
kubelet 설정
kubeconfig 파일 생성
CoreDNS 설치(네트워크 이름 해석용)

# kubeadm이 하지 않는 것
CNI 설치: 네트워크 플러그인(Calico, Flannel 등)은 사용자가 따로 설치해야 함
kubelet 설치: kubeadm은 kubelet이 설치돼 있어야 동작함
시스템 튜닝: 방화벽, 포트 오픈, swap 비활성화 등은 수동 필요
로드 밸런서: 멀티 마스터 구성 시 외부 로드배런서는 직접 구성해야 함

# kubeadm을 쓰는 이유
쿠버네티스를 수작업 없이 안정적으로 설치 가능
클러스터 구성의 표준 방식으로, 다른 도구(Minikube, EKS, K3s 등)보다 더 원형에 가까운 설치 방식
실제 운영 환경에서도 사용 가능(다만 프로덕션에서는 약간의 튜닝 필요)

# kubeadm은 필수 컴포넌트가 아님
kubeadm 없이도 쿠버네티스를 설치할 수 있음. 단, 매우 복잡
kubeadm은 설치를 단순화하는 편의 도구이지, 클러스터가 돌아가는 데 꼭 필요한 것은 아님