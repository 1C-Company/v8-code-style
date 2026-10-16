# Reference to a Constructor Function

For parameters of Structure, ValueTable, and ValueTree types, the type description must include a reference to the function that returns this structure, table, or tree (e.g., a reference to its constructor function).The validation lint tool considers it an error to specify generic Structure/ValueTable/ValueTree types if the method body accesses specific properties or keys of that variable.

## Incorrect

```bsl
// Structure acceptance
// Parameters:
// 		UserStructure - Structure
Function AcceptStructure(UserStructure) 

	UserStructure.Insert("Name", "Value");

EndFunction

```

## Correct

```bsl
// Structure acceptance
// Parameters:
// 		UserStructure - See StructureConstructor
Function AcceptStructure(UserStructure) 

	UserStructure.Insert("Name", "Value");

EndFunction

```

## See Also

[Description of Procedures and Functions, sec. 5.2.2](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Module_formatting/Describing_procedures_and_functions/?language=en)
