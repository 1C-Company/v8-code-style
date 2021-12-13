# Always use compilation pragma in form module

An absence of a compilation directive before the procedure means using a directive by default. 
A directive by default is `AtServer` that leads to extra server call and it is not working on web-client.

## Noncompliant Code Example


```bsl
Procedure Server()

EndProcedure
```

## Compliant Solution

```bsl
&AtServer
Procedure Server()

EndProcedure
 
```

## See

- [Form module](https://support.1ci.com/hc/en-us/articles/4403180966034-7-6-Form-module)
- [Using compilation directives and preprocessor commands](https://support.1ci.com/hc/en-us/articles/360011002560-Using-compilation-directives-and-preprocessor-commands)
- [General configuration requirements](https://support.1ci.com/hc/en-us/articles/360011107839-General-configuration-requirements)
