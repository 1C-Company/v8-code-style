# In document that allow posting don't set flat "Post (unpost) in privileged mode"

1.7. In all documents that require posting, the flags "Privileged mode when posting" 
and "Privileged mode when canceling posting" must be set, so you do not need to create 
roles that give rights to change registers subordinate to registrars.

Exception: documents intended for direct updating of register 
entries can be checked with access rights, but in this case it is necessary to provide 
roles that give rights to change registers.

## Noncompliant Solution

## Compliant Solution

## See

[Setting up roles and access rights](https://its.1c.ru/db/v8std#content:689:hdoc:1.7)