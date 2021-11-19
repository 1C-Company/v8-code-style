&Around("MyProcedure")
Procedure Ext_MyProcedure(Param) Export
	
	//Param = 1;
	Param = 2;
	
EndProcedure

&Around("MyFunction")
Function Ext_MyFunction() Export
	
	Result = ProceedWithCall();
	
	Result = Result + 1;
	
	Return Result;
	
EndFunction