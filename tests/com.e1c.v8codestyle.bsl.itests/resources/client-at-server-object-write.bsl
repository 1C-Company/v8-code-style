&AtClient
Procedure AfterWrite()

    ServerProcedure();

EndProcedure

&AtClientAtServerNoContext
Procedure ServerProcedure()

    Write();

EndProcedure