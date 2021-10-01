

Procedure ProductsOnWriteOnWrite(Source, Cancel) Export
	
	// Noncompliant
	Cancel = NeedCancel(Source) AND Cancel;
	
EndProcedure

Procedure Noncompliant(Param1, Cancel) Export
	
	Cancel = Param1 AND Cancel;
	
EndProcedure

Procedure Compliant(Param1, Cancel) Export
	
	Cancel = NeedCancel(Param1) AND Param1 OR Cancel;
	
EndProcedure

Function NeedCancel(Parameters)
	Return False;
EndFunction
