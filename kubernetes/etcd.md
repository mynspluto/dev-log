# etcd에서 lock
etcd에서 사용하는 락(lock) 수준은 일반적인 데이터베이스의 row-level lock, table-level lock과는 조금 다르며, **낙관적 락(optimistic locking)**과 분산 락(distributed lock) 모델을 동시에 제공합니다.

🧠 1. etcd의 내부 락 수준: Key-level Optimistic Locking
etcd는 기본적으로 락을 "키(key) 단위"로 관리합니다.

✅ 특징
항목	설명
락 범위	Key 또는 Key Range (/foo, /foo/*)
락 방식	낙관적 락 (Compare-And-Swap, resourceVersion)
병렬 처리	서로 다른 키는 동시에 수정 가능
충돌 감지	수정 시 revision 번호 기반 충돌 검사

예시:
클라이언트 A와 B가 /pods/nginx를 동시에 수정하면, etcd는 mod_revision을 비교해서 충돌 감지

A는 성공, B는 실패 → 재시도 필요

✔️ 각 키의 수정은 atomic하게 처리되고, 동시 접근 시 충돌이 감지됩니다.

🔐 2. etcd를 사용한 분산 락 시스템 구현: Distributed Mutex
etcd는 분산 락 시스템도 자체 제공하며, 이때는 "lease 기반 락" 또는 **"세션 락"**이라고 불립니다.

✅ 주요 API: etcdctl lock, clientv3/concurrency
lock은 key로 표현되고, lease와 함께 걸립니다.

락을 획득한 클라이언트가 종료되면 lease가 만료되어 자동 해제

예: 여러 클라이언트 중 하나만 특정 작업을 실행해야 할 때 사용

🔍 etcd 락 수준 요약
관점	설명
기본 동작	key-level optimistic locking (revision 비교)
동시 수정 감지	resourceVersion 또는 revision mismatch
분산 락 제공	lease + key 기반 분산 락 API
병렬 처리	서로 다른 key는 독립적으로 처리 가능

🎯 Kubernetes와 연관 지어 보면?
Kubernetes의 리소스는 etcd의 key-value 데이터입니다.

리소스를 수정할 때 resourceVersion이 바뀌므로, 낙관적 락 기반 충돌 감지를 통해 안정적인 분산 처리가 가능합니다.

리더 선출 등은 etcd의 분산 락 API를 이용합니다.

# 전용 vs 공동 배치
전용: etcd를 마스터 노드와 분리해서 별도의 노드에 배치
공통: 마스터 노드안에 kube-apiserver, controller-manager, scheduler 등과 같이 static pod로 etcd도 같이 보관

# 네트워크 고려사항
etcd의 기본 설정은 단일 테이터 센터의 대기 시간을 위해 설계
여러 데이터센터에 etcd를 배포할 때, etcd 멤버 간의 왕복 시간과 하드비트 설정 필요
다른 리전에 분산된 etcd 클러스터는 사용하지 않는 것이 좋다. 가능한 가까운 거리에 있는 것이 좋음