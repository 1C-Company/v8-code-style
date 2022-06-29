// @strict-types

// Parameters:
// List - ValueList of ValueListItem:
// * Value - String
Procedure NonComplaint(List) Export
	
	Number = 1;
	List.Add(Number);
	
EndProcedure

// Parameters:
// List - ValueList of ValueListItem:
// * Value - String
Procedure Complaint() Export
	
	Text = "";
    List.Add(Text);
	
EndProcedure

