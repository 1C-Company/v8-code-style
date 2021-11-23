
&AtClient
Procedure Noncompliant()

	Notify = new NotifyDescription("NoncompliantNotify", ThisObject);
	
	Notify = new NotifyDescription("NoncompliantNotify2", ThisObject);
	
EndProcedure

&AtClient
Procedure Compliant()

	Notify = new NotifyDescription("CompliantNotify", ThisObject);
	
EndProcedure

&AtClient
Procedure CompliantNotify() Export

EndProcedure

&AtClient
Procedure NoncompliantNotify()

EndProcedure

&AtServer
Procedure NoncompliantNotify2() Export

EndProcedure
