elasticsearch와 loki는 로그를 저장할 때 사용

elasticsearch는 인덱스 구조가 역색인
'단어1' -> doc1, doc2, doc3
'단어2' -> doc2, doc4
json 구조로 저장하기에 doc 조회후 정규표현식이나 파싱 로직 불필요요

loki는 라벨에 대한 조합을 해싱하여 해시테이블 구조로 검색 => hash(라벨의 조합) -> log1, log2, log3
hash(log_level: 'ERROR', api_server: 'payment-api-server') - > 'ERROR LOG pament-api-server thread-id-11: payment rejected because ...', '~~', '~~'
라벨의 범위가 무한할 수 없음
log_level: ERROR, INFO, DEBUG
api_server: payment-api-server, push-api-server
user_level: admin, user, vip_user
각 라벨의 조합을 해싱하여 각 조합에 대해 매칭되는 log 조회하는 구조라 조합이 무한하면 해시테이블의 row가 무한해짐 따라서 traceID등으로 라벨링 하는 것 불가능
학년과 반을 라벨링한다면 3학년 7반의 경우 hash(3학년, 7반) => [3학년 7반 1번 학생, 3학년 7반 2번 학생, ~~]로 조회가 됨
하지만 hash(3학년)으로 조회를 한다면 hash(3학년, 1반), hash(3학년, 2반), ~~, hash(3학년 11반)등 모든 조합에 대한 청크를 찾는 구조로 범위 검색에 적합하지 않음
또한 log를 json이 아닌 텍스트 형태로 저장하기에 각 로그에서 필요한 부분을 추출하려면 정규표현식이나 파싱 로직 필요