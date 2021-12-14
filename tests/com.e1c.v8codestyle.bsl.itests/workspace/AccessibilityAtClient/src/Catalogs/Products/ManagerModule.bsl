
Procedure Noncompiant() Export
	// empty
EndProcedure

#If Server Or ThickClientOrdinaryApplication Or ExternalConnection Then

Procedure ChoiceDataGetProcessing(ChoiceData, Parameters, StandardProcessing)
	// Complaint
EndProcedure

Procedure Compiant() Export
	// empty
EndProcedure

#EndIf

Procedure PresentationFieldsGetProcessing(Fields, StandardProcessing)
	// Complaint
EndProcedure

Procedure Noncompiant2() Export
	// empty
EndProcedure
