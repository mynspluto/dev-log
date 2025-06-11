## dependency 추가

```sh
# go kafka 라이브러리 설치 
go get github.com/confluentinc/confluent-kafka-go/kafka
go: downloading github.com/confluentinc/confluent-kafka-go v1.8.2

# kafka 라이브러리 업그레이드 
go get -u github.com/confluentinc/confluent-kafka-go/kafka

# go.mod 파일의 모든 라이브러리 업그레이드
go get -u all
```

## go.mod
의존성 버전 관리
프로젝트가 어떤 모듈/버전을 사용하는지 선언

## go.sum
go.mod에 명시된 의존성의 무결성을 검증하기 위한 체크섬 정보 저장
