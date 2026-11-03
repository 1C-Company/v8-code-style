&AtClient
Procedure AfterWrite()

    ServerProcedure();

EndProcedure

&AtClient
Procedure ServerProcedure()

    Write();

EndProcedure