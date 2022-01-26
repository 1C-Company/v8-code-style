# Using the "New Color" construction

To change the design, you should use style elements, and not
set specific values directly in the controls.

## Noncompliant Code Example

```bsl
Color = new Color(0,0,0); ...
```

## Compliant Solution

```bsl
Color = StyleColors.<Name of style element>; ...
```

This is required in order for similar controls to look
the same in all forms where they occur.

## See

- [Styles and style elements](https://support.1ci.com/hc/en-us/articles/4403174580370-5-5-22-Styles-and-style-elements)
