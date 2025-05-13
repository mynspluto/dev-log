
# JOIN 사용 예시

### 1. INNER JOIN

```sql
SELECT 
    e.employee_name,
    d.department_name
FROM 
    employees e
INNER JOIN 
    departments d ON e.department_id = d.id;
```
직원과 그들이 속한 부서 정보를 조회합니다.

### 2. LEFT JOIN

```sql
SELECT 
    c.customer_name,
    o.order_id,
    o.order_date
FROM 
    customers c
LEFT JOIN 
    orders o ON c.id = o.customer_id;
```
모든 고객과 그들의 주문 정보를 조회합니다. 주문이 없는 고객도 포함됩니다.

### 3. RIGHT JOIN

```sql
SELECT 
    e.employee_name,
    d.department_name
FROM 
    employees e
RIGHT JOIN 
    departments d ON e.department_id = d.id;
```
모든 부서와 그 부서에 속한 직원들을 조회합니다. 직원이 없는 부서도 포함됩니다.

### 4. FULL OUTER JOIN (MySQL에서는 UNION을 사용해 구현)

```sql
SELECT 
    e.employee_name,
    d.department_name
FROM 
    employees e
LEFT JOIN 
    departments d ON e.department_id = d.id
UNION
SELECT 
    e.employee_name,
    d.department_name
FROM 
    employees e
RIGHT JOIN 
    departments d ON e.department_id = d.id
WHERE 
    e.id IS NULL;
```
모든 직원과 모든 부서의 정보를 조회합니다.

### 5. SELF JOIN

```sql
SELECT 
    e.employee_name AS employee,
    m.employee_name AS manager
FROM 
    employees e
INNER JOIN 
    employees m ON e.manager_id = m.id;
```
직원과 그들의 관리자 정보를 조회합니다.

### 6. 다중 테이블 JOIN

```sql
SELECT 
    c.customer_name,
    o.order_id,
    p.product_name,
    oi.quantity,
    oi.price
FROM 
    customers c
INNER JOIN 
    orders o ON c.id = o.customer_id
INNER JOIN 
    order_items oi ON o.id = oi.order_id
INNER JOIN 
    products p ON oi.product_id = p.id;
```
고객, 주문, 주문 항목, 제품 테이블을 조인하여 주문 정보를 상세하게 조회합니다.

### 7. USING 구문 사용

```sql
SELECT 
    e.employee_name,
    d.department_name
FROM 
    employees e
INNER JOIN 
    departments d USING (department_id);
```
동일한 이름의 열을 기준으로 조인합니다.

### 8. JOIN과 GROUP BY 조합

```sql
SELECT 
    d.department_name,
    COUNT(e.id) AS employee_count,
    AVG(e.salary) AS avg_salary
FROM 
    departments d
LEFT JOIN 
    employees e ON d.id = e.department_id
GROUP BY 
    d.id, d.department_name;
```
각 부서별 직원 수와 평균 급여를 조회합니다. 직원이 없는 부서도 결과에 포함됩니다.

### 9. 비등가 JOIN (Non-Equi JOIN)

```sql
SELECT 
    e1.employee_name,
    e1.salary,
    COUNT(e2.id) AS higher_salary_count
FROM 
    employees e1
LEFT JOIN 
    employees e2 ON e1.salary < e2.salary
GROUP BY 
    e1.id, e1.employee_name, e1.salary
ORDER BY 
    e1.salary DESC;
```
각 직원보다 급여가 높은 직원의 수를 계산합니다.

### 10. 조건부 JOIN

```sql
SELECT 
    c.customer_name,
    o.order_id,
    o.order_date
FROM 
    customers c
LEFT JOIN 
    orders o ON c.id = o.customer_id AND o.order_date >= '2023-01-01';
```
2023년 1월 1일 이후의 주문만 조인하여 조회합니다.