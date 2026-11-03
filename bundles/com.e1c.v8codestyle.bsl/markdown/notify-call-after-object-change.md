# Updating Lists During Interactive User Actions

If a client procedure in a form module calls a server procedure that modifies an object using the Write(), Delete(), or SetDeletionMark() methods, the client procedure must call either the Notify() or NotifyChanged() procedure.

## Incorrect
```bsl
&AtClient
Procedure Incorrect()

	ModifyObjectOnServer();
	
EndProcedure

&AtServer
Procedure ModifyObjectOnServer()

	// Code ...

	Write();

EndProcedure

```

## Correct

```bsl
&AtClient
Procedure Correct()

	ModifyObjectOnServer();
	
	Notify("Write_Invoice", WriteParameters, Object.Ref);

EndProcedure

&AtServer
Procedure ModifyObjectOnServer()

	// Code ...

	Write();

EndProcedure
```

## See also:
[Standart](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Designing_user_interfaces/Implementation_of_list_forms/Dynamic_refresh_of_list_views/?language=en)