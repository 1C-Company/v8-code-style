# Virtual table filters should be in parameters

If you use virtual tables in queries, pass all conditions related to this virtual table to the table parameters. We do not recommended that you access virtual tables using conditions of WHERE clause, and other.

Such query returns a correct result but it is more difficult for DBMS to select an optimum method to execute that query. In some cases, it can cause DBMS optimizer errors and considerably slow down the query execution. 

## Noncompliant Code Example

For example, the following query uses the WHERE section to select data from a virtual table:

```bsl
Query.Text= "SELECT
| Products
|FROM
| AccumulationRegister.Stock.Balance()
|WHERE
| Warehouse = &Warehouse";
```

Upon executing this query, all records of a virtual table are selected. Then only those that meet the specified condition are sampled.

## Compliant Solution

We recommend that you restrict the number of records to be selected at a very early stage of query processing. To do so, pass conditions to virtual table parameters. 

```bsl
Query.Text= "SELECT
| Products
|FROM
| AccumulationRegister.Stock.Balance(, Warehouse = &Warehouse)";
```


## See

- [Accessing virtual table](https://support.1ci.com/hc/en-us/articles/360011121039-Accessing-virtual-table)
- [Using filters in queries with virtual tables](https://1c-dn.com/library/using_filters_in_queries_with_virtual_tables/)
- [Virtual table parameters](https://1c-dn.com/library/tutorials/practical_developer_guide_virtual_table_parameters3/)
