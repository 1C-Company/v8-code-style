# The right operand of the LIKE comparison operation is a table field.

Only a literal (a parameter) or an expression over literals can be the right operand of the LIKE (LIKE) comparison operation. Mask characters are the «_» ‑ any character and the «%» ‑ a sequence of any characters only.

## Noncompliant Code Example

It is forbidden to use a table field (a catalog attribute) in a query as the right operand of the LIKE comparison operation.

```bsl
... LIKE Table1.Field1
```

## Compliant Solution

When using LIKE in query texts, use only constant string literals or query parameters.

```bsl
... LIKE "123%"
```

## See

- [Appendix 8. Features of operating with different DBMS](https://support.1ci.com/hc/en-us/articles/6347699838098-8-3-IBM-Db2)
- [1C:Enterprise Development Standards:Working with queries](https://support.1ci.com/hc/en-us/articles/360011001500-Specifics-of-using-LIKE-operator-in-queries)
