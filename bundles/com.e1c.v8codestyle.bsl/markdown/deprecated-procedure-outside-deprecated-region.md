# Deprecated procedure (function) is oustside the Deprecated region

Deprecated **export** procedure (function) should be placed in the **Deprecated** region of the **Public** region in a common module area.
In procedures and functions available in the **Deprecated** region,
deviations from other development standards according to cl. 1.1 are allowed.
In this case, there is no need to rewrite the existing application code. 
If the decision is taken to delete all obsolete functions upon release of a new library revision,
such functions can be easily identified in the library code and deleted.

## Noncompliant Code Example

```bsl

#Region Public

// Deprecated. Instead, use SupportedProcedure

Procedure DeprecatedProcedure() Export

    DeprecatedProcedure()
    
EndProcedure

#EndRegion

```


## Compliant Solution

```bsl

#Region Public

#Region Deprecated

// Deprecated. Instead, use SupportedProcedure

Procedure DeprecatedProcedure() Export

    DeprecatedProcedure()
    
EndProcedure
    
#EndRegion

#EndRegion

```

## See

[Ensuring library compatibility](https://support.1ci.com/hc/en-us/articles/360011003280-Ensuring-library-compatibility)
