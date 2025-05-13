# GROUP BY 사용 예시

### 1. 기본 GROUP BY

```sql
SELECT 
    department_id,
    COUNT(*) AS employee_count,
    AVG(salary) AS avg_salary
FROM 
    employees
GROUP BY 
    department_id;
```
각 부서별 직원 수와 평균 급여를 조회합니다.

### 2. HAVING 절과 함께 사용

```sql
SELECT 
    department_id,
    AVG(salary) AS avg_salary
FROM 
    employees
GROUP BY 
    department_id
HAVING 
    AVG(salary) > 50000;
```
평균 급여가 50,000을 초과하는 부서만 조회합니다.

### 3. 다중 열 GROUP BY

```sql
SELECT 
    department_id,
    job_title,
    COUNT(*) AS employee_count,
    AVG(salary) AS avg_salary
FROM 
    employees
GROUP BY 
    department_id, job_title
ORDER BY 
    department_id, job_title;
```
부서와 직책별로 직원 수와 평균 급여를 조회합니다.

### 4. GROUP BY와 함께 CASE WHEN 사용

```sql
SELECT 
    department_id,
    SUM(CASE WHEN gender = 'M' THEN 1 ELSE 0 END) AS male_count,
    SUM(CASE WHEN gender = 'F' THEN 1 ELSE 0 END) AS female_count
FROM 
    employees
GROUP BY 
    department_id;
```
각 부서별 남성과 여성 직원의 수를 조회합니다.

### 5. WITH ROLLUP 수정자 사용

```sql
SELECT 
    department_id,
    job_title,
    COUNT(*) AS employee_count,
    SUM(salary) AS total_salary
FROM 
    employees
GROUP BY 
    department_id, job_title WITH ROLLUP;
```
부서와 직책별 소계와 총계를 함께 조회합니다.

### 6. GROUP_CONCAT 집계 함수 사용

```sql
SELECT 
    department_id,
    COUNT(*) AS employee_count,
    GROUP_CONCAT(employee_name ORDER BY salary DESC SEPARATOR ', ') AS employees
FROM 
    employees
GROUP BY 
    department_id;
```
각 부서별 직원들의 이름을 급여 내림차순으로 연결하여 조회합니다.
