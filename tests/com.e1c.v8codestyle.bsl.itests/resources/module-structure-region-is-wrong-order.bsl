
#If Server Or ThickClientOrdinaryApplication Or ExternalConnection Then

#Region private

Procedure PrivateMethod()
    // add code
EndProcedure

#EndRegion

#Region public

Procedure PublicMethod() Export
    PrivateMethod();
EndProcedure

#EndRegion

#Region internal

Procedure InternalMethod() Export
    PrivateMethod();
EndProcedure

#EndRegion

#EndIf
