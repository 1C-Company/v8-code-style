# Program invocation of form event handler

Assign a handler for each event. 
If different actions are required in case of events in different form items:

- Create a separate procedure or a function that executes the required action.
- Сreate a separate handler with a default name for each form item.
- Call the required procedure or function from each handler.

## Noncompliant Code Example

```bsl
&AtClient
Procedure ByPerformerOnChange(Item)
 FilterParameters = New Map();
 FilterParameters.Insert("ByAuthor", ByAuthor);
 FilterParameters.Insert("ByPerformer", ByPerformer);
 SetListFilter(List, FilterParameters);
EndProcedure

&AtClient
Procedure ByAuthorOnChange(Item)
 ByPerformerOnChange(Undefined);
EndProcedure
```

## Compliant Solution

```bsl
&AtClient
Procedure ByPerformerOnChange(Item)
 SetFilter();
EndProcedure

&AtClient
Procedure ByAuthorOnChange(Item)
 SetFilter();
EndProcedure

&AtServer
Procedure SetFilter()
 FilterParameters = New Map();
 FilterParameters.Insert("ByAuthor", ByAuthor);
 FilterParameters.Insert("ByPerformer", ByPerformer);
 SetListFilter(List, FilterParameters);
EndProcedure
```

These requirements are provided as logically event handlers are not intended for use in the module code but
are called directly by the platform. If you mix these two scenarios in a single procedure, 
its logic gets complicated and decreases its stability.
Instead of one predefined call scenario by an event from the platform, 
the procedure code needs to consider other "direct" calls from the code.

## See

- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
