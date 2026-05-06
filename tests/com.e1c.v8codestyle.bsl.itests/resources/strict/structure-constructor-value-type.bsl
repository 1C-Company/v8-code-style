// @strict-types

Procedure NonComplaint() Export
	
	Params = new Structure("Key1,
	|Key2, Key3", , 1);
	
EndProcedure

Procedure Complaint() Export
	
	Params = new Structure("Key1, Key2", 0, "");
	Params.Insert("Key3", "");
	
EndProcedure
