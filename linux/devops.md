✅ 핵심 운영체제 및 리눅스 개념
1. 프로세스 및 스레드
ps, top, htop, pidstat 등 명령어
프로세스 상태(R, S, Z 등), PID, PPID, nice/priority 개념
background/foreground, &, jobs, fg, bg, kill
systemd, init, 서비스 관리 (systemctl, service)

2. 시스템 부팅 구조
BIOS → Bootloader → Kernel → init/systemd
runlevel, target, systemctl get-default, journalctl

3. 파일 시스템 및 디렉토리 구조
/etc, /var, /usr, /opt, /home, /proc, /sys, /dev, /tmp
ext4, xfs, NFS 등 파일 시스템 종류
df, du, mount, umount, lsblk, blkid, fdisk

4. 사용자 및 권한
chmod, chown, umask
sudo, /etc/sudoers, visudo
passwd, /etc/passwd, /etc/shadow, /etc/group
ACL, SELinux/AppArmor 개념

5. 네트워킹
ip, ifconfig, netstat, ss, ip a, ip r
포트, IP, DNS, 서브넷, 게이트웨이
방화벽: iptables, firewalld, ufw
curl, ping, telnet, nc, dig
NAT, 포트포워딩, Proxy, Reverse Proxy, Load Balancer 개념

6. 디스크 I/O 및 성능 모니터링
iostat, iotop, vmstat, dstat, free, sar
load average, 메모리 사용량, swap 사용
uptime, top 해석
OOM Killer (dmesg, journalctl -k)

7. 로그 관리
/var/log/ 디렉토리 구조
journalctl, rsyslog, logrotate, ELK 연동 가능성

8. 패키지 관리
Debian 계열: apt, dpkg
RHEL 계열: yum, dnf, rpm
패키지 설치, 의존성 관리, 서비스 관리

9. 보안
SSH 설정: /etc/ssh/sshd_config
공개키/개인키 (~/.ssh/authorized_keys)
포트 제한, 방화벽 설정
루트 권한 관리, 계정 잠금, 로그 감시

✅ DevOps 실무에 자주 연결되는 개념
개념	연관된 DevOps 작업
crontab, systemd timer	자동화 스케줄링 (예: 백업, 로그 정리)
ulimit, sysctl	시스템 튜닝 (동시 연결 수, open file limit)
hostname, /etc/hosts	배포 환경 구성, DNS 우회
env, .bashrc, .profile	환경 변수 설정, 스크립트 자동화
inotify, auditd	파일 변경 감지, 보안 감사
/proc, /sys	시스템 상태 추적 및 커널 파라미터 확인

✅ DevOps를 위한 쉘 명령어 숙련도
텍스트 처리: grep, awk, sed, cut, tr, sort, uniq

파일 압축/해제: tar, zip, gzip, xz

권한 문제 해결: chown, chmod, setfacl

스크립트화: bash, sh, #!/bin/bash, 조건문, 반복문

✅ 커널 및 시스템 튜닝 (심화)
sysctl로 커널 파라미터 조정 (net.ipv4.tcp_tw_reuse 등)

커널 모듈: lsmod, modprobe

cgroup, namespace (컨테이너 기술과 연관)

✅ 데브옵스 실무에서의 활용 예시
상황	관련 리눅스 개념
CI/CD 중 빌드 실패	디스크 용량 확인 (df, du), 메모리 확인 (free, top)
배포 후 접속 불가	포트 열림 확인 (ss, iptables, firewalld)
서비스 죽음	journalctl, systemctl status, 로그 분석 (/var/log)
컨테이너 자원 부족	cgroup, top, docker stats, sysctl 조정