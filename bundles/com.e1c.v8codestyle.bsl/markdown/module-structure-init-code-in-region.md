# The initialization section contains initialize code

The initialization section contains operators that initialize module variables or an object (form).

## Noncompliant Code Example

```bsl

SupportEmail = "v8@1c.ru";
Ctor();
...


```bsl

## Compliant Solution

```bsl

#Region Initialize

SupportEmail = "v8@1c.ru";
Ctor();
...

#EndRegion

```bsl

## See


- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
