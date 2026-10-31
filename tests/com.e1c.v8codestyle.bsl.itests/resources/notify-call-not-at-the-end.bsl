&AtClient
Procedure AfterWrite()

    ServerProcedure();
    Notify("RecordChanged");
    Message("Done");

EndProcedure

&AtServer
Procedure ServerProcedure()

    Write();

EndProcedure