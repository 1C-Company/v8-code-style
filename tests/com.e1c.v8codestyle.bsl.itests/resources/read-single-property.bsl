Procedure DoIt()

    getCode(Catalogs.Catalog.GetRef());

EndProcedure

// Get code.
// 
// Parameters:
//  CatalogRef - CatalogRef.CatalogWrong - Catalog ref
// 
// Returns:
//  String - Get code
Function getCode(CatalogRef)
    
    return CatalogRef.Code;
    
EndFunction