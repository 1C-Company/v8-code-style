// @strict-types


Procedure Incorrect1() Export

	TestStucture = Correct2();
	TestStucture.Clear(); 
	TestStucture.Delete("Key2"); 
	TestStucture.Insert("Key1", Undefined); 
	TestStucture.Clear();
	TestStucture.Insert("Key1", 10);
	TestStucture.Key1 = "";

EndProcedure

Procedure Correct1() Export

	TestStucture = new Structure();
	TestStucture.Clear();
	TestStucture.Delete("Key2");
	TestStucture.Insert("Key1"); 
	TestStucture.Clear();
	TestStucture.Insert("Key1", 10);
	TestStucture.Clear();
	TestStucture.Key1 = "";

EndProcedure


// Parameters:
//  TestStucture - Structure:
// * Key1 - Boolean - 
// * Key2 - Number - 
Procedure Incorrect2(TestStucture) Export

	TestStucture.Insert("Key1", 10);
	TestStucture.Insert("Key3", 10);
	TestStucture.Clear();
	TestStucture.Delete("Key2");

EndProcedure

// Parameters:
//  TestStucture - See Correct2
Procedure Incorrect3(TestStucture) Export

	TestStucture.Insert("Key1", 10);
	TestStucture.Insert("Key3", 10);
	TestStucture.Clear();
	TestStucture.Delete("Key2");

EndProcedure

// Returns:
//  Structure:
// * Key1 - Boolean - 
// * Key2 - Number - 
Function Correct2() Export

	TestStucture = new Structure("Key1, Key2", true, 10);
	TestStucture.Insert("Key1", 10);

	Return TestStucture; 

EndFunction
