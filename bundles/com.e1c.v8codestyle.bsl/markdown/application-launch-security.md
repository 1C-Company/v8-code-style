# Application Launch Security

When launching an external program from code, the launch string must be composed so that it is assembled
only from trusted parts.
If one of the parts used to construct the launch string contains data retrieved from a database, from an input field
on a form, or read from the settings storage, then before launching the program, it is necessary to check whether the launch is safe. String data that does not contain the following characters is considered safe:
"$", "`", "|", "||", ";", "&", "&&".

## Noncompliant Code Example

## Compliant Solution

 ## See
- [Application Launch Security](https://its.1c.ru/db/v8std#content:774:hdoc)