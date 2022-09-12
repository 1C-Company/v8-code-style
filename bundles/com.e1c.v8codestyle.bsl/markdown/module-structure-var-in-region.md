# Variable description section

Variable description section. Name your variables according to the general variable naming rules. 
For variable usage recommendations, see Using global variables in modules.

For each variable, add a comment that clearly explains its purpose. We recommend that you add the comment 
to the same line where the variable is declared.

## Noncompliant Code Example

```bsl

Var PresentationCurrency;
Var SupportEmail;

```

## Compliant Solution

```bsl

#Region Variables

Var PresentationCurrency;
Var SupportEmail;
...

#EndRegion

```

## See


- [Module structure](https://1c-dn.com/library/module_structure/)
- [Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure)
