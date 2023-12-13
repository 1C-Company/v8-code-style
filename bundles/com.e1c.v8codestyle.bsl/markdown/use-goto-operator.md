# Goto operator use

Avoid using the GoTo operator in 1C:Enterprise language code as its use can result 
in complicated and ill-structured modules. It is difficult to understand the execution 
sequence and interrelation of its snippets. Instead of the GoTo operator, use other statements 
of 1C:Enterprise language. 

## Noncompliant Code Example

```bsl
If ChartOfCalculationTypes = Object.ChartOfCalculationTypes Then

  GoTo ChartOfCalculationTypes;

EndIf;
```

## Compliant Solution

```bsl
If ChartOfCalculationTypes = Object.ChartOfCalculationTypes Then

  ProcessChartOfCalculationTypes();

EndIf;
```

## See

- [Restrictions on using Go operator](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Using_1C_Enterprise_language_structures/Restrictions_on_using_Go_operator/?language=en)