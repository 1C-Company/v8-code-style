# Indentation in the Method Description Section

Text in declared sections of a comment must be indented relative to the section header.

If a description spans multiple lines, an additional indentation is required only for the second line. All following lines must be aligned with the second line.

If a new level of nesting starts within a section, all lines belonging to that level must have the same indentation. Therefore, a larger indentation of the first line is not considered an error if the other lines at the same level have the same indentation.

The Example and Call Options sections are exceptions to this rule: the second and subsequent lines must have only one additional indentation level.

## Incorrect

```bsl
// Procedure description
//
// Parameters:
//   Parameter1 – Type1 - first line of the parameter description,
//   Second line (error)
//     Third line
//   Parameter2 – Type2 - a new context starts here.
//		 Second line
//			Third line (error)
//   	 Parameter3 – Type2 - a new context starts here. (error)
//
Procedure Incorrect(Parameter1, Parameter2, Parameter3)
	
EndProcedure
```

## Correct

```bsl
// First line of a generic procedure description.
// Second line is written without indentation, as the "Description" section is not declared.
// Third and subsequent lines are written without indentation.
//
// Parameters:
//   Parameter1 – Type1 - first line of the parameter description,
//     Second line is offset relative to the first one.
//     Third line is written without additional offset.
//   Parameter2 – Type2 - a new context starts here.
//		 Second line
//		 Third line
//   Parameter3 – Type2 - a new context starts here.
//
Procedure Correct(Parameter1, Parameter2)
	
EndProcedure
```

## See more

[Standart, see 5.6](https://its.1c.ru/db/v8std#content:453:hdoc)
