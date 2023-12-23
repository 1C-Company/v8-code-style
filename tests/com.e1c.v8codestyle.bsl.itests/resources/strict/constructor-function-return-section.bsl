// @strict-types

// Returns:
//  Structure - Non complaint:
// * Key1 - Number -
// * Key3 - Number -
Function NonComplaint() Export
	
	return new Structure("Key1, Key2", 10, "");
	
EndFunction

// Returns:
//  Structure - complaint:
// * Key1 - Number -
// * Key2 - String -
Function Complaint() Export
	
	return new Structure("Key1, Key2", 10, "");
	
EndFunction

// Returns:
//  Structure - complaint:
// * Key1 - Number -
// * Key2 - String -
Function MissingReturnType() Export
	if (1 < 1) then
		return true;
	endif;
	
	return new Structure("Key1, Key2", 10, "");
EndFunction

// Returns:
//  Structure - complaint:
// * Key1 - Number -
// * Key2 - CommonModule -
Function CorrectCheckForCommonModule() Export
	
	return new Structure("Key1, Key2", 10, CommonModule);
EndFunction



