# Context server call in form event handler

In some client event handlers of a form module (BeforeWrite, AfterWrite, BeforeClose, OnClose, OnChange,
OnActivateRow, OnStartChoice, OnEndEdit, OnDragCheck, OnEditNewLine, OnTextEdited, OnTextChanged,
OnActivateCell) a context server call is not allowed.

A context server call is a call of a server procedure or function declared with the `&AtServer` compile
directive. Such a call transfers the whole form context to the server, therefore the platform forbids it
in the listed event handlers.

To fix the issue, make the called server procedure or function non-contextual (`&AtServerNoContext`),
passing only the minimal required data to the server, or move the call out of the handler (for example,
by using a wait handler).

## Noncompliant Code Example
```
&AtClient
 Procedure Attachable_AvailableFieldsAtEndOfEditing(Item, NewRow, CancelEdit)
 OnChangePatternAtServer();
 EndProcedure
```
## Compliant Solution

```bsl
&AtClient
 Procedure Attachable_AvailableFieldsAtEndOfEditing(Item, NewRow, CancelEdit)
 AttachIdleHandler("OnChangePattern", 0.1, True);
 EndProcedure

 &AtClient
 Procedure OnChangePattern()
 OnChangePatternAtServer();
 EndProcedure

 &AtServer
 Procedure OnChangePatternAtServer()
 ...
 EndProcedure
```

## See

- [Contextual and non-contextual transfer of control to the server 2.1](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Designing_user_interfaces/Implementation_of_forms/Contextual_and_non-contextual_control_transfer_to_the_server/?language=en)

