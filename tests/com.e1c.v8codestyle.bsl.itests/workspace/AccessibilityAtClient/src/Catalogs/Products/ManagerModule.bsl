
Procedure Noncompiant() Export
	// empty
EndProcedure

#If Server Or ThickClientOrdinaryApplication Or ExternalConnection Then

Procedure ChoiceDataGetProcessing(ChoiceData, Parameters, StandardProcessing)
	// Non-complaint
EndProcedure

Procedure Compiant() Export
	// empty
EndProcedure

#EndIf

Procedure FormGetProcessing(FormType, Parameters, SelectedForm, AdditionalInformation, StandardProcessing)
	// Complaint
EndProcedure

Procedure Noncompiant2() Export
	// empty
EndProcedure
