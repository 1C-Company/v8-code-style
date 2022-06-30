# The "IsInRole" method was used

To check access rights in the code, use the AccessRight method.

## Noncompliant Code Example

```bsl
If IsInRole("AddClient") Then ...
```

## Compliant Solution

```bsl
If AccessRight("Add", Metadata.Catalogs.Client) Then ...
```

This approach allows you to increase the code robustness when configuration roles are revised.

## See

- [Configuring roles and access rights](https://support.1ci.com/hc/en-us/articles/360011122599-Configuring-roles-and-access-rights)
- [Checking access rights](https://support.1ci.com/hc/en-us/articles/360011003180-Checking-access-rights)