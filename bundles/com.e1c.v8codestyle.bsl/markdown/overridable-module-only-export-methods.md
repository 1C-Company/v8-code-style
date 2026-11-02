# Overridable Modules Must Contain Only Exportable Functions

Overridable common modules must contain only exportable procedures that are called from the code of the library itself. In other words, procedures of overridable modules should not be called directly from the consumer configuration code.This restriction is intended to increase the robustness of the configuration code that calls library procedures and functions that make up the library's API. Only exportable procedures and functions of non-overridable common modules should be considered part of the library's API.

## Incorrect

```bsl
Function Test()
    // code
EndFunction
```

## Correct

```bsl
Function Test() Export
    // code
EndFunction
```

## See Also
[Standart clause 3.2](https://its.1c.ru/db/v8std#content:553:hdoc)