
#If Server Or ThickClientOrdinaryApplication Or ExternalConnection Then

#Region Public

Procedure PublicMethod() Export
    PrivateMethod();
EndProcedure

#Region Public

Procedure InternalMethod() Export
    PrivateMethod();
EndProcedure

#EndRegion

#EndRegion

#Region Private

Procedure PrivateMethod()
    // add code
EndProcedure

#EndRegion

#EndIf
