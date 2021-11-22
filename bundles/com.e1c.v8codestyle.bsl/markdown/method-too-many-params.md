# Method has to many parameters

It is not recommended to declare many parameters in methods (you need to focus on the number of no more than seven parameters),
while there should not be many parameters with default values (you need to focus on the number of no more than three such parameters). 
Otherwise, the readability of the calling code is greatly reduced. 
For example, you can easily make a mistake in the number of commas when passing optional parameters.

## Noncompliant Code Example

```bsl
// Adds a new field to a form and initializes it with default values.
Function AddFormField(Name,
      Heading = Undefined,
      OnChangeHandler = "",
      OnBeginSelectionHandler = "",
      MarginWidth,
      Backcolor = Undefined,
      TitleBackColor = Undefined,
      Parent = Undefined,
      HeaderPicture = Undefined,
      DataPath = Undefined,
      FieldReadOnly = False,
      ChoiceParameterLinks = Undefined)
…
EndFunction
­
// calling the function
NewField = AddFormField("OldPrice" + ColumnName, NStr("en='Price'"),,, 12, BackColor, TitleColor, NewFolder,,,True);
NewField.TextColor = WebColors.Gray;
```
## Compliant Solution

```bsl
// Adds a new field to a form and initializes it with default values.
Function NewFormField(FieldName) 
…
EndFunction
­
// calling the function
NewField = NewFormField("OldPrice" + ColumnName);
NewField.Heading = NStr("en='Price'");
NewField.BackColor = BackColor;
NewField.TextColor = WebColors.Gray;
NewField.… = …
…
```

## See

- [Procedure and function parameters](https://1c-dn.com/library/procedure_and_function_parameters/)
