&AtClient
Procedure AfterWrite()

    ServerMethod1();
    ServerMethod2();

EndProcedure

&AtServer
Procedure ServerMethod1()

    Message("Test");

EndProcedure

&AtServer
Procedure ServerMethod2()

    Write();

EndProcedure