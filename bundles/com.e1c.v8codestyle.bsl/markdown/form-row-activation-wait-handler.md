# One-time idle handler in the OnActivateRow event handler

The client event handler `OnActivateRow` (`ПриАктивизацииСтроки`) is called synchronously every time a row is
activated. Performing server calls directly in this handler leads to synchronous server calls and repeated
requests while the user moves through the rows of a list.

Instead, the handler should attach a one-time idle handler (`AttachIdleHandler` with the one-time parameter
set to `True`) and perform the server calls inside the idle handler procedure. Attaching the idle handler
postpones the calls until the event finishes, and the one-time mode prevents repeated calls on the following
row activations.


## Noncompliant Code Example

```bsl
&AtClient
Procedure OnActivateRow(Item, Row)
	HandleRow(Item, Row);
EndProcedure
```

```bsl
&AtClient
Procedure OnActivateRow(Item, Row)
	AttachIdleHandler("IdleHandler", 1, False);
EndProcedure
```

```bsl
&AtClient
Procedure OnActivateRow(Item, Row)
	AttachIdleHandler("IdleHandler", 1, True);
	ServerCall();
EndProcedure
```

## Compliant Solution

```bsl
&AtClient
Procedure OnActivateRow(Item, Row)
	AttachIdleHandler("IdleHandler", 1, True);
EndProcedure

&AtClient
Procedure IdleHandler()
	ServerCall();
EndProcedure
```

## See

- [Contextual and non-contextual transfer of control to the server 2.2](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Designing_user_interfaces/Implementation_of_forms/Contextual_and_non-contextual_control_transfer_to_the_server/?language=en)
