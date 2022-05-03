# Outdated alias used

Usage of outdated self reference `ThisForm` (when referencing method, property or attribute)

## Noncompliant Code Example

```bsl
Var myParam;

Function test() Export
	// code here
EndFunction

ThisForm.myParam = ThisForm.test();
```

## Compliant Solution

```bsl
Var myParam;

Function test() Export
    // code here
EndFunction

ThisObject.myParam = ThisObject.test();
```

## See

