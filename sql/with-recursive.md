# MySQL WITH RECURSIVE 사용 예시

MySQL 8.0 이상에서는 재귀적 공통 테이블 식(Recursive Common Table Expressions, Recursive CTE)을 사용할 수 있습니다. WITH RECURSIVE는 계층적 데이터나 그래프 구조와 같은 복잡한 쿼리를 작성할 때 특히 유용합니다.

## 기본 구조

```sql
WITH RECURSIVE cte_name AS (
    -- 초기 쿼리 (비재귀 부분)
    SELECT initial_values
    UNION [ALL]
    -- 재귀 쿼리 (자기 참조 부분)
    SELECT recursive_values
    FROM cte_name
    WHERE termination_condition
)
SELECT * FROM cte_name;
```

## 예시

### 1. 숫자 시퀀스 생성

```sql
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1
    FROM numbers
    WHERE n < 10
)
SELECT * FROM numbers;
```
1부터 10까지의 숫자 시퀀스를 생성합니다.

### 2. 날짜 시퀀스 생성

```sql
WITH RECURSIVE date_sequence AS (
    SELECT CAST('2023-01-01' AS DATE) AS date
    UNION ALL
    SELECT DATE_ADD(date, INTERVAL 1 DAY)
    FROM date_sequence
    WHERE date < '2023-01-31'
)
SELECT * FROM date_sequence;
```
2023년 1월의 모든 날짜 시퀀스를 생성합니다.

### 3. 조직도 계층 구조 조회 (직원-관리자 관계)

```sql
WITH RECURSIVE org_hierarchy AS (
    -- 최상위 관리자 (manager_id가 NULL인 직원)
    SELECT id, employee_name, manager_id, 1 AS level, employee_name AS path
    FROM employees
    WHERE manager_id IS NULL
    
    UNION ALL
    
    -- 하위 직원들
    SELECT e.id, e.employee_name, e.manager_id, h.level + 1, 
           CONCAT(h.path, ' > ', e.employee_name)
    FROM employees e
    JOIN org_hierarchy h ON e.manager_id = h.id
)
SELECT * FROM org_hierarchy
ORDER BY path;
```
조직의 계층 구조를 레벨과 경로 정보와 함께 조회합니다.

### 4. 카테고리 계층 구조 조회

```sql
WITH RECURSIVE category_tree AS (
    -- 최상위 카테고리 (parent_id가 NULL인 카테고리)
    SELECT id, category_name, parent_id, 0 AS depth, 
           CAST(category_name AS CHAR(1000)) AS path
    FROM categories
    WHERE parent_id IS NULL
    
    UNION ALL
    
    -- 하위 카테고리
    SELECT c.id, c.category_name, c.parent_id, ct.depth + 1,
           CONCAT(ct.path, ' > ', c.category_name)
    FROM categories c
    JOIN category_tree ct ON c.parent_id = ct.id
)
SELECT * FROM category_tree
ORDER BY path;
```
카테고리의 계층 구조를 깊이와 경로 정보와 함께 조회합니다.

### 5. 그래프에서 경로 찾기

```sql
WITH RECURSIVE path_finder AS (
    -- 시작점
    SELECT start_node AS current, end_node AS destination, 
           1 AS depth, CAST(start_node AS CHAR(1000)) AS path, 0 AS is_cycle
    FROM graph_edges
    WHERE start_node = 'A'
    
    UNION ALL
    
    -- 다음 노드로 이동
    SELECT e.end_node, p.destination, p.depth + 1,
           CONCAT(p.path, ' -> ', e.end_node),
           LOCATE(e.end_node, p.path) > 0 AS is_cycle
    FROM graph_edges e
    JOIN path_finder p ON e.start_node = p.current
    WHERE p.depth < 10 AND p.is_cycle = 0
)
SELECT * FROM path_finder
WHERE current = destination
ORDER BY depth
LIMIT 1;
```
그래프에서 'A'에서 시작하여 특정 목적지까지의 최단 경로를 찾습니다.

### 6. 피보나치 수열 생성

```sql
WITH RECURSIVE fibonacci AS (
    -- 초기값: 0과 1
    SELECT 0 AS n, 0 AS fib_n, 1 AS fib_next
    UNION ALL
    -- 다음 피보나치 수 계산
    SELECT n + 1, fib_next, fib_n + fib_next
    FROM fibonacci
    WHERE n < 20
)
SELECT n, fib_n FROM fibonacci;
```
피보나치 수열의 처음 20개 항을 생성합니다.

### 7. 파일 시스템 계층 구조 조회

