Procedure FillCheckProcessing(Cancel, CheckedAttributes)

	CommonModule2.ChangeCheckedProcedure(CheckedAttributes); // Here error
	CommonModule2.DeleteUncheckedAttributesFromArray(CheckedAttributes); // Here error

EndProcedure