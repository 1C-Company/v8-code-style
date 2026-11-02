&AtClient
Procedure ClientProcedure()
	
	ServerProcedure();
	
EndProcedure

&AtServer
Procedure ServerProcedure()
	
	ObjTestCatalog = GetObject();
	ObjTestCatalog.Write();	
		
EndProcedure

&AtServer
Function GetObject()

	Return Catalogs.TestCatalog.CreateItem();

EndFunction