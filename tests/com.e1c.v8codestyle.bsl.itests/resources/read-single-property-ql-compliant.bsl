Procedure DoIt()

    getCode(Catalogs.Catalog.GetRef());

EndProcedure

// Get code.
// 
// Parameters:
//  CatalogRef - CatalogRef.Catalog - Catalog ref
// 
// Returns:
//  String - Get code
Function getCode(CatalogRef)
    
    Query = New Query;
    Query.Text =
        "SELECT
        |   Catalog.Code
        |FROM
        |   Catalog.Catalog AS Catalog
        |WHERE
        |   Catalog.Ref = &Ref";
    
    Query.SetParameter("Ref", CatalogRef);
    
    QueryResult = Query.Execute();
    
    SelectionDetailRecords = QueryResult.Select();
    
    While SelectionDetailRecords.Next() Do
        return SelectionDetailRecords.Code;
    EndDo;
    
EndFunction