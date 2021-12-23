# Method or variable accessible AtClient

Method or variable accessible AtClient in manager or object module

## Noncompliant Code Example

```bsl

Var moduleVar;

Procedure BeforeDelete(Cancel)
	// Non-compliant
EndProcedure


Procedure Noncompiant() Export
	// empty
EndProcedure

moduleVar = Undefined;

```

## Compliant Solution

```bsl

#If Server Or ThickClientOrdinaryApplication Or ExternalConnection Then

Var moduleVar;

Procedure BeforeDelete(Cancel)
	// Compliant
EndProcedure

Procedure Compiant() Export
	// empty
EndProcedure


moduleVar = Undefined;

#Else
	Raise NStr("en = 'Invalid object call on the client.'");
#EndIf

```

## See

- [Thick client support in managed applications that run in the client/server mode](https://1c-dn.com/library/thick_client_support_in_managed_applications_that_run_in_the_client_server_mode/)
- [Thick client support, managed application, client-server](https://support.1ci.com/hc/en-us/articles/360010988300-Thick-client-support-managed-application-client-server)
- [PresentationGetProcessing() and PresentationFieldsGetProcessing() event handlers](https://support.1ci.com/hc/en-us/articles/360011001340-PresentationGetProcessing-and-PresentationFieldsGetProcessing-event-handlers)
