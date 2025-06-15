## Record Types
- A
maps a hostname to IPv4 example.com => 1.2.3.4
- AAAA
maps a hostname to IPv6
- CNAME
maps a hostname to another hostname
- NS
Name Servers for the hosted zone

## Hosted Zones
- public hosted zones
외부에서도 접근

- private hosted zones
vpc 내에서만 접근

## Routing Policy

- Latency Based
A 타입의 Record를 여러개 등록했을 때 지연시간 적은 쪽으로 연결

- Failover(Active-Passive)
A 타입의 Record를 여러개 등록했을 때 Primary에서 장애 발생시 Secondary로 연결