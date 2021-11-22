// @strict-types

Procedure NonComplaint(Module) Export
	
	Key1 = CustomAction().Key1;
	
EndProcedure

Procedure Complaint() Export
	
	Key1 = CustomAction2().Key1;
	
EndProcedure


Function CustomAction() Export
	
	return new Structure("Key1");
	
EndFunction

Function CustomAction2() Export
	
	return new Structure("Key1", 10);
	
EndFunction
