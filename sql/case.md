```sql
SELECT
    id,
    name,
    salary,
    CASE
        WHEN salary >= 6000 THEN 'High'
        WHEN salary >= 4000 THEN 'Medium'
        ELSE 'Low'
    END AS salary_grade
FROM employees;
```