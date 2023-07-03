// @strict-types

Procedure NonComplaint() Export
	Var TestVar;
	// empty
EndProcedure

Procedure Complaint() Export
	Var TestVar; // Number
	
	MyArray = NewArray();
	
EndProcedure

// Parameters:
//  Object - Arbitrary
//  AttributeName - String
Procedure Complaint2(Object, AttributeName) Export
	
	TestVar2 = Object[AttributeName]; // Number
	
	TestVar3 = Object[AttributeName];
	
	#If MobileStandaloneServer Then
		TestVar4 = Object[AttributeName]; 
	#EndIf
	
EndProcedure

// Returns:
// Array of Number - new array
Function NewArray()

EndFunction
