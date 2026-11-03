#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)

	If (1 = 1) Then
		If (1 <> 1) Then
			RetReference(CheckedAttributes).Add("new");
		EndIf;
	EndIf;

EndProcedure

Function RetReference(MyVar)
	If (1 = 1) Then
		Return MyVar;	
	EndIf;
	Return New Array;
EndFunction

#EndRegion
