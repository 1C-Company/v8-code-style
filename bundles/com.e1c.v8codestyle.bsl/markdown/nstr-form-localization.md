# Checks string localization use Nstr.


## Noncompliant Code Example

ShowMessageBox(, "Text");

## Compliant Solution

ShowMessageBox(, NSTR("en='Text'"));

## See
https://its.1c.ru/db/v8std#content:761:hdoc