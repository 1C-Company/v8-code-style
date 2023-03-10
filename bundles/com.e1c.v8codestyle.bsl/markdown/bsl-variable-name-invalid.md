# Rules for variable names generation

1. To name a variable, use conventional terms related to the respective subject area so 
   that the variable's name would be descriptive and convey its purpose to the reader.
2. Variable names mustn't contain spaces, even when it is composed of a few words. 
   Each word in a variable name starts with a capital letter, including pronounces and prepositions.
3. Variable names mustn't start with an underscore.
4. Variable names must contain more than one character, except for loop counters. 
   It is allowed to assign loop counters with single-character names.
5. Name Boolean variables so that the name would convey the true state of the variable.

## Invalid variable name examples

```bsl
arrOfAtrribute, _SetTypeDoc, nS
```

## Correct variable name examples
	 
```bsl
Var DialogWorkingCatalog; 
Var NumberOfPacksInTheBox;
NewDocumentRef;
```
	
## See

- [Rules for variable names generation](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Module_formatting/Rules_for_variable_names_generation/)