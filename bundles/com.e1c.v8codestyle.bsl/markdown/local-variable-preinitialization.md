# Local Variable Initialization

This check searches for variables that have not been initialized within the current scope or any of its parent scopes.

## Incorrect

```bsl
Procedure Incorrect()

	If 1 = 1 Then
		A = 1;
	EndIf;
	
	B = A;

EndProcedure
```

## Correct

```bsl
Procedure Correct()
	
	A = 0;

	If 1 = 1 Then
		A = 1;
	EndIf;
	
	B = A;

EndProcedure
```

## See also

[Standard](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Using_1C_Enterprise_language_structures/Local_variable_preinitialization/?language=en)