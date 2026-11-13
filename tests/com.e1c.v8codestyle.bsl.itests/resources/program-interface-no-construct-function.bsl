#Region Public

Function NoConstruct1()

	Return 1;
	
EndFunction

Function NoConstruct2()

	If 1 = 1 Then

		Return 1;

	EndIf;
	
	Return new Array;
	
EndFunction

Function NoConstruct3()

	If 1 = 1 Then

		Return 1;
		
	Else

		Return 2;

	EndIf;
	
	Return new Array;
	
EndFunction

#EndRegion