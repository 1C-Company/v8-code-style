# In document that require posting don't set flat "Post (unpost) in privileged mode"

All documents that require posting must have the "Privileged mode for posting" 
and "Privileged mode for unposting" check boxes selected. Therefore it is not necessary 
to create roles that grant rights to change registers subordinate to recorders.

Exception: documents intended for direct adjustment of register records can be posted 
with access right verification, but in this case, it is necessary to include roles that 
grant rights to change registers.

## Noncompliant Solution

## Compliant Solution

## See

[Configuring roles and access rights](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Setting_data_access_rights/Configuring_roles_and_access_rights/)