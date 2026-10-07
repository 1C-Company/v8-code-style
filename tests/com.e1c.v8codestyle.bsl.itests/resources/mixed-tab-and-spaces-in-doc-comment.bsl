// ...
// Call options:
//	 Some(String, Int) - description
//	 description
Procedure Some(Param1, Param2)
EndProcedure

// ...
// Example:
//  Res = Some(String, Int)
//  	If (1 = 1) Then
//  EndIf	
Procedure Some3(Param1, Param2)
EndProcedure

// ...
//	 Parameters:
//		Param1 - String - description
//			description
//		Param2 - String - description
//			description
Procedure Some(Param1, Param2)
EndProcedure

// ...
// Returns:
//  	String - Some
//			description
Function Some(Param1, Param2)

	Return "";

EndFunction

// ...
// Returns:
//	 String - Some
//		 description
Function Some(Param1, Param2)

	Return "";

EndFunction

//		 error
//	 error
// Returns:
//  String - Some
//		description
Function Some(Param1, Param2)

	Return "";

EndFunction