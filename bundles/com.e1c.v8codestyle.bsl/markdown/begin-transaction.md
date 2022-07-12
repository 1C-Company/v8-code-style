# The scheme of working with transactions is broken

There should be no executable code between begin transaction and try,
the try operator was not found after calling begin transaction

## Noncompliant Code Example

```bsl
    BeginTransaction();
    CommitTransaction();
```

## Compliant Solution

```bsl
    BeginTransaction();
    Try
    // ...
    CommitTransaction();
    Except
    // ...
    RollbackTransaction();
    // ...
    Raise;
    EndTry;
```

## See

- [Catching exceptions in code](https://support.1ci.com/hc/en-us/articles/360011002440-Catching-exceptions-in-code)
- [Transactions: rules of use](https://support.1ci.com/hc/en-us/articles/360011121239-Transactions-rules-of-use)