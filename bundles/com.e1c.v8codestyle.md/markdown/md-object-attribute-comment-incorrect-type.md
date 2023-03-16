# Attribute "Comment" for documents

For all documents, it is recommended to create the Comment attribute (string of unlimited length). 
In this attribute, users can write down various notes of an official nature on the document 
that are not related to the application specifics of the document (for example, the reason for marking for deletion, etc.). 
Access to the attribute for users must be configured in the same way as to the document itself 
(if the document is read-only, then the comment is read-only; if there is write access to the document, 
then the value of the attribute can also be changed).

Multiline edit must be enabled.

## Noncompliant Code Example

## Compliant Solution

## See

[Attribute "Comment" for documents](https://its.1c.ru/db/v8std#content:531:hdoc:1)