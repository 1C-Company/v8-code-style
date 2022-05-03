# Excessive named self reference in manager module

Excessive usage of named self reference in manager module (when referencing method, property or attribute).

## Noncompliant Code Example

Inside Catalog manager module named "MyCatalog":

```bsl
Var myParam;

Function test() Export
    // code here
EndFunction

Catalog.MyCatalog.myParam = Catalog.MyCatalog.test();
```

## Compliant Solution

Inside Catalog manager module named "MyCatalog":

```bsl
Var myParam;

Function test() Export
    // code here
EndFunction

myParam = test();
```

## See

