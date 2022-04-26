# Excessive named self reference in common module

Excessive usage of named self reference in common module (when referencing method, property or attribute).
For cached modules self reference is allowed.

## Noncompliant Code Example

Inside common module named "MyModule":

```bsl
Var myParam;

Function test() Export
	// code here
EndFunction

MyModule.myParam = MyModule.test();
```

## Compliant Solution

Inside common module named "MyModule":

```bsl
Var myParam;

Function test() Export
    // code here
EndFunction

myParam = test();
```

Inside common module named "MyModuleCached":

```bsl
Var myParam;

Function test() Export
    // code here
EndFunction

myParam = MyModuleCached.test();
```

## See

