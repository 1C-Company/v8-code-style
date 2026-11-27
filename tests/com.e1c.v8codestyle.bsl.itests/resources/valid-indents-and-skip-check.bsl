// ...
// Call options:
//  Some(String, Int) - description
//  description
//@skip-check my-some-check
Procedure Some(Param1, Param2)
EndProcedure

// ...
// Example:
//  Res = Some(String, Int)
//  If (1 = 1) Then
//  EndIf
// @skip-check my-some-check
Procedure Some3(Param1, Param2)
EndProcedure

// ...
// Parameters:
//  Param1 - String - description
//		description
//  Param2 - String - description
//		description
//  @skip-check my-some-check
Procedure Some(Param1, Param2)
EndProcedure

// ...
// Returns:
//  String - Some
//    description
//                    @skip-check my-some-check
Function Some(Param1, Param2)

	Return "";

EndFunction

// ...
// Returns:
//  String - Some
   //@skip-check my-some-check
Function Some(Param1, Param2)

	Return "";

EndFunction

// ...
//@skip-check my-some-check
// Returns:
//  String - Some
Function Some(Param1, Param2)

	Return "";

EndFunction