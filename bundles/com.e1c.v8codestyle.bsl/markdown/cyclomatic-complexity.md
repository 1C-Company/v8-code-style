# The cyclomatic complexity of the method is exceeded

Thomas J. McCabe developed a cyclomatic metric for code testing tasks.
His proposed method calculates the number of linearly independent program execution threads.
The difficulty value corresponds to the number of tests required.

The most effective way to reduce the complexity score is to decompose the code and simplify logical expressions.

## Compute rules:

### Increments:

- Loops:
```bsl
For Item In Collection Do
    // code
EndDo;
```
```bsl
For index = 1 To N Do
    // code
EndDo;
```
```bsl
While Conditional Do
    // code
EndDo;
```
- Conditional statements:
```bsl
If Conditional Then // +1 point
    // code
EndIf;

If Conditional Then // too +1 point
    // code
Else
    // code
EndIf;
```
```bsl
If Conditional1 Then // +1 point
    // code
ElseIf Conditional2 Then // +1 additional point 
    // code
ElseIf Conditional3 Then // +1 additional point
    // code
Else // the Else does not increase the complexity
    // code
EndIf;
```
- Try-Exception blocks:
```bsl
Try
    // code
Exception
    // code
EndTry;
```
- Booleands operands: AND, OR
- Trenary operator:
```bsl
?(Coditional, Value1, Value2);
```
- Procedure or Function initially has a complexity equal to 1
