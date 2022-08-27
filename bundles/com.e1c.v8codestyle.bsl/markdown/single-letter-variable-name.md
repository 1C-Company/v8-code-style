# Variable has a single letter name

Checks if variable (parameter, declared or initialized) has a name, which length is 
less than or equal to the value of parameter, input by user (1 by default)
There is one exception: counters in for-loops can have names of any length

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
    var variable; 
EndProcedure
```

```bsl
Procedure CorrectInitailizationName() Export
    variable = 0;
EndProcedure
```

```bsl
Procedure CorrrectParameterName(parameter) Export
    
EndProcedure
```

```bsl
Procedure prod()
    For i = 1 To 5 Do
        
    EndDo;
КонецПроцедуры 

```bsl
Procedure prod2()
    days = new array();
    days.add("Mn");
    days.add("Tu");
    
    For Each d In days Do
        d = "l";
    EndDo;
КонецПроцедуры
```


## See
