# Using export variables in modules

In most cases, instead of variable program modules, more appropriate development tools of the 1C:Enterprise platform should be used. 
Since the scope (use) of such variables is difficult to control, they often become a source of hard-to-reproduce errors.

## Noncompliant Code Example

```bsl

Var ConvertFiles Export;

Procedure BeforeWrite(Cancel)

  If FileConversion Then
  ...

EndProcedure

// calling code
FileObject.FileConversion = True;
FileObject.Write();

```

## Compliant Solution

```bsl

Procedure BeforeWrite(Cancel)

  If AdditionalProperties.Property("FileConversion") Then
  ...

EndProcedure

// calling code
FileObject.AdditionalProperties.Insert("FileConversion", True);
FileObject.Write();

```

## See

