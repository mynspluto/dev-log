# java 명령어 실행
\$JAVA_HOME/bin/java의 가상 머신 바이너리를 찾아 해당 파일로 엔트리 포인트 클래스 이름을 포함한 명렬줄 인수 전달
명령줄 플래그에 따라 가상 머신 초기화(힙 크기, 가비지 컬렉션 설정 등)
실행 중인 머신 조사하여 시스템 정보(CPU 코어, 메모리 용량, 사용 가능한 CPU 명령 집합 등) 수집
가상 머신은 수집된 정보를 활용해 설정(CPU 코어 수를 기반으로 가비지 컬렉션에서 사용할 스레드 수, 공용 스레들 풀의 크기 등) 최적화
자바 클래스와 관련 메타데이터를 저장할 저장소 초기화, 핫스팟에서는 이를 메타공간이라고 부름
새로운 스레드에서 JNI_CreateJavaVM 함수에 의해 가상 머신 생성
가비지 컬렉션 스레드, JIT 컴파일 스레드와 같은 가상 머신 내부 스레드도 함께 시작

# 클래스 로드
클래스를 JVM이 사용할 수 있도록 메모리에 올리는 과정
클래스가 로드 됐다는건 .class 바이트코드가 JVM 메모리(메서드 영역 등)에 올라갔다는 의미. 
다만, 클래스가 로드되었어도 초기화는 아직 안 됐을 수 도 있음

# 부트스트래핑 클래스
명령줄 인수로 지정된 엔트리 클래스보다 먼저 로딩되고 실행되는 클래스
예를 들어 java com.example.MyApp에서 MyApp이 엔트리 클래스
1. JVM 기동
- JVM이 메모리 할당, GC, 스레드 시스템 등 기본적인 런타임 환경 초기화
2. Bootstrap ClassLoader 작동
- java.lang.Object, java.lang.String, java.lang.Class, java.lang.System 같은 핵심 클래스 먼저 로딩
- 이 단계에서 부트스트래핑 클래스들이 로딩됨
3. main 메서드를 가진 엔트리 클래스 로딩
- 위 예시에서는 com.example.MyApp 클래스가 어플리케이션 클래스 로더에 의해 로드
- public static void main(String[] args)를 찾아 로딩

# jar
jar는 바이너리 형식으로 압축된 .class 파일들과 리소스들이 포함된 ZIP 형식의 아카이브
구성요소는
- .class 파일들(컴파일된 바이트코드)
- META-INF/MANIFEST.MF(실행 정보 등 메타데이터)
- .properties, .xml, .png 같은 리소스 파일

# 자바 클래스 로드 및 초기화 흐름
1. Loading
실행 시점: 클래스가 최초 사용될 때 발생
- 클래스 파일(.class)을 찾아서 메모리에 로드(.class 파일을 디스크, JAR, 네트워크 등에서 읽음)
2. Linking
실행 시점: 클래스 로드 이후 바로 실행
- Verification - 클래스 형식이 유효한지 검사
- Preparation - static 필드에 기본값 할당(0, null 등)
3. Initialization
실행 시점: 링크 완료 후, 클래스 최초 사용 시 단 1회 실행
- JVM이 clinit 메서드를 호출해서 static 변수 초기화 및 static 블록 실행
- static 필드의 실제 값 할당 + static 블록 실행 -> 여기서 초기화 블록이 실행됨
이 모든 과정은 클래스가 처음 사용될 때 단 1회 수행됨(Class.forName(), new, static 메서드 호출 등으로 트리거됨)
4. 인스턴스 생성
실행 시점: new 키워드 호출 시마다 실행
- 생성자 실행 전에 인스턴스 초기화 블록 실행, 그리고 생성자 실행


## 초기화 블록
클래스나 인스턴스가 생성될 때 실행되는 코드 블록(인스턴스 블록, static 블록)
```java
public class MyClass {
    // 실행 시점: 클래스 로드 시 1회, 용도: static 필드 초기화
    static {
        System.out.println("static block");
    }

    // 실행 시점: 객체 생성 시마다(static 참조만 할 땐 실행 안됨), 모든 생성자에 앞서 실행, 용도: 생성자 공통 코드 실행
    {
        System.out.println("instance block");
    }

    public MyClass() {
        System.out.println("constructor");
    }
}
```

jar 내부 구조
```bash
com/
 └── example/
      ├── MyApp.class
      └── Utils.class
META-INF/
 └── MANIFEST.MF

```

```bash
# jar 없이 실행
javac com/example/MyApp.java
java com.exmaple.MyApp

# cp 없이는 현재 디렉토리에서만 클래스를 찾으므로 외부 라이브러리 등 추가하려면 cp 옵션 필요
java -cp gson-2.10.1.jar MyApp

# jar안에 엔트리 포인트 명시되어 있으면 가능 app.jar 안의 META-INF/MANIFEST.MF에서 Main-Class 항목에 정의된 클래스를 찾아서 main 함수 실행
java -jar app.jar
```