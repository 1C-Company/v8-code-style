# Outdated alias used

You should use the alias `ThisObject` instead of the obsolete `ThisForm` in the form module

## Noncompliant Code Example

```bsl
Var myParam;

Function test() Export
	// code here
EndFunction

ThisForm.myParam = ThisForm.test();
```

## Noncompliant Code Example

```bsl
	Notification = New NotifyDescription("ShowQueryEnding", ThisForm);
	ShowQuery(Notification, QuestionText, ...);
```

## Compliant Solution

```bsl
Var myParam;

Function test() Export
    // code here
EndFunction

ThisObject.myParam = ThisObject.test();
```

## Compliant Solution

```bsl
	Notification = New NotifyDescription("ShowQueryEnding", ThisObject);
	ShowQuery(Notification, QuestionText, ...);
```

## See

