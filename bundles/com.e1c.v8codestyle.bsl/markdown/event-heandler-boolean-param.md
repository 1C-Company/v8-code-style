# Use event handler boolean parameter

In event handlers of object modules, record sets, forms, etc., containing the `Cancel` parameter (`OnWrite`, `FillCheckProcessing`, `DescriptionStartListChoice`, etc.), you should not set the value `False` to this parameter.
This requirement is due to the fact that, usually, in the code of event handlers, the `Cancel` parameter can be set in several consecutive checks at once (or in several event subscriptions to the same event). 
In this case, by the time the next check is performed, the `Cancel` parameter may already contain the value `True` in advance, and you can mistakenly reset it back to `False`.
In addition, the number of these checks may increase when the configuration is customizing during the integration.

## Noncompliant Code Example


```bsl
Procedure ОбработкаПроверкиЗаполнения(Cancel, CheckingAttributes) 
  ... 
  Cancel = IsFilleCheckError(); 
  ... 
ProcedueEnd
```

## Compliant Solution


```bsl
Procedure FillCheckProcessing(Cancel, CheckingAttributes) 
  . . . 
  if IsFilleCheckError() The 
    Cancel = Истина;
  EndIf; 
  ... 
ProcedueEnd 
```

Or

```bsl
Cancel = Cancel Or IsFilleCheckError(); 
```


## See

