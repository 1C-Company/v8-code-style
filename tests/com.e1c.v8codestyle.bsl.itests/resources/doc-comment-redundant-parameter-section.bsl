
// Parameters:
//  Parameters - Method should not have parameter section
Procedure NonComplaint() Export
	// empty
EndProcedure

// without parameter setion
Procedure Complaint() Export
	// empty
EndProcedure

// Parameters:
//  Parameters - local methods may not contains all parameters
Procedure Complaint2(Parameters, SecondParameter)
	// empty
EndProcedure
