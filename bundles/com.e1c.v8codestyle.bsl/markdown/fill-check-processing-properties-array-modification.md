# Fill Check Processing Event Handler

Using modifiable methods directly on the CheckedAttributes array within the FillCheckProcessing event handler is prohibited; you must use the SSL method CommonUse.RemoveUncheckedAttributesFromArray() or its custom equivalent that ensures safe element removal according to the official 1C template.

## Incorrect

```bsl
Procedure FillCheckProcessing(Cancel, CheckedAttributes)
    If LegalIndividualEntity <> Enums.LegalIndividualEntity.IndividualEntity Then
        UncheckedAttributes.Delete("IndividualEntrepreneur");
    EndIf;
EndProcedure;
```

## Correct
```bsl
Procedure FillCheckProcessing(Cancel, CheckedAttributes)
    UncheckedAttributes = New Array();
    If LegalIndividualEntity <> Enums.LegalIndividualEntity.IndividualEntity Then
        UncheckedAttributes.Add("IndividualEntrepreneur");
    EndIf;
    RemoveUncheckedAttributesFromArray(CheckedAttributes, UncheckedAttributes);
EndProcedure;

Procedure RemoveUncheckedAttributesFromArray(AttributesArray, UncheckedAttributesArray) Export
    For Each ArrayItem From UncheckedAttributesArray Do
        IndexNumber = AttributesArray.Find(ArrayItem);
        If IndexNumber <> Undefined Then
            AttributesArray.Delete(IndexNumber);
        EndIf;
    EndLoop;
EndProcedure
```

## See also
https://its.1c.ru/db/v8std#content:463:hdoc