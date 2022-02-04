
// For export method all parameters should be in parameter section
// 
// Parameters:
//  Parameters - Only first parameter here
Procedure NonComplaint(Parameters, SecondParameter) Export
	// empty
EndProcedure

// Parameters:
//  Parameters - Method should not have parameter section
Procedure NonComplaint2() Export
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
