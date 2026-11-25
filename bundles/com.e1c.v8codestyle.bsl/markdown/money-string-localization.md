# Check the localization of money strings

When using the Standard Subsystem Library in a configuration, do not use the Number type constructor to obtain a 
currency field type description. Instead, use a function that returns a description based on the defined type.

## Noncompliant Code Example

TypeDescriptionSum = New TypeDescription("Number", New NumberQualifiers(15,2));

## Compliant Solution

DescriptionTypes Amount = WorkingWithCurrencyRates.MoneyFieldTypeDescription();

## See
[Money Fields: Localization Requirements](https://its.1c.ru/db/v8std#content:778:hdoc)