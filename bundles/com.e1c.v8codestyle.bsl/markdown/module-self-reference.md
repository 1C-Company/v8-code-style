# Self reference is excessive

Excessive usage of self reference with use of `ThisObject` (when referencing method, property or attribute)

For form modules only check self reference for methods and existing properties
(if `Check only existing form properties` parameter is set, otherwise, check for all cases)

## Noncompliant Code Example

```bsl
Var myParam;

Function test() Export
	// code here
EndFunction

ThisObject.myParam = ThisObject.test();
```

## Compliant Solution

```bsl
Var myParam;

Function test() Export
    // code here
EndFunction

myParam = test();
```

## See

