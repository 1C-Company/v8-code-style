// @strict-types

// Parameters:
//  Module - CommonModule - abstract type of any common module
Procedure NonComplaint(Module) Export
	Module = 10;
EndProcedure

// Parameters:
//  Module - CommonModule - abstract type of any common module
Procedure Complaint(Module) Export
	Module = CommonModule; // here is the name of common module
EndProcedure
