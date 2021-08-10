Procedure QueryCorrect(SomeParameter) Export

	Query = New Query;

	Query.SetParameter("SomeParameter", SomeParameter);

	Query.Text =
	"SELECT
	|	1";

	Query.Execute();

EndProcedure

Function QueryExecutionCorrect(Query) Export

	Result = Query.Execute();

	Return Result;

EndFunction

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
		Selection = Query.Execute().Select();
		Selection.Next();
	EndDo;

EndProcedure

Procedure ForToStatementIncorrect(SomeArray) Export

	Query = New Query;

	Query.Text =
	"SELECT
	|	1";

	For ArrayElement = 1 To 10 Do
		Query.SetParameter("SomeParameter", ArrayElement);
		Result = Query.ExecuteBatch();
		Result[0].Select();
	EndDo;

EndProcedure

Procedure WhileStatementIncorrect(SomeArray) Export

	Query = New Query;

	Query.Text =
	"SELECT
	|	1";

	While SomeArray.Count() > 0 And SomeArray.Count() < 5 Do
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

	While SomeParameter = 0 Do
		Result = QueryExecutionCorrect(Query);
		Result.Select();
	EndDo;

EndProcedure

Procedure LoopCallsMethodWithOtherMethodIncorrect(SomeParameter) Export

	Query = New Query;

	Query.Text =
	"SELECT
	|	1";

	While SomeParameter = 0 Do
		MethodCallsQueryCorrect(Query);
	EndDo;

EndProcedure

Procedure LoopWithConditionsIncorrect(SomeArray) Export

	While SomeArray.Count() = 0 Do
		If SomeArray.Size() = 1 Then
			ForEachStatementIncorrect(SomeArray);

		ElsIf SomeArray.Size() = 2 Then
			ForToStatementIncorrect(SomeArray);

		Else
			WhileStatementIncorrect(SomeArray);

		EndIf;
	EndDo;

EndProcedure