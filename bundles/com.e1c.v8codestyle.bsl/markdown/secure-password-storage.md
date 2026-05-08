# Check use Secure password storage

Passwords should not be stored in form details; they should be retrieved
only on the server side and immediately before use.
Otherwise, when opening a form with masked password input (or viewing), 
the password is transmitted from the server to the client in cleartext,
making it vulnerable to interception.

## Noncompliant Code Example


## Compliant Solution
Procedure OnCreateAtServer()

SetPrivilegedMode(True);
 Passwords = Common.ReadDataFromSecureStorage(Object.Ref, "Password, SMTPPassword");
 SetPrivilegedMode(False);

Password = ?(ValueIsFilled(Passwords.Password), ThisObject.UUID, "");
 SMTPPassword = ?(ValueIsFilled(Passwords.SMTPPassword), ThisObject.UUID, "");

EndProcedure


## See

- [Secure password storage](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/General_security_issues/Secure_password_storage/?language=en)