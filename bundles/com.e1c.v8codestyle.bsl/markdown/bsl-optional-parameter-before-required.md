# Optional parameters before required

Optional parameters (with default values) must follow required parameters (without default values). 

## Noncompliant Code Example

```bsl
Function ExchangeRateOnDate(Date = Undefined, Currency) Export
```

## Compliant Solution

```bsl
Function ExchangeRateOnDate(Currency, Date = Undefined) Export
```

## See

- [Procedure and function parameters](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Module_formatting/Procedure_and_function_parameters/)