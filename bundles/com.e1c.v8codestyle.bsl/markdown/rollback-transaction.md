# The scheme of working with transactions is broken

Rollback transaction must be in a try-catch, 
there should be no executable code between exception and rollback transaction, 
there is no begin transaction for rollback transaction, 
there is no commit transaction for begin transaction.

## Noncompliant Code Example

```bsl
    BeginTransaction();
    Try
    // ...
    CommitTransaction();
    Except
    // ...
    Raise;
    EndTry;
    RollbackTransaction();
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