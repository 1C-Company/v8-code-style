# The "Lock()" call is out of the try block

The rule checks for initialization of the data lock. If the
creation of a lock is found, the call of the "Lock()" method is checked,
and the call must be in a try block.

## Noncompliant Code Example

```bsl
DataLock = new DataLock;
DataLockItem = DataLock.Add("Document.Test");
DataLockItem.Mode = DataLockMode.Exclusive;
DataLock.Lock();
```

## Compliant Solution

```bsl
BeginTransaction();
Try
    
    DataLock = new DataLock;
    DataLockItem = DataLock.Add("Document.Test");
    DataLockItem.Mode = DataLockMode.Exclusive;
    DataLock.Lock();
    
    CommitTransaction();
   
Except
    RollbackTransaction();

Raise;

EndTry;
```

## See

- [Catching exceptions in code](https://support.1ci.com/hc/en-us/articles/360011002440-Catching-exceptions-in-code)
- [Transactions: rules of use](https://support.1ci.com/hc/en-us/articles/360011121239-Transactions-rules-of-use)