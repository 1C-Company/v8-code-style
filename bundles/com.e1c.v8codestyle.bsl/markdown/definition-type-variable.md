# Determining the type of a variable's value

Determining the type of a variable's value must be done by comparing it to the type, and not by any other method.

## Noncompliant Code Example

If Object.Metadata().Name = "Name" Then

## Compliant Solution

If TypeOf(Object) = Type("DocumentRef.Document") Then

 ## See

- [Determining the type of a variable](https://its.1c.ru/db/v8std#content:442:hdoc)