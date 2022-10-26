# Using the "New Font" construction

To change the design, you should use style elements and not
set specific values directly in the controls.

## Noncompliant Code Example

```bsl
Font = new Font(, 1, True, True, False, True, 100); ...
```

## Compliant Solution

```bsl
Font = StyleFonts.<Name of style element>; ...
```

This is required in order for similar controls to look
the same in all forms where they occur.

## See

- [Styles and style elements](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_8.3.18_Developer_Guide/Chapter_5._Configuration_objects/5.5.__Common__configuration_branch/5.5.22._Styles_and_style_elements/5.5.22._Styles_and_style_elements)
