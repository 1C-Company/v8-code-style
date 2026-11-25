#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)
	
	MyVar = RetReference(CheckedAttributes);
	MyVar.Add("hello");

EndProcedure

Function RetReference(MyVar)
	Return MyVar;	
EndFunction

#EndRegion
