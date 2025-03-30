# MongoDB 레플리카 셋 가이드

## MongoDB 기본 명령어

### mongod
- 서버
- 사용예시
  - `& "C:\Program Files\MongoDB\Server\8.0\bin\mongod.exe" --replSet rs0 --dbpath="c:\data\db" --port 27017`
  - watch 사용을 위해 시작시 `--replSet rs0` 옵션 필요

### mongosh
- 클라이언트
- 사용예시
  - `& "C:\Program Files\MongoDB\mongosh-2.4.2-win32-x64\bin\mongosh.exe" --port 27017`
  - 접속 후 watch 사용을 위해
    ```sh
    rs.initiate({
      _id: "rs0",
      members: [
        { _id: 0, host: "localhost:27017" }
      ]
    })
    ```

## MongoDB 레플리카 셋

### 레플리카 셋의 기본 구성
- **최소 3개 노드**: 일반적으로 1개의 Primary 노드와 2개 이상의 Secondary 노드로 구성
- **홀수 개수의 노드**: 과반수(majority) 투표를 통한 선출 메커니즘을 사용하기 때문에 홀수 개수가 권장됨

### 레플리카 셋 사용 이유
- **고가용성(High Availability)**:
  - Primary 노드가 실패할 경우 자동으로 새로운 Primary를 선출
  - 서비스 중단 없이 지속적인 운영 가능

- **데이터 안정성(Data Safety)**:
  - 데이터가 여러 노드에 복제되어 저장
  - 하드웨어 장애 시에도 데이터 손실 방지

- **읽기 확장성(Read Scalability)**:
  - Secondary 노드에서 읽기 작업 분산 가능
  - 읽기 부하 분산으로 성능 향상

- **재해 복구(Disaster Recovery)**:
  - 다른 데이터 센터에 복제본 배치 가능
  - 지역적 장애에도 대응 가능

- **백업과 유지보수**:
  - 다운타임 없이 백업 및 유지보수 작업 수행 가능

- **특수 기능 지원**:
  - Change Streams(실시간 데이터 변경 감지)와 같은 기능은 레플리카 셋에서만 사용 가능
  - 이 설정의 주요 목적은 MongoDB의 Change Streams 기능 활성화
  - Change Streams이란 데이터 변경 사항을 실시간으로 감지하여 이에 반응할 수 있게 하는 기능
  - 프로덕션 환경에선 여러 멤버로 구성된 레플리카 셋 구성, 학습 목적으로는 단일 인스턴스 레플리카로 충분

### 데이터 복제 메커니즘
- Primary 노드에 쓰기 작업이 발생하면 해당 작업은 oplog(operation log)에 기록
- Secondary 노드들은 Primary의 oplog를 지속적으로 복제하여 자신의 데이터에 적용
- 이를 통해 모든 노드가 동일한 데이터 상태 유지

### 데이터 복제의 목적
- **데이터 중복성**: 여러 서버에 동일한 데이터를 저장하여 하드웨어 장애 시 데이터 손실 방지
- **고가용성**: 한 서버가 다운되더라도 다른 서버에서 서비스 계속 제공
- **부하 분산**: 읽기 작업을 여러 서버로 분산하여 성능 향상
- **백업 및 재해 복구**: 지리적으로 분산된 노드를 구성하여 재해 상황에도 대비

### 복제 지연(Replication Lag)
- Secondary 노드가 Primary의 변경사항을 따라잡는 데 걸리는 시간을 의미
- 네트워크 상태, 서버 부하 등에 따라 달라질 수 있음
- 중요한 모니터링 지표로 사용됨