# Self reference is excessive

Excessive usage of self reference with use of `ThisObject` (when referencing method, property or attribute).

Check common module, object module, recordset module, value manager module, form module.
Check of onject module, recordset module and value manager module can be disable, if
`Check object (recordset, value manager) module` isn't set.

For form modules only check self reference for methods and existing properties
(if `Check only existing form properties` parameter is set, otherwise, check for all cases).

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

