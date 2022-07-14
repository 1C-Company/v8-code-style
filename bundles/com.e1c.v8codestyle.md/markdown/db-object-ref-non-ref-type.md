# Database object with composite type

Composite type attributes used in join conditions, filters, and for ordering must contain only reference attribute types 
(CatalogRef.…, DocumentRef.…, and other). 
Do not include any other non-reference types in this type. 
For example: String, Number, Date, UUID, Boolean, and ValueStorage.

## Noncompliant Code Example

## Compliant Solution

## See

[Restrictions on the use of composite type attributes](https://support.1ci.com/hc/en-us/articles/360011120479-Restrictions-on-the-use-of-composite-type-attributes)