# 리눅스 파일시스템 구조

## 주요 디렉토리별 용도

| 디렉토리 | 용도 | DevOps 관련 포인트 |
|----------|------|-------------------|
| `/` | 루트 디렉토리 | 모든 디렉토리의 최상위, 파일시스템 마운트 포인트 |
| `/bin` | 기본 실행 파일 | 시스템 부팅과 복구에 필요한 명령어 (ls, cp, mv, bash 등) |
| `/sbin` | 시스템 관리자용 실행 파일 | root 권한 필요한 명령어 (mount, ifconfig, iptables 등) |
| `/lib` | 공유 라이브러리 | 시스템 부팅과 /bin, /sbin 실행에 필요한 라이브러리 |
| `/usr` | 사용자 프로그램 | 대부분의 애플리케이션과 유틸리티 설치 위치 |
| `/usr/bin` | 사용자 실행 파일 | 일반 사용자용 명령어 (git, docker, kubectl 등) |
| `/usr/sbin` | 비필수 시스템 관리 명령어 | 시스템 관리용 추가 도구들 |
| `/usr/local` | 직접 설치 프로그램 경로 | 소스 빌드 후 이 경로 사용 |
| `/usr/share` | 아키텍처 독립적 데이터 | 문서, 맨페이지, 설정 템플릿 등 |
| `/opt` | 외부 소프트웨어 설치 경로 | Java, ELK 등 비표준 프로그램 |
| `/etc` | 설정 파일 | nginx.conf, sshd_config, fstab, crontab |
| `/var` | 가변 데이터 | 로그, 스풀, 캐시, 데이터베이스 등 |
| `/var/log` | 시스템 로그 | syslog, messages, dmesg, auth.log 등 |
| `/var/spool` | 큐 파일 | cron, at, mail 등 |
| `/var/lib` | 애플리케이션 데이터 | mysql, docker, kubernetes 등의 데이터 저장소 |
| `/var/cache` | 캐시 데이터 | 애플리케이션 임시 캐시 파일들 |
| `/home` | 사용자 홈 디렉토리 | 각 사용자별 개인 디렉토리 |
| `/root` | root 사용자 홈 | 시스템 관리자 계정의 홈 디렉토리 |
| `/tmp` | 임시 파일 | 재부팅 시 삭제되는 임시 파일들 |
| `/dev` | 디바이스 파일 | 하드웨어 디바이스 인터페이스 (/dev/sda, /dev/null 등) |
| `/proc` | 프로세스/커널 정보 | cat /proc/cpuinfo, /proc/meminfo, /proc/[pid]/ |
| `/sys` | 커널-하드웨어 인터페이스 | CPU scaling, block info 등 |
| `/run` | PID, 소켓 | 서비스 상태 확인 시 활용 (*.pid, *.sock) |
| `/boot` | 부트로더 파일 | 커널 이미지, initrd, GRUB 설정 |
| `/mnt` | 임시 마운트 포인트 | 외부 스토리지 임시 마운트 |
| `/media` | 미디어 자동 마운트 | USB, CD/DVD 등 자동 마운트 |

## DevOps 중요 경로

### 설정 관리
- `/etc/`: 시스템 및 서비스 설정
- `/etc/systemd/system/`: systemd 서비스 정의
- `/etc/nginx/`: nginx 설정
- `/etc/docker/`: Docker 설정

### 로그 및 모니터링
- `/var/log/`: 모든 시스템 로그
- `/var/log/nginx/`: nginx 로그
- `/var/log/mysql/`: MySQL 로그
- `/proc/`: 실시간 시스템 정보

### 애플리케이션 데이터
- `/var/lib/docker/`: Docker 데이터
- `/var/lib/mysql/`: MySQL 데이터
- `/opt/`: 서드파티 애플리케이션

### 임시 및 캐시
- `/tmp/`: 임시 파일 (재부팅 시 삭제)
- `/var/tmp/`: 영구적 임시 파일
- `/var/cache/`: 애플리케이션 캐시

## 디렉토리명 약자

### 주요 약자 설명
| 디렉토리 | 약자 | 의미 | 설명 |
|----------|------|------|------|
| `/opt` | optional | 옵셔널 | 시스템 기본 패키지가 아닌 선택적 소프트웨어 |
| `/var` | variable | 베리어블 | 시스템 운영 중 계속 변화하는 가변 데이터 |
| `/bin` | binary | 바이너리 | 이진 실행 파일 |
| `/lib` | library | 라이브러리 | 공유 라이브러리 파일 |
| `/etc` | et cetera | 기타 | 설정 파일들 (라틴어) |
| `/usr` | Unix System Resources | 유닉스 시스템 자원 | 사용자 프로그램 및 데이터 |
| `/tmp` | temporary | 템포러리 | 임시 파일 |
| `/dev` | device | 디바이스 | 하드웨어 디바이스 파일 |
| `/proc` | process | 프로세스 | 프로세스 및 커널 정보 |
| `/sys` | system | 시스템 | 커널-하드웨어 인터페이스 |

