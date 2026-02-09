# Tags are placed incorrectly, inside the language construct

## Noncompliant Code Example

```bsl

Procedure NewProcedure(XDTOObject)
      
    If True // @fqn    
    Then // @fqn
    
        Value = "";  
    ElseIf False
      
    Then // @noN-nls-1
        
        Value = "";
        
    Else
        Value = "";
        
    EndIf;    
     
EndProcedure

```

## Compliant Solution

```bsl

Procedure NewProcedure(XDTOObject)
      
    If True Then 
    
        Value = ""; // @fqn  
    ElseIf False
      
    Then 
        
        Value = ""; // @noN-nls-1
        
    Else
        Value = "";
        
    EndIf;    
     
EndProcedure

```

## See