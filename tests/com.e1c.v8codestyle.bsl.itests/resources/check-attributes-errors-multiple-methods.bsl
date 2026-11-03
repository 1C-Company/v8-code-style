#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)

	// Error
	CheckedAttributes.Add("new1");

	// Error
	PassToChain(CheckedAttributes);

	// Error
	PassToForbiddenDelete(CheckedAttributes);

	// Error
	DeleteUncheckedAttributesFromArray(CheckedAttributes);

EndProcedure

Procedure PassToChain(Arr)
	MiddleMethod(Arr);
EndProcedure

Procedure MiddleMethod(Arr)
	Arr.Add("new3"); // ERROR
EndProcedure

Procedure PassToForbiddenDelete(Arr)
	ForbiddenDelete(Arr);
EndProcedure

Procedure ForbiddenDelete(Arr)
	Arr.Delete("old"); // ERROR — не УдалитьНепроверяемыеРеквизитыИзМассива
EndProcedure

Procedure DeleteUncheckedAttributesFromArray(Arr) // УдалитьНепроверяемыеРеквизитыИзМассива
	Arr.Add("new4"); // ERROR — в этом методе разрешён только Delete
EndProcedure

#EndRegion