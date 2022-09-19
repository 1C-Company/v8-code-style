# Typed value is added to untyped collection

Checks that collection method ```Add()``` is calling for untyped collection

## Noncompliant Code Example

```bsl
// @strict-types

Result = New Array();
	
Result.Add(42);
```

## Compliant Solution

```bsl
// @strict-types

Result = New Array(); // Array of Number
	
Result.Add(42);
```

## See

