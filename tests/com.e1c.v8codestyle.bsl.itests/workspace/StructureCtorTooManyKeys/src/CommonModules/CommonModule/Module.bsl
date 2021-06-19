
Procedure Correct(Parameters) Export
	
	Parameters = new Structure("Key1, Key2, Key3");
	
EndProcedure

Procedure Incorrect(Parameters) Export
	
	Parameters = new Structure("Key1, Key2, Key3, Key4");
	
EndProcedure

Procedure Suppressed(Parameters) Export
	
	//@skip-check structure-consructor-too-many-keys
	Parameters = new Structure("Key1, Key2, Key3, Key4");
	
EndProcedure
