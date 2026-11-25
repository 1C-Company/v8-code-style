&AtClient
Procedure ClientProcedure()
	
	ServerProcedure();
	
EndProcedure

&AtServer
Procedure ServerProcedure()
	
	ObjTestCatalog = Catalogs.TestCatalog.CreateItem();
	ObjTestCatalog.Write();	
		
EndProcedure