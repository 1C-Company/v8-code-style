# Attachable event handler name

Programmatically added event handler name should match pattern: **Attachable_** prefix

## Noncompliant Code Example

```bsl
// Parameters:
//  Item - FormField
Procedure Incorrect(Item)
	
	Item.SetAction("OnChange", "IncorrectOnChange");
	
EndProcedure
```

## Compliant Solution

```bsl
// Parameters:
//  Item - FormField
Procedure Correct(Item)
	
	Item.SetAction("OnChange", "Attachable_CorrectOnChange");
	
EndProcedure
```

## See

- [Form module event handlers attached in code](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Code_conventions/Using_1C_Enterprise_language_structures/Form_module_event_handlers_attached_in_code/)
