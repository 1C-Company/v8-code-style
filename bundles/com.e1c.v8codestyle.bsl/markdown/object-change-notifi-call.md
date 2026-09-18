# Updating Lists on Interactive User Actions

In an object form, the AfterWrite event must contain a call to the Notify procedure, specifically Notify("Write_xxx").
The check verifies notification calls within the context of a single handler.

## Correct

```bsl
&AtClient
Procedure AfterWrite(WriteParameters)

EndProcedure
```

## Incorrect

```bsl
&AtClient
Procedure AfterWrite(WriteParameters)
  Notify("Write_ExpenseInvoice", WriteParameters, Object.Ref);
EndProcedure
```

## See more:
[Standard, item 3](https://its.1c.ru/db/v8std#content:558:hdoc)