&AtClient
Procedure AfterWrite()

    ServerProcedure();

EndProcedure

&AtServer
Procedure ServerProcedure()

    Message("Test");

EndProcedure