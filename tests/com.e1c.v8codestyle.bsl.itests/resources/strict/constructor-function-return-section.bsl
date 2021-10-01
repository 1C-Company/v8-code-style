// @strict-types

// Returns:
//  Structure - Non complaint:
// * Key1 - Number -
// * Key3 - Number -
Function NonComplaint() Export
	
	return new Structure("Key1, Key2", 10, "");
	
EndFunction

// Returns:
//  Structure - Non complaint:
// * Key1 - Number -
// * Key2 - String -
Function Complaint() Export
	
	return new Structure("Key1, Key2", 10, "");
	
EndFunction

