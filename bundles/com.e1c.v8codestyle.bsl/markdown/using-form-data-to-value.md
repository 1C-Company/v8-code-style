# Using FormAttributeToValue and FormDataToValue

In most cases, FormAttributeToValue is preferable to FormDataToValue. 

Sticking to this recommendation will help you to keep the code consistent with other 1C:Enterprise applications. Also, 
FormAttributeToValue has simpler syntax, that is, less chance to make a code mistake. 
FormDataToValue requires the data type to be specified explicitly.Example:

SignaturesTable = FormDataToValue(SignaturesTable, Type("ValueTable"));

In contrast, FormAttributeToValue doesn't require data type to be specified. Example:

SignaturesTable = FormAttributeToValue("SignaturesTable");

1C:Enterprise supports both the FormAttributeToValue and FormDataToValue methods, 
but the last-mentioned is considered more usable. In terms of efficiency and output, the methods are equal.

## Noncompliant Code Example

```bsl
SignaturesTable = FormDataToValue(SignaturesTable, Type("ValueTable"));
```

## Compliant Solution

```bsl
SignaturesTable = FormAttributeToValue("SignaturesTable");
```

## See
[Using FormAttributeToValue and FormDataToValue](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Using_applied_objects_and_universal_value_collections/Using_FormAttributeToValue_and_FormDataToValue)
