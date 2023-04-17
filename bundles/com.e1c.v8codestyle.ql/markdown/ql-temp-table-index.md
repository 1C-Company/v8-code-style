# Temporary table should have indexes

1. Indexing is reasonable if:
	1.1 A large temporary table is involved in a join (regardless of on which side). 
	    Add fields involved in the BY condition to the index.
	1.2 A temporary table is called in a subquery of the construct of the logical IN (...) operator. 
	    To the index, add fields of the temporary table from a selection list that match fields listed 
	    on the left side of the logical IN(...) operator.
2. You do not need to index small temporary tables consisting of less than 1,000 records.

## Noncompliant Code Example

## Compliant Solution

## See

- [Using temporary tables](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Data_processing/Optimizing_queries/Using_temporary_tables/)