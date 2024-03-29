# Dynamic list field title is empty

Specify headers for columns of a dynamic list that are displayed in a query as a combination of other columns or that have their own alias assigned. 
Do not use headers that are automatically generated by name or alias.

Examples when you need to specify column headers explicitly:

```bsl
SELECT
    Table.Field1 AS Field2
    CAST(Table.Field1 AS STRING(100)) AS Field3
```

In this case, when a field is created in a query and a name is assigned to it, 
the synonym is not automatically obtained from metadata as there is no attribute related to this field. 
The interface text editor does not find headers for dynamic list columns, to which an alias is assigned in a query. 
You need to specify headers of dynamic list columns even if field names are not displayed on the form as a user can see column headers upon setting up form fields (clicking More, Change form...).

## See

- [Form items: localization requirements](https://support.1ci.com/hc/en-us/articles/360011122779-Form-items-localization-requirements)
