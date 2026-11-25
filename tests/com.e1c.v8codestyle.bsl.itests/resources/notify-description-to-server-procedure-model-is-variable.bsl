Procedure SomeProcecure(SomeObject)

#If Client Then

	Notify = new NotifyDescription("NoncompliantNotify", SomeObject);
	
#EndIf

EndProcedure

Procedure SomeProcecure2(SomeObject)

#If Client Then

	Notify = new NotifyDescription("NoncompliantNotify", SomeObject.ThisForm);
	
#EndIf

EndProcedure
