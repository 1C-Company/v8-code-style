# Missing semicolon at the end of statement

The semicolon at the end of the last statement is not required, but is preferred.

## Noncompliant Code Example

Procedure procedureName(Parameters)
    
    A = 1
    
EndProcedure

## Compliant Solution

Procedure procedureName(Parameters)
    
    A = 1;
    
EndProcedure