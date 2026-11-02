#Region Abcd

Var ModuleRef;

Procedure FillCheckProcessing(Cancel, CheckedAttributes)

	UncheckedAttributes = New Array;
	UncheckedAttributes.Add("Attr1");
	DeleteUncheckedAttributesFromArray(CheckedAttributes, UncheckedAttributes);

	PrepareUncheckedList(UncheckedAttributes);
	DeleteUncheckedAttributesFromArray(CheckedAttributes, UncheckedAttributes);

	ModuleRef = CheckedAttributes;
	УдалитьНепроверяемыеРеквизитыИзМассива();

	LocalCopy = CheckedAttributes;
	LocalCopy = New Array;
	LocalCopy.Add("not-checked-attributes");
	DoSomethingWithLocalArray(LocalCopy);

	CallAllowedDelete(CheckedAttributes);

EndProcedure

Procedure PrepareUncheckedList(List)
	List.Add("Attr2");
EndProcedure

Procedure DeleteUncheckedAttributesFromArray(AttributesArray, UncheckedAttributesArray) // УдалитьНепроверяемыеРеквизитыИзМассива
	For Each Item In UncheckedAttributesArray Do
		Index = AttributesArray.Find(Item);
		If Index <> Undefined Then
			AttributesArray.Delete(Index);
		EndIf;
	EndDo;
EndProcedure

Procedure УдалитьНепроверяемыеРеквизитыИзМассива()
	ModuleRef.Delete("Attr3");
EndProcedure

Procedure DoSomethingWithLocalArray(Arr)
	Arr.Add("safe");
	Arr.Delete(0);
	Arr.Clear();
EndProcedure

Procedure CallAllowedDelete(Attributes)
	Unchecked = New Array;
	Unchecked.Add("Attr4");
	DeleteUncheckedAttributesFromArray(Attributes, Unchecked);
EndProcedure

#EndRegion