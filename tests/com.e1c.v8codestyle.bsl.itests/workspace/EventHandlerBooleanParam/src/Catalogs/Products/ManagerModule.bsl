

Procedure PresentationGetProcessing(Data, Presentation, StandardProcessingRenamed)
	
	// Noncompliant
	StandardProcessingRenamed = NeedProcess() OR False AND StandardProcessingRenamed;
	
EndProcedure


Procedure FormGetProcessing(FormType, Parameters, SelectedForm, AdditionalInformation, StandardProcessingRename)
	
	// Compliant
	StandardProcessingRename = False;
	
EndProcedure


Procedure PresentationFieldsGetProcessing(Fields, StandardProcessing)
	
	// Compliant
	StandardProcessing = StandardProcessing AND NeedProcess();
	
EndProcedure

Function NeedProcess()
	Return True;
EndFunction
