#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)
	
	DeleteMethod(CheckedAttributes);

EndProcedure

Procedure DeleteMethod(MyVar) 
	DeleteSubMethod(MyVar);
EndProcedure

Procedure DeleteSubMethod(MyVar)
	MyVar.Delete("new");
EndProcedure

#EndRegion
