#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)
	
	RetReference(CheckedAttributes).Add("hello");

EndProcedure

Function RetReference(MyVar)
	Return MyVar;	
EndFunction

#EndRegion
