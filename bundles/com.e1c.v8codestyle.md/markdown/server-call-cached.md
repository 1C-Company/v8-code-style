# Common server modules for calling from the client contain server procedures and functions or cached modules.

Name common server modules to be called from the client according to general rules of naming metadata objects. 
Make sure they include the "ServerCall" postfix.
Use the "Cached" and "ClientCached" postfixes for the modules that implement functions with repeated use of return values 
(upon the call or session time) on the server and on the client respectively.


## Noncompliant Code Example

## Compliant Solution

## See

[Common modules creating rules](https://support.1ci.com/hc/en-us/articles/360010988260-Common-modules-creating-rules)