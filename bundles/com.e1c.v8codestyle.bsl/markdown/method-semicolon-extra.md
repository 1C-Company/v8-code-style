# Exta semicolon at the end of method declaration

A semicolon at the end of a method declaration is not an error, but its absence is preferable.

## Noncompliant Code Example

Procedure procedureName(Parameters);
    
    A = 1;
    
EndProcedure

## Compliant Solution

Procedure procedureName(Parameters)
    
    A = 1;
    
EndProcedure