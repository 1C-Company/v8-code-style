#Region Abcd

Var MyVar;

Procedure FillCheckProcessing(Cancel, CheckedAttributes)
	
	MyVar = CheckedAttributes;
	RetReference().Add("new");

EndProcedure

Function RetReference()
	Return MyVar;
EndFunction

#EndRegion
