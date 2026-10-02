# The return value section of the documentation comment contains correct collection types

## Noncompliant Code Example

```bsl
  
// Return value:
//  String:
//      * Key1 - String
Function Incorrect(Parameters) Export
    // empty
EndFunction

```

## Compliant Solution

```bsl

// Return value:
//  Structure:
//      * Key1 - String
Function Correct(Parameters) Export
    // empty
EndFunction

```

## See