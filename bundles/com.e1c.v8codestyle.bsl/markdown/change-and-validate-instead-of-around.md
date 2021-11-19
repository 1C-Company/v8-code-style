# Use pragma &ChangeAndValidate instead of &Around

Starting with the platform 8.3.16, you can use pragma &ChangeAndValidate instead of pragma &Around in cases where there is no ProceedWithCall call inside the method 

## Noncompliant Code Example

```bsl
&Around("MyFunction")
Function Ext1_MyFunction()
	
	//Return 1;
	Return 2;
	
EndFunction
```

## Compliant Solution

```bsl
&ChangeAndValidate("MyFunction")
Function Ext1_MyFunction()
	
	#Delete
	Return 1;
	#EndDelete
	#Insert
	Return 2;
	#EndInsert
	
EndFunction
```

## See

