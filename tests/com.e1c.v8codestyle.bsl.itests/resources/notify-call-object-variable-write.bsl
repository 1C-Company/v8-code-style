&AtClient
Procedure ClientProcedure()
	
	ServerProcedure();
	
	Notify("EventName", "SomeData", ThisObject);

EndProcedure

&AtServer
Procedure ServerProcedure()
	
	ObjTestCatalog = Catalogs.TestCatalog.CreateItem();
	ObjTestCatalog.Write();	
		
EndProcedure