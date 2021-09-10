# Empty except statement
It is incorrect to hide the issue from the user and administrator
## Compliant Solution

```bsl
Try
    ExecuteOperation();
Except
    ShowMessageBox(,NStr("en = 'Cannot execute the operation.'"));
EndTry;
```

## Noncompliant Code Example
```bsl
Try
  // Code that throws an exception
  ....
Except
  // Writing an event to the event log for the system administrator.
  WriteLogEvent(NStr("en = 'Executing an operation'"),
     EventLogLevel.Error,,,
     DetailErrorDescription(ErrorInfo()));
  Raise;
EndTry;
```
## Suppress check
Also you can suppress check on try-except statement and leave the clear message:
```bsl
// @skip-check empty-except-statement - suppress because...
Try
  ... 
Except
EndTry;
```
## See
- [Catching exceptions in code](https://support.1ci.com/hc/en-us/articles/360011002440-Catching-exceptions-in-code)