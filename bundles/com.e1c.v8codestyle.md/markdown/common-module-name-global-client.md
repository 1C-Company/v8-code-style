# Global client common module should end with Global suffix no Client suffix

3.2.1. Add the "Global" postfix for global modules. In this case, you do not need to add the "Client" postfix. 


## Noncompliant Code Example

FilesOperationsGlobalClient, InfobaseUpdateGlobalClient.

## Compliant Solution

FilesOperationsGlobal, InfobaseUpdateGlobal.

## See

[Common modules creating rules](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Creating_and_modifying_metadata_objects/Configuration_operation_arrangement/Common_modules_creating_rules/)