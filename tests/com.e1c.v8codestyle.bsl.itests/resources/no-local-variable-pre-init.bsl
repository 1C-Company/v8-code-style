Procedure Some()

	If 1 = 1 Then
		A = 10;
	Endif;

	B = A; // ERROR

EndProcedure

Procedure Some2()

	If 1 = 1 Then
	
		If 1 = 1 Then
			A = 10;
		Endif;

		B = A; // ERROR
	Endif;

EndProcedure

Procedure Some3()

	While 1 = 1 Do
		A = 100;
	EndDo;

	B = A; // ERROR

EndProcedure

Procedure Some4(U1, U2)

	If U1 Then
		A = 1;
	ElsIf U2 Then
		A = 2;
	EndIf;

	B = A; // ERROR

EndProcedure

Procedure Some5(Array)

	Try
		A = 1;
	Except
	EndTry;

	B = A; // ERROR

	For Each Item In Array Do
		C = Item;
	EndDo;

	D = C; // ERROR

	For I = 1 To 10 Do
		E = I;
	EndDo;

	F = E; // ERROR

EndProcedure

Procedure Some6(U)

	If U Then
		A = 1;
	Else
		A = 2;
	EndIf;

	B = A; // ERROR, no preinitialization

EndProcedure

Procedure Some7(Array)

	For Each Item In Array Do
		If Item = 1 Then
			A = Item;
		EndIf;
		
		B = A;
	EndDo;

EndProcedure