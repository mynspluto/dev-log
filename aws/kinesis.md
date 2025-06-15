# Kinesis
실시간 데이터 수집, 처리, 분석
Data Streams, Data Firhose, Data Analytics, Video Stream 등으로 구성

## Data Streams
데이터 수집에 사용
여러 개의 샤드로 구성, 각자 번호를 가짐
개발자가 처리 로직 직접 구현
샤드 수, 리소스 개발자가 조정
데이터 저장 24시간 ~ 365일
producer, consumer 사이에 위치
producer: Applications, Client, SDK, Kinesis Agent
consumers: EC2, Lambda, Kinesis Data Firehose, Kinesis Data Analytics

## Firehose
데이터 적재에 사용, Firehose에 저장하진 않고 대상(S3, Redshift 등)에 전달
Data Streams로부터 받은 데이터를 Batch Writes로 S3, Redshift, OpenSearch 등에 저장
이때 배치 처리 로직 신경안써도 됨
데이터 저장 안함
Datadog, MongoDB, HTTP Endpoint 전달도 가능
Fully Managed로 스케일링 신경 안써도 됨 serverless임

## Data Streams, Firehose
데이터 전처리, 실시간 분석을 안할거면 firehose로 충분
로그, 이벤트 등만 적재하는 경우 firehose로 충분
데이터별 분기처리, 변환, 처리 로직 필요한 경우 streams 필요

## Data Analytics
Data Streams, Data Firehose를 소스로 데이터를 가져옴
SQL로 실시간 분석 가능
S3 등에서 데이터 참조 가능
for SQL, for Apache Flink 두 종류로 구분
Data Streams, Data Firehose로 분석 결과 전송
Data Streams로 전송하는 경우 Lambda, Application가 대상
Data Firebase로 전송하는 경우 S3, Redshift가 대상