# NStr string literal format

## Noncompliant Code Example

Should pass only single string literal in first paramenter of the NStr function.

```bsl
Procedure NonCompliant1(Message) Export
	
	Message = NStr("en = 'User message'" + Chars.LF);
	
EndProcedure
```

The string literal in first parameter should not be emty.

```bsl
Procedure NonCompliant2(Message) Export
	
	Message = NStr("");
	
EndProcedure
```

The format of string literal should be valid: `"key1 = 'value 2'; key2 = 'value 2';"`.

```bsl
Procedure NonCompliant3(Message) Export
	
	Message = NStr("en = User message");
	
EndProcedure
```

The language code should be existing language code in configuration languages.

```bsl
Procedure NonCompliant4(Message) Export
	
	Message = NStr("en2 = 'User message'");
	
EndProcedure
```

The message for language code should not be empty.

```bsl
Procedure NonCompliant5(Message) Export
	
	Message = NStr("en = ''");
	
EndProcedure
```

The message for language code should not ends with space.

```bsl
Procedure NonCompliant6(Message) Export
	
	Message = NStr("en = 'User message '");
	
EndProcedure
```

The message for language code should not ends with new line.

```bsl
Procedure NonCompliant7(Message) Export
	
	Message = NStr("en = 'User message
	|'");
	
EndProcedure
```

## Compliant Solution


```bsl

Procedure Compliant(Message) Export
	
	Message = NStr("en = 'User message'");
	
EndProcedure
```

## See

- [About Nstr](https://1c-dn.com/1c_enterprise/nstr/)
- [Interface texts in code: localization requirements](https://support.1ci.com/hc/en-us/articles/360011003480-Interface-texts-in-code-localization-requirements)
