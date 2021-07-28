Procedure QueryCorrect(SomeParameter) Export

	Query = New Query;

	Query.SetParameter("SomeParameter", SomeParameter);

	Query.Text =
	"SELECT
	|	1";

	Query.Execute();

EndProcedure

Procedure QueryExecutionCorrect(Query) Export

	Query.Execute();

EndProcedure

Procedure MethodCallsQueryCorrect(SomeParameter) Export

	QueryCorrect(SomeParameter);

EndProcedure

Procedure ForEachStatementIncorrect(SomeArray) Export

	Query = New Query;

	Query.Text =
	"SELECT
	|	1";

	For Each ArrayElement In SomeArray Do
		Query.SetParameter("SomeParameter", ArrayElement);
		Query.Execute();
	EndDo;

EndProcedure

Procedure ForToStatementIncorrect(SomeArray) Export

	Query = New Query;

	Query.Text =
	"SELECT
	|	1";

	For ArrayElement = 1 To 10 Do
		Query.SetParameter("SomeParameter", ArrayElement);
		Query.ExecuteBatch();
	EndDo;

EndProcedure

Procedure WhileStatementIncorrect(SomeArray) Export

	Query = New Query;

	Query.Text =
	"SELECT
	|	1";

	While True Do
		Query.ExecuteBatchWithIntermediateData();
	EndDo;

EndProcedure

Procedure MethodCallsIncorrectMethodCorrect(SomeArray) Export

	ForEachStatementIncorrect(SomeArray);

EndProcedure

Procedure LoopCallsMethodIncorrect(SomeParameter) Export

	Query = New Query;

	Query.Text =
	"SELECT
	|	1";

	While True Do
		QueryExecutionCorrect(Query);
	EndDo;

EndProcedure

Procedure LoopCallsMethodWithOtherMethodIncorrect(SomeParameter) Export

	Query = New Query;

	Query.Text =
	"SELECT
	|	1";

	While True Do
		MethodCallsQueryCorrect(Query);
	EndDo;

EndProcedure

Procedure LoopWithConditionsIncorrect(SomeArray) Export

	While True Do
		If SomeArray.Size() = 1 Then
			ForEachStatementIncorrect(SomeArray);
			
		ElsIf SomeArray.Size() = 2 Then
			ForToStatementIncorrect(SomeArray);
			
		Else
			WhileStatementIncorrect(SomeArray);
			
		EndIf;
	EndDo;

EndProcedure