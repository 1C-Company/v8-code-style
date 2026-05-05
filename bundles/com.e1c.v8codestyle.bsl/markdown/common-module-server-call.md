# Restriction on setting the "Server Call" flag for common modules

Don't force the Call Server checkbox on all common modules with the Server flag. 
Such shared modules should contain only those procedures and functions that are truly intended to be called from 
client code and guarantee the execution of only those actions (and the transfer of only those data to the client) 
that the user is authorized to perform while working in the program. For example, a server function implementing 
a calculation algorithm should transfer the final result of that calculation to the client, but not the initial 
(or intermediate) data for the calculation, which may not be accessible to the current user.

## Noncompliant Code Example

## Compliant Solution

## See
[Restrictions on the use of Run and Eval on the server]https://its.1c.ru/db/v8std#content:679:hdoc