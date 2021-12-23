
Procedure Noncompliant()

#If Client Then

	Notify = new NotifyDescription("NoncompliantNotify", ThisObject);
	
	Notify = new NotifyDescription("NoncompliantNotify2", ThisObject);
	
#EndIf

EndProcedure

Procedure Noncompliant2()

#If Client Then
	
	Notify = new NotifyDescription("NoncompliantNotify", CommonModule.ThisObject);
	
	Notify = new NotifyDescription("NoncompliantNotify2", CommonModule.ThisObject);
	
#EndIf

EndProcedure

Procedure NoncompliantNotify()

EndProcedure

Procedure NoncompliantNotify2() Export

EndProcedure
