Function Test()
    
    Query = New Query;
    
    QueryResult = Query.Execute();
    QuerySelect = QueryResult.Select();
    
    Try
    If QuerySelect.Next() Then
       return true;
    else 
        return false;
    EndIF
    Catch
    
    EndTry 
    
EndFunction