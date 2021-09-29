# Using "FOR UPDATE" clause

The FOR UPDATE clause is intended for locking specific data (which is avaialble for reading from a transaction belonging to another connection)
in advance while it is being read, to avoid deadlocks later, when it will be written. We do not recommended that you use the FOR UPDATE clause as it is irrelevant in managed lock mode.

## Noncompliant Code Example

```bsl
SELECT 
   Doc.Ref 
FROM 
   Document.RetailSale Doc
WHERE 
   Doc.Ref = &DocumentRef
FOR UPDATE AccumulationRegister.MutualSettlementsByAgreement.Balance
```

## Compliant Solution

```bsl
SELECT 
   Doc.Ref 
FROM 
   Document.RetailSale Doc
WHERE 
   Doc.Ref = &DocumentRef
```

## See

- [Using managed lock mode](https://support.1ci.com/hc/en-us/articles/360011002120-Using-managed-lock-mode)