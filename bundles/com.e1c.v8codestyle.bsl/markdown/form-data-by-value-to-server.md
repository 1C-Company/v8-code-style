# Passing form data by value to the server

Form data objects (`FormDataStructure`, `FormDataCollection`, `FormDataStructureAndCollection`,
`FormDataTree`, `SpreadsheetDocument`) should not be passed by value as parameters of a non-context server
call from the client.

Such objects are always transferred to the server as a whole, regardless of which part of them is actually
needed. Passing them to a non-context server call (`&AtServerNoContext`) serializes and sends the entire
object, which degrades performance. If the form data object must be processed on the server, it should be
handled there by an explicit context server call (`&AtServer`), or only the minimal required data should be
passed to the non-context server call instead of the whole object.

## Noncompliant Code Example

```bsl
 &AtClient
 Procedure CanceledOrderRowsCount()
 RowsCount = CommonModuleServerCall.CanceledRowsCount(Object.Goods); // Suboptimal transfer of the Goods table to the server.
 EndProcedure

// Common server module CommonModuleServerCall.

 Function CanceledRowsCount(TabularSection);
 FoundRows = TabularSection.FindRows(New Structure("Canceled", True));
 Return FoundRows.Count();
 EndFunction
```

## Compliant Solution

```
 &AtClient
 Procedure CanceledOrderRowsCount()
 RowsCount = CanceledRowsCount(); // The Goods table is implicitly transferred by 1C:Enterprise in an optimal way.
 EndProcedure

&AtServer
 Procedure CanceledRowsCount()
 Return CommonModuleServerCall.CanceledRowsCount(Object.Goods); // The server-to-server call with no additional overhead.
 EndProcedure

// Common server module CommonModuleServerCall.

 Function CanceledRowsCount(TabularSection);
 FoundRows = TabularSection.FindRows(New Structure("Canceled", True));
 Return FoundRows.Count()
 EndFunction
```

## See

- [Contextual and non-contextual transfer of control to the server 3](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Designing_user_interfaces/Implementation_of_forms/Contextual_and_non-contextual_control_transfer_to_the_server/?language=en)
