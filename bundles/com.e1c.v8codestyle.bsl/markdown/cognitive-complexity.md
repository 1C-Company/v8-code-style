# The cognitive complexity of the method is exceeded

The high score of the cognitive complexity to complicate perception and maintainability of the written code.

The most effective way to reduce the complexity score is to decompose the code and simplify logical expressions.

## Compute rules:

### Increments:

- Conditional statements:
```bsl
If Conditional1 Then // +1 point
    // Code
ElseIf Conditional2 Then // +1 point
    // Code
Else // +1 point
    // Code
EndIf;
```
- Trenary operator:
```bsl
?(conditional, positive_code, negative_code); // +1 point
```
- Loops:
```bsl
For Each Element In Collection Do // +1 point
    // code
EndDo;
```
```bsl
For index = 1 To N Do // +1 point
	// code
EndDo;
```
```bsl
While True Do // +1 point
	// code
EndDo;
```
- Exception block:
```bsl
...
Exception // +1 point
    // code
EndTry;
```
- GoTo:
```bsl
Goto ~Label; // +1 point
```
- boolean operands AND, OR
```bsl
    conditional1 = predicate1 OR predicate2; // +1 point
    conditional2 = predicate1 AND predicate2; // +1 point
```
- recursive call:
```bsl
Procedure RecursiveCall(Collection)
    If conditional Then
        RecursiveCall(...); // +1 point
    EndIf;
EndProcedure
```

### Nesting increments:

- IF-part of conditional statement:
```bsl
If conditional1 then // +nesting level
    // code
ElseIf conditional2 then
    // code
Else
    // code
EndIf;
```
- Trenary operator:
```bsl
    ?(conditional, value1, value2); // +nesting level
```
- Loops:
```bsl
For Each Element In Collection Do // +nesting level
    // code
EndDo;
```
```bsl
For index = 1 To N Do // +nesting level
	// code
EndDo;
```
```bsl
While True Do // +nesting level
	// code
EndDo;
```
- exception block:
```bsl
...
Exception // +nesting level
    // code
EndTry;
```

### Nesting level:

- Conditional statements:
```bsl
If Conditional1 Then // increment depth
    // Code
ElseIf Conditional2 Then // increment depth
    // Code
Else // increment depth
    // Code
EndIf;
```

- Trenary operator:
```bsl
?(
    conditional,
    // increment depth,
    // increment depth
)
```
- Loops:
```bsl
For Each Element In Collection Do
    // increment depth
EndDo;
```
```bsl
For index = 1 To N Do
	// increment depth
EndDo;
```
```bsl
While True Do
	// increment depth
EndDo;
```
- Exception block:
```bsl
...
Exception
    // increment depth
EndTry;
```
- Nesting method:
```bsl
Method(
    // increment depth
    NestingMethod(
        // increment depth
    )
);
```
