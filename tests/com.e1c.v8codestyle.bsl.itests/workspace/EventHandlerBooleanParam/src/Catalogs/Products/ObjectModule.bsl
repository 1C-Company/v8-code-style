
Procedure BeforeWrite(CancelRenamed)
	
	// Noncompliant
	CancelRenamed = CancelRenamed AND NeedProcess() AND False;
	
EndProcedure

Procedure BeforeDelete(CancelRenamed)
	
	// Compliant
	CancelRenamed = True;
	
EndProcedure


Procedure FillCheckProcessing(Cancel, CheckedAttributes)
	
	// Compliant
	Cancel = Cancel OR NeedProcess();
	
EndProcedure


Function NeedProcess()
	Return True;
EndFunction
