# The common module must have at least one export method

The common module must have at least one export method.

## Noncompliant Code Example

```bsl

Procedure Test()
//TODO
EndProcedure

```

## Compliant Solution

```bsl

#Region Internal

Procedure Test() Export
//TODO
EndProcedure

#EndRegion

```

## See

- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Module_formatting/Module_structure/)