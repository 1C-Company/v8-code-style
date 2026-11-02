Function Test()
    
    Query = New Query;
    
    QueryResult = Query.Execute();
    QuerySelect = QueryResult.Select();
    
    If QuerySelect.Next() Then
       return true;
    else 
        return false;
    EndIF
    
EndFunction