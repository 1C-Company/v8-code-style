# Optional form parameter access

Form parameters must be declared explicitly on the Parameters tab of the form editor.
In this case, the OnCreateOnServer handler code does not need to check for the existence of properties
at the Parameters structure, and the composition of the form parameters itself is explicitly declared 
(therefore, they are not requiredrecover by examining the entire code of the OnCreateOnServer handler).

## Noncompliant Code Example

```bsl
&AtServer
Procedure OnCreateAtServer(Cancel, StandardProcessing)
    FirstNameLastName = Undefined;
    If Parameters.Property("FirstNameLastName", FirstNameLastName) Then
        Object.Name = FirstNameLastName;
    EndIf;
EndProcedure
```

## Compliant Solution

```bsl
&AtServer
Procedure OnCreateAtServer(Cancel, StandardProcessing)
    Object.Name = Parameters.FirstNameLastName;
EndProcedure
```

## See

- [Opening parameterized forms](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Designing_user_interfaces/Implementation_of_form/Opening_parameterized_forms/)