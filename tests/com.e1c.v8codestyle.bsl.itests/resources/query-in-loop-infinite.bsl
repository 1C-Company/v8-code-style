Procedure WhileStatementInfiniteLoop(SomeArray) Export

	Query = New Query;

	Query.Text =
	"SELECT
	|	1";

	While True Do
		Query.ExecuteBatchWithIntermediateData();
	EndDo;

EndProcedure