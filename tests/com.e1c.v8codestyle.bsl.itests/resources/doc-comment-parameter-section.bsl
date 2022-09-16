
// For export method all parameters should be in parameter section
// 
// Parameters:
//  Parameters - Only first parameter here
Procedure NonComplaint(Parameters, SecondParameter) Export
	// empty
EndProcedure

// See Complaint
Procedure NonComplaint2(Parameters, SecondParameter) Export
	// empty
EndProcedure

// No parameter section
Procedure NonComplaint3(Parameters) Export
	// empty
EndProcedure

// Parameters:
//  Parameters - Structure
Procedure Complaint(Parameters) Export
	// empty
EndProcedure

// Parameters:
//  Parameters - local methods may not contains all parameters
Procedure Complaint2(Parameters, SecondParameter)
	// empty
EndProcedure

// local methods may not contains parameters secrion
Procedure Complaint3(Parameters)
	// empty
EndProcedure

