### 자바 23 에러
Caused by: java.lang.IllegalArgumentException: Java 23 (67) is not supported by the current version of Byte Buddy which officially supports Java 22 (66) - update Byte Buddy or set net.bytebuddy.experimental as a VM property

### 종속성 호환 에러
testImplementation 'org.springframework.boot:spring-boot-starter-test'만 있으면 

밑의 목록 전부 필요 없음
//	testImplementation 'org.junit.jupiter:junit-jupiter-api:5.10.1'
//	testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
//	testImplementation 'org.mockito:mockito-junit-jupiter:5.7.0'
//	testImplementation 'org.mockito:mockito-core:5.7.0'
//
//	testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.10.1'

## RepositoryTest 실패
testImplementation 'com.h2database:h2' 추가 해야됨
application-properties에서 spring.datasource.url의 왼쪽 데이터베이스 모양 아이콘 클릭하여 db 생성해야함