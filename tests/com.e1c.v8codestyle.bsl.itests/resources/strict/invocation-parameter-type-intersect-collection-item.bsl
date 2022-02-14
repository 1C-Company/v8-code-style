// @strict-types

// Parameters:
// MapParamenter - Map of KeyAndValue:
//  * Key - Number -
//  * Value - Number -
Procedure NonComplaint(MapParamenter) Export
	
	Array = new Array; // Array of Number
	Array.Add("");
	
	MapParamenter.Insert("",
	   False);
	
EndProcedure


// Parameters:
// MapParamenter - Map of KeyAndValue:
//  * Key - Number -
//  * Value - Number -
Procedure Complaint(MapParamenter) Export
	
	Array = new Array; // Array of Number
	Array.Add(10);
	
	MapParamenter.Insert(10,
	   10);
	
EndProcedure

