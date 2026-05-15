# A method in a module extension has the same visibility as the source method.

It is considered an error for an extension of a method to make it compile in contexts in which the source method does not compile.

## Noncompliant Code Example

Module configuration

#If Server Then

Procedure Server1()
Everywhere1();
EndProcedure

Procedure Везде1() Export
EndProcedure

#EndIf

Function Test() Export
EndFunction

Module extension

&After("Server1")
Procedure Ext1_Server1()
Everywhere1();
EndProcedure


## Compliant Solution

#If Server Then

Procedure Server1()
Everywhere1();
EndProcedure

Procedure Везде1() Export
EndProcedure

#EndIf

Function Test() Export
EndFunction

Module extension

#If Server Then

&After("Server1")
Procedure Ext1_Server1()
Everywhere1();
EndProcedure

#EndIf

