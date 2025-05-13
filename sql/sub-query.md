# 서브쿼리 사용 예시

### 1. WHERE 절에서 단일 값 서브쿼리

```sql
SELECT 
    employee_name, 
    salary
FROM 
    employees
WHERE 
    salary > (SELECT AVG(salary) FROM employees);
```
평균 급여보다 높은 급여를 받는 직원들을 조회합니다.

### 2. IN 연산자와 함께 사용하는 서브쿼리

```sql
SELECT 
    product_name, 
    price
FROM 
    products
WHERE 
    category_id IN (SELECT id FROM categories WHERE category_name = '전자제품');
```
'전자제품' 카테고리에 속하는 모든 제품을 조회합니다.

### 3. EXISTS 연산자와 함께 사용하는 서브쿼리

```sql
SELECT 
    customer_name
FROM 
    customers c
WHERE 
    EXISTS (SELECT 1 FROM orders o WHERE o.customer_id = c.id AND o.order_date > '2023-01-01');
```
2023년 1월 1일 이후에 주문한 고객들을 조회합니다.

### 4. FROM 절에서 사용하는 서브쿼리 (파생 테이블)

```sql
SELECT 
    dept_name, 
    avg_salary
FROM 
    (SELECT 
        d.name AS dept_name, 
        AVG(e.salary) AS avg_salary
     FROM 
        departments d
     JOIN 
        employees e ON d.id = e.department_id
     GROUP BY 
        d.name) AS dept_stats
WHERE 
    avg_salary > 50000;
```
평균 급여가 50,000 이상인 부서를 조회합니다.

### 5. SELECT 절에서 사용하는 서브쿼리 (스칼라 서브쿼리)

```sql
SELECT 
    product_name,
    price,
    (SELECT AVG(price) FROM products) AS avg_price,
    price - (SELECT AVG(price) FROM products) AS price_diff
FROM 
    products;
```
각 제품의 가격과 평균 가격, 그리고 평균과의 차이를 조회합니다.

### 6. 상관 서브쿼리 (Correlated Subquery)

```sql
SELECT 
    department_name,
    employee_name,
    salary
FROM 
    employees e
WHERE 
    salary = (SELECT MAX(salary) FROM employees WHERE department_id = e.department_id);
```
각 부서에서 가장 높은 급여를 받는 직원을 조회합니다.

### 7. 다중 열 서브쿼리

```sql
SELECT 
    product_name, 
    category_id, 
    price
FROM 
    products
WHERE 
    (category_id, price) IN (SELECT category_id, MAX(price) FROM products GROUP BY category_id);
```
각 카테고리에서 가장 비싼 제품을 조회합니다.

### 8. WITH 절(CTE)과 함께 사용하는 서브쿼리

```sql
WITH HighSalaryEmployees AS (
    SELECT 
        id, 
        employee_name, 
        department_id
    FROM 
        employees
    WHERE 
        salary > 60000
)
SELECT 
    d.department_name,
    COUNT(h.id) AS high_salary_count
FROM 
    departments d
LEFT JOIN 
    HighSalaryEmployees h ON d.id = h.department_id
GROUP BY 
    d.department_name;
```
각 부서별로 급여가 60,000을 초과하는 직원 수를 조회합니다.

### 9. INSERT 문에서 사용하는 서브쿼리

```sql
INSERT INTO employee_stats (department_id, avg_salary, max_salary)
SELECT 
    department_id,
    AVG(salary),
    MAX(salary)
FROM 
    employees
GROUP BY 
    department_id;
```
부서별 평균 급여와 최대 급여를 employee_stats 테이블에 삽입합니다.

### 10. UPDATE 문에서 사용하는 서브쿼리

```sql
UPDATE products
SET price = price * 1.1
WHERE category_id IN (SELECT id FROM categories WHERE category_name = '식품');
```
'식품' 카테고리의 모든 제품 가격을 10% 인상합니다.

### 11. 중첩 서브쿼리 (Nested Subquery)

```sql
SELECT 
    customer_name,
    total_purchases
FROM 
    customers
WHERE 
    total_purchases > (
        SELECT AVG(total_purchases) FROM customers
        WHERE country_id = (
            SELECT id FROM countries WHERE country_name = '대한민국'
        )
    );
```
한국 고객들의 평균 구매액보다 많이 구매한 모든 고객을 조회합니다.