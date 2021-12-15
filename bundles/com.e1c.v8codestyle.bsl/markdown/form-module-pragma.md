# Use form module compilation pragma


Compilation directives (pragma)

```bsl
&AtClient
&AtServer
&AtServerNoContext
```

should be applied only in the code of managed form modules and in the code of command modules. 
In other modules, it is recommended to use instructions to the preprocessor.

In server or client common modules, the execution context is obvious, so there is no sense in compilation directives. 
In  Client-Server common modules  the use of compilation directives makes it difficult to understand which procedures (functions) are available in the end.


## Noncompliant Code Example

## Compliant Solution

## See

- [Using compilation directives](https://1c-dn.com/library/using_compilation_directives/)
- [Knowledge base  - Compilation directives](https://1c-dn.com/library/tutorials/practical_developer_guide_compilation_directives/)
