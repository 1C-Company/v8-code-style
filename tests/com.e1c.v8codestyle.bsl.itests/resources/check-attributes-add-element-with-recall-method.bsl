#Region Abcd

Procedure FillCheckProcessing(Cancel, CheckedAttributes)

	SomeProcedure(CheckedAttributes);

	SomeProcedure(CheckedAttributes);

EndProcedure

Procedure SomeProcedure(MyVar)
	MyVar.Add("new"); // ERROR
EndProcedure

#EndRegion
