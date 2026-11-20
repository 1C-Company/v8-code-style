
&AtClient
Procedure Noncompliant()

	Notify = new NotifyDescription("NoncompliantNotify", ThisObject);
	
	Notify = new NotifyDescription("NoncompliantNotify2", ThisObject);
	
	Notify = new NotifyDescription("NoSuchProcedure", ThisObject);
	
EndProcedure

&AtClient
Procedure Compliant()

	Notify = new NotifyDescription("CompliantNotify", ThisObject);

	Notify = new NotifyDescription("NoSuchProcedure", FormOvner);
	
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
