# Indentation in Documentation Comments

A comment line must not contain indentations that mix tabs and spaces simultaneously.

## Incorrect

```bsl
//	 This line contains both a tab and a space character in its indentation (error)
//
//  Parameters:
//   Parameter1 – Type1 - description
//		 This line contains two tabs and a space character simultaneously (error)
//   Parameter2 – Type2 - description
//		 Second line
Procedure Incorrect(Parameter1, Parameter2)
	
EndProcedure

```

## Correct

```bsl
//  This line contains two spaces in its indentation
//
//  Parameters:
//   Parameter1 – Type1 - description
//		This line contains two tab characters in its indentation
//   Parameter2 – Type2 - description
//		Second line
Procedure Correct(Parameter1, Parameter2)
	
EndProcedure
```

## See also

