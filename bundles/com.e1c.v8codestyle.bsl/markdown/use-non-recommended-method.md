# Using non-recommended methods

There are not recommended methods, instead of which either SSL methods or other methods should be used.

## Noncompliant Code Example

```bsl
Message("Text");

Date = CurrentDate();

CommonForm1 = GetForm("CommonForm.CommonForm1");
```

```bsl
Find(Catalog.Name, "Joy");
```

## Compliant Solution
	 
```bsl
Message = New UserMessage();
Message.Text = ("Text");
Message.Message(); 

Date = CurrentSessionDate();

OpenForm("CommonForm.CommonForm1");
```
	
```bsl
StrFind(Catalog.Name, "Joy");
```
	
## See

- [Restriction on the use of the Message method](https://its.1c.ru/db/v8std#content:418:hdoc)
- [Working in different time zones](https://its.1c.ru/db/v8std#content:643:hdoc:2.1)
- [Opening forms](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Designing_user_interfaces/Implementation_of_form/Opening_forms/?language=en)
- [Version 8.3](https://kb.1ci.com/1C_Enterprise_Platform/What___s_New/What___s_New/Version_8.3.6/?language=en)