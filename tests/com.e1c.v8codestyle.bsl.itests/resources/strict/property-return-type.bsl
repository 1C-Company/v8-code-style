// @strict-types

Procedure NonComplaint(Module) Export
	
	Key1 = CostomAction().Key1;
	
EndProcedure

Procedure Complaint() Export
	
	Key1 = CostomAction2().Key1;
	
EndProcedure


Function CostomAction() Export
	
	return new Structure("Key1");
	
EndFunction

Function CostomAction2() Export
	
	return new Structure("Key1", 10);
	
EndFunction
