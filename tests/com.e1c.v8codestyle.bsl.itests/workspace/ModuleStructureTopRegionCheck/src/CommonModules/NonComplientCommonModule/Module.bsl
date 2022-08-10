
#Region NonPublic

Procedure PublicMethod() Export
	PrivateMethod();
EndProcedure

#EndRegion

#Region Internal

Procedure InternalMethod() Export
	PrivateMethod();
EndProcedure

#EndRegion

#Region Private

Procedure PrivateMethod()
	// add code
EndProcedure

#EndRegion