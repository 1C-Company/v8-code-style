# Variable naming conventions

1. Names of variables should be formed from the terms of the subject area in such a way that its purpose is clear from the name of the variable.
2. Names should be formed by removing spaces between words. In this case, each word in the name is written with a capital letter. One-letter prepositions and pronouns are also written in capital letters.
3. Variable names must not begin with an underscore.
4. Variable names should not consist of a single character. The use of single-character variable names is only allowed for loop counters.
5. Variables reflecting the state of a certain flag should be named as the true value of this flag is written.

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

- [Variable naming conventions](https://its.1c.ru/db/v8std#content:454:hdoc:3)