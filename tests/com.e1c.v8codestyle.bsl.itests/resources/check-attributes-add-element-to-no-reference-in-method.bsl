#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)
	
	MyVar = CheckedAttributes;
	MyVar = New Array;
	AddMethod(MyVar);

EndProcedure

Procedure AddMethod(MyVar) 
	MyVar.Add("new");
EndProcedure

#EndRegion
