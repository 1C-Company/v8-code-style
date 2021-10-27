# Using non-recommended methods

There are not recommended methods, instead of which either SSL methods or other methods should be used.

## Noncompliant Code Example

	```bsl

	Message("Text");
	Date = CurrentDate();

	```

## Compliant Solution
	 
	 ```bsl

	Message = New UserMessage();
	Message.Text = ("Text");
	Message.Message(); 

	Date = CurrentSessionDate();

	```
	
## See

[Restriction on the use of the Message method](https://its.1c.ru/db/v8std#content:418:hdoc)
[Working in different time zones](https://its.1c.ru/db/v8std#content:643:hdoc:2.1)