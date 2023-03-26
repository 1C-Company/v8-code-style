# Functional option don't set flag "Privileged get mode"

1.8. All functional options must have the "Privileged mode upon receiving" 
check boxes selected.

Exception: a configuration can include parameterized functional options, for which developers 
provide for differences in values obtained by users with different rights.
Example: There is a parameterized functional option UseCurrencyUponSettlementsWithPersonnel, 
which is parameterized by the company. If a user receives its value in the context of their rights, 
they will not see the Currency field in the document if they do not have a company where currency 
accounting is applied.

## Noncompliant Code Example

## Compliant Solution

## See

[Configuring roles and access rights](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Setting_data_access_rights/Configuring_roles_and_access_rights/)