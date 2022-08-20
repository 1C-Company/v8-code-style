# Variable has a single letter name

Checks if variable (parameter, declared or initialized) has a single letter
name, which violates code style rules.

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