&AtClient
Procedure SomeProcecure()
	
	SomeObject = ThisObject;
	Notify = new NotifyDescription("NoncompliantNotify", SomeObject);

EndProcedure

&AtClient
Procedure NoncompliantNotify()

EndProcedure