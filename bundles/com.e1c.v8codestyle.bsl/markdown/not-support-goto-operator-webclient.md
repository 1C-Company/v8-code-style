# Unsupported Goto operator

Do not use the GoTo operator in common modules with the "Client (managed application)" flag selected, 
command modules, and client code of managed form modules as this method is not supported in the web client.

## Noncompliant Code Example

If ChartOfCalculationTypes = Object.ChartOfCalculationTypes Then

 GoTo ~ChartOfCalculationTypes;

 EndIf;

## Compliant Solution:

If ChartOfCalculationTypes = Object.ChartOfCalculationTypes Then

 ProcessChartOfCalculationTypes();

 EndIf;
 
 ## See
 
[GoTo operator](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Using_1C_Enterprise_language_structures/GoTo_operator/?language=en)
 