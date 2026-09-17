#Region Abcd

Var MyVar;

Procedure FillCheckProcessing(Cancel, CheckedAttributes)
	
	MyVar = CheckedAttributes;
	DeleteUncheckedAttributesFromArray();

EndProcedure

Procedure DeleteUncheckedAttributesFromArray() // УдалитьНепроверяемыеРеквизитыИзМассива
	MyVar.Delete("new");
EndProcedure

#EndRegion
