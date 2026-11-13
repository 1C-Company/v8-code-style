// Some procedure
// Parameters:
//	SomeStruct - Structure
//	SomeTree - ValueTree
//	SomeTable - ValueTable
Procedure Some(SomeStruct, SomeTree, SomeTable)

	Value = SomeStruct.Key;
	Value = SomeTree.Key;
	Value = SomeTable.Key;

EndProcedure

// Some procedure
// Parameters:
//	SomeStruct - Structure
//	SomeTree - ValueTree
//	SomeTable - ValueTable
Procedure Some2(SomeStruct, SomeTree, SomeTable)

	Value = SomeStruct.Count();
	If Value = SomeTree.Key Then
		// ...	
	ElsIf Value = SomeTable.Key Then
	
	EndIf;

EndProcedure