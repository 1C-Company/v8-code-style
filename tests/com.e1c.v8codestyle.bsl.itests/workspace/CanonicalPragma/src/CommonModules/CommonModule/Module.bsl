Procedure MyCorrectProcedureBeforeAfter(Param) Export
	
	Param = 1;
	
EndProcedure

Function MyCorrectFunctionAround(Param) Export
	
	Param = 1;
	
	Return Param;
	
EndFunction

Function MyCorrectFunctionChangeAndValidate(Param) Export
	
	Param = 1;
	
	Return Param;
	
EndFunction

Procedure MyIncorrectProcedureBeforeAfter(Param) Export
	
	Param = 1;
	
EndProcedure

Function MyIncorrectFunctionAround(Param) Export
	
	Param = 1;
	
	Return Param;
	
EndFunction

Function MyIncorrectFunctionChangeAndValidate(Param) Export
	
	Param = 1;
	
	Return Param;
	
EndFunction

