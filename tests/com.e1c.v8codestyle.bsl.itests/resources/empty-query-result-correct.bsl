Function Test()
		
	Query = New Query;
	
	QueryResult = Query.Execute();
	QuerySelect = QueryResult.Select();
	
    return NO QueryResult.isEmpty();
	
EndFunction