# Checking if query result is empty

To check whether a query result is empty, employ the IsEmpty method. Otherwise, it will take time to fetch data from the result set and save it to a value table.

## Noncompliant Code Example

Selection = Query.Execute().Select();
If Selection.Next() Then
Return True;
Else
Return False;
EndIf;

## Compliant Solution

Return NOT Query.Execute().IsEmpty()

## See
[Checking if query result is empty](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Data_processing/Working_with_queries/Checking_if_query_result_is_empty/?language=en)