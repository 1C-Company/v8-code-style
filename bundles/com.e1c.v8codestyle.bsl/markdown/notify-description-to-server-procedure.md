# Notify description to Server procedure

Server procedure specified in parameter of notify description.

The call of the notify is not support by web-client.

## Noncompliant Code Example

```bsl
&AtClient
Procedure Noncompliant()

	Notify = new NotifyDescription("NoncompliantNotify", ThisObject);
	
EndProcedure

&AtServer
Procedure NoncompliantNotify() Export
	// Procedure is not avalable at client!

EndProcedure
```

## Compliant Solution

```bsl
&AtClient
Procedure Compliant()

	Notify = new NotifyDescription("CompliantNotify", ThisObject);
	
EndProcedure

&AtClient
Procedure CompliantNotify() Export
	// Procedure is avalable at client!
	
EndProcedure
```

## See

