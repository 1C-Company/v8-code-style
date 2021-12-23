
Var moduleVar;


Procedure BeforeDelete(Cancel)
	// Non-compliant
EndProcedure


Procedure Noncompiant() Export
	// empty
EndProcedure

#If Server Or ThickClientOrdinaryApplication Or ExternalConnection Then

Procedure OnWrite(Cancel)
	// Compliant
EndProcedure

Procedure Compiant() Export
	// empty
EndProcedure

#Else
	Raise NStr("en = 'Invalid object call on the client.'");
#EndIf


moduleVar = Undefined;
