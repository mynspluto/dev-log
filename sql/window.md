# 윈도우 함수 사용 예시

### 1. ROW_NUMBER() 함수

```sql
SELECT 
    id, 
    name, 
    score,
    ROW_NUMBER() OVER (ORDER BY score DESC) AS rank
FROM students;
```
이 쿼리는 학생들의 점수를 내림차순으로 정렬하고 각 행에 순위를 매깁니다.

### 2. PARTITION BY를 사용한 그룹별 순위

```sql
SELECT
    id,
    department,
    salary,
    ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) AS dept_rank
FROM employees;
```
이 쿼리는 부서별로 급여 순위를 매깁니다.

### 3. RANK() 함수 (동점자 처리)

```sql
SELECT
    id,
    name,
    score,
    RANK() OVER (ORDER BY score DESC) AS rank
FROM students;
```
RANK()는 동점자에게 같은 순위를 부여하고, 다음 순위는 건너뜁니다.

### 4. DENSE_RANK() 함수

```sql
SELECT
    id,
    name,
    score,
    DENSE_RANK() OVER (ORDER BY score DESC) AS dense_rank
FROM students;
```
DENSE_RANK()는 동점자에게 같은 순위를 부여하지만, 다음 순위를 건너뛰지 않습니다.

### 5. 누적 합계 계산

```sql
SELECT
    id,
    date,
    amount,
    SUM(amount) OVER (ORDER BY date) AS running_total
FROM transactions;
```
날짜순으로 거래 금액의 누적 합계를 계산합니다.

### 6. 이동 평균 계산

```sql
SELECT
    id,
    date,
    value,
    AVG(value) OVER (ORDER BY date ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) AS moving_avg
FROM stock_prices;
```
현재 행과 이전 2개 행의 이동 평균을 계산합니다.

### 7. 퍼센타일 계산

```sql
SELECT
    id,
    score,
    PERCENT_RANK() OVER (ORDER BY score) AS percentile
FROM exam_results;
```
점수의 백분위 순위를 계산합니다.

### 8. 첫 번째/마지막 값 가져오기

```sql
SELECT
    id,
    date,
    price,
    FIRST_VALUE(price) OVER (PARTITION BY product_id ORDER BY date) AS initial_price,
    LAST_VALUE(price) OVER (PARTITION BY product_id ORDER BY date 
                           ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS latest_price
FROM product_prices;
```
각 제품의 초기 가격과 최신 가격을 가져옵니다.

### 9. 이전/다음 값 가져오기

```sql
SELECT
    id,
    date,
    value,
    LAG(value) OVER (ORDER BY date) AS previous_value,
    LEAD(value) OVER (ORDER BY date) AS next_value
FROM measurements;
```
각 측정값의 이전 값과 다음 값을 가져옵니다.

### 10. 그룹 내 비율 계산

```sql
SELECT
    id,
    department,
    salary,
    salary / SUM(salary) OVER (PARTITION BY department) AS dept_salary_ratio
FROM employees;
```
각 직원의 급여가 부서 총 급여에서 차지하는 비율을 계산합니다.

**주의사항:**
- MySQL에서 윈도우 함수는 버전 8.0 이상에서만 지원됩니다.
- 예약어와의 충돌을 피하기 위해 별칭을 잘 지정하는 것이 중요합니다.
- 서브쿼리를 사용할 때는 성능에 주의해야 합니다. 복잡한 서브쿼리는 때로 JOIN으로 대체하는 것이 더 효율적일 수 있습니다.
- 실행 계획(EXPLAIN)을 확인하여 쿼리 최적화를 고려하는 것이 좋습니다.