### 특별한 경우
- `/etc`: 원래는 "et cetera"(기타)의 라틴어 약자
- `/usr`: 과거 "user"로 알려졌지만 실제로는 "Unix System Resources"
- `/var`: 시스템 운영 중에 내용이 계속 변하는(variable) 디렉토리들

## 실제 소프트웨어 설치 위치

### 패키지 관리자 vs 수동 설치

#### 패키지 관리자 설치 (apt, yum, dnf 등)
```bash
# MySQL을 패키지 관리자로 설치하는 경우
sudo apt install mysql-server  # Ubuntu/Debian
sudo yum install mysql-server   # CentOS/RHEL
```

**설치 위치:**
- **실행 파일**: `/usr/bin/mysql`, `/usr/sbin/mysqld`
- **설정 파일**: `/etc/mysql/` 또는 `/etc/my.cnf`
- **데이터 파일**: `/var/lib/mysql/` (데이터베이스 파일들)
- **로그 파일**: `/var/log/mysql/`
- **임시 파일**: `/tmp/` 또는 `/var/tmp/`

#### 수동/바이너리 설치
```bash
# MySQL을 수동으로 설치하는 경우
wget https://dev.mysql.com/get/Downloads/MySQL-8.0/mysql-8.0.x-linux-glibc2.12-x86_64.tar.xz
tar -xf mysql-8.0.x-linux-glibc2.12-x86_64.tar.xz -C /opt/
```

**설치 위치:**
- **전체 프로그램**: `/opt/mysql/` (모든 파일이 한 곳에)

### `/opt` vs `/usr` vs `/var` 구분

| 구분 | 설치 위치 | 사용 사례 | 예시 |
|------|-----------|-----------|------|
| **패키지 관리자** | `/usr/*` | 리눅스 배포판 공식 패키지 | MySQL, Nginx, Docker |
| **수동 설치** | `/opt/*` | 서드파티, 상용 소프트웨어 | Oracle Database, IntelliJ IDEA |
| **소스 빌드** | `/usr/local/*` | 직접 컴파일한 소프트웨어 | 커스텀 빌드 소프트웨어 |

### 데이터 vs 프로그램 분리

#### MySQL 예시
```
프로그램 파일들:
├── /usr/bin/mysql          # MySQL 클라이언트
├── /usr/sbin/mysqld        # MySQL 서버 데몬
├── /etc/mysql/my.cnf       # 설정 파일
└── /usr/share/mysql/       # 공유 파일들

데이터 파일들:
├── /var/lib/mysql/         # 실제 데이터베이스 파일들
├── /var/log/mysql/         # 로그 파일들
└── /run/mysqld/            # PID, 소켓 파일들
```

#### Docker 예시
```
프로그램 파일들:
├── /usr/bin/docker         # Docker 클라이언트
├── /usr/bin/dockerd        # Docker 데몬
└── /etc/docker/            # 설정 파일들

데이터 파일들:
├── /var/lib/docker/        # 이미지, 컨테이너 데이터
└── /var/log/docker/        # 로그 파일들
```

### 주요 원칙

1. **프로그램 실행 파일**: `/usr/bin`, `/usr/sbin` (패키지 관리자 설치)
2. **설정 파일**: `/etc/`
3. **가변 데이터**: `/var/lib/`, `/var/log/`, `/var/cache/`
4. **서드파티 소프트웨어**: `/opt/` (전체 패키지)
5. **사용자 빌드**: `/usr/local/`

### `/opt`에 설치되는 대표적인 소프트웨어
- **Oracle Database**: `/opt/oracle/`
- **JetBrains IDE**: `/opt/idea/`, `/opt/pycharm/`
- **Elasticsearch**: `/opt/elasticsearch/`
- **커스텀 애플리케이션**: `/opt/myapp/`
- **상용 소프트웨어**: `/opt/vendor-software/`

## 설치 방법 요약

### 1. 패키지 관리자 설치 (권장)

**특징:**
- ✅ 의존성 자동 해결
- ✅ 자동 업데이트 지원
- ✅ 시스템 통합 (systemd 서비스 등)
- ✅ 보안 패치 자동 적용

**명령어:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server nginx docker.io

# CentOS/RHEL/Rocky
sudo yum install mysql-server nginx docker
sudo dnf install mysql-server nginx docker  # RHEL 8+

# Arch Linux
sudo pacman -S mysql nginx docker
```

**설치 위치:**
- 실행파일: `/usr/bin/`, `/usr/sbin/`
- 설정: `/etc/`
- 데이터: `/var/lib/`, `/var/log/`

### 2. 수동/바이너리 설치

**특징:**
- ✅ 최신 버전 사용 가능
- ✅ 커스텀 설정 유지
- ❌ 의존성 수동 관리
- ❌ 업데이트 수동 진행

**명령어:**
```bash
# MySQL 바이너리 설치 예시
wget https://dev.mysql.com/get/Downloads/MySQL-8.0/mysql-8.0.x-linux.tar.xz
sudo tar -xf mysql-8.0.x-linux.tar.xz -C /opt/
sudo ln -s /opt/mysql-8.0.x /opt/mysql

