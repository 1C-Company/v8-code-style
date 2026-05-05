# Restriction execute external code check.

When using the Standard Subsystem Library in a configuration, you should use the library's component 
connection methods and completely avoid direct use of platform-specific mechanisms for connecting 
external components, such as:
AttachAddIn
BeginInstallAddIn
InstallAddIn
BeginAttachIngAddin
LoadAddIn

Should use the methods from the Standard Subsystems Library.

## Noncompliant Code Example

AttachAddIn

## Compliant Solution

CommonClient.AttachComponentFromLayout;

## See

- [Restriction execute external code check](https://its.1c.ru/db/v8std#content:669:hdoc)
