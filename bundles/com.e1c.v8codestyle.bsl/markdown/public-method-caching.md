# Checks cached public

You should not create an API in modules that reuse return values.

## Noncompliant Code Example

```bsl

#Region Public

Procedure GetData() Export
EndProcedure

#EndRegion

```

## Compliant Solution

```bsl

#Region Internal

Procedure GetData() Export
EndProcedure

#EndRegion

```

## See
