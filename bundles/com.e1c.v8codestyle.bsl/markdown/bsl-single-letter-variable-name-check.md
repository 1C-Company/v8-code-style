# Variable has a single letter name

Variable has a single letter name

## Noncompliant Code Example

```bsl
Procedure IncorrectDeclaredName() Export
    //@skip-check module-unused-local-variable
    var a;
EndProcedure
```

```bsl
Procedure IncorrectInitializationName() Export
    //@skip-check module-unused-local-variable
    a = 0;
EndProcedure
```

```bsl
//@skip-check module-empty-method
Procedure IncorrectParameterName(p) Export
    
EndProcedure
```


## Compliant Solution

```bsl
Procedure correctDecalaredName() Export
    //@skip-check module-unused-local-variable
    var variable; 
EndProcedure
```

```bsl
Procedure CorrectInitailizationName() Export
    //@skip-check module-unused-local-variable
    variable = 0;
EndProcedure
```

```bsl
//@skip-check module-empty-method
Procedure CorrrectParameterName(parameter) Export
    
EndProcedure
```


## See