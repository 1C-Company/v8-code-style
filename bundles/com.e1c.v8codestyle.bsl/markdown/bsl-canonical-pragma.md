# Pragma is written canonically

Pragmas are written canonically (as in the documentation or the Syntax Helper).

## Noncompliant Code Example

```bsl
&CHANGEandvalidate("MyFunction")
Function Ext1_MyFunction()
	
EndFunction
```

## Compliant Solution

```bsl
&ChangeAndValidate("MyFunction")
Function Ext1_MyFunction()
	
EndFunction
```

## See

