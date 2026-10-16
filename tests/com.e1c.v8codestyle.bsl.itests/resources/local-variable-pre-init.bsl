Var ModuleVariable;

Procedure Some()

	A = 1;
	If 1 = 1 Then
		A = 10;
	Endif;

	B = A;

EndProcedure

Procedure Some2()

	Var A = 1;

	If 1 = 1 Then
		A = 10;
	Endif;

	B = A;

EndProcedure

Procedure Some3(A)

	If 1 = 1 Then
		A = 10;
	Endif;

	B = A;

EndProcedure

Procedure Some4()

	If 1 = 1 Then
	
		A = 1;
	
		If 1 = 1 Then
			A = 10;
		Endif;

		B = A;
	Endif;

EndProcedure

Procedure Some5(U)

	Var A; // preinitialization

	If U Then
		A = 1;
	EndIf;

	B = A;

EndProcedure

Procedure Some6(Parameter)

	A = ModuleVariable;
	B = Parameter;

EndProcedure

Procedure Some7(Array)

	Sum = 0;

	For Each Item In Array Do
		Sum = Sum + Item;
	EndDo;

	Message(Sum);

EndProcedure

Procedure Test(U)

	If U Then
		A = 1;
	EndIf;

	A = 2;
	B = A;

EndProcedure
