# Checking the maximum number of empty rows

## Noncompliant Code Example

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

