#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)
	
	DeleteEl(CheckedAttributes);

EndProcedure

Procedure DeleteEl(MyVar)
	MyVar.Delete("old");	
EndProcedure

#EndRegion
