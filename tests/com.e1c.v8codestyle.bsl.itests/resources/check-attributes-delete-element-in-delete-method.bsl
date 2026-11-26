#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)
	
	DeleteUncheckedAttributesFromArray(CheckedAttributes);

EndProcedure

Procedure DeleteUncheckedAttributesFromArray(MyVar) // УдалитьНепроверяемыеРеквизитыИзМассива
	MyVar.Delete("old");
EndProcedure

#EndRegion
