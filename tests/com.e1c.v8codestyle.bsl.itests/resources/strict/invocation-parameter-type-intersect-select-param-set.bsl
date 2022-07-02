// @strict-types

// Parameters:
// TabularSection - TabularSection
Procedure NonComplaint(TabularSection) Export
	
	Filter = New Structure();;
    Row = TabularSection.Unload(Filter,
         1);
	
EndProcedure

// Parameters:
// TabularSection - TabularSection
Procedure NonComplaint2(TabularSection) Export
	
    Row = TabularSection.Unload(1,
         "LineNumber, Ref");
	
EndProcedure

// Parameters:
// TabularSection - TabularSection
Procedure Complaint(TabularSection) Export
	
	Filter = New Structure();;
    Row = TabularSection.Unload(Filter, 
        "LineNumber, Ref");
	
EndProcedure
