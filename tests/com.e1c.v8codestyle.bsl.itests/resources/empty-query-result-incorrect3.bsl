Function Test()
    
    Query = New Query;
    
    QueryResult = Query.Execute();
    QuerySelect = QueryResult.Select();
    
    for 1 to 5 do
    If QuerySelect.Next() Then
       return true;
    else 
        return false;
    EndIF
    EndFor 
    
EndFunction