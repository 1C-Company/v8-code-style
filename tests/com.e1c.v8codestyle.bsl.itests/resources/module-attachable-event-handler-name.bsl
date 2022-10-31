
// Parameters:
//  Item - FormField
Procedure Incorrect(Item)
	
	Item.SetAction("OnChange", "CorrectOnChange");
	
EndProcedure


// Parameters:
//  Item - FormField
Procedure Correct(Item)
	
	Item.SetAction("OnChange", "Attachable_CorrectOnChange");
	
EndProcedure