```sql
WITH RECURSIVE file_tree AS (
    -- 루트 디렉토리
    SELECT id, name, parent_id, 'directory' AS type, 0 AS depth,
           CAST(name AS CHAR(1000)) AS path
    FROM files
    WHERE parent_id IS NULL AND type = 'directory'
    
    UNION ALL
    
    -- 하위 파일과 디렉토리
    SELECT f.id, f.name, f.parent_id, f.type, ft.depth + 1,
           CONCAT(ft.path, '/', f.name)
    FROM files f
    JOIN file_tree ft ON f.parent_id = ft.id
)
SELECT * FROM file_tree
ORDER BY path;
```
파일 시스템의 계층 구조를 깊이와 경로 정보와 함께 조회합니다.

### 8. 계층적 데이터의 총합 계산

```sql
WITH RECURSIVE item_hierarchy AS (
    -- 최상위 항목
    SELECT id, name, parent_id, quantity, price, quantity * price AS value
    FROM items
    WHERE parent_id IS NULL
    
    UNION ALL
    
    -- 하위 항목
    SELECT i.id, i.name, i.parent_id, i.quantity, i.price, i.quantity * i.price AS value
    FROM items i
    JOIN item_hierarchy ih ON i.parent_id = ih.id
)
SELECT parent_id, SUM(value) AS total_value
FROM item_hierarchy
GROUP BY parent_id WITH ROLLUP;
```
계층적 구조의 항목에 대한 총 가치를 계산합니다.

### 9. 특정 깊이까지의 댓글 트리 조회

```sql
WITH RECURSIVE comment_tree AS (
    -- 최상위 댓글 (parent_id가 NULL인 댓글)
    SELECT id, content, user_id, created_at, parent_id, 0 AS depth
    FROM comments
    WHERE post_id = 100 AND parent_id IS NULL
    
    UNION ALL
    
    -- 대댓글
    SELECT c.id, c.content, c.user_id, c.created_at, c.parent_id, ct.depth + 1
    FROM comments c
    JOIN comment_tree ct ON c.parent_id = ct.id
    WHERE c.post_id = 100 AND ct.depth < 3
)
SELECT * FROM comment_tree
ORDER BY COALESCE(parent_id, id), id;
```
게시물 ID가 100인 게시물의 댓글과 3단계 깊이까지의 대댓글을 조회합니다.

### 10. 데이터 분할(파티셔닝)

```sql
WITH RECURSIVE date_partitions AS (
    -- 시작 날짜
    SELECT MIN(created_at) AS start_date, 
           DATE_ADD(MIN(created_at), INTERVAL 1 MONTH) AS end_date
    FROM transactions
    
    UNION ALL
    
    -- 다음 파티션
    SELECT end_date, DATE_ADD(end_date, INTERVAL 1 MONTH)
    FROM date_partitions
    WHERE end_date <= (SELECT MAX(created_at) FROM transactions)
)
SELECT start_date, end_date, 
       COUNT(t.id) AS transaction_count,
       SUM(t.amount) AS total_amount
FROM date_partitions dp
LEFT JOIN transactions t ON t.created_at >= dp.start_date AND t.created_at < dp.end_date
GROUP BY dp.start_date, dp.end_date
ORDER BY dp.start_date;
```
거래 데이터를 월별로 파티셔닝하여 각 월의 거래 수와 총액을 계산합니다.

## 주의사항

1. **무한 루프 방지**: 재귀 쿼리에는 항상 종료 조건(WHERE 절)을 포함해야 합니다. 그렇지 않으면 무한 루프에 빠질 수 있습니다.

2. **최대 반복 횟수**: MySQL에서는 기본적으로 재귀 깊이가 1000으로 제한되어 있습니다. 이 제한은 `cte_max_recursion_depth` 시스템 변수로 조정할 수 있습니다.

   ```sql
   SET @@cte_max_recursion_depth = 5000;
   ```

3. **성능 고려**: 복잡한 재귀 쿼리는 성능에 영향을 줄 수 있습니다. 특히 큰 데이터셋이나 깊은 계층 구조에서는 성능 최적화가 필요할 수 있습니다.

4. **UNION vs UNION ALL**: `UNION`은 중복 행을 제거하지만, `UNION ALL`은 모든 행을 포함합니다. 성능을 위해 중복이 발생하지 않는 경우에는 `UNION ALL`을 사용하는 것이 좋습니다.

5. **재귀 쿼리 디버깅**: 복잡한 재귀 쿼리를 디버깅하는 경우, 초기 쿼리와 재귀 쿼리를 단계별로 테스트하고 중간 결과를 확인하는 것이 도움이 됩니다.

WITH RECURSIVE는 계층적 데이터 처리, 경로 탐색, 시퀀스 생성 등 복잡한 쿼리를 단순화할 수 있는 강력한 도구입니다. 하지만 적절한 종료 조건과 성능 최적화에 주의해야 합니다.