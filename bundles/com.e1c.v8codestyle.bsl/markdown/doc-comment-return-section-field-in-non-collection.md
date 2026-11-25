# The return value section of the documentation comment contains correct collection types
# If a return parameter is described as a collection, but the parameter type itself is not a collection, 
# this is considered an error.

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