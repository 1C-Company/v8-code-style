# Reference to a Constructor Function

For parameters of Structure, ValueTable, and ValueTree types, the type description must include a reference to the function that returns this structure, table, or tree (e.g., a reference to its constructor function).
The validation lint tool considers it an error to specify generic Structure/ValueTable/ValueTree types if the method body contains an access to the properties or methods of a variable of that type.

## Incorrect

```bsl
// Structure acceptance
// Parameters:
// 		UserStructure - Structure
Function AcceptStructure(UserStructure) 

	UserStructure.Name = "Name";

EndFunction

```

## Correct

```bsl
// Structure acceptance
// Parameters:
// 		UserStructure - See StructureConstructor
Function AcceptStructure(UserStructure) 

	UserStructure.Name = "Name";

EndFunction

// Structure acceptance
// Parameters:
// 		UserStructure - Structure - See StructureConstructor
Function AcceptStructure(UserStructure) 

	UserStructure.Name = "Name";

EndFunction

// Structure acceptance, also correct
// Parameters:
// 		UserStructure - Structure:
//        * Name - String - name of person
//        * Age - Number - age of person
Function AcceptStructure2(UserStructure) 

	UserStructure.Name = "Name";

EndFunction
```

## See Also

[Description of Procedures and Functions, sec. 5.2.2](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Module_formatting/Describing_procedures_and_functions/?language=en)
