Procedure QueryCorrect(SomeParameter) Export

	SimpleQuery = New Query;

	SimpleQuery.SetParameter("SomeParameter", SomeParameter);

	SimpleQuery.Text =
	"SELECT
	|	1";

	SimpleQuery.Execute();

EndProcedure

Function QueryExecutionCorrect(Query) Export

	Result = Query.Execute();

	Return Result;

EndFunction

Procedure MethodCallsQueryCorrect(SomeParameter) Export

	QueryCorrect(SomeParameter);

EndProcedure

Procedure ForEachStatementIncorrect(SomeArray) Export

	ForEachQuery = New Query;

	ForEachQuery.Text =
	"SELECT
	|	1";

	For Each ArrayElement In SomeArray Do
		ForEachQuery.SetParameter("SomeParameter", ArrayElement);
		Selection = ForEachQuery.Execute().Select();
		Selection.Next();
	EndDo;

EndProcedure

Procedure ForToStatementIncorrect(SomeArray) Export

	ForToQuery = New Query;

	ForToQuery.Text =
	"SELECT
	|	1";

	For ArrayElement = 1 To 10 Do
		ForToQuery.SetParameter("SomeParameter", ArrayElement);
		Result = ForToQuery.ExecuteBatch();
		Result[0].Select();
	EndDo;

EndProcedure

Procedure WhileStatementIncorrect(SomeArray) Export

	WhileQuery = New Query;

	WhileQuery.Text =
	"SELECT
	|	1";

	While SomeArray.Count() > 0 And SomeArray.Count() < 5 Do
		WhileQuery.ExecuteBatchWithIntermediateData();
	EndDo;

EndProcedure

Procedure MethodCallsIncorrectMethodCorrect(SomeArray) Export

	ForEachStatementIncorrect(SomeArray);

EndProcedure

Procedure LoopCallsMethodIncorrect(SomeParameter) Export

	LoopCallQuery = New Query;

	LoopCallQuery.Text =
	"SELECT
	|	1";

	While SomeParameter = 0 Do
		Result = QueryExecutionCorrect(LoopCallQuery);
		Result.Select();
	EndDo;

EndProcedure

Procedure LoopCallsMethodWithOtherMethodIncorrect(SomeParameter) Export

	MethodCallQuery = New Query;

	MethodCallQuery.Text =
	"SELECT
	|	1";

	While SomeParameter = 0 Do
		MethodCallsQueryCorrect(MethodCallQuery);
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

Function GetNewQuery(ArrayElement)
	
	FunctionQuery = New Query;

	FunctionQuery.Text =
	"SELECT
	|	1";
	
	FunctionQuery.SetParameter("SomeParameter", ArrayElement);

	Return FunctionQuery;
	
EndFunction

Procedure QueryTypeFromFunctionIncorrect(SomeArray) Export
	
	For Each ArrayElement In SomeArray Do
		Selection = GetNewQuery(ArrayElement).Execute().Select();
		Selection.Next();
	EndDo;
	
EndProcedure
