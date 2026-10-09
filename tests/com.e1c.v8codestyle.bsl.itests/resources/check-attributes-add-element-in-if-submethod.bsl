#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)

	If (1 = 1) Then
		If (RetBoolean(CheckedAttributes)) Then
		EndIf;
	EndIf;

EndProcedure

Function RetBoolean(MyVar)
	If (1 = 1) Then
		MyVar.Add("some");
		Return False;
	EndIf;
	Return True;
EndFunction

#EndRegion
