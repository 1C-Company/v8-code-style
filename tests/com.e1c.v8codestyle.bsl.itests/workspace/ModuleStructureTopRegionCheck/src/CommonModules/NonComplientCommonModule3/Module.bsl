
#Region Private

Procedure PrivateMethod()
	// add code
EndProcedure

#EndRegion

#Region Public

Procedure PublicMethod() Export
	PrivateMethod();
EndProcedure

#EndRegion

#Region Internal

Procedure InternalMethod() Export
	PrivateMethod();
EndProcedure

#EndRegion