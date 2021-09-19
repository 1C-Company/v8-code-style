&Before("MyCorrectProcedureBeforeAfter")
Procedure Ext_MyCorrectProcedureBefore(Param) Export
	// Before
EndProcedure

&After("MyCorrectProcedureBeforeAfter")
Procedure Ext_MyCorrectProcedureAfter(Param) Export
	// After
EndProcedure

&Around("MyCorrectFunctionAround")
Function Ext_MyCorrectFunctionAround(Param) Export
	// Around
EndFunction

&ChangeAndValidate("MyCorrectFunctionChangeAndValidate")
Function Ext_MyCorrectFunctionChangeAndValidate(Param) Export

	Param = 1;

	Return Param;

EndFunction

&BeforE("MyIncorrectProcedureBeforeAfter")
Procedure Ext_MyIncorrectProcedureBefore(Param) Export
	// Before
EndProcedure

&AFter("MyIncorrectProcedureBeforeAfter")
Procedure Ext_MyIncorrectProcedureAfter(Param) Export
	// After
EndProcedure

&ArOUnd("MyIncorrectFunctionAround")
Function Ext_MyIncorrectFunctionAround(Param) Export
	// Around
EndFunction

&CHangeandVAlidate("MyIncorrectFunctionChangeAndValidate")
Function Ext_MyIncorrectFunctionChangeAndValidate(Param) Export

	Param = 1;

	Return Param;

EndFunction

&UnknownPragma("UnknownPragma")
Procedure UnknownPragma() Export
	// Unknown pragma
EndProcedure