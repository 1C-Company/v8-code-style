#Region Abcd

Var MyVar;

Procedure FillCheckProcessing(Cancel, CheckedAttributes)
	
	MyVar = CheckedAttributes; 

	AddAttribute();

EndProcedure

Procedure AddAttribute()
	
	MyVar.Add("new");

EndProcedure

#EndRegion
