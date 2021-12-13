// @strict-types

Procedure NonComplaint() Export
	
	Result = StrConcat(, ); 
	
EndProcedure

// Parameters:
//  Parameters - Structure:
//  * Key1 - Number - has type for key
Procedure Complaint(Parameters) Export
	
	Result = InformationRegisters["MyName"].SliceFirst(, Parameters);
	
EndProcedure

// Parameters:
//  Parameters - Structure:
//  * Key1 - Number - has type for key
Procedure Complaint2(Parameters) Export
	
	Result = InformationRegisters["MyName"].SliceFirst(Undefined, Parameters);
	
EndProcedure
