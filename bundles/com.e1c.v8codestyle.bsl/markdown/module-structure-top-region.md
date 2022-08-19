# Module stucture top regions

A program module (common module, object module, object manager module, form module, command module, and so on) 
can have the following sections ordered as listed below:

- module header
- variable description section
- export procedures and functions of the module, which comprise its program interface
- event handlers of an object (form)
- internal procedures and functions of the module
- initialization section

Some sections only appear in modules of specific types. For example, event handlers of form items can only exist in 
form modules, while the variable description and initialization sections cannot be defined in nonglobal common modules, 
object manager modules, record set modules, constant value modules, or the session module.

Dividing the module code into sections makes the code easier to read and modify for different authors (developers), 
both during group development and during application customization within specific deployment projects.

## Noncompliant Code Example

```bsl

#Region Public
// Enter code here.
#EndRegion

#Region Internal
// Enter code here.

#Region Private
// Enter code here.
#EndRegion

#EndRegion


```

## Compliant Solution

```bsl

#Region Public
// Enter code here.
#EndRegion

#Region Internal
// Enter code here.
#EndRegion

#Region Private
// Enter code here.
#EndRegion

```

## See


- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
