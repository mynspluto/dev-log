# SSM
Systems Manager
공통 노드 작업을 중앙에서 관리하도록 SSM Agent가 ec2 등에 설치되어있고 이를 중앙에서 관리

## run command
ec2 인스턴스의 내부 작업
ec2를 태그 등으로 그룹화하여 종속성을 설치하거나 특정 작업을 한번에 작업

## automations
ec2 뿐 아니라 AWS 전체 서비스 대상
복잡한 작업 시나리오나 반복적인 운영 작업 자동화 airflow dag와 비슷

## session manager
aws 웹 내에서 터미널 쓰게 하는 것
ec2에 ssh 없이 접속하여 ssh 포트를 완전히 닫을 수 있음

## Default Host Management Configuration
EC2 인스턴스를 자동으로 SSM에 등록하고 관리할 수 있또록 하는 기본 구성 템플릿
EC2 인스턴스 생성시 설정 가능

## Hybrid Enviroment
온프레미스 서비스를 AWS EC2 처럼 SSM으로 관리할 수 있게 해주는 기능
AWS 외부의 온프레미스 서버임에도 Session Manager, Run Command 등의 기능 사용할 수 있게 됨

## IoT Greengrass
로컬 디바이스(엣지 디바이스)에서도 AWS 서비스를 실행할 수 있게 하는 엣지 컴퓨팅 플랫폼
amazon-ssm-agent를 apt 등으로 받아서 cli로 해당 클라이언트 SSM에 등록

## Compliance
Managed Instances가 특정 규칙(패치 적용 여부, 설정 값) 등을 준수하고 있는지 자동으로 평가
평가 결과는 SSM 콘솔이나 API를 통해 확인 가능

## OpsCenter
SSM에서 실행한 Run Command, Authmation 작업 등의 운영 관련 이벤트와 문제(OpsItem)를 중앙에서 모니터링 및 관리