# Java 설치 예시
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz
sudo tar -xf jdk-17_linux-x64_bin.tar.gz -C /opt/
sudo ln -s /opt/jdk-17.x.x /opt/java
```

**설치 위치:**
- 전체: `/opt/소프트웨어명/`

### 3. 소스 빌드 설치

**특징:**
- ✅ 최대 커스터마이징 가능
- ✅ 특정 기능 활성화/비활성화
- ❌ 컴파일 시간 소요
- ❌ 의존성 복잡

**명령어:**
```bash
# Nginx 소스 빌드 예시
wget http://nginx.org/download/nginx-1.x.x.tar.gz
tar -xf nginx-1.x.x.tar.gz
cd nginx-1.x.x
./configure --prefix=/usr/local/nginx --with-http_ssl_module
make
sudo make install
```

**설치 위치:**
- 전체: `/usr/local/`

### 4. 컨테이너 설치 (Docker)

**특징:**
- ✅ 격리된 환경
- ✅ 포터빌리티
- ✅ 쉬운 버전 관리
- ❌ 오버헤드 존재

**명령어:**
```bash
# MySQL 컨테이너 실행
docker run -d --name mysql-server \
  -e MYSQL_ROOT_PASSWORD=password \
  -v /var/lib/mysql:/var/lib/mysql \
  mysql:8.0

# Nginx 컨테이너 실행
docker run -d --name nginx-server \
  -p 80:80 \
  -v /etc/nginx:/etc/nginx:ro \
  nginx:latest
```

**설치 위치:**
- 컨테이너 데이터: `/var/lib/docker/`
- 마운트 볼륨: 사용자 지정

### 5. Snap/Flatpak 설치

**특징:**
- ✅ 샌드박스 환경
- ✅ 자동 업데이트
- ✅ 크로스 플랫폼
- ❌ 용량 많이 차지

**명령어:**
```bash
# Snap 설치
sudo snap install code docker kubectl

# Flatpak 설치
flatpak install flathub org.mozilla.firefox
```

**설치 위치:**
- Snap: `/snap/`
- Flatpak: `/var/lib/flatpak/`

## 설치 방법 선택 가이드

| 상황 | 권장 방법 | 이유 |
|------|-----------|------|
| **운영 서버** | 패키지 관리자 | 안정성, 보안 패치 |
| **개발 환경** | Docker/패키지 관리자 | 빠른 설정, 버전 관리 |
| **최신 버전 필요** | 수동 설치 | 공식 패키지보다 최신 |
| **특수 기능 필요** | 소스 빌드 | 커스텀 옵션 |
| **테스트/실험** | Docker | 격리된 환경 |
| **데스크톱 앱** | Snap/Flatpak | 샌드박스, 편의성 |

# Elasticsearch 데이터 어디에 저장되는 지에 대한 답변시 심화 질문
- 백업 의도
"그럼 백업은 어떻게 해요?"
"백업 주기는 어떻게 설정하나요?"
"스냅샷이 뭐예요? 단순 파일 복사랑 다른가요?"
"복구할 때는 어떻게 해요?"

- 용량 관리 의도
"용량이 계속 늘어나는데 어떻게 관리해요?"
"어떤 인덱스가 용량을 많이 차지하나요?"
"오래된 데이터는 어떻게 정리해요?"
"샤드 크기는 어떻게 최적화하나요?"

- 마이그레이션 의도
"다른 서버로 옮기려면 어떻게 해요?"
"무중단으로 마이그레이션 가능한가요?"
"클러스터 설정도 함께 복사해야 하나요?"
"플러그인이나 템플릿도 옮겨야 하나요?"

- 성능 이슈 의도
"디스크 I/O가 높은데 최적화 방법이 있나요?"
"샤드 개수는 어떻게 설정해야 하나요?"
"SSD vs HDD 어떤 게 좋나요?"
"메모리는 얼마나 할당해야 하나요?"

- 설정 변경 의도
"데이터 경로를 바꾸려면 어떻게 해요?"
"기존 데이터는 어떻게 옮기나요?"
"설정 변경 후 재시작해야 하나요?"
"권한 설정은 어떻게 해요?"

- 트러블 슈팅 의도
"인덱스가 red 상태인데 어떻게 복구해요?"
"샤드가 unassigned 상태예요"
"로그에서 뭘 봐야 하나요?"
"디스크 풀이라고 나오는데?"

- 상황별 체크리스트 준비
백업 → 스냅샷, ILM, 복구 절차
용량 → 모니터링, 정리, 최적화
마이그레이션 → reindex, 설정 복사
성능 → 튜닝, 하드웨어, 모니터링