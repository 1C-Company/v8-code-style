# Security of software called through public interfaces

When using integration with third-party applications via open interfaces (in particular, via COM),
 it is necessary to disable the execution of arbitrary code by means of the called application.

## Noncompliant Code Example

ObjectWord = New COMObject("Word.Application");
Doc = ObjectWord.Documents.Open(FileName);

## Compliant Solution

ObjectWord = New COMObject("Word.Application");
ObjectWord.WordBasic.DisableAutoMacros(1);
Document = ObjectWord.Documents.Open(FileName);

 ## See
- [Security of software called through public interfaces](https://its.1c.ru/db/v8std#content:775:hdoc)