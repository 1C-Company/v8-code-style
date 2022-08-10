# Check the method is outside a region

The method is outside a region

The Interface section contains export procedures and functions intended for use by other configuration objects or 
by other programs (for example, via an external connection).
The Internal procedures and functions section contains procedures and functions that comprise 
the internal implementation of the common module. When a common module is a part of some functional subsystem 
that includes multiple metadata objects, this section can also contain other internal export procedures and 
functions intended to be called only from other objects of this subsystem.

## Noncompliant Code Example

```bsl

Procedure Noncompliant()
//...
EndProcedure

```bsl

## Compliant Solution

```bsl

#Region Private

Procedure Compliant()
//...
EndProcedure

#EndRegion

```bsl

## See


- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
