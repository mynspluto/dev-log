## 가비지 컬렉션(GC)이란?
- JVM에서 더 이상 참조되지 않는 객체를 찾아서 자동으로 메모리에서 해제하는 메커니즘
- 메모리 누수 방지 및 프로그램 안정성 향상
- 개발자가 직접 메모리 해제를 관리하지 않아도 됨

## GC 대상이 되는 경우
- **더 이상 참조되지 않는 객체**
- **도달할 수 없는 객체** (root 객체부터 참조 경로가 끊어진 객체)
- ex) 지역 변수 범위를 벗어난 객체, null 할당된 객체

### 코드 예시 - GC 대상이 되는 경우

```java
public class GCExample {
    public static void main(String[] args) {
        // 1. 참조 변수가 없는 객체 (Anonymous Object)
        new String("이 객체는 생성 직후 GC 대상이 됩니다");
        
        // 2. null 할당으로 인한 참조 끊김
        String str = new String("이 객체는 곧 GC 대상이 됩니다");
        str = null;  // 원래 참조하던 String 객체는 GC 대상이 됨
        
        // 3. 지역 변수 범위를 벗어난 객체
        createObject();  // 메서드 종료 후 내부 객체는 GC 대상이 됨
        
        // 4. 새로운 객체 할당으로 기존 참조 끊김
        String oldStr = new String("원래 객체");
        oldStr = new String("새 객체");  // "원래 객체" String은 GC 대상이 됨
        
        // 5. 순환 참조이지만 외부에서 참조가 끊긴 객체
        Island island1 = new Island();
        Island island2 = new Island();
        island1.ref = island2;  // island1이 island2를 참조
        island2.ref = island1;  // island2가 island1을 참조 (순환 참조)
        
        island1 = null; 
        island2 = null;  // 외부 참조가 끊겨서 두 객체 모두 GC 대상이 됨
        
        System.gc();  // GC 실행 요청 (보장은 안됨)
    }
    
    static void createObject() {
        Object localObj = new Object();  // 이 객체는 메서드 종료 시 GC 대상이 됨
    }
    
    static class Island {
        Island ref;
    }
}

public class MemoryExample {
    public static void main(String[] args) {
        // 스택: main 메서드의 스택 프레임 생성
        // 스택에 count 변수 저장 (값: 5)
        int count = 5;
        
        // 힙: Person 객체 생성
        // 스택: person 참조 변수는 스택에 저장됨
        // 힙: "John"이라는 String 객체도 힙에 생성됨
        Person person = new Person("John");
        
        // 메서드 호출 시 processData의 새 스택 프레임 생성
        // 매개변수 p(참조)와 c(값)가 스택에 복사됨
        processData(person, count);
        
        // processData 메서드가 종료되면 스택 프레임이 제거됨
        // person 객체는 여전히 힙에 있고 main 메서드에서 참조 중
        System.out.println(person.getName());  // "John (processed)"
        System.out.println(count);  // 여전히 5 (값 전달이므로 변경 안됨)
    }
    
    static void processData(Person p, int c) {
        // 스택: p(참조), c(값), temp 변수들이 프레임에 저장
        String temp = p.getName() + " (processed)";
        p.setName(temp);  // 힙의 Person 객체 수정
        
        c = c + 10;  // 스택의 지역 변수 c만 수정됨, 호출자의 count는 변경 안됨
        
        // 다른 새 객체 생성
        Person anotherPerson = new Person("Temporary");
        // 메서드 종료 시 anotherPerson에 대한 참조가 사라져 GC 대상이 됨
    }
    
    static class Person {
        private String name;  // 인스턴스 변수는 힙에 저장
        
        public Person(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
}
```

## 메모리 영역 - 힙과 스택
- **힙 (Heap)**
  - 객체가 동적으로 생성되는 영역
  - GC의 관리 대상
- **스택 (Stack)**
  - 메서드 호출 시 생성되는 프레임, 지역 변수 저장
  - 메서드 종료 시 자동 해제 (GC 관리 대상 아님)

### 코드 예시 - 힙과 스택
```java
public class MemoryExample {
    public static void main(String[] args) {
        // 스택: main 메서드의 스택 프레임 생성
        // 스택에 count 변수 저장 (값: 5)
        int count = 5;
        
        // 힙: Person 객체 생성
        // 스택: person 참조 변수는 스택에 저장됨
        // 힙: "John"이라는 String 객체도 힙에 생성됨
        Person person = new Person("John");
        
        // 메서드 호출 시 processData의 새 스택 프레임 생성
        // 매개변수 p(참조)와 c(값)가 스택에 복사됨
        processData(person, count);
        
        // processData 메서드가 종료되면 스택 프레임이 제거됨
        // person 객체는 여전히 힙에 있고 main 메서드에서 참조 중
        System.out.println(person.getName());  // "John (processed)"
        System.out.println(count);  // 여전히 5 (값 전달이므로 변경 안됨)
    }
    
    static void processData(Person p, int c) {
        // 스택: p(참조), c(값), temp 변수들이 프레임에 저장
        String temp = p.getName() + " (processed)";
        p.setName(temp);  // 힙의 Person 객체 수정
        
        c = c + 10;  // 스택의 지역 변수 c만 수정됨, 호출자의 count는 변경 안됨
        
        // 다른 새 객체 생성
        Person anotherPerson = new Person("Temporary");
        // 메서드 종료 시 anotherPerson에 대한 참조가 사라져 GC 대상이 됨
    }
    
    static class Person {
        private String name;  // 인스턴스 변수는 힙에 저장
        
        public Person(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
}
```


 ## GC 청소 방식
1. **Mark & Sweep**
  - 사용 중인 객체를 표시(Mark) → 사용하지 않는 객체를 제거(Sweep)
2. **Mark & Compact**
  - Mark & Sweep 후 **메모리 단편화 제거**를 위해 살아있는 객체를 압축(Compact)
3. **Copying**
  - 객체를 한 영역에서 다른 영역으로 복사 → 참조 업데이트
4. **Generational**
  - 객체의 생존 주기에 따라 **영역(Young, Old) 분리하여 관리**

## GC 동작 과정
1. **Stop-The-World**
  - GC 수행 시 모든 애플리케이션 스레드 일시 정지
2. **Mark 단계**
  - GC Root에서 도달 가능한 객체를 탐색, 표시
3. **Sweep/Compact 단계**
  - 도달하지 못한 객체 제거 및 메모리 정리
4. **메모리 회수 후 애플리케이션 재개**

## GC 종류

### 🔹 Serial GC
- 단일 스레드로 GC 수행
- 작은 힙, 싱글코어 환경에 적합
- `-XX:+UseSerialGC`

### 🔹 Parallel GC
- 멀티 스레드 병렬 GC
- 처리량(Throughput) 중시
- `-XX:+UseParallelGC`

### 🔹 CMS (Concurrent Mark-Sweep) GC
- 응답 속도(Low Latency) 중시
- Mark, Sweep 단계 앱과 동시 실행
- `-XX:+UseConcMarkSweepGC` (JDK9부터 deprecated)

### 🔹 G1 (Garbage First) GC
- Region 기반 관리
- 예측 가능한 Stop-The-World 시간
- `-XX:+UseG1GC` (JDK9 이후 기본)

### 🔹 ZGC
- 초저지연(Low Latency) GC
- Pause time 10ms 이내
- `-XX:+UseZGC` (JDK11 이후)

### 🔹 Shenandoah GC
- RedHat 개발
- Concurrent Compacting 지원
- `-XX:+UseShenandoahGC` (OpenJDK11 이후)