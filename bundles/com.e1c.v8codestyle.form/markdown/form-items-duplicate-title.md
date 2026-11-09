# Duplicate titles of form items

The check finds form items of the same type - buttons and groups - that have the same title within one context.
The contexts are the form itself, the command bar, a submenu and the context menu.

## Noncompliant Code Example

```
Form
 Group "Actions"
 Group "Settings"
 Group "Actions" // the title repeats the first group
```

## Compliant Solution

```
Form
 Group "Actions"
 Group "Settings"
```

## See

- [Restrictions on the use of identical texts on form controls](https://its.1c.ru/db/v8std#content:526:hdoc)
