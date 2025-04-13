# Java 동시성 컬렉션 및 락 개념 정리

## 컬렉션 비교

### Vector vs ArrayList vs CopyOnWriteArrayList

| 항목 | Vector | ArrayList | CopyOnWriteArrayList |
|------|--------|-----------|----------------------|
| **동기화** | ✅ 전체 메서드에 `synchronized` | ❌ 동기화 안 됨 | ✅ 내부 복사로 동기화 |
| **성능** | 느림 (전역 락) | 빠름 | 읽기 빠름, 쓰기 느림 |
| **동시 쓰기** | ❌ 락 대기로 불가 | ❌ 문제 발생 | ❌ 쓰기 손실 가능성 있음 |
| **특징** | 레거시, 쓰레드 안전 | 일반적 용도에 적합 | 읽기 많고 쓰기 적을 때 |

> 🔥 **Vector**는 전역 락으로 안전하지만 느리며, **CopyOnWriteArrayList**는 읽기 성능이 뛰어나지만 쓰기 손실 가능성 존재.

### CopyOnWriteArrayList의 문제점
* 모든 쓰기 연산(`add`, `remove`)이 내부 배열을 **복사**하고 교체하여 동기화
* 동시 쓰기 시, **Lost Update(쓰기가 덮어써짐)** 발생 가능
* 쓰기 충돌 발생 시 병합(Merge)이 없으며 마지막 쓰기 결과만 반영

> ✔️ "동기화 ≠ 동시성 문제 해결"

### add와 remove의 동시 가능 여부

| 컬렉션 | 동시 실행 가능 여부 | 이유 |
|--------|-------------------|------|
| **Vector** | ❌ 불가능 | 전체 메서드에 `synchronized` |
| **ArrayList** | ❌ 위험 | 동기화 없음 → 경쟁 상태 발생 |
| **CopyOnWriteArrayList** | ✅ 가능하나 신뢰 불가 | 복사-교체 구조, 병합 없음 |
| **ConcurrentLinkedQueue** | ✅ 가능 | Lock-free 구조, 손실 없음 |

## 동기화 방식

### synchronized 키워드의 본질

#### 메서드 단위 `synchronized`
```java
public synchronized void foo() {
    // 객체(this)에 락
}
```
* 같은 객체의 다른 synchronized 메서드도 동시에 실행 불가

#### 클래스 단위 `static synchronized`
```java
public static synchronized void bar() {
    // 클래스 단위 락
}
```
* 클래스 전체에 락을 걸어 동기화

### Vector 동기화 방식
* 모든 `add()`, `remove()`, `get()` 작업에 전역 락 적용 → **안전하지만 느림**
* 하나의 스레드가 작업 중이면 다른 스레드는 대기


## 필드 단위 락

### 방법 1: Custom Lock 사용
```java
private final Object fieldLock = new Object();
private int counter = 0;

public void increment() {
    synchronized (fieldLock) {
        counter++;
    }
}
```

### 방법 2: ReentrantLock 사용
```java
private final ReentrantLock counterLock = new ReentrantLock();
private int counter = 0;

public void increment() {
    counterLock.lock();
    try {
        counter++;
    } finally {
        counterLock.unlock();
    }
}
```
* **ReentrantLock**은 조건 변수(Condition), 타임아웃 락, 공정성 설정 가능

### 읽기-쓰기 분리 가능 락
* **ReadWriteLock** 또는 **StampedLock**을 사용하면 **읽기-쓰기 동시 처리** 가능

## 핵심 요약
* **Vector**: 전역 락 적용 → 안전하지만 느림
* **CopyOnWriteArrayList**: 읽기 최적화, 쓰기 손실 가능성 있음
* **synchronized**: 객체 단위로 락을 걸어 동기화
* **ReentrantLock** 등 고급 락으로 더 정교한 동기화 가능
* 완벽한 동시성 처리를 위해 **Concurrent 계열 컬렉션** 활용 권장