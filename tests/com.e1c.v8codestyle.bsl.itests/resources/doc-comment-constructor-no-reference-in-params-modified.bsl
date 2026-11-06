// Some procedure
// Parameters:
//	SomeStruct - Structure
Procedure Some(SomeStruct)

	SomeStruct["Key"] = "Value";

EndProcedure

// Some procedure
// Parameters:
//	SomeStruct - Structure
Procedure Some2(SomeStruct)

	SomeStruct.Key = "Value";
	SomeStruct["Key"] = "Value";

EndProcedure

// Some procedure
// Parameters:
//	SomeStruct - Structure
Procedure Some3(SomeStruct)

	SomeStruct.Add("Column");

EndProcedure

// Some procedure
// Parameters:
//	SomeTable - ValueTable
//	SomeStruct - Structure
//	SomeTree - ValueTree
Procedure Some4(SomeTable, SomeStruct, SomeTree)

	SomeTable.Add();
	SomeTree.Columns.Add("Some");
	SomeStruct.Insert("Some", "Some");

EndProcedure

// Some procedure
// Parameters:
//	SomeTable - ValueTable
//	SomeStruct - Structure
//	SomeTree - ValueTree
Procedure Some5(SomeTable, SomeStruct, SomeTree)

	SomeTable.Delete(1);
	SomeTree.Columns.Delete(1);
	SomeStruct.Delete("Some");

EndProcedure

// Some procedure
// Parameters:
//	SomeTable - ValueTable
//	SomeStruct - Structure
//	SomeTree - ValueTree
Procedure Some6(SomeTable, SomeStruct, SomeTree)

	SomeTable.Clear();
	SomeTree.Columns.Clear();
	SomeStruct.Clear();

EndProcedure

// Parameters:
//	SomeStruct - Structure - just a description
Procedure Some(SomeStruct)

	SomeStruct.Insert("Key", "Value");

EndProcedure