# Check DataExchange.Load in event handler

Mandatory checking of DataExchange.Load is absent in event handler

## Noncompliant Code Example

```bsl
Procedure BeforeWrite(Cancel)
// handler code
// ...
EndProcedure
```

## Compliant Solution

```bsl
Procedure BeforeWrite(Cancel)
If DataExchange.Load Then
     Return;
EndIf;

// handler code
// ...
EndProcedure
```

## See

