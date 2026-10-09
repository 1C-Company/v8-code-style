#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)

	If (1 = 1) Then
		If (1 <> 1) Then

		Else
			MyVar = RetSomeArray(CheckedAttributes);
			MyVar.Add("new");
		EndIf;
	EndIf;

EndProcedure

Function RetSomeArray(MyVar)
	If (1 = 1) Then
		MyVar.Add("new");
	EndIf;
	Return New Array;
EndFunction

#EndRegion
